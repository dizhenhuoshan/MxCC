package princeYang.mxcc.ast;

public class IntConstNode extends ConstNode
{
    int value;

    public IntConstNode(Location location, int value)
    {
        this.location = location;
        this.constType = new IntType();
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public void accept(AstVisitor visitor)
    {
        visitor.visit(this);
    }
}
