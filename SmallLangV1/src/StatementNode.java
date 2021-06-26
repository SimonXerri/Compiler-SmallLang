public class StatementNode implements Node {

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
