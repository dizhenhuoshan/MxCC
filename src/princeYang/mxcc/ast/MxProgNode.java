package princeYang.mxcc.ast;

import java.util.List;

public class MxProgNode extends Node
{
    List<DeclNode> declNodesList;

    public MxProgNode(Location location, List<DeclNode> declNodesList)
    {
        this.location = location;
        this.declNodesList = declNodesList;
    }

    public List<DeclNode> getDeclNodesList()
    {
        return declNodesList;
    }

    @Override
    public void accept(AstVisitor visitor)
    {
        visitor.visit(this);
    }
}
