package princeYang.mxcc.ast;

public class MemoryAccessExprNode extends ExprNode
{
    ExprNode hostExpr;
    String memberStr;

    public MemoryAccessExprNode(Location location, AssocType assocType, ExprNode hostExpr, String memberStr)
    {
        this.location = location;
        this.assocType = assocType;
        this.hostExpr = hostExpr;
        this.memberStr = memberStr;
    }

    public ExprNode getHostExpr()
    {
        return hostExpr;
    }

    public String getMemberStr()
    {
        return memberStr;
    }

    @Override
    public void accept(AstVisitor visitor)
    {
        visitor.visit(this);
    }
}
