package princeYang.mxcc.backend;

import princeYang.mxcc.Config;
import princeYang.mxcc.ir.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GlobalVarProcessor
{
    IRROOT irRoot;
    private Map<IRFunction, GlobalVarFuncInfo> globalVarFuncInfoMap = new HashMap<IRFunction, GlobalVarFuncInfo>();

    private VirtualReg genVreg(GlobalVarFuncInfo funcInfo, StaticData staticData)
    {
        VirtualReg vReg = funcInfo.globalVarRegMap.get(staticData);
        if (vReg == null)
        {
            vReg = new VirtualReg(staticData.getIdent());
            funcInfo.globalVarRegMap.put(staticData, vReg);
        }
        return vReg;
    }

    private boolean isStaticMemAccess(IRInstruction instruction)
    {
        if (instruction instanceof Load)
            return ((Load) instruction).isStaticData();
        if (instruction instanceof Store)
            return ((Store) instruction).isStaticData();
        return false;
    }

    public void process()
    {
        // generate vreg for static data
        for (IRFunction function : irRoot.getFunctionMap().values())
        {
            GlobalVarFuncInfo globalVarFuncInfo = new GlobalVarFuncInfo();
            Map<IRReg, IRReg> renameMap = new HashMap<IRReg, IRReg>();
            globalVarFuncInfoMap.put(function, globalVarFuncInfo);
            for (BasicBlock basicBlock : function.getReversePostOrder())
            {
                for (IRInstruction instruction = basicBlock.getHeadInst(); instruction != null; instruction = instruction.getNext())
                {
                    if (!isStaticMemAccess(instruction))
                    {
                        List<IRReg> usedRegList = instruction.getUsedIRReg();
                        if (!usedRegList.isEmpty())
                        {
                            renameMap.clear();
                            for (IRReg reg : usedRegList)
                            {
                                if (reg instanceof StaticData && !(reg instanceof StaticStr))
                                    renameMap.put(reg, genVreg(globalVarFuncInfo, (StaticData) reg));
                                else
                                    renameMap.put(reg, reg);
                            }
                            instruction.setUsedIRReg(renameMap);
                        }
                        IRReg definedReg = instruction.getDefinedReg();
                        if (definedReg != null && definedReg instanceof StaticData)
                        {
                            VirtualReg vReg = genVreg(globalVarFuncInfo, (StaticData) definedReg);
                            instruction.setDefinedReg(vReg);
                            globalVarFuncInfo.definedGlobalVar.add((StaticData) definedReg);
                        }
                    }
                }
            }

            // load static data when function start
            BasicBlock blockEnter = function.getBlockEnter();
            IRInstruction headInst = blockEnter.getHeadInst();
            for(Map.Entry<StaticData, VirtualReg> mapEntry : globalVarFuncInfo.globalVarRegMap.entrySet())
            {
                StaticData globalVar = mapEntry.getKey();
                VirtualReg vReg = mapEntry.getValue();
                headInst.prepend(new Load(blockEnter, vReg, globalVar, Config.regSize, globalVar instanceof StaticStr));
            }
        }

        for (IRFunction buildInFunc : irRoot.getBuildInFuncMap().values())
            globalVarFuncInfoMap.put(buildInFunc, new GlobalVarFuncInfo());

        // calculate recur used static data
        for (IRFunction function : irRoot.getFunctionMap().values())
        {
            GlobalVarFuncInfo funcInfo = globalVarFuncInfoMap.get(function);
            funcInfo.recurDefinedGlobalVar.addAll(funcInfo.definedGlobalVar);
            funcInfo.recurUsedGlobalVar.addAll(funcInfo.globalVarRegMap.keySet());
            for (IRFunction calleFunc : function.recurCalleeSet)
            {
                
            }

        }


        for (IRFunction function : irRoot.getFunctionMap().values())
        {
            GlobalVarFuncInfo funcInfo = globalVarFuncInfoMap.get(function);
            Set<StaticData> usedGlobalVar = funcInfo.globalVarRegMap.keySet();
            if (!usedGlobalVar.isEmpty())
            {
                for (BasicBlock basicBlock : function.getReversePostOrder())
                {
                    for (IRInstruction instruction = basicBlock.getHeadInst(); instruction != null; instruction = instruction.getNext())
                    {
                        if ()
                    }
                }
            }
        }

    }

}
