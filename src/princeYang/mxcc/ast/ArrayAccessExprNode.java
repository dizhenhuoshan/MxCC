package princeYang.mxcc.ast;

public class ArrayAccessExprNode extends ExprNode
{
    ExprNode arrExpr;
    ExprNode subExpr;

    public  ArrayAccessExprNode(Location location, AssocType assocType, ExprNode arrExpr, ExprNode subExpr)
    {
        this.location = location;
        this.assocType = assocType;
        this.arrExpr = arrExpr;
        this.subExpr = subExpr;
    }

    public ExprNode getArrExpr()
    {
        return arrExpr;
    }

    public ExprNode getSubExpr()
    {
        return subExpr;
    }
}
