import java.util.List;

public class ProgramNode implements Node {

    List<StatementNode> statements_list;

    public ProgramNode(List<StatementNode> statements_list){
        this.statements_list = statements_list;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
