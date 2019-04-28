package princeYang.mxcc.ir;

public class Immediate
{
    int value;

    public Immediate(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    public void setValue(int value)
    {
        this.value = value;
    }

    public void accept(IRVisitor visitor)
    {
        visitor.visit(this);
    }
}
