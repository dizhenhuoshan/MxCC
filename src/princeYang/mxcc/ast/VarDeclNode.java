package princeYang.mxcc.ast;

public class VarDeclNode extends DeclNode
{
    private TypeNode VarType;
    private ExprNode InitValue;

    public VarDeclNode(Location location, String name, TypeNode varType, ExprNode initValue)
    {
        this.location = location;
        this.name = name;
        this.VarType = varType;
        this.InitValue = initValue;
    }

    public ExprNode getInitValue()
    {
        return InitValue;
    }

    public TypeNode getVarType()
    {
        return VarType;
    }
}
