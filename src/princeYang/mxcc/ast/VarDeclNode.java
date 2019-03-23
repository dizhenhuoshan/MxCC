package princeYang.mxcc.ast;

public class VarDeclNode extends DeclNode
{
    public Type VarType;
    public ExprNode InitValue;

    public VarDeclNode(Location location, String name, Type varType, ExprNode initValue)
    {
        this.location = location;
        this.name = name;
        this.VarType = VarType;
        this. InitValue = initValue;
    }

    public ExprNode getInitValue()
    {
        return InitValue;
    }

    public Type getVarType()
    {
        return VarType;
    }
}
