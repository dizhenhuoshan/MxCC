package princeYang.mxcc.ast;

abstract public class ExprNode extends Node
{
    Type type;
    AssocType assocType;
    boolean isLeftValue;

    public Type getType()
    {
        return type;
    }

    public void setType(Type type)
    {
        this.type = type;
    }

    public boolean isLeftValue()
    {
        return isLeftValue;
    }

    public void setLeftValue(boolean leftValue)
    {
        isLeftValue = leftValue;
    }

    public AssocType getAssocType()
    {
        return assocType;
    }
}
