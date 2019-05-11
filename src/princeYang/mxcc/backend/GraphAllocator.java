package princeYang.mxcc.backend;

import princeYang.mxcc.ir.IRFunction;
import princeYang.mxcc.ir.IRROOT;
import princeYang.mxcc.ir.PhysicalReg;
import princeYang.mxcc.ir.VirtualReg;

import java.util.*;

public class GraphAllocator
{
    private IRROOT irRoot;
    private List<PhysicalReg> generalRegs = new ArrayList<PhysicalReg>();
    private PhysicalReg pReg0, pReg1;
    private int colorNum;
    public Map<VirtualReg, GraphRegInfo> regGraphRegInfoMap = new HashMap<VirtualReg, GraphRegInfo>();
    public List<VirtualReg> vRegOrder = new ArrayList<VirtualReg>();
    public Set<VirtualReg> vRegNodes = new HashSet<VirtualReg>();
    public Set<PhysicalReg> usedColorSet = new HashSet<PhysicalReg>();
    public Set<VirtualReg> underflowRegNodes = new HashSet<VirtualReg>();

    public GraphAllocator(IRROOT irRoot)
    {
        this.irRoot = irRoot;
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
    }

    private GraphRegInfo getGraphRegInfo(VirtualReg vReg)
    {
        GraphRegInfo graphRegInfo = regGraphRegInfoMap.get(vReg);
        if (graphRegInfo == null)
        {
            graphRegInfo = new GraphRegInfo();
            regGraphRegInfoMap.put(vReg, graphRegInfo);
        }
        return graphRegInfo;
    }

    private void addEdge(VirtualReg des, VirtualReg arr)
    {
        getGraphRegInfo(arr).neighbors.add(des);
        getGraphRegInfo(des).neighbors.add(arr);
    }

    private void removeRegNode(VirtualReg vReg)
    {
        GraphRegInfo regInfo = regGraphRegInfoMap.get(vReg);
        regGraphRegInfoMap.remove(vReg);
        regInfo.removed = true;
        for (VirtualReg neighbor : regInfo.neighbors)
        {
            GraphRegInfo neighborInfo = regGraphRegInfoMap.get(vReg);
            if (!neighborInfo.removed)
            {
                neighborInfo.degree--;
                if (neighborInfo.degree < colorNum)
                    underflowRegNodes.add(neighbor);
            }
        }
    }

    private


}
