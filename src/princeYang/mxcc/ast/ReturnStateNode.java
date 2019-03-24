package princeYang.mxcc.ast;

public class ReturnStateNode extends StateNode
{
    ExprNode retExpr;

    public ReturnStateNode(Location location, ExprNode retExpr)
    {
        this.location = location;
        this.retExpr = retExpr;
    }

    public ExprNode getRetExpr()
    {
        return retExpr;
    }
}
