public class IfStatementNode extends StatementNode {

    ExpressionNode condition;
    StatementNode trueBlock;
    StatementNode falseBlock;

    public IfStatementNode(ExpressionNode condition, StatementNode trueBlock, StatementNode falseBlock){
        this.condition = condition;
        this.trueBlock = trueBlock;
        this.falseBlock = falseBlock;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
