package princeYang.mxcc.ir;

import princeYang.mxcc.errors.MxError;

import java.util.*;

public class FuncCall extends IRInstruction
{
    private IRFunction function;
    private IRReg retReg;
    private List<IRValue> args;

    public FuncCall(BasicBlock basicBlock, IRFunction function, IRReg retReg, List<IRValue> args)
    {
        super(basicBlock);
        this.function = function;
        this.retReg = retReg;
        this.args = args;
        reloadUsedRV();
    }

    @Override
    public void accept(IRVisitor visitor)
    {
        visitor.visit(this);
    }

    @Override
    public void reloadUsedRV()
    {
        usedIRReg.clear();
        usedIRValue.clear();
        for (IRValue arg : args)
        {
            usedIRValue.add(arg);
            if (arg instanceof IRReg)
                usedIRReg.add((IRReg) arg);
        }
    }

    @Override
    public void setUsedIRReg(Map<IRReg, IRReg> renameMap)
    {
        for (IRValue arg : args)
        {
            if (arg instanceof IRReg)
                if (!Collections.replaceAll(args, arg, renameMap.get(arg)))
                    throw new MxError("IRFuncCall setUsedIRReg Error! arg not found in renameMap\n");

        }
        reloadUsedRV();
    }

    @Override
    public IRInstruction copyAndRename(Map<Object, Object> renameMap)
    {
        List<IRValue> newArgs = new ArrayList<IRValue>();
        for (IRValue arg : args)
            newArgs.add((IRValue) renameMap.getOrDefault(arg, arg));
        return new FuncCall((BasicBlock) renameMap.getOrDefault(getFatherBlock(), getFatherBlock()),
                function, (IRReg) renameMap.getOrDefault(retReg, retReg), newArgs);

    }

    @Override
    public IRReg getDefinedReg()
    {
        return retReg;
    }

    @Override
    public void setDefinedReg(IRReg vIRReg)
    {
        this.retReg = vIRReg;
    }

    public List<IRValue> getArgs()
    {
        return args;
    }

    public IRFunction getFunction()
    {
        return function;
    }

    public IRReg getRetReg()
    {
        return retReg;
    }
}
