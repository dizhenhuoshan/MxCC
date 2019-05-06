package princeYang.mxcc.ir;

public interface IRVisitor
{
    void visit(RealReg IRNode);
    void visit(VirtualReg IRNode);
    void visit(Immediate IRNode);
    void visit(Pop IRNode);
    void visit(Push IRNode);
    void visit(Return IRNode);
    void visit(BinaryOperation IRNode);
    void visit(UnaryOperation IRNode);
    void visit(HeapAllocate IRNode);
    void visit(Jump IRNode);
    void visit(Load IRNode);
    void visit(Store IRNode);
    void visit(Move IRNode);
    void visit(PhysicalReg IRNode);
    void visit(Branch branch);
    void visit(Comparison comparison);
    void visit(FuncCall funcCall);
    void visit(StaticStr staticStr);
    void visit(StaticVar staticVar);
}
