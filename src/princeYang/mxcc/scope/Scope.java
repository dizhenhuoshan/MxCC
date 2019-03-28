package princeYang.mxcc.scope;

import princeYang.mxcc.errors.MxError;

import java.util.HashMap;

public class Scope
{
    private HashMap<String, Entity> entityMap = new HashMap<>();
    private Scope father;
    private boolean isTop;
    static private String varPrefix = "__var__";
    static private String funcPrefix = "__func__";
    static private String classPrefix = "__class__";
    static private String thisPrefix = "__this__";

    public Scope()
    {
        father = null;
        isTop = true;
    }

    public Scope(Scope father)
    {
        this.father = father;
        isTop = false;
    }

    public void insertVar(Entity entity)
    {
        if (!(entity instanceof VarEntity))
            throw new MxError(String.format("Scope: inserVar entry type ERROR!"));
        String key = varPrefix + entity.getIdent();
        if (checkID(entity.getIdent()))
            entityMap.put(key, entity);
        else throw new MxError(String.format("Scope: identifier: %s already exists!\n", entity.getIdent()));
    }

    public void insertFunc(Entity entity)
    {
        if (!(entity instanceof FuncEntity))
            throw new MxError(String.format("Scope: inserFunc entry type ERROR!"));
        String key = funcPrefix + entity.getIdent();
        if (checkID(entity.getIdent()))
            entityMap.put(key, entity);
        else throw new MxError(String.format("Scope: identifier: %s already exists!\n", entity.getIdent()));
    }

    public void insertClass(Entity entity)
    {
        if (!(entity instanceof ClassEntity))
            throw new MxError(String.format("Scope: inserClass entry type ERROR!"));
        String key = classPrefix + entity.getIdent();
        if (checkID(entity.getIdent()))
            entityMap.put(key, entity);
        else throw new MxError(String.format("Scope: identifier: %s already exists!\n", entity.getIdent()));
    }

    public VarEntity getVar(String ident)
    {
        String key;
        if (!ident.startsWith(varPrefix))
            key = varPrefix + ident;
        else key = ident;
        VarEntity entity = (VarEntity) entityMap.get(key);
        if (entity != null || isTop)
            return entity;
        else return father.getVar(key);
    }

    public FuncEntity getFunc(String ident)
    {
        String key;
        if (!ident.startsWith(funcPrefix))
            key = funcPrefix + ident;
        else key = ident;
        FuncEntity entity = (FuncEntity) entityMap.get(key);
        if (entity != null || isTop)
            return entity;
        else return father.getFunc(key);
    }

    public ClassEntity getClass(String ident)
    {
        String key;
        if (!ident.startsWith(classPrefix))
            key = classPrefix + ident;
        else key = ident;
        ClassEntity entity = (ClassEntity) entityMap.get(key);
        if (entity != null || isTop)
            return entity;
        else return father.getClass(key);
    }

    public boolean checkID(String ident)
    {
        return !(entityMap.containsKey(varPrefix + ident)
                || entityMap.containsKey(funcPrefix + ident)
                || entityMap.containsKey(classPrefix + ident));
    }


    public Entity getSelfVar(String ident)
    {
        return entityMap.get(varPrefix + ident);
    }

    public Entity getSelfFunc(String ident)
    {
        return entityMap.get(funcPrefix + ident);
    }

    public Entity getSelfClass(String ident)
    {
        return entityMap.get(classPrefix + ident);
    }

    public Scope getFather()
    {
        return father;
    }

    public boolean isTop()
    {
        return isTop;
    }
}
