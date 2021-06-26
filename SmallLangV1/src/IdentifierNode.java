public class IdentifierNode extends ExpressionNode {

    String identifier_name;

    public IdentifierNode(String identifier_name){
        this.identifier_name = identifier_name;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
