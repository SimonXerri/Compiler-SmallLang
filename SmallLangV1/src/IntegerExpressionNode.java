public class IntegerExpressionNode extends ExpressionNode {
    int value;

    public IntegerExpressionNode(int value){
        this.value = value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
