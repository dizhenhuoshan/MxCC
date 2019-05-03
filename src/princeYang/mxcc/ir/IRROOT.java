package princeYang.mxcc.ir;

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

    static public final String buildInPrint = "print";
    static public final String buildInPrintln = "println";
    static public final String buildInPrintInt = "printInt";
    static public final String buildInPrintlnInt = "printlnInt";
    static public final String buildInGetString = "getString";
    static public final String buildInGetInt = "getInt";
    static public final String buildInToString = "toString";

    static public final String buildInStringConcat = "string.concat";
    static public final String buildInStringEqual = "string.equal";
    static public final String buildInStringLess = "string.less";

    static public final String buildInClassStringLength = "__class__string__length";
    static public final String buildInClassStringSubString = "__class__string__substring";
    static public final String buildInClassStringParseInt = "__class__string__parseInt";
    static public final String buildInClassStringOrd = "__class__string__ord";
    static public final String buildInClassArraySize = "__class____array__size";

    private List<String> buildInList = new ArrayList<String>();

    public IRROOT()
    {
        buildInList.add(buildInPrint);
        buildInList.add(buildInPrintln);
        buildInList.add(buildInPrintInt);
        buildInList.add(buildInPrintlnInt);
        buildInList.add(buildInGetString);
        buildInList.add(buildInGetInt);
        buildInList.add(buildInToString);
        buildInList.add(buildInStringConcat);
        buildInList.add(buildInStringEqual);
        buildInList.add(buildInStringLess);
        buildInList.add(buildInClassStringLength);
        buildInList.add(buildInClassStringSubString);
        buildInList.add(buildInClassStringParseInt);
        buildInList.add(buildInClassStringOrd);
        buildInList.add(buildInClassArraySize);

        IRFunction buildInFunc;

        for (String buildInName : buildInList)
        {
            buildInFunc = new IRFunction(buildInName, buildInName);
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

    public void addStaticData(StaticData staticData)
    {
        this.staticDataList.add(staticData);
    }
}