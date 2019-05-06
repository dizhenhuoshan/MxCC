package princeYang.mxcc.frontend;

import princeYang.mxcc.Config;
import princeYang.mxcc.ast.*;
import princeYang.mxcc.errors.MxError;
import princeYang.mxcc.ir.*;
import princeYang.mxcc.scope.ClassEntity;
import princeYang.mxcc.scope.FuncEntity;
import princeYang.mxcc.scope.Scope;
import princeYang.mxcc.scope.VarEntity;

import java.util.*;

public class IRBuilder extends ScopeScanner
{
    private IRROOT irRoot = new IRROOT();
    private Scope globalScope, currentScope;
    private List<GlobalVarInit> globalVarInitList = new ArrayList<GlobalVarInit>();
    private boolean isArgDecl = false, memAccessing = false;
    private BasicBlock currentBlock;
    private IRFunction currentFunc;
    private String currentClass;
    private BasicBlock currentLoopStepBlock = null, currentLoopAfterBlock = null;

    public IRBuilder(Scope globalScope)
    {
        this.globalScope = globalScope;
    }

    @Override
    public void visit(MxProgNode node)
    {
        currentScope = globalScope;
        for (DeclNode declNode : node.getDeclNodesList())
        {
            if (declNode instanceof VarDeclNode)
                declNode.accept(this);
            else if (declNode instanceof FuncDeclNode)
            {
                FuncEntity funcEntity = currentScope.getFunc(declNode.getIdentName());
                irRoot.addFunction(new IRFunction(funcEntity));
            }
            else if (declNode instanceof ClassDeclNode)
            {
                ClassEntity classEntity = currentScope.getClass(declNode.getIdentName());
                currentScope = classEntity.getClassScope();
                for (FuncDeclNode classFuncDecl : ((ClassDeclNode) declNode).getFuncDeclList())
                {
                    FuncEntity funcEntity = currentScope.getFunc(declNode.getIdentName());
                    irRoot.addFunction(new IRFunction(funcEntity));
                }
                currentScope = currentScope.getFather();
            }
        }

        FuncDeclNode varInitFunc = globalVarInitFunc();
        varInitFunc.accept(this);

        for (DeclNode declNode: node.getDeclNodesList())
        {
            if (declNode instanceof FuncDeclNode)
                declNode.accept(this);
            else if (declNode instanceof ClassDeclNode)
                declNode.accept(this);
            else if (!(declNode instanceof VarDeclNode))
                throw new MxError("IRBuilder: declNode Type is invalid in visiting MxProgNode!\n");
        }
        updateRecursiveCalleeSet();
    }

    @Override
    public void visit(VarDeclNode node)
    {
        VarEntity varEntity = currentScope.getVar(node.getIdentName());
        if (varEntity.isUnUsed())
            return;
        if (!currentScope.isGlobalScope())
        {
            // Not global variable
            VirtualReg virtualReg = new VirtualReg(varEntity.getIdent());
            varEntity.setIrReg(virtualReg);
            if (isArgDecl)
                currentFunc.insertArgReg(virtualReg);
            if (node.getInitValue() == null)
            {
                if (!isArgDecl)
                    currentBlock.appendInst(new Move(currentBlock, virtualReg, new Immediate(0)));
            }
            else
            {
                if (node.getInitValue() instanceof ConstBoolNode || node.getInitValue().getType() instanceof BoolType)
                {
                    node.getInitValue().setBoolTrueBlock(new BasicBlock(currentFunc, "boolTrueBlock"));
                    node.getInitValue().setBoolFalseBlock(new BasicBlock(currentFunc, "boolFalseBlock"));
                }
                node.getInitValue().accept(this);
                processAssign(virtualReg, node.getInitValue(), 0, Config.regSize, false);
            }
        }
        else
        {
            // Global variable
            Type varType = node.getVarType().getType();
            StaticVar staticVar = new StaticVar(node.getIdentName(), Config.regSize);
            irRoot.addStaticData(staticVar);
            varEntity.setIrReg(staticVar);
            if (node.getInitValue() != null)
            {
                GlobalVarInit globalVarInit = new GlobalVarInit(node.getIdentName(), node.getInitValue());
                globalVarInitList.add(globalVarInit);
            }
        }
    }

