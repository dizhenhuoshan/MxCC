package princeYang.mxcc.ast;

public class IdentExprNode extends ExprNode
{
    String identName;

    public IdentExprNode(Location location, String identName)
    {
        this.location = location;
        this.assocType = AssocType.LEFT;
        this.identName = identName;
    }

    public String getIdentName()
    {
        return identName;
    }

    @Override
    public void accept(AstVisitor visitor)
    {
        visitor.visit(this);
    }
}
