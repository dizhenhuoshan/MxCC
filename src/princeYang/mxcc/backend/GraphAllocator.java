package princeYang.mxcc.backend;

import princeYang.mxcc.Config;
import princeYang.mxcc.errors.MxError;
import princeYang.mxcc.ir.*;

import java.util.*;

public class GraphAllocator
{
    private IRROOT irRoot;
    private LivelinessAnalyst livelinessAnalyst;
    private List<PhysicalReg> generalRegs = new ArrayList<PhysicalReg>();
    private PhysicalReg pReg0, pReg1;
    private int colorNum;
    private IRFunction currentFunc;
    public Stack<VirtualReg> regStack = new Stack<VirtualReg>();
    public Map<VirtualReg, GraphRegInfo> regGraphInfoMap = new HashMap<VirtualReg, GraphRegInfo>();
    public Map<IRReg, IRReg> renameMap = new HashMap<IRReg, IRReg>();
    public Set<VirtualReg> nodeSet = new HashSet<VirtualReg>();
    public Set<PhysicalReg> usedColorSet = new HashSet<PhysicalReg>();
    public Set<VirtualReg> underflowRegNodes = new HashSet<VirtualReg>();

    public GraphAllocator(IRROOT irRoot)
    {
        this.irRoot = irRoot;
        this.livelinessAnalyst = new LivelinessAnalyst(irRoot);
        this.generalRegs.addAll(NASMRegSet.generalRegs);
        // check whether r8/ r9 will be used
        int maxFuncArg = 0;
        for (IRFunction function : irRoot.getFunctionMap().values())
        {
            if (function.getParavRegList().size() > maxFuncArg)
                maxFuncArg = function.getParavRegList().size();
        }
        if (maxFuncArg >= 5)
            generalRegs.remove(NASMRegSet.r8);
        if (maxFuncArg >= 6)
            generalRegs.remove(NASMRegSet.r9);
        if (irRoot.isContainShiftDiv())
        {
            pReg0 = generalRegs.get(0);
            pReg1 = generalRegs.get(1);
        }
        else
        {
            pReg0 = NASMRegSet.rbx;
            pReg1 = generalRegs.get(0);
        }

        irRoot.setPreg0(pReg0);
        irRoot.setPreg1(pReg1);

        this.generalRegs.remove(pReg0);
        this.generalRegs.remove(pReg1);
        colorNum = generalRegs.size();
    }

    private void setFuncArgReg()
    {
        IRInstruction headInst = currentFunc.getBlockEnter().getHeadInst();
//             if args > 6, get them from stack
        for (int i = 6; i < currentFunc.getParavRegList().size(); i++)
        {
            VirtualReg virtualArgReg = currentFunc.getParavRegList().get(i);
            StackSlot paraSlot = new StackSlot(String.format("arg%d", i), currentFunc, true);
            currentFunc.getParaSlotMap().put(virtualArgReg, paraSlot);
            headInst.prepend(new Load(currentFunc.getBlockEnter(), virtualArgReg, paraSlot, Config.regSize, 0));
        }
        // for args less than 6, get them from the register
        if (currentFunc.getParavRegList().size() >= 1)
            currentFunc.getParavRegList().get(0).setEnforcedReg(NASMRegSet.rdi);
        if (currentFunc.getParavRegList().size() >= 2)
            currentFunc.getParavRegList().get(1).setEnforcedReg(NASMRegSet.rsi);
        if (currentFunc.getParavRegList().size() >= 3)
            currentFunc.getParavRegList().get(2).setEnforcedReg(NASMRegSet.rdx);
        if (currentFunc.getParavRegList().size() >= 4)
            currentFunc.getParavRegList().get(3).setEnforcedReg(NASMRegSet.rcx);
        if (currentFunc.getParavRegList().size() >= 5)
            currentFunc.getParavRegList().get(4).setEnforcedReg(NASMRegSet.r8);
        if (currentFunc.getParavRegList().size() >= 6)
            currentFunc.getParavRegList().get(5).setEnforcedReg(NASMRegSet.r9);
    }

