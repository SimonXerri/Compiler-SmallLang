import java.util.List;

public class FormalParamsNode extends StatementNode {

    List<StatementNode> formalParams;

    public FormalParamsNode(List<StatementNode> formalParams){
        this.formalParams = formalParams;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
