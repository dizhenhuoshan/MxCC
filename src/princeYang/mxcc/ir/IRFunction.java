package princeYang.mxcc.ir;

import princeYang.mxcc.scope.FuncEntity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class IRFunction
{
    private String funcName;
    private IRFuncType funcType;
    private LinkedList<BasicBlock> basicBlocks;
    private BasicBlock bbEnter;
    private BasicBlock bbLeave;
    private boolean hasRetValue = false, hasRecursive = false, isInClass = false;
    private String buildInName;
    private FuncEntity funcEntity;
    public Set<IRFunction> setCallee = new HashSet<IRFunction>();
    public Set<IRFunction> setRecurCallee = new HashSet<IRFunction>();

    public IRFunction() {}

    public IRFunction(String funcName, String buildInName)
    {
        this.funcName = funcName;
        this.buildInName = buildInName;
        this.funcEntity = null;
        this.funcType = IRFuncType.BuildIn;
    }

    public IRFunction(FuncEntity funcEntity)
    {
        this.funcName = funcEntity.getIdent();
        this.funcEntity = funcEntity;
        if (funcEntity.isInClass())
        {
            // TODO: function name in class.
            this.isInClass = true;
        }
    }

    public LinkedList<BasicBlock> getBasicBlocks()
    {
        return basicBlocks;
    }

}
