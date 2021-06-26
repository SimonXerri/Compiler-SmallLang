import java.util.List;

public class BlockNode extends StatementNode {

    List<StatementNode> statements;

    public BlockNode(List<StatementNode> statements){
        this.statements = statements;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
