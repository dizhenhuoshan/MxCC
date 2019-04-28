package princeYang.mxcc.ir;

public interface IRVisitor
{
    void visit(RealReg IRNode);
    void visit(VirtualReg IRNode);
    void visit(Immediate IRNode);
    void visit(BinaryOperation IRNode);
    void visit(Branch IRNode);
}
