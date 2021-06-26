public class ExpressionNode implements Node {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
