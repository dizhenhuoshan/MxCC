package princeYang.mxcc.ast;
import java.util.List;

public class FuncDeclNode extends DeclNode
{
    ConstructType constructType;
    TypeNode retType;
    List<VarDeclNode> paraDeclList;
    FuncBlockNode funcBlock;

    public FuncDeclNode(Location location, String identName, TypeNode retType, List<VarDeclNode> paraDeclList, FuncBlockNode funcBlock)
    {
        this.location = location;
        this.identName = identName;
        this.paraDeclList = paraDeclList;
        this.funcBlock = funcBlock;
        if (retType == null)
        {
            this.constructType = ConstructType.CONSTRUCT;
            this.retType = null;
        }
        else
        {
            this.constructType = ConstructType.NORMAL;
            this.retType = retType;
        }
    }

    public boolean isConstruct()
    {
        return this.constructType.equals(ConstructType.CONSTRUCT);
    }

    public FuncBlockNode getFuncBlock()
    {
        return funcBlock;
    }

    public List<VarDeclNode> getParaDeclList()
    {
        return paraDeclList;
    }

    public TypeNode getRetType()
    {
        return retType;
    }

    @Override
    public void accept(AstVisitor visitor)
    {
        visitor.visit(this);
    }
}
