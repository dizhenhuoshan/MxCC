package princeYang.mxcc.ast;

abstract public class ExprNode extends Node
{
    Type type;
    AssocType assocType;

    public Type getType()
    {
        return type;
    }

    public AssocType getAssocType()
    {
        return assocType;
    }
}
