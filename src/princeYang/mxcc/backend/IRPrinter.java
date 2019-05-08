package princeYang.mxcc.backend;

import princeYang.mxcc.ir.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IRPrinter implements IRVisitor
{

    private PrintStream out;
    private Map<BasicBlock, String> blockMap = new HashMap<BasicBlock, String>();
    private Map<VirtualReg, String> virtualRegMap = new HashMap<VirtualReg, String>();
    private Map<StaticData, String> staticDataMap = new HashMap<StaticData, String>();
    private Map<String, Integer> blockCnt = new HashMap<String, Integer>();
    private Map<String, Integer> vRegCnt = new HashMap<String, Integer>();
    private Map<String, Integer> staticDataCnt = new HashMap<String, Integer>();
    private Set<BasicBlock> visitedBlocks = new HashSet<BasicBlock>();
    private boolean isStaticDef = false;

    public IRPrinter(PrintStream out)
    {
        this.out = out;
    }

    @Override
    public void visit(RealReg IRNode)
    {

    }

    @Override
    public void visit(VirtualReg IRNode)
    {

    }

    @Override
    public void visit(Immediate IRNode)
    {

    }

    @Override
    public void visit(Pop IRNode)
    {

    }

    @Override
    public void visit(Push IRNode)
    {

    }

    @Override
    public void visit(Return IRNode)
    {

    }

    @Override
    public void visit(BinaryOperation IRNode)
    {

    }

    @Override
    public void visit(UnaryOperation IRNode)
    {

    }

    @Override
    public void visit(HeapAllocate IRNode)
    {

    }

    @Override
    public void visit(Jump IRNode)
    {

    }

    @Override
    public void visit(Load IRNode)
    {

    }

    @Override
    public void visit(Store IRNode)
    {

    }

    @Override
    public void visit(Move IRNode)
    {

    }

    @Override
    public void visit(PhysicalReg IRNode)
    {

    }

    @Override
    public void visit(Branch branch)
    {

    }

    @Override
    public void visit(Comparison comparison)
    {

    }

    @Override
    public void visit(FuncCall funcCall)
    {

    }

    @Override
    public void visit(StaticStr staticStr)
    {

    }

    @Override
    public void visit(StaticVar staticVar)
    {

    }
}