    private GraphRegInfo getGraphRegInfo(VirtualReg vReg)
    {
        GraphRegInfo graphRegInfo = regGraphInfoMap.get(vReg);
        if (graphRegInfo == null)
        {
            graphRegInfo = new GraphRegInfo();
            regGraphInfoMap.put(vReg, graphRegInfo);
        }
        return graphRegInfo;
    }

    private void addEdge(VirtualReg des, VirtualReg arr)
    {
        getGraphRegInfo(arr).neighbors.add(des);
        getGraphRegInfo(des).neighbors.add(arr);
    }

    private void buildGraph()
    {
        for (VirtualReg paraReg : currentFunc.getParavRegList())
            getGraphRegInfo(paraReg);
        for (BasicBlock basicBlock : currentFunc.getReversePreOrder())
        {
            for (IRInstruction instruction = basicBlock.getHeadInst(); instruction != null; instruction = instruction.getNext())
            {
                IRReg definedReg = instruction.getDefinedReg();
                if (definedReg instanceof VirtualReg)
                {
                    GraphRegInfo regInfo = getGraphRegInfo((VirtualReg) definedReg);
                    if (instruction instanceof Move)
                    {
                        IRValue srcValue = ((Move) instruction).getValue();
                        if (srcValue instanceof VirtualReg)
                        {
                            regInfo.suggestSame.add((VirtualReg) srcValue);
                            getGraphRegInfo((VirtualReg) srcValue).suggestSame.add((VirtualReg) definedReg);
                        }
                        for (VirtualReg vReg : instruction.liveOut)
                        {
                            if (vReg!= srcValue && vReg != definedReg)
                                addEdge(vReg, (VirtualReg) definedReg);
                        }
                    }
                    else
                    {
                        for (VirtualReg vReg : instruction.liveOut)
                        {
                            if (vReg != definedReg)
                                addEdge(vReg, (VirtualReg) definedReg);
                        }
                    }
                }
            }
        }
        for (GraphRegInfo regInfo : regGraphInfoMap.values())
            regInfo.degree = regInfo.neighbors.size();
    }

    private void removeRegNode(VirtualReg vReg)
    {
        GraphRegInfo regInfo = regGraphInfoMap.get(vReg);
        for (VirtualReg neighbor : regInfo.neighbors)
        {
            GraphRegInfo neighborInfo = regGraphInfoMap.get(vReg);
            if (!neighborInfo.removed)
            {
                neighborInfo.degree--;
                if (neighborInfo.degree < colorNum)
                    underflowRegNodes.add(neighbor);
            }
        }
        regInfo.removed = true;
        regStack.push(vReg);
        nodeSet.remove(vReg);
    }

    private void colorize()
    {
        nodeSet.addAll(regGraphInfoMap.keySet());
        for (VirtualReg vReg : nodeSet)
        {
            if (regGraphInfoMap.get(vReg).degree < colorNum)
                underflowRegNodes.add(vReg);
        }
        while (!nodeSet.isEmpty())
        {
            while (!underflowRegNodes.isEmpty())
            {
                Iterator<VirtualReg> iterator = underflowRegNodes.iterator();
                VirtualReg vReg = iterator.next();
                iterator.remove();
                removeRegNode(vReg);
            }
            if (nodeSet.isEmpty())
                break;
            Iterator<VirtualReg> iterator = nodeSet.iterator();
            VirtualReg node = iterator.next();
            iterator.remove();
            removeRegNode(node);
        }
        while (!regStack.isEmpty())
        {
            VirtualReg vReg = regStack.pop();
            GraphRegInfo regInfo = regGraphInfoMap.get(vReg);
            usedColorSet.clear();
            for (VirtualReg neighbor : regInfo.neighbors)
            {
                GraphRegInfo neighborInfo = regGraphInfoMap.get(neighbor);
                if (!neighborInfo.removed && neighborInfo.colorReg instanceof PhysicalReg)
                    usedColorSet.add((PhysicalReg) neighborInfo.colorReg);
            }
            PhysicalReg forcedReg = vReg.getEnforcedReg();
            if (forcedReg != null)
            {
                if (usedColorSet.contains(forcedReg))
                    throw new MxError("Graph Allocator: forcedReg has been used!\n");
                regInfo.colorReg = forcedReg;
            }
            else
            {
                for (VirtualReg suggestReg : regInfo.suggestSame)
                {
                    IRReg color = getGraphRegInfo(suggestReg).colorReg;
                    if (color instanceof PhysicalReg && !(usedColorSet.contains(color)))
                    {
                        regInfo.colorReg = color;
                        break;
                    }
                }
                if (regInfo.colorReg == null) // unluckily :( , but still have chance :)
                {
                    for (PhysicalReg pReg : generalRegs)
                    {
                        if (!usedColorSet.contains(pReg))
                        {
                            regInfo.colorReg = pReg;
                            break;
                        }
                    }
                    if (regInfo.colorReg == null) // very unluckily, :( :( , but we still have memory :)
                    {
                        regInfo.colorReg = currentFunc.getParaSlotMap().get(vReg);
                        if (regInfo.colorReg == null)
                            regInfo.colorReg = new StackSlot(vReg.getvRegName(), currentFunc, false);
                    }
                }
            }
            regInfo.removed = false;
        }
    }

