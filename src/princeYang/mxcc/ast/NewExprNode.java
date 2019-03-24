package princeYang.mxcc.ast;

import java.util.List;

public class NewExprNode extends ExprNode
{
    Type newType;
    int totalDim;
    List<ExprNode> knownDims;

    public NewExprNode(Location location, AssocType assocType, Type newType, int totalDim, List<ExprNode> knownDims)
    {
        this.location = location;
        this.assocType = assocType;
        this.newType = newType;
        this.totalDim = totalDim;
        this.knownDims = knownDims;
    }

    public Type getNewType()
    {
        return newType;
    }

    public int getTotalDim()
    {
        return totalDim;
    }

    public List<ExprNode> getKnownDims()
    {
        return knownDims;
    }
}
