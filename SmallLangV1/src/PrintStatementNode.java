public class PrintStatementNode extends StatementNode {

    ExpressionNode expression;

    public PrintStatementNode(ExpressionNode expression){
        this.expression = expression;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
