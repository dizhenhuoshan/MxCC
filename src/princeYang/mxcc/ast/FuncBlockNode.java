package princeYang.mxcc.ast;
import java.util.List;

public class FuncBlockNode extends StateNode
{
    List<StateNode> funcStateList;
    List<VarDeclNode> varDeclList;

    public FuncBlockNode(Location location, List<StateNode> funcStateList, List<VarDeclNode> varDeclList)
    {
        this.location = location;
        this.funcStateList = funcStateList;
        this.varDeclList = varDeclList;
    }

    public List<VarDeclNode> getVarDeclList()
    {
        return varDeclList;
    }

    public List<StateNode> getFuncStateList()
    {
        return funcStateList;
    }

    @Override
    public void accept(AstVisitor visitor)
    {
        visitor.visit(this);
    }
}
