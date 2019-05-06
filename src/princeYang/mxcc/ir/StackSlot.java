package princeYang.mxcc.ir;

public class StackSlot extends IRReg
{
    private String ident;
    private IRFunction parentFunc;

    public StackSlot(String ident, IRFunction parentFunc, boolean isArgStack)
    {
        this.ident = ident;
        this.parentFunc = parentFunc;
        if(!isArgStack)
            parentFunc.getStackSlots().add(this);

    }

    @Override
    public void accept(IRVisitor visitor)
    {
    }

    @Override
    public IRValue copy()
    {
        return null;
    }

    public String getIdent()
    {
        return ident;
    }

    public IRFunction getParentFunc()
    {
        return parentFunc;
    }
}
