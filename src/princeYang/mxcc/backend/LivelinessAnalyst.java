package princeYang.mxcc.backend;

import princeYang.mxcc.ir.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LivelinessAnalyst
{
    private IRROOT irRoot;

    public LivelinessAnalyst(IRROOT irRoot)
    {
        this.irRoot = irRoot;
    }

    public void anylyseLiveness(IRFunction function)
    {
        Set<VirtualReg> liveIn = new HashSet<VirtualReg>();
        Set<VirtualReg> liveOut = new HashSet<VirtualReg>();
        boolean done = false;
        // init liveness set
        // cautious: reversed PRE order need.
        for (BasicBlock basicBlock : function.getReversePreOrder())
        {
            for (IRInstruction instruction = basicBlock.getHeadInst(); instruction != null; instruction = instruction.getNext())
            {
                instruction.liveIn.clear();
                instruction.liveOut.clear();
            }
        }

        while (!done)
        {
            done = true;
            for (BasicBlock basicBlock : function.getReversePreOrder())
            {
                for (IRInstruction instruction = basicBlock.getTailInst(); instruction != null; instruction = instruction.getPrev())
                {
                    liveIn.clear();
                    liveOut.clear();
                    if (instruction instanceof BranchBaseInst)
                    {
                        if (instruction instanceof Branch)
                        {
                            liveOut.addAll(((Branch) instruction).getThenBlock().getHeadInst().liveIn);
                            liveOut.addAll(((Branch) instruction).getElseBlock().getHeadInst().liveIn);
                        }
                        if(instruction instanceof Jump)
                            liveOut.addAll(((Jump) instruction).getTargetBlock().getHeadInst().liveIn);
                    }
                    else
                    {
                        if (instruction.getNext() != null)
                            liveOut.addAll(instruction.getNext().liveIn);
                    }
                    liveIn.addAll(liveOut);
                    IRReg definedReg = instruction.getDefinedReg();
                    if (definedReg instanceof VirtualReg)
                        liveIn.remove(definedReg);
                    for (IRReg usedReg : instruction.getUsedIRReg())
                    {
                        if (usedReg instanceof VirtualReg)
                            liveIn.add((VirtualReg) usedReg);
                    }
                    if (!instruction.liveIn.equals(liveIn))
                    {
                        done = false;
                        instruction.liveIn.clear();
                        instruction.liveIn.addAll(liveIn);
                    }
                    if (!instruction.liveOut.equals(liveOut))
                    {
                        done = false;
                        instruction.liveOut.clear();
                        instruction.liveOut.addAll(liveOut);
                    }
                }
            }
        }
    }
}
