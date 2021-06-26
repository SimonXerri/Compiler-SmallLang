public class WhileStatementNode extends StatementNode {

    ExpressionNode expressionNode;
    StatementNode block;

    public WhileStatementNode(ExpressionNode expressionNode, StatementNode block){
        this.expressionNode = expressionNode;
        this.block = block;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
