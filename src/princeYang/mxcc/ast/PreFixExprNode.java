package princeYang.mxcc.ast;

public class PreFixExprNode extends ExprNode
{
    ExprNode postExpr;
    Operators.PreFixOp preFixOp;

    public PreFixExprNode(Location location, AssocType assocType, ExprNode postExpr, Operators.PreFixOp preFixOp)
    {
        this.location = location;
        this.assocType = assocType;
        this.postExpr = postExpr;
        this.preFixOp = preFixOp;
    }

    public ExprNode getPostExpr()
    {
        return postExpr;
    }

    public Operators.PreFixOp getPreFixOp()
    {
        return preFixOp;
    }

    @Override
    public void accept(AstVisitor visitor)
    {
        visitor.visit(this);
    }
}
