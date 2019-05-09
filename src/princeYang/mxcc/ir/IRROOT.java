package princeYang.mxcc.ir;

import princeYang.mxcc.ast.ForStateNode;
import princeYang.mxcc.ast.StateNode;
import princeYang.mxcc.backend.NASMRegSet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class IRROOT
{
    private Map<String, IRFunction> functionMap = new LinkedHashMap<String, IRFunction>();
    private Map<String, IRFunction> buildInFuncMap = new LinkedHashMap<String, IRFunction>();
    private Map<String, StaticStr> staticStrMap = new LinkedHashMap<String, StaticStr>();
    private List<StaticData> staticDataList = new ArrayList<StaticData>();
    private Map<ForStateNode, IRFor> IRForMap = new LinkedHashMap<ForStateNode, IRFor>();
    private boolean containShiftDiv = false;

    static public final String buildInPrint = "print";
    static public final String buildInPrintln = "println";
    static public final String buildInGetString = "getString";
    static public final String buildInGetInt = "getInt";
    static public final String buildInToString = "toString";

    static public final String buildInStringConcat = "string.concat";
    static public final String buildInStringEqual = "string.equal";
    static public final String buildInStringNequal = "string.nequal";
    static public final String buildInStringLess = "string.less";
    static public final String buildInStringLessEqual = "string.lessEqual";

    static public final String buildInClassStringLength = "__class__string__length";
    static public final String buildInClassStringSubString = "__class__string__substring";
    static public final String buildInClassStringParseInt = "__class__string__parseInt";
    static public final String buildInClassStringOrd = "__class__string__ord";
    static public final String buildInClassArraySize = "__class____array__size";

    // for spot optim
    static public final String buildInPrintForInt = "printForInt";
    static public final String buildInPrintlnForInt = "printlnForInt";

    private List<String> buildInList = new ArrayList<String>();

    public IRROOT()
    {
        buildInList.add(buildInPrint);
        buildInList.add(buildInPrintln);
        buildInList.add(buildInGetString);
        buildInList.add(buildInGetInt);
        buildInList.add(buildInToString);
        buildInList.add(buildInStringConcat);
        buildInList.add(buildInStringEqual);
        buildInList.add(buildInStringNequal);
        buildInList.add(buildInStringLess);
        buildInList.add(buildInStringLessEqual);
        buildInList.add(buildInClassStringLength);
        buildInList.add(buildInClassStringSubString);
        buildInList.add(buildInClassStringParseInt);
        buildInList.add(buildInClassStringOrd);
        buildInList.add(buildInClassArraySize);
        // for spot optim
        buildInList.add(buildInPrintForInt);
        buildInList.add(buildInPrintlnForInt);

        IRFunction buildInFunc;

        for (String buildInName : buildInList)
        {
            buildInFunc = new IRFunction(buildInName, buildInName);
            buildInFunc.getUsedGeneralPReg().addAll(NASMRegSet.generalRegs);
            processBuildIn(buildInFunc);
        }
    }

    private void processBuildIn(IRFunction buildInFunc)
    {
        this.buildInFuncMap.put(buildInFunc.getFuncName(), buildInFunc);
    }

    public void addFunction(IRFunction function)
    {
        this.functionMap.put(function.getFuncName(), function);
    }

    public Map<String, IRFunction> getFunctionMap()
    {
        return functionMap;
    }

    public Map<String, IRFunction> getBuildInFuncMap()
    {
        return buildInFuncMap;
    }

    public Map<ForStateNode, IRFor> getIRForMap()
    {
        return IRForMap;
    }

    public Map<String, StaticStr> getStaticStrMap()
    {
        return staticStrMap;
    }

    public void addStaticData(StaticData staticData)
    {
        this.staticDataList.add(staticData);
    }

    public boolean isContainShiftDiv()
    {
        return containShiftDiv;
    }

    public void setContainShiftDiv(boolean containShiftDiv)
    {
        this.containShiftDiv = containShiftDiv;
    }

    public List<StaticData> getStaticDataList()
    {
        return staticDataList;
    }
}
