package princeYang.mxcc.ast;
import java.util.List;

public class FunctionCallExprNode extends ExprNode
{
    ExprNode funcExpr;
    List<ExprNode> paraList;

    public FunctionCallExprNode(Location location, ExprNode funcExpr, List<ExprNode> paraList)
    {
        this.location = location;
        this.assocType = AssocType.LEFT;
        this.funcExpr = funcExpr;
        this.paraList = paraList;
    }

    public ExprNode getFuncExpr()
    {
        return funcExpr;
    }

    public List<ExprNode> getParaList()
    {
        return paraList;
    }

    @Override
    public void accept(AstVisitor visitor)
    {
        visitor.visit(this);
    }
}
