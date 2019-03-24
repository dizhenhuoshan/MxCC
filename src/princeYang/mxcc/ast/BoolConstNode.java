package princeYang.mxcc.ast;

public class BoolConstNode extends ConstNode
{
    boolean value;

    public BoolConstNode(Location location, boolean value)
    {
        this.location = location;
        this.constType = new BoolType();
        this.value = value;
    }

    public boolean getValue()
    {
        return value;
    }
}
