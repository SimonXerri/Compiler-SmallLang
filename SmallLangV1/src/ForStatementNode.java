public class ForStatementNode extends StatementNode {

    StatementNode variableDecl;
    ExpressionNode expressionNode;
    StatementNode assignment;
    StatementNode block;

    public ForStatementNode(StatementNode variableDecl, ExpressionNode expressionNode, StatementNode assignment, StatementNode block){
        this.variableDecl = variableDecl;
        this.expressionNode = expressionNode;
        this.assignment = assignment;
        this.block = block;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
