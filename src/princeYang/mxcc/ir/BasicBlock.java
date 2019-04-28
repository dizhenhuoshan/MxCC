package princeYang.mxcc.ir;

import java.util.LinkedList;

public class BasicBlock
{
    private IRFunction parentFunction;
    private String blockName;
    private IRInstruction headInst = null, tailInst = null;
    private LinkedList<BasicBlock> prevBB, nextBB;

    private static int globalBBID = 0;
    private int localBBID;

    public BasicBlock(IRFunction parentFunction, String blockName)
    {
        this.parentFunction = parentFunction;
        this.blockName = blockName;
        this.localBBID = globalBBID++;
        parentFunction.getBasicBlocks().add(this);
        prevBB = new LinkedList<BasicBlock>();
        nextBB = new LinkedList<BasicBlock>();
    }

    public int getLocalBBID()
    {
        return localBBID;
    }

    public IRInstruction getHeadInst()
    {
        return headInst;
    }

    public IRInstruction getTailInst()
    {
        return tailInst;
    }

    public void setHeadInst(IRInstruction headInst)
    {
        this.headInst = headInst;
    }

    public void setTailInst(IRInstruction tailInst)
    {
        this.tailInst = tailInst;
    }
}
