import java.util.List;

public class FunctionCallNode extends ExpressionNode {

    String identifier;
    List<ExpressionNode> parameters;

    public FunctionCallNode(String identifier, List<ExpressionNode> parameters){
        this.identifier = identifier;
        this.parameters = parameters;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
