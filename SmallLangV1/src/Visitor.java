interface Visitor {
    public void visit(AdditiveOpNode node);
    public void visit(AssignmentNode node);
    public void visit(BlockNode node);
    public void visit(BooleanExpressionNode node);
    public void visit(ExpressionNode node);
    public void visit(FloatExpressionNode node);
    public void visit(FormalParamsNode node);
    public void visit(FormalParamNode node);
    public void visit(ForStatementNode node);
    public void visit(FunctionCallNode node);
    public void visit(FunctionDeclNode node);
    public void visit(IdentifierNode node);
    public void visit(IfStatementNode node);
    public void visit(IntegerExpressionNode node);
    public void visit(MultiplicativeOpNode node);
    public void visit(PrintStatementNode node);
    public void visit(ProgramNode node);
    public void visit(RelationalOpNode node);
    public void visit(ReturnStatementNode node);
    public void visit(StatementNode node);
    public void visit(VariableDeclerationNode node);
    public void visit(UnaryExpressionNode node);
    public void visit(WhileStatementNode node);

}