    @Override
    public void visit(FuncDeclNode node)
    {
        String funcIdent = node.getIdentName();
        if (currentClass != null)
            funcIdent = "__class__" + funcIdent;
        currentFunc = irRoot.getFunctionMap().get(funcIdent);
        currentFunc.generateEntry();
        currentBlock = currentFunc.getBlockEnter();
        // function para
        Scope prevScope = currentScope;
        currentScope = node.getFuncBlock().getScope();
        if (currentClass != null)
        {
            // this is the first para of a class function
            VirtualReg virtualReg = new VirtualReg("this");
            currentScope.getVar("this").setIrReg(virtualReg);
            currentFunc.insertArgReg(virtualReg);
        }
        isArgDecl = true;
        for (VarDeclNode varDeclNode : node.getParaDeclList())
            varDeclNode.accept(this);
        isArgDecl = false;
        currentScope = prevScope;
        // for main function, make global init.
        if (node.getIdentName().equals("main"))
            currentBlock.appendInst(new FuncCall(currentBlock, irRoot.getFunctionMap().get("__globalVarInit"),
                    null, new ArrayList<IRValue>()));
        node.getFuncBlock().accept(this);
        // for defalut return value
        if (!currentBlock.isContainJump())
        {
            if (node.getRetType() == null || node.getRetType().getType() instanceof VoidType)
                currentBlock.setJumpInst(new Return(currentBlock, null));
            else
                currentBlock.setJumpInst(new Return(currentBlock, new Immediate(0)));
        }
        // if function have multiply return, merge them to one block
        if (currentFunc.getReturnInstList().size() > 1)
        {
            BasicBlock mergeRetBlock = new BasicBlock(currentFunc, "__mergeReturn__" + currentFunc.getFuncName());
            VirtualReg returnReg;
            if (node.getRetType() == null || node.getRetType().getType() instanceof VoidType)
                returnReg = null;
            else
                returnReg = new VirtualReg("returnReg");
            List<Return> returnList = new ArrayList<Return>(currentFunc.getReturnInstList());
            for (Return returnInst : returnList)
            {
                BasicBlock parentBlock = returnInst.getFatherBlock();
                if (returnInst.getRetValue() != null)
                    returnInst.prepend(new Move(parentBlock, returnReg, returnInst.getRetValue()));
                returnInst.remove();
                parentBlock.setJumpInst(new Jump(parentBlock, mergeRetBlock));
            }
            mergeRetBlock.setJumpInst(new Return(mergeRetBlock, returnReg));
            currentFunc.setBlockLeave(mergeRetBlock);
        }
        else
            // only one return inst, use it for final return block
            currentFunc.setBlockLeave(currentFunc.getReturnInstList().get(0).getFatherBlock());

        currentFunc = null;
    }

    @Override
    public void visit(ClassDeclNode node)
    {
        currentClass = node.getIdentName();
        currentScope = globalScope;
        for (FuncDeclNode funcDecl: node.getFuncDeclList())
            funcDecl.accept(this);
        currentClass = null;
    }

    @Override
    public void visit(FuncBlockNode node)
    {
        currentScope = node.getScope();
        for (Node subNode : node.getStateList())
        {
            if (subNode instanceof StateNode || subNode instanceof VarDeclNode)
                subNode.accept(this);
            else
                throw new MxError("IR FuncBlockNode: node in StateList is invalid!\n");
        }
        currentScope = currentScope.getFather();
    }

