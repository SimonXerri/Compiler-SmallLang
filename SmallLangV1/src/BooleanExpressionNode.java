public class BooleanExpressionNode extends ExpressionNode {
    boolean polarity;

    public BooleanExpressionNode(boolean polarity){
        this.polarity = polarity;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
