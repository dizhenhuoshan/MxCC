package princeYang.mxcc.ast;

public class BinaryExprNode extends ExprNode
{
    ExprNode lhs;
    Operators.BinaryOp bop;
    ExprNode rhs;

    public BinaryExprNode(Location location, ExprNode lhs, Operators.BinaryOp bop, ExprNode rhs)
    {
        this.location = location;
        this.assocType = AssocType.LEFT;
        this.lhs = lhs;
        this.bop = bop;
        this.rhs = rhs;
    }

    public ExprNode getLhs()
    {
        return lhs;
    }

    public Operators.BinaryOp getBop()
    {
        return bop;
    }

    public ExprNode getRhs()
    {
        return rhs;
    }

    @Override
    public void accept(AstVisitor visitor)
    {
        visitor.visit(this);
    }
}
