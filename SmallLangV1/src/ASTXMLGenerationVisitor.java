public class ASTXMLGenerationVisitor implements Visitor {

    private int number_tabs;

    public ASTXMLGenerationVisitor(){
        number_tabs = 0;
    }

    @Override
    public void visit(AdditiveOpNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }

        System.out.println(tabs + "<AdditiveOpNode Operator = \"" + node.operator + "\" >" );
        number_tabs++;
        node.LHS.accept(this);
        node.RHS.accept(this);
        number_tabs--;
        System.out.println(tabs + "</AdditiveOpNode>");
    }

    @Override
    public void visit(AssignmentNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }
        System.out.println(tabs + "<AssignmentNode>");
        number_tabs++;
        tabs += "\t";
        System.out.println(tabs + "<Identifier>" + node.identifier + "</Identifier>");
        node.expressionNode.accept(this);
        number_tabs--;
        tabs = tabs.substring(0, tabs.length()-1);
        System.out.println(tabs + "</AssignmentNode>");
    }

    @Override
    public void visit(BlockNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }

        for(int i = 0; i < node.statements.size(); ++i){
            node.statements.get(i).accept(this);
        }
    }

    @Override
    public void visit(BooleanExpressionNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }
        System.out.println(tabs + "<BooleanConst>" + node.polarity + "</BooleanConst>");
    }

    @Override
    public void visit(UnaryExpressionNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }
        System.out.println(tabs + "<UnaryExpressionNode>");
        number_tabs++;
        node.expressionNode.accept(this);
        number_tabs--;
        System.out.println(tabs + "</UnaryExpressionNode>");
    }

    @Override
    public void visit(ExpressionNode node) {
        ////
    }

    @Override
    public void visit(FloatExpressionNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }
        System.out.println(tabs + "<FloatConst>" + node.value + "</FloatConst>");
    }

    @Override
    public void visit(FormalParamsNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }
        System.out.println(tabs + "<FormalParameters>");
        number_tabs++;
        for (int i = 0; i < node.formalParams.size(); ++i){
            node.formalParams.get(i).accept(this);
        }
        number_tabs--;
        System.out.println(tabs + "</FormalParameters>");

    }

    @Override
    public void visit(FormalParamNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }
        System.out.println(tabs + "<FormalParam Type: \"" + node.type + "\" >" + node.identifier + "</FormalParam>");
    }

    @Override
    public void visit(ForStatementNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }

        System.out.println(tabs + "<ForStatementNode>");
        number_tabs++;
        tabs += "\t";

        if (node.variableDecl != null){
            node.variableDecl.accept(this);
        }
        node.expressionNode.accept(this);
        if (node.assignment != null){
            node.assignment.accept(this);
        }
        System.out.println(tabs + "<Block>");
        number_tabs++;
        node.block.accept(this);
        number_tabs--;
        System.out.println(tabs + "</Block>");
        number_tabs--;
        tabs = tabs.substring(0, tabs.length()-1);
        System.out.println(tabs + "</ForStatementNode>");
    }

    @Override
    public void visit(FunctionCallNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }
        System.out.println(tabs + "<FunctionCall>");
        number_tabs++;
        tabs += "\t";
        System.out.println(tabs + "<Identifier>" + node.identifier + "</Identifier>");

        if (node.parameters.size() != 0){
            System.out.println(tabs + "<Parameters>");
            number_tabs++;
            for (int i = 0; i < node.parameters.size(); ++i){
                node.parameters.get(i).accept(this);
            }
            number_tabs--;
            System.out.println(tabs + "</Parameters>");
        }
        number_tabs--;
        tabs = tabs.substring(0, tabs.length()-1);
        System.out.println(tabs + "</FunctionCall>");
    }

    @Override
    public void visit(FunctionDeclNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }

        System.out.println(tabs + "<FunctionDeclNode>");
        number_tabs++;
        tabs += "\t";
        System.out.println(tabs + "<Identifier>" + node.identifier + "</Identifier>");

        if (node.formalParams != null){
            node.formalParams.accept(this);
        }
        System.out.println(tabs + "<ReturnType>" + node.type + "</ReturnType>");
        System.out.println(tabs + "<Block>");
        number_tabs++;
        node.block.accept(this);
        number_tabs--;
        System.out.println(tabs + "</Block>");
        number_tabs--;
        tabs = tabs.substring(0, tabs.length()-1);
        System.out.println(tabs + "</FunctionDeclNode>");
    }

    @Override
    public void visit(IdentifierNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }
        System.out.println(tabs + "<Identifier>" + node.identifier_name + "</Identifier>");
    }

    @Override
    public void visit(IfStatementNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }

        System.out.println(tabs + "<IfStatementNode>");
        number_tabs++;
        node.condition.accept(this);
        tabs += "\t";
        System.out.println(tabs + "<TrueBlock>");
        number_tabs++;
        node.trueBlock.accept(this);
        number_tabs--;
        System.out.println(tabs + "</TrueBlock>");

        if (node.falseBlock != null){
            System.out.println(tabs + "<FalseBlock>");
            number_tabs++;
            node.falseBlock.accept(this);
            number_tabs--;
            System.out.println(tabs + "</FalseBlock>");
        }

        number_tabs--;
        tabs = tabs.substring(0, tabs.length()-1);
        System.out.println(tabs + "</IfStatementNode>");
    }

    @Override
    public void visit(IntegerExpressionNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }
        System.out.println(tabs + "<IntegerConst>" + node.value + "</IntegerConst>");
    }

    @Override
    public void visit(MultiplicativeOpNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }

        System.out.println(tabs + "<MultiplicativeOpNode Operator = \"" + node.operator + "\" >" );
        number_tabs++;
        node.LHS.accept(this);
        node.RHS.accept(this);
        number_tabs--;
        System.out.println(tabs + "</MultiplicativeOpNode>");
    }

    @Override
    public void visit(PrintStatementNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }
        System.out.println(tabs + "<PrintStatementNode>");
        number_tabs++;
        node.expression.accept(this);
        number_tabs--;
        System.out.println(tabs + "</PrintStatementNode>");
    }

    @Override
    public void visit(RelationalOpNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }

        System.out.println(tabs + "<RelationalOpNode Operator = \"" + node.operator + "\" >" );
        number_tabs++;
        node.LHS.accept(this);
        node.RHS.accept(this);
        number_tabs--;
        System.out.println(tabs + "</RelationalOpNode>");
    }

    @Override
    public void visit(StatementNode node) {
        ////
    }

    @Override
    public void visit(VariableDeclerationNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }

        System.out.println(tabs + "<VariableDeclarationNode>");
        number_tabs++;
        tabs += "\t";
        System.out.println(tabs + "<Identifier Type = \"" + node.type + "\" >" + node.identifier + "</Identifier>");
        node.expression.accept(this);
        number_tabs--;
        tabs = tabs.substring(0, tabs.length()-1);
        System.out.println(tabs + "</VariableDeclarationNode>");
    }

    @Override
    public void visit(WhileStatementNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }

        System.out.println(tabs + "<WhileStatementNode>");
        number_tabs++;
        tabs += "\t";
        node.expressionNode.accept(this);
        System.out.println(tabs + "<Block>");
        number_tabs++;
        node.block.accept(this);
        number_tabs--;
        System.out.println(tabs + "</Block>");
        number_tabs--;
        tabs = tabs.substring(0, tabs.length()-1);
        System.out.println(tabs + "</WhileStatementNode>");
    }

    @Override
    public void visit(ReturnStatementNode node) {
        String tabs = "";
        for (int i = 0; i < number_tabs; ++i){
            tabs += "\t";
        }

        System.out.println(tabs + "<ReturnStatementNode>");
        number_tabs++;
        node.expressionNode.accept(this);
        number_tabs--;
        System.out.println(tabs + "</ReturnStatementNode>");
    }

    @Override
    public void visit(ProgramNode node) {
        if (node != null){
            System.out.println();
            System.out.println("Printing the AST Generated by Parser in XML Format.");
            System.out.println();

            String tabs = "";
            for (int i = 0; i < number_tabs; ++i){
                tabs += "\t";
            }
            System.out.println(tabs + "<ProgramNode>");
            number_tabs++;
            for(int i = 0; i < node.statements_list.size(); ++i){

                if (node.statements_list.get(i) instanceof BlockNode){
                    tabs += "\t";
                    System.out.println(tabs + "<BlockNode>");
                    number_tabs++;
                    node.statements_list.get(i).accept(this);
                    number_tabs--;
                    System.out.println(tabs + "</BlockNode>");
                    tabs = tabs.substring(0, tabs.length()-1);
                }else{
                    node.statements_list.get(i).accept(this);
                }



                if (i != node.statements_list.size()-1){
                    System.out.println();
                }
            }
            number_tabs--;
            System.out.println(tabs + "<ProgramNode>");
        } else{
            System.out.println();
            System.out.println("ERROR: Unable to Print AST since an error occurred in the generation of the AST.");
            System.out.println();
        }
    }
}