    @Override
    public void visit(WhileStateNode node)
    {
        BasicBlock loopBodyBlock = new BasicBlock(currentFunc, "__loop__while_body");
        BasicBlock loopAfterBlock = new BasicBlock(currentFunc, "__loop__while_after");
        BasicBlock condBlock = new BasicBlock(currentFunc, "__loop__while_cond");
        BasicBlock prevLoopCondBlock = currentLoopStepBlock, prevLoopAfterBlock = currentLoopAfterBlock;
        currentLoopStepBlock = condBlock;
        currentLoopAfterBlock = loopAfterBlock;

        // check cond -> loop -> check cond -> loop -> ......
        currentBlock.setJumpInst(new Jump(currentBlock, condBlock));
        currentBlock = condBlock;
        node.getConditionExpr().setBoolTrueBlock(loopBodyBlock);
        node.getConditionExpr().setBoolFalseBlock(loopAfterBlock);
        node.getConditionExpr().accept(this);

        // for while(1/0) simply use branch of condition block
        if (node.getConditionExpr() instanceof ConstBoolNode)
            currentBlock.setJumpInst(new Branch(currentBlock, node.getConditionExpr().getRegValue(),
                    node.getConditionExpr().getBoolTrueBlock(), node.getConditionExpr().getBoolFalseBlock()));

        // proocess loop body
        currentBlock = loopBodyBlock;
        node.getLoopState().accept(this);
        if (!currentBlock.isContainJump())
            currentBlock.setJumpInst(new Jump(currentBlock, condBlock));

        // done, escape while
        currentLoopStepBlock = prevLoopCondBlock;
        currentLoopAfterBlock = prevLoopAfterBlock;
        currentBlock = loopAfterBlock;
    }

    @Override
    public void visit(ForStateNode node)
    {
        BasicBlock stopBlock, stepBlock;
        BasicBlock loopBodyBlock = new BasicBlock(currentFunc, "__loop__for_body");
        BasicBlock loopAfterBlock = new BasicBlock(currentFunc, "__loop__for_after");
        // check condition(stop block) -> loop(body block) -> step(step block) -> check condition -> loop -> ......
        if (node.getStopExpr() != null)
            stopBlock = new BasicBlock(currentFunc, "__loop__for_stop");
        else stopBlock = loopBodyBlock;
        if (node.getStepExpr() != null)
            stepBlock = new BasicBlock(currentFunc, "__loop__for_step");
        else stepBlock = stopBlock;
        irRoot.getIRForMap().put(node, new IRFor(stopBlock, stepBlock, loopBodyBlock, loopAfterBlock));
        stopBlock.setForStateNode(node);
        stepBlock.setForStateNode(node);
        loopBodyBlock.setForStateNode(node);
        loopAfterBlock.setForStateNode(node);
        // for multi level for, backup current loop info and enter next level.
        BasicBlock prevLoopStepBlock = currentLoopStepBlock, prevLoopAfterBlock = currentLoopAfterBlock;
        currentLoopStepBlock = stepBlock;
        currentLoopAfterBlock = loopAfterBlock;
        if (node.getStartExpr() != null)
            node.getStartExpr().accept(this);
        currentBlock.setJumpInst(new Jump(currentBlock, stopBlock));

        // condition check
        if (node.getStopExpr() != null)
        {
            currentBlock = stopBlock;
            // condition false -> escape loop
            node.getStopExpr().setBoolFalseBlock(loopAfterBlock);
            // condition true -> continue loop
            node.getStopExpr().setBoolTrueBlock(loopBodyBlock);
            node.getStopExpr().accept(this);
            // for const bool state, simply use if branch escape the loop
            if (node.getStopExpr() instanceof ConstBoolNode)
                currentBlock.setJumpInst(new Branch(currentBlock, node.getStopExpr().getRegValue(),
                        node.getStopExpr().getBoolTrueBlock(), node.getStopExpr().getBoolFalseBlock()));
        }

        // step process
        if (node.getStepExpr() != null)
        {
            currentBlock = stepBlock;
            node.getStepExpr().accept(this);
            // after step check condition
            currentBlock.setJumpInst(new Jump(currentBlock, stopBlock));
        }

        // loop body process
        currentBlock = loopBodyBlock;
        if (node.getLoopState() != null)
        {
            node.getLoopState().accept(this);
            if (!currentBlock.isContainJump())
                currentBlock.setJumpInst(new Jump(currentBlock, stepBlock));
        }
        else
        {
            if (!currentBlock.isContainJump())
                currentBlock.setJumpInst(new Jump(currentBlock, stepBlock));
        }

        // escape for
        currentLoopStepBlock = prevLoopStepBlock;
        currentLoopAfterBlock = prevLoopAfterBlock;
        currentBlock = loopAfterBlock;
    }

