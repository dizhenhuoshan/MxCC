package princeYang.mxcc.ast;

public class IdentExprNode extends ExprNode
{
    String identName;
    Type identType;

    public IdentExprNode(Location location, AssocType assocType, String identName, Type identType)
    {
        this.location = location;
        this.assocType = assocType;
        this.identName = identName;
        this.identType = identType;
    }

    public String getIdentName()
    {
        return identName;
    }

    public Type getIdentType()
    {
        return identType;
    }

    @Override
    public void accept(AstVisitor visitor)
    {
        visitor.visit(this);
    }
}
