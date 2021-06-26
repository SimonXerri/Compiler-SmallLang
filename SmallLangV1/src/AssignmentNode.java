public class AssignmentNode extends StatementNode {
    String identifier;
    ExpressionNode expressionNode;

    public AssignmentNode(String identifier, ExpressionNode expressionNode){
        this.identifier = identifier;
        this.expressionNode = expressionNode;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