    @Override
    public void visit(IfStateNode node)
    {
        BasicBlock thenBlock = new BasicBlock(currentFunc, "__branch__if_then");
        BasicBlock afterBlock = new BasicBlock(currentFunc, "__branch_if_after");
        BasicBlock elseBlock = null;
        if (node.getElseState() == null)
        {
            // true -> then  false -> escape if
            node.getConditionExpr().setBoolTrueBlock(thenBlock);
            node.getConditionExpr().setBoolFalseBlock(afterBlock);
        }
        else
        {
            // true -> then   false -> else
            elseBlock = new BasicBlock(currentFunc, "__branch__if_else");
            node.getConditionExpr().setBoolTrueBlock(thenBlock);
            node.getConditionExpr().setBoolFalseBlock(elseBlock);
        }
        node.getConditionExpr().accept(this);
        if (node.getConditionExpr() instanceof ConstBoolNode)
            currentBlock.setJumpInst(new Branch(currentBlock, node.getConditionExpr().getRegValue(),
                    node.getConditionExpr().getBoolTrueBlock(), node.getConditionExpr().getBoolFalseBlock()));

        currentBlock = thenBlock;
        node.getThenState().accept(this);
        if (!currentBlock.isContainJump())
            currentBlock.setJumpInst(new Jump(currentBlock, afterBlock));
        if (node.getElseState() != null)
        {
            currentBlock = elseBlock;
            node.getElseState().accept(this);
            if (!currentBlock.isContainJump())
                currentBlock.setJumpInst(new Jump(currentBlock, afterBlock));
        }
        currentBlock = afterBlock;
    }

    @Override
    public void visit(ReturnStateNode node)
    {
        Type returnType = node.getRetExpr().getType();
        // void return
        if (returnType == null || returnType instanceof VoidType)
            currentBlock.setJumpInst(new Jump(currentBlock, null));
        else if (!(node.getRetExpr() instanceof ConstBoolNode) && returnType instanceof BoolType)
        {
            // bool return type
            VirtualReg retReg = new VirtualReg("boolReturnReg");
            node.getRetExpr().setBoolFalseBlock(new BasicBlock(currentFunc, null));
            node.getRetExpr().setBoolTrueBlock(new BasicBlock(currentFunc, null));
            node.getRetExpr().accept(this);
            processAssign(retReg, node.getRetExpr(), 0, Config.regSize, false);
            currentBlock.setJumpInst(new Return(currentBlock, retReg));
        }
        else
        {
            // normal return type
            node.getRetExpr().accept(this);
            currentBlock.setJumpInst(new Return(currentBlock, node.getRetExpr().getRegValue()));
        }
    }

    @Override
    public void visit(BreakStateNode node)
    {
        currentBlock.setJumpInst(new Jump(currentBlock, currentLoopAfterBlock));
    }

    @Override
    public void visit(ContinueStateNode node)
    {
        currentBlock.setJumpInst(new Jump(currentBlock, currentLoopStepBlock));
    }

    @Override
    public void visit(ExprStateNode node)
    {
        node.getExprState().accept(this);
    }

