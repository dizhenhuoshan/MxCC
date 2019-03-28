package princeYang.mxcc.frontend;

import princeYang.mxcc.ast.*;
import princeYang.mxcc.errors.MxError;
import princeYang.mxcc.scope.ClassEntity;
import princeYang.mxcc.scope.FuncEntity;
import princeYang.mxcc.scope.Scope;

public class ScopeBuilder extends ScopeScanner
{
    private int loopLevel;
    private Scope globalScope, currentScope;
    private Type currentReturnType;

    public ScopeBuilder(Scope globalScope)
    {
        this.globalScope = globalScope;
        this.currentScope = globalScope;
        this.loopLevel = 0;
    }

    private boolean VarInitCheck(VarDeclNode varDeclNode)
    {
        //TODO:
        return varDeclNode == null;
    }

    @Override
    public void visit(MxProgNode mxProgNode)
    {
        for (DeclNode declNode : mxProgNode.getDeclNodesList())
        {
            if (declNode instanceof VarDeclNode || declNode instanceof FuncDeclNode || declNode instanceof ClassDeclNode)
                declNode.accept(this);
            else throw new MxError(declNode.getLocation(), "declNode gets fucking wrong!\n");
        }
    }

    @Override
    public void visit(ClassDeclNode classDeclNode)
    {
        ClassEntity classEntity = (ClassEntity) currentScope.getClass(classDeclNode.getIdentName());
        if (classEntity == null)
            throw new MxError(classDeclNode.getLocation(), "class declNode entity boomed!\n");
        currentScope = classEntity.getClassScope();
        for (FuncDeclNode funcDeclNode : classDeclNode.getFuncDeclList())
            funcDeclNode.accept(this);
        for (VarDeclNode varDeclNode : classDeclNode.getVarDeclList())
        {
            if (varDeclNode.getInitValue() != null)
                VarInitCheck(varDeclNode);
        }
        currentScope = currentScope.getFather();
        if (currentScope != globalScope)
            throw new MxError(classDeclNode.getLocation(), "Scope: class scope parent NOT GLOBAL\n");
    }

    @Override
    public void visit(FuncDeclNode funcDeclNode)
    {
        FuncEntity funcEntity = (FuncEntity) currentScope.getFunc(funcDeclNode.getIdentName());
        if (funcEntity == null)
            throw new MxError(funcDeclNode.getLocation(), "function declNode Entity boomed!\n");
        if (funcEntity.getRetType() instanceof ClassType)
        {
            if (currentScope.getClass(((ClassType)(funcEntity.getRetType())).getClassName()) == null)
                throw new MxError(funcDeclNode.getLocation(), "function return type undefined!\n");
        }
        currentScope = funcEntity.getFuncScope();
        for (VarDeclNode varDeclNode : funcDeclNode.getParaDeclList())
            varDeclNode.accept(this);
        currentScope = currentScope.getFather();
        funcDeclNode.getFuncBlock().accept(this);
    }
}
