package princeYang.mxcc.ast;

public class StringType extends Type
{
    StringType()
    {
        this.baseType = BaseType.DTYPE_STRING;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj.getClass().equals(StringType.class);
    }
}