    @Override
    public void visit(FunctionCallExprNode node)
    {
        FuncEntity funcEntity = node.getFuncEntity();
        String targetFuncName = funcEntity.getIdent();
        ExprNode thisExpr = null;
        List<IRValue> paraList = new ArrayList<IRValue>();
        if (funcEntity.isInClass())
        {
            if (node.getFuncExpr() instanceof MemoryAccessExprNode)
                thisExpr = ((MemoryAccessExprNode) node.getFuncExpr()).getHostExpr();
            else
            {
                if (currentClass != null)
                {
                    thisExpr = new ThisExprNode(null);
                    thisExpr.setType(new ClassType(currentClass));
                }
                else throw new MxError("IR Builder: In FunctionCall node this para is invalid!\n");
            }
            thisExpr.accept(this);
            String hostClassName;
            if (thisExpr.getType() instanceof ClassType)
                hostClassName = ((ClassType) thisExpr.getType()).getClassIdent();
            else if (thisExpr.getType() instanceof ArrayType)
                hostClassName = "__array";
            else if (thisExpr.getType() instanceof StringType)
                hostClassName = "string";
            else throw new MxError("IR Builder: In FunctionCall node thisHostClassName is invalid!\n");
            targetFuncName = genClassFuncName(hostClassName, targetFuncName);
            paraList.add(thisExpr.getRegValue());
        }
        // build in functions
        if (funcEntity.isBuildIn())
            buildInFunctionProcessor(node, targetFuncName, funcEntity, thisExpr);
        else
        {
            for (ExprNode para : node.getParaList())
            {
                para.accept(this);
                paraList.add(para.getRegValue());
            }
            VirtualReg destReg = new VirtualReg(null);
            IRFunction calleeFunc = irRoot.getFunctionMap().get(targetFuncName);
            currentBlock.appendInst(new FuncCall(currentBlock, calleeFunc, destReg, paraList));
            node.setRegValue(destReg);
            if (node.getBoolFalseBlock() != null)
                currentBlock.setJumpInst(new Branch(currentBlock, destReg, node.getBoolTrueBlock(), node.getBoolFalseBlock()));
        }
    }

    @Override
    public void visit(MemoryAccessExprNode node)
    {
        boolean prevMemAccessing = memAccessing;
        this.memAccessing = false;
        node.getHostExpr().accept(this);
        this.memAccessing = prevMemAccessing;

        String classIdent = ((ClassType) node.getHostExpr().getType()).getClassIdent();
        IRValue classAddr = node.getHostExpr().getRegValue();
        ClassEntity classEntity = currentScope.getClass(classIdent);
        VarEntity memberVarEntity = classEntity.getClassScope().getSelfVar(node.getMemberStr());

        if (memAccessing)
        {
            node.setAddrValue(classAddr);
            node.setAddrOffset(memberVarEntity.getMemOffset());
        }
        else
        {
            VirtualReg destReg = new VirtualReg(null);
            node.setRegValue(destReg);
            currentBlock.appendInst(new Load(currentBlock, destReg, classAddr, memberVarEntity.getType().getSize(), memberVarEntity.getMemOffset()));
            if (node.getBoolFalseBlock() != null)
                currentBlock.setJumpInst(new Branch(currentBlock, destReg, node.getBoolTrueBlock(), node.getBoolFalseBlock()));
        }
    }

    @Override
    public void visit(ArrayAccessExprNode node)
    {
        boolean prevMemAccessing = memAccessing;
        this.memAccessing = false;
        node.getArrExpr().accept(this);
        node.getSubExpr().accept(this);
        this.memAccessing = prevMemAccessing;

        VirtualReg destReg = new VirtualReg(null);
        Immediate elemSize = new Immediate(node.getArrExpr().getType().getSize());
        // TODO: MUL can be replaced with shift
        currentBlock.appendInst(new BinaryOperation(currentBlock, node.getSubExpr().getRegValue(), elemSize, IRBinaryOp.MUL, destReg));
        currentBlock.appendInst(new BinaryOperation(currentBlock, destReg, node.getArrExpr().getRegValue(), IRBinaryOp.ADD, destReg));
        if (memAccessing)
        {
            node.setAddrValue(destReg);
            node.setAddrOffset(Config.regSize);
        }
        else
        {
            node.setRegValue(destReg);
            currentBlock.appendInst(new Load(currentBlock, destReg, destReg, node.getArrExpr().getType().getSize(), Config.regSize));
            if (node.getBoolFalseBlock() != null)
                currentBlock.setJumpInst(new Branch(currentBlock, destReg, node.getBoolTrueBlock(), node.getBoolFalseBlock()));
        }
    }

