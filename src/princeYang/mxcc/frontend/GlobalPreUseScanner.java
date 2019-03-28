package princeYang.mxcc.frontend;

import princeYang.mxcc.ast.*;
import princeYang.mxcc.scope.ClassEntity;
import princeYang.mxcc.scope.Entity;
import princeYang.mxcc.scope.FuncEntity;
import princeYang.mxcc.scope.Scope;

public class GlobalPreUseScanner extends ScopeScanner
{
    Scope globalScope;

    public GlobalPreUseScanner(Scope globalScope)
    {
        this.globalScope = globalScope;
    }

    @Override
    public void visit(MxProgNode mxProgNode)
    {
        for (DeclNode declNode : mxProgNode.getDeclNodesList())
        {
            if (!(declNode instanceof VarDeclNode))
                declNode.accept(this);
        }
    }

    @Override
    public void visit(ClassDeclNode classDeclNode)
    {
        Entity entity = new ClassEntity(classDeclNode, globalScope);
        globalScope.insertClass(entity);
    }

    @Override
    public void visit(FuncDeclNode funcDeclNode)
    {
        Entity entity = new FuncEntity(funcDeclNode, globalScope);
        globalScope.insertFunc(entity);
    }
}
