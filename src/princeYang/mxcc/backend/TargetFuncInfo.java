package princeYang.mxcc.backend;

import princeYang.mxcc.ir.IRValue;
import princeYang.mxcc.ir.PhysicalReg;
import princeYang.mxcc.ir.StackSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TargetFuncInfo
{
    public int stackSlotNum = 0, extraArgsNum = 0;
    public List<PhysicalReg> usedCalleeSaveRegs;
    public List<PhysicalReg> usedCallerSaveRegs;
    public Map<StackSlot, Integer> stackSlotOffsetMap;

    public TargetFuncInfo()
    {
        usedCalleeSaveRegs = new ArrayList<PhysicalReg>();
        usedCallerSaveRegs = new ArrayList<PhysicalReg>();
        stackSlotOffsetMap = new HashMap<StackSlot, Integer>();
    }
}
