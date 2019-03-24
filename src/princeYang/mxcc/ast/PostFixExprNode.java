package princeYang.mxcc.ast;

public class PostFixExprNode extends ExprNode
{
    ExprNode preExpr;
    Operators.PostFixOp postFixOp;

    public PostFixExprNode(Location location, AssocType assocType, ExprNode preExpr, Operators.PostFixOp postFixOp)
    {
        this.location = location;
        this.assocType = assocType;
        this.preExpr = preExpr;
        this.postFixOp = postFixOp;
    }

    public ExprNode getPreExpr()
    {
        return preExpr;
    }

    public Operators.PostFixOp getPostFixOp()
    {
        return postFixOp;
    }
}
