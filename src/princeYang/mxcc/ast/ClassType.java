package princeYang.mxcc.ast;

public class ClassType extends Type
{
    String className;

    public ClassType(String className)
    {
        this.baseType = BaseType.STYPE_CLASS;
        this.className = className;
    }

    public String getClassName()
    {
        return className;
    }

    @Override
    public boolean equals(Object obj)
    {
        return (obj.getClass() == ClassType.class) &&
                (((ClassType) obj).className.equals(className));
    }

    @Override
    public String toString()
    {
        return String.format("ClassType(%s)", className);
    }
}
