package princeYang.mxcc.ast;
import java.util.List;

public class FuncBlockNode extends StateNode
{
    List<StateNode> funcStateList;
    List<DeclNode> varDeclList;

    public FuncBlockNode(Location location, List<StateNode> funcStateList, List<DeclNode> varDeclList)
    {
        this.location = location;
        this.funcStateList = funcStateList;
        this.varDeclList = varDeclList;
    }

    public List<DeclNode> getVarDeclList()
    {
        return varDeclList;
    }

    public List<StateNode> getFuncStateList()
    {
        return funcStateList;
    }
}
