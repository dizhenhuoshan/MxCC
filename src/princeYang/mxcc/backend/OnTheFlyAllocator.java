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
    private PhysicalReg preg0, preg1;

    public OnTheFlyAllocator(IRROOT irRoot)
    {
        this.irRoot = irRoot;
        this.physicalRegList.addAll(NASMRegSet.generalRegs);
        physicalRegList.remove(NASMRegSet.r8);
        physicalRegList.remove(NASMRegSet.r9);
        if (irRoot.isContainShiftDiv())
        {
            preg0 = physicalRegList.get(0);
            preg1 = physicalRegList.get(1);
        }
        else
        {
            preg0 = NASMRegSet.rbx;
            preg1 = physicalRegList.get(0);
        }
        irRoot.setPreg0(preg0);
        irRoot.setPreg1(preg1);
        this.physicalRegList.remove(preg0);
        this.physicalRegList.remove(preg1);
    }

    private void setFuncArgReg()
    {
        for (IRFunction irFunction : irRoot.getFunctionMap().values())
        {
            IRInstruction headInst = irFunction.getBlockEnter().getHeadInst();
            // if args > 6, get them from stack
            for (int i = 0; i < irFunction.getArgvRegList().size(); i++)
            {
                VirtualReg virtualArgReg = irFunction.getArgvRegList().get(i);
                StackSlot argSlot = new StackSlot(String.format("arg%d", i), irFunction, true);
                irFunction.getArgSlotMap().put(virtualArgReg, argSlot);
                if (i >= 6)
                    headInst.prepend(new Load(irFunction.getBlockEnter(), virtualArgReg, argSlot, Config.regSize, 0));
            }
            // for args less than 6, get them from the register
            if (irFunction.getArgvRegList().size() >= 1)
                irFunction.getArgvRegList().get(0).setEnforcedReg(NASMRegSet.rdi);
            if (irFunction.getArgvRegList().size() >= 2)
                irFunction.getArgvRegList().get(1).setEnforcedReg(NASMRegSet.rsi);
            if (irFunction.getArgvRegList().size() >= 3)
                irFunction.getArgvRegList().get(2).setEnforcedReg(NASMRegSet.rdx);
            if (irFunction.getArgvRegList().size() >= 4)
                irFunction.getArgvRegList().get(3).setEnforcedReg(NASMRegSet.rcx);
            if (irFunction.getArgvRegList().size() >= 5)
                irFunction.getArgvRegList().get(4).setEnforcedReg(NASMRegSet.r8);
            if (irFunction.getArgvRegList().size() >= 6)
                irFunction.getArgvRegList().get(5).setEnforcedReg(NASMRegSet.r9);
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
            Map<VirtualReg, PhysicalReg> physicalRegMap = new HashMap<VirtualReg, PhysicalReg>();
            function = func;
            stackSlots.clear();
            stackSlots.putAll(func.getArgSlotMap());

            for (BasicBlock basicBlock : function.getReversePostOrder())
            {
                for (IRInstruction instruction = basicBlock.getHeadInst(); instruction != null; instruction = instruction.getNext())
                {
                    int cnt = 0;
                    physicalRegMap.clear();
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
                            {
                                if (reg instanceof VirtualReg)
                                {
                                    PhysicalReg pReg = ((VirtualReg) reg).getEnforcedReg();
                                    if (pReg == null)
                                    {
                                        pReg = physicalRegMap.get((VirtualReg) reg);
                                            if (pReg == null)
                                            {
                                                pReg = physicalRegList.get(cnt++);
                                                physicalRegMap.put((VirtualReg) reg, pReg);
                                            }
                                    }
                                    regRenameMap.put(reg, pReg);
                                    if (pReg.isGeneral())
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
                        {
                            pReg = physicalRegMap.get((VirtualReg) definedReg);
                                if (pReg == null)
                                    pReg = physicalRegList.get(cnt++);
                        }
                        if (pReg.isGeneral())
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
