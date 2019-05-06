package princeYang.mxcc.ir;

import princeYang.mxcc.ast.ForStateNode;
import princeYang.mxcc.errors.MxError;

import java.util.HashSet;
import java.util.Set;

public class BasicBlock
{
    private IRFunction parentFunction;
    private String blockName;
    private IRInstruction headInst = null, tailInst = null;
    private Set<BasicBlock> prevBlock, nextBlock;
    private boolean containJump = false;
    private ForStateNode forStateNode;

    private static int globalBBID = 0;
    private int localBBID;

    public BasicBlock(IRFunction parentFunction, String blockName)
    {
        this.parentFunction = parentFunction;
        this.blockName = blockName;
        this.localBBID = globalBBID++;
        parentFunction.getBasicBlocks().add(this);
        prevBlock = new HashSet<BasicBlock>();
        nextBlock = new HashSet<BasicBlock>();
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

    public void appendInst(IRInstruction newTail)
    {
        tailInst.append(newTail);
        this.tailInst = newTail;
    }

    public Set<BasicBlock> getPrevBlock()
    {
        return prevBlock;
    }

    public Set<BasicBlock> getNextBlock()
    {
        return nextBlock;
    }

    public ForStateNode getForStateNode()
    {
        return forStateNode;
    }

    public void setForStateNode(ForStateNode forStateNode)
    {
        this.forStateNode = forStateNode;
    }

    public void addNextBlock(BasicBlock nextBlock)
    {
        if (nextBlock != null)
            nextBlock.getPrevBlock().add(this);
        this.nextBlock.add(nextBlock);
    }

    public void deleteNextBlock(BasicBlock nextBlock)
    {
        if (nextBlock != null)
            nextBlock.getPrevBlock().remove(this);
        this.nextBlock.remove(nextBlock);
    }

    public boolean isContainJump()
    {
        return containJump;
    }

    public void setJumpInst (BranchBaseInst jumpInst)
    {
        appendInst(jumpInst);
        this.containJump = true;
        if (jumpInst instanceof Return)
            this.parentFunction.getReturnInstList().add((Return) jumpInst);
        else if (jumpInst instanceof Jump)
            addNextBlock(((Jump) jumpInst).getTargetBlock());
        else if (jumpInst instanceof Branch)
        {
            addNextBlock(((Branch) jumpInst).getThenBlock());
            addNextBlock(((Branch) jumpInst).getElseBlock());
        }
        else throw new MxError("IR BasicBlock: jumpInst is invalid in setJumpInst!\n");
    }

    public void deleteJumpInst()
    {
        if (tailInst instanceof Return)
            parentFunction.getReturnInstList().remove((Return) tailInst);
        else if (tailInst instanceof Jump)
            deleteNextBlock(((Jump) tailInst).getTargetBlock());
        else if (tailInst instanceof Branch)
        {
            deleteNextBlock(((Branch) tailInst).getThenBlock());
            deleteNextBlock(((Branch) tailInst).getElseBlock());
        }
        else
            throw new MxError("IR BasicBlock: jumpInst is invalid in deleteJumpInst!\n");
    }
}
