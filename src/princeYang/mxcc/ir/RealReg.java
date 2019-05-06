package princeYang.mxcc.ir;

public class RealReg extends IRReg
{
    public void accept(IRVisitor visitor)
    {
        visitor.visit(this);
    }

    public IRValue copy()
    {
        return null;
    }
}
