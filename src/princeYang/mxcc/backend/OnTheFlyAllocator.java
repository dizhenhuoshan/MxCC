package princeYang.mxcc.backend;

import princeYang.mxcc.Config;
import princeYang.mxcc.ir.*;

import java.util.*;

public class OnTheFlyAllocator
{
    private IRROOT irRoot;
    private IRFunction function;
    private Map<VirtualReg, StackSlot> stackSlots = new HashMap<VirtualReg, StackSlot>();
    private List<PhysicalReg> physicalRegList = new ArrayList<PhysicalReg>();

    public OnTheFlyAllocator(IRROOT irRoot)
    {
        this.irRoot = irRoot;
        this.physicalRegList.addAll(NASMRegSet.generalRegs);
    }

    public void setFuncArgReg()
    {
        for (IRFunction irFunction : irRoot.getFunctionMap().values())
        {
            IRInstruction headInst = irFunction.getBlockEnter().getHeadInst();
            // if args > 6, get them from stack
            for (int i = 6; i < irFunction.getArgvRegList().size(); i++)
            {
                VirtualReg virtualArgReg = irFunction.getArgvRegList().get(i);
                StackSlot argSlot = new StackSlot(String.format("arg%d", i), irFunction, true);
                irFunction.getArgSlotMap().put(virtualArgReg, argSlot);
                headInst.prepend(new Load(irFunction.getBlockEnter(), virtualArgReg, argSlot, Config.regSize, 0));
            }
            // for args less than 6, get them from the register
            if (irFunction.getArgvRegList().size() >= 1)
                irFunction.getArgvRegList().get(0).setEnforcedReg(NASMRegSet.rdi);
            if (irFunction.getArgvRegList().size() >= 2)
                irFunction.getArgvRegList().get(1).setEnforcedReg(NASMRegSet.rsi);
            if (irFunction.getArgvRegList().size() >= 3)
                irFunction.getArgvRegList().get(0).setEnforcedReg(NASMRegSet.rdx);
            if (irFunction.getArgvRegList().size() >= 4)
                irFunction.getArgvRegList().get(1).setEnforcedReg(NASMRegSet.rcx);
            if (irFunction.getArgvRegList().size() >= 5)
                irFunction.getArgvRegList().get(0).setEnforcedReg(NASMRegSet.r8);
            if (irFunction.getArgvRegList().size() >= 6)
                irFunction.getArgvRegList().get(1).setEnforcedReg(NASMRegSet.r9);
        }
    }

    private StackSlot getStackSlot(VirtualReg virtualReg)
    {
        StackSlot slot = stackSlots.get(virtualReg);
        if (slot == null)
        {
            slot = new StackSlot(virtualReg.getvRegName(), function, false);
            stackSlots.put(virtualReg, slot);
        }
        return slot;
    }

    public void allocateReg()
    {
        setFuncArgReg();
        for (IRFunction func : irRoot.getFunctionMap().values())
        {
            Map<IRReg, IRReg> regRenameMap = new HashMap<IRReg, IRReg>();
            function = func;
            stackSlots.clear();
            stackSlots.putAll(func.getArgSlotMap());

            for (BasicBlock basicBlock : function.getReversePostOrder())
            {
                for (IRInstruction instruction = basicBlock.getHeadInst(); instruction != basicBlock.getTailInst(); instruction = instruction.getNext())
                {
                    int cnt = 0;
                    if (instruction instanceof FuncCall)
                    {
                        List<IRValue> funcArgs = ((FuncCall) instruction).getArgs();
                        IRValue nowArg;
                        for (int i = 0; i < funcArgs.size(); i++)
                        {
                            nowArg = funcArgs.get(i);
                            if (nowArg instanceof VirtualReg)
                            {
                                IRReg argAddr = getStackSlot((VirtualReg) nowArg);
                                funcArgs.set(i, argAddr);
                            }
                        }
                    }
                    else
                    {
                        Collection<IRReg> usedReg = instruction.getUsedIRReg();
                        if (!usedReg.isEmpty())
                        {
                            regRenameMap.clear();
                            for (IRReg reg : usedReg)
                                regRenameMap.put(reg, reg);
                            for (IRReg reg : usedReg)
                            {
                                if (reg instanceof VirtualReg)
                                {
                                    PhysicalReg pReg = ((VirtualReg) reg).getEnforcedReg();
                                    if (pReg == null)
                                        physicalRegList.get(cnt++);
                                    regRenameMap.put(reg, pReg);
                                    function.getUsedGeneralPReg().add(pReg);
                                    IRReg addr = getStackSlot((VirtualReg) reg);
                                    instruction.prepend(new Load(basicBlock, pReg, addr, Config.regSize, 0));
                                }
                            }
                            instruction.setUsedIRReg(regRenameMap);
                        }
                    }

                    IRReg definedReg = instruction.getDefinedReg();
                    if (definedReg instanceof VirtualReg)
                    {
                        PhysicalReg pReg = ((VirtualReg) definedReg).getEnforcedReg();
                        if (pReg == null)
                            pReg = physicalRegList.get(cnt++);
                        function.getUsedGeneralPReg().add(pReg);
                        instruction.setDefinedReg(pReg);
                        IRReg addr = getStackSlot((VirtualReg) definedReg);
                        instruction.append(new Store(basicBlock, pReg, addr, Config.regSize, 0));
                        // skip the new added store
                        instruction = instruction.getNext();
                    }
                }
            }
        }
    }
}
