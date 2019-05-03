package princeYang.mxcc.frontend;

import princeYang.mxcc.Config;
import princeYang.mxcc.ast.*;
import princeYang.mxcc.errors.MxError;
import princeYang.mxcc.ir.*;
import princeYang.mxcc.scope.ClassEntity;
import princeYang.mxcc.scope.FuncEntity;
import princeYang.mxcc.scope.Scope;
import princeYang.mxcc.scope.VarEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IRBuilder extends ScopeScanner
{
    private IRROOT irRoot = new IRROOT();
    private Scope globalScope, currentScope;
    private List<GlobalVarInit> globalVarInitList = new ArrayList<GlobalVarInit>();
    private boolean isArgDecl = false;
    private BasicBlock currentBlock;
    private IRFunction currentFunc;

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
                if (node.getInitValue() instanceof BoolConstNode || node.getInitValue().getType() instanceof BoolType)
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

    public IRROOT getIrRoot()
    {
        return irRoot;
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
        }
    }
}
