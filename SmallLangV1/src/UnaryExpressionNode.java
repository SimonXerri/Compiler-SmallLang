public class UnaryExpressionNode extends ExpressionNode {

    ExpressionNode expressionNode;

    public UnaryExpressionNode(ExpressionNode expressionNode){
        this.expressionNode = expressionNode;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
