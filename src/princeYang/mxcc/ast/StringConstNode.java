package princeYang.mxcc.ast;

public class StringConstNode extends ConstNode
{
    String value;

    public StringConstNode(Location location, String value)
    {
        this.location = location;
        this.constType = new StringType();
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public void accept(AstVisitor visitor)
    {
        visitor.visit(this);
    }
}
