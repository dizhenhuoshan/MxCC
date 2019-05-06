package princeYang.mxcc.ir;

public class StaticStr extends StaticData
{
    private String staticValue;

    public StaticStr(String staticValue)
    {
        super("static_string", 4);
        this.staticValue = staticValue;
    }

    @Override
    public void accept(IRVisitor visitor)
    {
        visitor.visit(this);
    }

    public String getStaticValue()
    {
        return staticValue;
    }

    public void setStaticValue(String staticValue)
    {
        this.staticValue = staticValue;
    }
}