    @Override
    public void visit(AssignExprNode node)
    {
        boolean memAccessingOp = checkMemAccessing(node.getLhs());
        this.memAccessing = memAccessingOp;
        node.getLhs().accept(this);

        if (node.getRhs().getType() instanceof BoolType && !(node.getRhs() instanceof ConstBoolNode))
        {
            node.getRhs().setBoolTrueBlock(new BasicBlock(currentFunc, null));
            node.getRhs().setBoolFalseBlock(new BasicBlock(currentFunc, null));
        }
        node.getRhs().accept(this);

        int memOffset = 0;
        IRValue destValue;
        if (memAccessing)
        {
            memOffset = node.getLhs().getAddrOffset();
            destValue = node.getLhs().getAddrValue();
        }
        else
            destValue = node.getLhs().getRegValue();
        processAssign(destValue, node.getRhs(), memOffset, Config.regSize,  memAccessing);
        node.setRegValue(node.getRhs().getRegValue());
    }

    @Override
    public void visit(IdentExprNode node)
    {
        VarEntity varEntity = node.getVarEntity();
        if (varEntity.getIrReg() != null)
        {
            node.setRegValue(varEntity.getIrReg());
            if (node.getBoolFalseBlock() != null)
                currentBlock.setJumpInst(new Branch(currentBlock, varEntity.getIrReg(), node.getBoolTrueBlock(), node.getBoolFalseBlock()));
        }
        else
        {
            ThisExprNode thisExprNode = new ThisExprNode(null);
            thisExprNode.setType(new ClassType(currentClass));
            MemoryAccessExprNode memoryAccessExprNode = new MemoryAccessExprNode(null, thisExprNode, node.getIdentName());
            memoryAccessExprNode.accept(this);
            if (memAccessing)
            {
                node.setAddrOffset(memoryAccessExprNode.getAddrOffset());
                node.setAddrValue(memoryAccessExprNode.getAddrValue());
            }
            else
            {
                node.setRegValue(memoryAccessExprNode.getRegValue());
                if (node.getBoolFalseBlock() != null)
                    currentBlock.setJumpInst(new Branch(currentBlock, memoryAccessExprNode.getRegValue(), node.getBoolTrueBlock(), node.getBoolFalseBlock()));
            }
            node.setMemAccessing(true); // It's actually this.identifier
        }
    }

    @Override
    public void visit(NewExprNode node)
    {
        
    }

    public IRROOT getIrRoot()
    {
        return irRoot;
    }

    public String genClassFuncName(String className, String funcName)
    {
        return "__class__" + className + "__" + funcName;
    }

    private FuncDeclNode globalVarInitFunc()
    {
        String globalInitName = "__globalVarInit";
        List<Node> varInitStateList = new ArrayList<Node>();
        for (GlobalVarInit globalVarInit : globalVarInitList)
        {
            IdentExprNode lhs = new IdentExprNode(null, globalVarInit.getVarName());
            VarEntity varEntity = globalScope.getVar(globalVarInit.getVarName());
            lhs.setVarEntity(varEntity);
            varInitStateList.add(new AssignExprNode(null, lhs, globalVarInit.getInitValue()));
        }

        FuncBlockNode globalInitBlock = new FuncBlockNode(null, varInitStateList);
        globalInitBlock.setScope(new Scope(globalScope));
        FuncDeclNode globalVarInitFunc = new FuncDeclNode(null, globalInitName,
                new TypeNode(null, nullType), new ArrayList<VarDeclNode>(), globalInitBlock);
        FuncEntity funcEntity = new FuncEntity(globalVarInitFunc, globalInitBlock.getScope());
        globalScope.insertFunc(funcEntity);
        IRFunction irGlobalInitFunc = new IRFunction(funcEntity);
        irRoot.addFunction(irGlobalInitFunc);
        return globalVarInitFunc;
    }

