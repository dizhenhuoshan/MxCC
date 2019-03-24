package princeYang.mxcc.ast;

public class ForStateNode extends StateNode
{
    ExprNode startExpr;
    ExprNode stopExpr;
    ExprNode stepExpr;
    StateNode loopState;

    public ForStateNode(Location location, ExprNode startExpr, ExprNode stopExpr, ExprNode stepExpr, StateNode loopState)
    {
        this.location = location;
        this.startExpr = startExpr;
        this.stopExpr = stopExpr;
        this.stepExpr = stepExpr;
        this.loopState = loopState;
    }

    public ExprNode getStartExpr()
    {
        return startExpr;
    }

    public ExprNode getStopExpr()
    {
        return stopExpr;
    }

    public ExprNode getStepExpr()
    {
        return stepExpr;
    }

    public StateNode getLoopState()
    {
        return loopState;
    }
}
