package princeYang.mxcc.frontend;

import princeYang.mxcc.ast.*;
import princeYang.mxcc.errors.MxError;
import princeYang.mxcc.scope.Entity;
import princeYang.mxcc.scope.FuncEntity;
import princeYang.mxcc.scope.Scope;
import princeYang.mxcc.scope.VarEntity;

public class InsideClassPreUseScanner extends ScopeScanner
{
    private Scope globalScope, currentScope;
    private ClassType classType;

    public InsideClassPreUseScanner(Scope globalScope)
    {
        this.globalScope = globalScope;
        this.currentScope = globalScope;
    }

    @Override
    public void visit(MxProgNode mxProgNode)
    {
        for (DeclNode declNode : mxProgNode.getDeclNodesList())
        {
            if (declNode instanceof ClassDeclNode)
                declNode.accept(this);
        }
    }

    @Override
    public void visit(ClassDeclNode classDeclNode)
    {
        currentScope = (globalScope.getClass(classDeclNode.getIdentName())).getClassScope();
        classType = (ClassType) (globalScope.getClass(classDeclNode.getIdentName())).getType();
        for (FuncDeclNode funcDeclNode : classDeclNode.getFuncDeclList())
        {
            funcDeclNode.accept(this);
        }
        for(VarDeclNode varDeclNode : classDeclNode.getVarDeclList())
        {
            varDeclNode.accept(this);
        }
        currentScope = currentScope.getFather();
        classType = null;
    }

    @Override
    public void visit(FuncDeclNode funcDeclNode)
    {
        FuncEntity funcEntity = currentScope.getFunc(funcDeclNode.getIdentName());
        currentScope = funcEntity.getFuncScope();
        VarEntity entity = new VarEntity("this", classType);
        currentScope.insertVar(entity);
        funcEntity.getFuncParas().add(entity);
        if (funcDeclNode.isConstruct() && !(funcDeclNode.getIdentName().equals(classType.getClassIdent())))
            throw new MxError(funcDeclNode.getLocation(), "Only construct function no return! \n");
        currentScope = currentScope.getFather();
    }

    @Override
    public void visit(VarDeclNode varDeclNode)
    {
        if (varDeclNode.getVarType().getType() instanceof ClassType)
        {
            if (currentScope.getClass(((ClassType)(varDeclNode.getVarType().getType())).getClassIdent()) == null)
                throw new MxError(varDeclNode.getLocation(), String.format("Scope: class %s is not defined\n",
                        ((ClassType)varDeclNode.getVarType().getType()).getClassIdent()));
        }
        Entity entity = new VarEntity(classType.getClassIdent() ,varDeclNode);
        currentScope.insertVar(entity);
    }
}