    private void updateRecursiveCalleeSet()
    {
        Set<IRFunction> recursiveCalleeSet = new HashSet<IRFunction>();
        boolean flag = true;
        for (IRFunction irFunction : irRoot.getFunctionMap().values())
        {
            irFunction.updateCalleeSet();
            irFunction.recurCalleeSet.clear();
        }
        while (flag)
        {
            flag = false;
            for (IRFunction irFunction : irRoot.getFunctionMap().values())
            {
                recursiveCalleeSet.clear();
                recursiveCalleeSet.addAll(irFunction.calleeSet);
                for (IRFunction calleeFunc : irFunction.calleeSet)
                    recursiveCalleeSet.addAll(calleeFunc.recurCalleeSet);
                if (!(irFunction.recurCalleeSet.equals(recursiveCalleeSet)))
                {
                    irFunction.recurCalleeSet.clear();
                    irFunction.recurCalleeSet.addAll(recursiveCalleeSet);
                    flag = true;
                }
            }
        }
    }

    private void processAssign(IRValue dest, ExprNode src, int offset, int size, boolean accessMem)
    {
        if (src.getBoolTrueBlock() == null)
        {
            if (accessMem)
                currentBlock.appendInst(new Store(currentBlock, src.getRegValue(), dest, size, offset));
            else
                currentBlock.appendInst(new Move(currentBlock, (IRReg) dest, src.getRegValue()));
        }
        else
        {
            BasicBlock mergeBlock = new BasicBlock(currentFunc, "merge BasicBlock");
            if (accessMem)
            {
                src.getBoolFalseBlock().appendInst(new Store(src.getBoolFalseBlock(), new Immediate(0),
                        dest, Config.regSize, offset));
                src.getBoolTrueBlock().appendInst(new Store(src.getBoolTrueBlock(), new Immediate(1),
                        dest, Config.regSize, offset));
            }
            else
            {
                src.getBoolFalseBlock().appendInst(new Move(src.getBoolFalseBlock(), (VirtualReg) dest, new Immediate(0)));
                src.getBoolTrueBlock().appendInst(new Move(src.getBoolTrueBlock(), (VirtualReg) dest, new Immediate(1)));
            }
            if (!src.getBoolFalseBlock().isContainJump())
                src.getBoolFalseBlock().setJumpInst(new Jump(src.getBoolFalseBlock(), mergeBlock));
            if (!src.getBoolTrueBlock().isContainJump())
                src.getBoolTrueBlock().setJumpInst(new Jump(src.getBoolTrueBlock(), mergeBlock));
        }
    }

