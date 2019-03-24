package princeYang.mxcc.ast;
import java.util.List;

public class FuncDeclNode extends DeclNode
{
    enum ConstructType{NORMAL, CONSTRUCT};
    ConstructType constructType;
    TypeNode retType;
    List<VarDeclNode> paraDeclList;
    FuncBlockNode funcBlock;

    public FuncDeclNode(Location location, String name, TypeNode retType, List<VarDeclNode> paraDeclList, FuncBlockNode funcBlock)
    {
        this.location = location;
        this.name = name;
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
}
