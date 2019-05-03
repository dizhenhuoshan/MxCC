package princeYang.mxcc.ast;

import princeYang.mxcc.scope.VarEntity;

public class IdentExprNode extends ExprNode
{
    private String identName;
    private VarEntity varEntity;

    public IdentExprNode(Location location, String identName)
    {
        this.location = location;
        this.assocType = AssocType.LEFT;
        this.identName = identName;
    }

    public String getIdentName()
    {
        return identName;
    }

    public VarEntity getVarEntity()
    {
        return varEntity;
    }

    public void setVarEntity(VarEntity varEntity)
    {
        this.varEntity = varEntity;
    }

    @Override
    public void accept(AstVisitor visitor)
    {
        visitor.visit(this);
    }
}
