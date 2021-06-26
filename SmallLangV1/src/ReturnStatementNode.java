public class ReturnStatementNode extends StatementNode {

    ExpressionNode expressionNode;

    public ReturnStatementNode(ExpressionNode expressionNode) {
        this.expressionNode = expressionNode;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