    private void rewriteInstruction()
    {
        for (BasicBlock basicBlock : currentFunc.getReversePreOrder())
        {
            for (IRInstruction instruction = basicBlock.getHeadInst(); instruction != null; instruction = instruction.getNext())
            {
                if (!(instruction instanceof FuncCall))
                {
                    Collection<IRReg> usedRegs = instruction.getUsedIRReg();
                    if (!usedRegs.isEmpty())
                    {
                        boolean usedPreg0 = false;
                        renameMap.clear();
                        for (IRReg reg : usedRegs)
                        {
                            if (reg instanceof VirtualReg)
                            {
                                IRReg color = regGraphInfoMap.get(reg).colorReg;
                                if (color instanceof StackSlot)
                                {
                                    PhysicalReg pReg;
                                    if (!usedPreg0)
                                    {
                                        pReg = pReg0;
                                        usedPreg0 = true;
                                    }
                                    else
                                        pReg = pReg1;
                                    instruction.prepend(new Load(basicBlock, pReg, color, Config.regSize, 0));
                                    renameMap.put(reg, pReg);
                                    currentFunc.getUsedGeneralPReg().add(pReg);
                                }
                                else
                                {
                                    renameMap.put(reg, color);
                                    currentFunc.getUsedGeneralPReg().add((PhysicalReg) color);
                                }
                            }
                            else
                                renameMap.put(reg, reg);
                        }
                        instruction.setUsedIRReg(renameMap);
                    }
                }
                else
                {
                    List<IRValue> paraList = ((FuncCall) instruction).getParas();
                    for (int i = 0; i < paraList.size(); i++)
                    {
                        IRValue para = paraList.get(i);
                        if (para instanceof VirtualReg)
                            paraList.set(i, regGraphInfoMap.get(para).colorReg);
                    }
                }
                IRReg definedReg = instruction.getDefinedReg();
                if (definedReg instanceof VirtualReg)
                {
                    IRReg color = regGraphInfoMap.get(definedReg).colorReg;
                    if (color instanceof StackSlot)
                    {
                        instruction.setDefinedReg(pReg0);
                        instruction.append(new Store(basicBlock, pReg0, color, Config.regSize, 0));
                        currentFunc.getUsedGeneralPReg().add(pReg0);
                        // need to skip the new added store!
                        instruction = instruction.getNext();
                    }
                    else
                    {
                        instruction.setDefinedReg(color);
                        currentFunc.getUsedGeneralPReg().add((PhysicalReg) color);
                    }
                }
            }
        }
    }

    public void allocateReg()
    {
        for (IRFunction function : irRoot.getFunctionMap().values())
        {
            currentFunc = function;
            setFuncArgReg();
            livelinessAnalyst.anylyseLiveness(function);
            regGraphInfoMap.clear();
            nodeSet.clear();
            underflowRegNodes.clear();
            regStack.clear();
            buildGraph();
            colorize();
            rewriteInstruction();
        }
    }


}
