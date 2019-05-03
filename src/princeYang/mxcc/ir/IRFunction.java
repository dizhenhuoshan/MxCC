package princeYang.mxcc.ir;

import princeYang.mxcc.scope.FuncEntity;

import java.util.*;

public class IRFunction
{
    private String funcName;
    private IRFuncType funcType;
    private LinkedList<BasicBlock> basicBlocks;
    private BasicBlock bbEnter;
    private BasicBlock bbLeave;
    private boolean hasRetValue = false, hasRecursive = false, isInClass = false;
    private String buildInName;
    private FuncEntity funcEntity;

    private List<VirtualReg> argvRegList = new ArrayList<VirtualReg>();
    private List<BasicBlock> reversePreOrder = null, reversePostOrder = null;
    private List<StackSlot> stackSlots = new ArrayList<StackSlot>();

    public List<Return> returnInstList = new ArrayList<Return>();
    public Set<IRFunction> calleeSet = new HashSet<IRFunction>();
    public Set<IRFunction> recurCalleeSet = new HashSet<IRFunction>();


    public IRFunction() {}

    public IRFunction(String funcName, String buildInName)
    {
        this.funcName = funcName;
        this.buildInName = buildInName;
        this.funcEntity = null;
        this.funcType = IRFuncType.BuildIn;
    }

    public IRFunction(FuncEntity funcEntity)
    {
        this.funcName = funcEntity.getIdent();
        this.funcEntity = funcEntity;
        if (funcEntity.isInClass())
        {
            funcName = "__class__" + funcEntity.getIdent();
            this.isInClass = true;
        }
    }

    public LinkedList<BasicBlock> getBasicBlocks()
    {
        return basicBlocks;
    }

    public List<StackSlot> getStackSlots()
    {
        return stackSlots;
    }

    public String getFuncName()
    {
        return funcName;
    }

    public void updateCalleeSet()
    {
        calleeSet.clear();
        for (BasicBlock basicBlock : reversePostOrder)
        {
            IRInstruction instruction = null;
            for (instruction = basicBlock.getHeadInst(); instruction != null;
                 instruction = instruction.getNext())
                if (instruction instanceof FuncCall)
                    calleeSet.add(((FuncCall) instruction).getFunction());
        }
    }

    public void insertArgReg(VirtualReg virtualReg)
    {
        argvRegList.add(virtualReg);
    }

}
