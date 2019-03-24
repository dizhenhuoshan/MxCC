package princeYang.mxcc.ast;

public class IfStateNode extends StateNode
{
    ExprNode conditionExpr;
    StateNode thenState;
    StateNode elseState;

    public IfStateNode(Location location, ExprNode conditionExpr, StateNode thenState, StateNode elseState)
    {
        this.location = location;
        this.conditionExpr = conditionExpr;
        this.thenState = thenState;
        this.elseState = elseState;
    }

    public ExprNode getConditionExpr()
    {
        return conditionExpr;
    }

    public StateNode getThenState()
    {
        return thenState;
    }

    public StateNode getElseState()
    {
        return elseState;
    }
}
