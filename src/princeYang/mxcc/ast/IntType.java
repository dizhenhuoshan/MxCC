package princeYang.mxcc.ast;

public class IntType extends Type
{
    IntType()
    {
        this.baseType = BaseType.DTYPE_INT;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj.getClass().equals(IntType.class);
    }
}