    private void buildInFunctionProcessor(FunctionCallExprNode callExprNode, String targetFuncName, FuncEntity funcEntity, ExprNode thisExpr)
    {
        IRFunction calleeFunc;
        List<IRValue> paras = new ArrayList<IRValue>();
        ExprNode para0, para1;
        VirtualReg destVReg;
        boolean prevMemAccessing = this.memAccessing;
        switch (targetFuncName)
        {
            case IRROOT.buildInPrint:
            case IRROOT.buildInPrintln:
                para0 = callExprNode.getParaList().get(0);
                printfProcessor(targetFuncName, para0);
                break;

            case IRROOT.buildInGetString:
                destVReg = new VirtualReg("getStringBuffer");
                calleeFunc = irRoot.getBuildInFuncMap().get(targetFuncName);
                currentBlock.appendInst(new FuncCall(currentBlock, calleeFunc, destVReg, paras));
                callExprNode.setRegValue(destVReg);
                break;

            case IRROOT.buildInGetInt:
                destVReg = new VirtualReg("getIntBuffer");
                calleeFunc = irRoot.getBuildInFuncMap().get(targetFuncName);
                currentBlock.appendInst(new FuncCall(currentBlock, calleeFunc, destVReg, paras));
                callExprNode.setRegValue(destVReg);
                break;

            case IRROOT.buildInToString:
                destVReg = new VirtualReg("toStringRes");
                calleeFunc = irRoot.getBuildInFuncMap().get(targetFuncName);
                currentBlock.appendInst(new FuncCall(currentBlock, calleeFunc, destVReg, paras));
                callExprNode.setRegValue(destVReg);
                break;

            case IRROOT.buildInClassStringLength:
                // TODO: string length
                break;

            case IRROOT.buildInClassStringSubString:
                destVReg = new VirtualReg("subStringRes");
                para0 = callExprNode.getParaList().get(0);
                para1 = callExprNode.getParaList().get(1);
                para0.accept(this);
                para1.accept(this);
                paras.add(thisExpr.getRegValue());
                paras.add(para0.getRegValue());
                paras.add(para1.getRegValue());
                calleeFunc = irRoot.getBuildInFuncMap().get(targetFuncName);
                currentBlock.appendInst(new FuncCall(currentBlock, calleeFunc, destVReg, paras));
                callExprNode.setRegValue(destVReg);
                break;

            case IRROOT.buildInClassStringParseInt:
                destVReg = new VirtualReg("paserIntRes");
                paras.add(thisExpr.getRegValue());
                calleeFunc = irRoot.getBuildInFuncMap().get(targetFuncName);
                currentBlock.appendInst(new FuncCall(currentBlock, calleeFunc, destVReg, paras));
                callExprNode.setRegValue(destVReg);
                break;

            case IRROOT.buildInClassStringOrd:
                destVReg = new VirtualReg("ordRes");
                para0 = callExprNode.getParaList().get(0);
                para0.accept(this);
                paras.add(thisExpr.getRegValue());
                paras.add(para0.getRegValue());
                calleeFunc = irRoot.getFunctionMap().get(targetFuncName);
                currentBlock.appendInst(new FuncCall(currentBlock, calleeFunc, destVReg, paras));
                callExprNode.setRegValue(destVReg);
                break;

            case IRROOT.buildInClassArraySize:
                //TODO: Array size
                break;
        }
        this.memAccessing = prevMemAccessing;
    }

    private void printfProcessor(String funcName, ExprNode printValue)
    {
        if (!(printValue.getType() instanceof StringType))
            throw new MxError("IR Builder: printProcessor value is not String!\n");
        // print (a + b) / println(a + b)
        if (printValue instanceof BinaryExprNode)
        {
            printfProcessor("print", ((BinaryExprNode) printValue).getLhs());
            printfProcessor(funcName, ((BinaryExprNode) printValue).getRhs());
        }
        else
        {
            List<IRValue> args = new ArrayList<IRValue>();
            IRFunction calleeFunc;
            // print(toString(i)) -> printInt(i)
            if (printValue instanceof FunctionCallExprNode && ((FunctionCallExprNode) printValue).getFuncEntity().getIdent() == "toString")
            {
                ExprNode intValue = ((FunctionCallExprNode) printValue).getParaList().get(0);
                intValue.accept(this);
                args.add(intValue.getRegValue());
                calleeFunc = irRoot.getBuildInFuncMap().get(funcName + "ForInt");
            }
            else
            {
                printValue.accept(this);
                args.add(printValue.getRegValue());
                calleeFunc = irRoot.getBuildInFuncMap().get(funcName);
            }
            currentBlock.appendInst(new FuncCall(currentBlock, calleeFunc, null, args));
        }
    }

    private boolean checkIdentMemAccess(IdentExprNode node)
    {
        if (node.hasMemAccessChecked())
            return node.isMemAccessing();
        else
        {
            if (currentClass != null)
            {
                VarEntity varEntity = currentScope.getVar(node.getIdentName());
                node.setMemAccessing(varEntity.getIrReg() == null);
            }
            else
                node.setMemAccessing(false);
            node.setMemAccessChecked(true);
            return node.isMemAccessing();
        }
    }

    private boolean checkMemAccessing(ExprNode node)
    {
        return node instanceof ArrayAccessExprNode || node instanceof MemoryAccessExprNode
                || (node instanceof IdentExprNode && checkIdentMemAccess((IdentExprNode) node));
    }
}
