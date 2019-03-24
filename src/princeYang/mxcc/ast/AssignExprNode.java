package princeYang.mxcc.ast;

public class AssignExprNode extends ExprNode
{
    ExprNode lhs;
    ExprNode rhs;

    public AssignExprNode(Location location, AssocType assocType, ExprNode lhs, ExprNode rhs)
    {
        this.location = location;
        this.assocType = assocType;
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public ExprNode getLhs()
    {
        return lhs;
    }

    public ExprNode getRhs()
    {
        return rhs;
    }
}
