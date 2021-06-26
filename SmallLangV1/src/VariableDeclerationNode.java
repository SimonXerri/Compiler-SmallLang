public class VariableDeclerationNode extends StatementNode {

    String identifier;
    String type;
    ExpressionNode expression;

    public VariableDeclerationNode(String identifier, String type, ExpressionNode expression){
        this.identifier = identifier;
        this.type = type;
        this. expression = expression;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
