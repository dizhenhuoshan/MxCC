package princeYang.mxcc.backend;

import princeYang.mxcc.Config;
import princeYang.mxcc.ir.IRFunction;
import princeYang.mxcc.ir.IRROOT;
import princeYang.mxcc.ir.PhysicalReg;

import java.util.HashMap;
import java.util.Map;

import static princeYang.mxcc.backend.NASMRegSet.*;

public class NASMFormProcessor
{
    private IRROOT irRoot;
    private Map<IRFunction, TargetFuncInfo> targetFuncInfoMap = new HashMap<IRFunction, TargetFuncInfo>();

    public NASMFormProcessor(IRROOT irRoot)
    {
        this.irRoot = irRoot;
    }

    private void funcPreProcessor()
    {
        for (IRFunction irFunction : irRoot.getFunctionMap().values())
        {
            TargetFuncInfo funcInfo = new TargetFuncInfo();

            // calleeSave & callerSave Regs
            funcInfo.usedCalleeSaveRegs.add(rbx);
            funcInfo.usedCalleeSaveRegs.add(rbp);
            for (PhysicalReg pReg : irFunction.getUsedGeneralPReg())
            {
                if (pReg.isCalleeSave())
                    funcInfo.usedCalleeSaveRegs.add(pReg);
                if (pReg.isCallerSave())
                    funcInfo.usedCallerSaveRegs.add(pReg);
            }

            // stacks
            funcInfo.stackSlotNum = irFunction.getStackSlots().size();
            for (int i = 0; i < funcInfo.stackSlotNum; i++)
                funcInfo.stackSlotOffsetMap.put(irFunction.getStackSlots().get(i), i * Config.regSize);
            // WARNING: Align rsp !!!
            // 8 bytes for a reg, stack need align to 16 bytes
            if ((funcInfo.usedCalleeSaveRegs.size() + funcInfo.stackSlotNum) % 2 == 0)
                funcInfo.stackSlotNum++;
        }
    }

}
