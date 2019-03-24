package princeYang.mxcc.ast;
import java.util.List;

public class ClassDeclNode extends DeclNode
{
    List<VarDeclNode> varDeclList;
    List<FuncDeclNode> funcDeclList;

    public ClassDeclNode(Location location, String name, List<VarDeclNode> varDeclList, List<FuncDeclNode> funcDeclList)
    {
        this.location = location;
        this.name = name;
        this.varDeclList = varDeclList;
        this.funcDeclList = funcDeclList;
    }

    public List<FuncDeclNode> getFuncDeclList()
    {
        return funcDeclList;
    }

    public List<VarDeclNode> getVarDeclList()
    {
        return varDeclList;
    }
}
