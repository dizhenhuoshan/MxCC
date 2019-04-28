package princeYang.mxcc.scope;

import princeYang.mxcc.ast.*;

public class VarEntity extends Entity
{
    private Location location;
    String classIdent = null;
    boolean inClass = false;
    boolean inGlobal = false;

    public VarEntity(String ident, Type type)
    {
        super(ident, type);
    }

    public VarEntity(VarDeclNode varDeclNode)
    {
        super(varDeclNode.getIdentName(), varDeclNode.getVarType().getType());
        this.location = varDeclNode.getLocation();
    }

    public VarEntity(String classIdent, String ident, Type type)
    {
        super(ident, type);
        this.classIdent = classIdent;
        this.inClass = true;
    }

    public VarEntity(String classIdent, VarDeclNode varDeclNode)
    {
        super(varDeclNode.getIdentName(), varDeclNode.getVarType().getType());
        this.classIdent = classIdent;
        this.inClass = true;
    }

    public Location getLocation()
    {
        return location;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public boolean isInGlobal()
    {
        return inGlobal;
    }

    public void setInGlobal(boolean inGlobal)
    {
        this.inGlobal = inGlobal;
    }


}
