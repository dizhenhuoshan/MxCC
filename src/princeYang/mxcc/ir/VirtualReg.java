package princeYang.mxcc.ir;

public class VirtualReg extends IRReg
{
    private String vRegName;
    private RealReg enforcedReg = null;

    public VirtualReg(String vRegName)
    {
        this.vRegName = vRegName;
    }

    @Override
    public void accept(IRVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public VirtualReg copy()
    {
        return null;
    }

    public String getvRegName()
    {
        return vRegName;
    }

    public RealReg getEnforcedReg()
    {
        return enforcedReg;
    }

    public void setEnforcedReg(RealReg enforcedReg)
    {
        this.enforcedReg = enforcedReg;
    }
}
