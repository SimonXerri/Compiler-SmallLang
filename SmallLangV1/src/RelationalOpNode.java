public class RelationalOpNode extends ExpressionNode {

    String operator;
    ExpressionNode LHS;
    ExpressionNode RHS;

    public RelationalOpNode(String operator, ExpressionNode LHS, ExpressionNode RHS){
        this.operator = operator;
        this.LHS = LHS;
        this.RHS = RHS;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
