public class FloatExpressionNode extends ExpressionNode {
    float value;

    public FloatExpressionNode(float value){
        this.value = value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
