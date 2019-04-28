package princeYang.mxcc.ast;

public class NullConstNode extends ConstNode
{
    public NullConstNode(Location location)
    {
        this.location = location;
    }

    @Override
    public void accept(AstVisitor visitor)
    {
        visitor.visit(this);
    }
}
