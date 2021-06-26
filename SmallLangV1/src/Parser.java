import java.util.ArrayList;
import java.util.List;

public class Parser {

    Lexer lexer;
    Token token;

    public Parser(Lexer lexer){
        this.lexer = lexer;
    }

    private ExpressionNode parseNumber(){
        ExpressionNode node = null;

        if(token.getType().equals("INT_TOK")){      // in the case where the number is an integer
            node = new IntegerExpressionNode((int)token.getValue());
        }else{  //in the case where the number is a float
            node = new FloatExpressionNode(token.getValue());
        }

        token = lexer.GetNextToken();
        return node;
    }

    private ExpressionNode parseBooleanLiteral(){
        ExpressionNode node = null;

        if(token.getCharacters().equals("true")){
            node = new BooleanExpressionNode(true);
        }else{
            node = new BooleanExpressionNode(false);
        }

        token = lexer.GetNextToken();
        return node;
    }

    private ExpressionNode parseLiteral(){
        ExpressionNode node = null;

        if(token.getType().equals("BOOL_LIT_TOK")){
            node = parseBooleanLiteral();
            return node;
        }else if(token.getType().equals("FLOAT_TOK") || token.getType().equals("INT_TOK")){
            node = parseNumber();
            return node;
        }

        return null;
    }

    private ExpressionNode parseIdentifierAndFunctionCall(){
        String identifier = token.getCharacters();

        token = lexer.GetNextToken();
        if(token.getType().equals("PUNC_TOK") && token.getCharacters().equals("(")){
            List<ExpressionNode> parameters = new ArrayList<>(3);
            token = lexer.GetNextToken();

            if (token.getType().equals("PUNC_TOK") && token.getCharacters().equals(")")) {  // in the case where no parameters are passed
                token = lexer.GetNextToken();
                return new FunctionCallNode(identifier, parameters);
            }

            while(!token.getType().equals("EOF_TOK")){

                ExpressionNode node = null;
                node = parseExpression();

                if (node == null){
                    System.out.println();
                    System.out.println("ERROR: Expected Expression as parameter or ')' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
                    System.out.println();
                    return null;

                }else{
                    parameters.add(node);
                }

                if (token.getType().equals("PUNC_TOK") && token.getCharacters().equals(")")){
                    break;
                } else if (token.getType().equals("PUNC_TOK") && token.getCharacters().equals(",")){
                    token = lexer.GetNextToken();
                    continue;
                } else {
                    System.out.println();
                    System.out.println("ERROR: Expected ',' or ')' in parameter on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
                    System.out.println();
                    return null;
                }
            }

            token = lexer.GetNextToken();
            return new FunctionCallNode(identifier, parameters);
        }else {
            return new IdentifierNode(identifier);
        }
    }

    private ExpressionNode parseSubExpression(){
        token = lexer.GetNextToken();
        ExpressionNode node = parseExpression();

        if(node == null){
            return null;
        }

        if(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(")")){
            token = lexer.GetNextToken();
            return node;
        }else{
            System.out.println();
            System.out.println("ERROR: Expected ')' character on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }
    }

    private ExpressionNode parseUnary(){
        token = lexer.GetNextToken();
        ExpressionNode node = parseExpression();

        if(node == null){
            return null;
        }else{
            return new UnaryExpressionNode(node);
        }
    }

    private ExpressionNode parseFactor(){
        ExpressionNode node = null;

        if (token.getType().equals("INT_TOK") || token.getType().equals("FLOAT_TOK") || token.getType().equals("BOOL_LIT_TOK")){
            node = parseLiteral();
        } else if (token.getType().equals("ID_TOK")){
            node = parseIdentifierAndFunctionCall();
        } else if (token.getType().equals("PUNC_TOK") && token.getCharacters().equals("(")){
            node = parseSubExpression();
        }else if (token.getType().equals("NOT_TOK") || (token.getType().equals("ADD_OP_TOK") && token.getCharacters().equals("-"))){
            node = parseUnary();
        }else {
            System.out.println();
            System.out.println("ERROR: Parsing Factor on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        return node;
    }

    private ExpressionNode parseTerm(){
        ExpressionNode node = parseFactor();

        if(node == null){
            System.out.println();
            System.out.println("ERROR: Unable to parse Factor on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        if (token.getType().equals("MULTI_OP_TOK")){
            String operator = token.getCharacters();
            MultiplicativeOpNode multNodeMain = null;
            token = lexer.GetNextToken();
            ExpressionNode multNode = parseTerm();
            if (multNode == null){
                return null;
            }
            multNodeMain = new MultiplicativeOpNode(operator, node, multNode);
            return multNodeMain;

        } else{
            return node;
        }
    }

    private ExpressionNode parseSimpleExpression(){
        ExpressionNode node = parseTerm();

        if(node == null){
            System.out.println();
            System.out.println("ERROR: Unable to parse Term on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        if (token.getType().equals("ADD_OP_TOK")){
            String operator = token.getCharacters();
            AdditiveOpNode addNodeMain = null;
            token = lexer.GetNextToken();
            ExpressionNode addNode = parseSimpleExpression();
            if (addNode == null){
                return null;
            }
            addNodeMain = new AdditiveOpNode(operator, node, addNode);
            return addNodeMain;

        } else{
            return node;
        }
    }

    private ExpressionNode parseExpression(){
        ExpressionNode node = parseSimpleExpression();

        if(node == null){
            System.out.println();
            System.out.println("ERROR: Unable to parse Simple Expression on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        if (token.getType().equals("REL_OP_TOK")){
            String operator = token.getCharacters();
            RelationalOpNode relNodeMain = null;
            token = lexer.GetNextToken();
            ExpressionNode relNode = parseExpression();
            if (relNode == null){
                return null;
            }
            relNodeMain = new RelationalOpNode(operator, node, relNode);
            return relNodeMain;

        } else{
            return node;
        }
    }

    private StatementNode parseBlock(){
        List<StatementNode> statements = new ArrayList<>(5);
        token = lexer.GetNextToken();
        while(!token.getType().equals("EOF_TOK")){
            if (token.getType().equals("PUNC_TOK") && token.getCharacters().equals("}")){
                break;
            }else{
                StatementNode statement = parseStatement();
                if (statement == null){
                    return null;
                }

                statements.add(statement);
            }
        }

        BlockNode node = new BlockNode(statements);
        token = lexer.GetNextToken();
        return node;

    }

    private StatementNode parseAssignment(){
        String identifier = token.getCharacters();
        token = lexer.GetNextToken();
        if (token.getType().equals("ASSIGN_TOK")){
            token = lexer.GetNextToken();
            ExpressionNode expressionNode = parseExpression();

            if (expressionNode == null){
                return null;
            }

            return new AssignmentNode(identifier, expressionNode);

        } else {
            System.out.println();
            System.out.println("ERROR: Expected '=' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

    }

    private StatementNode parseVariableDecl(){
        token = lexer.GetNextToken();
        String identifier = null;
        String type = null;

        if(token.getType().equals("ID_TOK")){
            identifier = token.getCharacters();
        }else{
            System.out.println();
            System.out.println("ERROR: Expected identifier on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(":"))){
            System.out.println();
            System.out.println("ERROR: Expected ':' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        if (token.getType().equals("TYPE_TOK") || token.getType().equals("AUTO_TOK")){
            type = token.getCharacters();
        }else{
            System.out.println();
            System.out.println("ERROR: Expected Token Type on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        if (!token.getType().equals("ASSIGN_TOK")){
            System.out.println();
            System.out.println("ERROR: Expected '=' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        ExpressionNode expressionNode = parseExpression();

        if (expressionNode == null){
            return null;
        }

        return new VariableDeclerationNode(identifier, type, expressionNode);
    }

    private StatementNode parsePrintStatement(){
        token = lexer.GetNextToken();

        ExpressionNode expressionNode = parseExpression();

        if (expressionNode == null){
            return null;
        }

        return new PrintStatementNode(expressionNode);
    }

    private StatementNode parseReturnStatement(){
        token = lexer.GetNextToken();

        ExpressionNode expressionNode = parseExpression();

        if (expressionNode == null){
            return null;
        }

        return new ReturnStatementNode(expressionNode);
    }

    private StatementNode parseIfStatement(){
        ExpressionNode condition = null;
        StatementNode trueBlock = null;
        StatementNode falseBlock = null;

        token = lexer.GetNextToken();
        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals("("))){
            System.out.println();
            System.out.println("ERROR: Expected '(' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        condition = parseExpression();
        if (condition == null){
            return null;
        }

        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(")"))){
            System.out.println();
            System.out.println("ERROR: Expected ')' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals("{"))){
            System.out.println();
            System.out.println("ERROR: Expected '{' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        trueBlock = parseBlock();
        if (trueBlock == null){
            return null;
        }

        if (token.getType().equals("ELSE_TOK")){
            token = lexer.GetNextToken();
            if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals("{"))){
                System.out.println();
                System.out.println("ERROR: Expected '{' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
                System.out.println();
                return null;
            }

            falseBlock = parseBlock();
            if (falseBlock == null){
                return null;
            }

            return new IfStatementNode(condition, trueBlock, falseBlock);
        }else{
            return new IfStatementNode(condition, trueBlock, falseBlock); // if falseBlock is equal to null it means there is no else statement.
        }
    }

    private StatementNode parseForStatement(){
        StatementNode variableDecl;
        ExpressionNode expressionNode;
        StatementNode assignment;
        StatementNode block;
        token = lexer.GetNextToken();

        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals("("))){
            System.out.println();
            System.out.println("ERROR: Expected '(' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        if (token.getType().equals("LET_TOK")){
            variableDecl = parseVariableDecl();
            if (variableDecl == null){
                return null;
            }
        }else {
            variableDecl = null;  // no variable declaration is made in the for statement
        }

        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(";"))){
            System.out.println();
            System.out.println("ERROR: Expected ';' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        expressionNode = parseExpression();
        if (expressionNode == null){
            return null;
        }

        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(";"))){
            System.out.println();
            System.out.println("ERROR: Expected ';' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        if (token.getType().equals("ID_TOK")){
            assignment = parseAssignment();
            if (assignment == null){
                return null;
            }
        }else {
            assignment = null;  // no assignment declaration was made in the for statement
        }

        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(")"))){
            System.out.println();
            System.out.println("ERROR: Expected ')' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals("{"))){
            System.out.println();
            System.out.println("ERROR: Expected '{' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        block = parseBlock();
        if (block == null){
            return  null;
        }

        return new ForStatementNode(variableDecl, expressionNode, assignment, block);
    }

    private StatementNode parseWhileStatement(){
        ExpressionNode expressionNode;
        StatementNode block;
        token = lexer.GetNextToken();

        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals("("))){
            System.out.println();
            System.out.println("ERROR: Expected '(' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        expressionNode = parseExpression();
        if (expressionNode == null){
            return null;
        }

        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(")"))){
            System.out.println();
            System.out.println("ERROR: Expected ')' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals("{"))){
            System.out.println();
            System.out.println("ERROR: Expected '{' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        block = parseBlock();
        if (block == null){
            return null;
        }

        return new WhileStatementNode(expressionNode, block);
    }

    private StatementNode parseFormalParam(){
        String identifier = token.getCharacters();
        String type = null;

        token = lexer.GetNextToken();
        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(":"))){
            System.out.println();
            System.out.println("ERROR: Expected ':' in Parameter on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        if (token.getType().equals("TYPE_TOK") || token.getType().equals("AUTO_TOK")){
            type = token.getCharacters();
        }else{
            System.out.println();
            System.out.println("ERROR: Expected Token Type in Parameter on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        return new FormalParamNode(identifier, type);
    }

    private StatementNode parseFormalParams(){
        List<StatementNode> formalParams = new ArrayList<>(3);

        StatementNode firstFormalParam = parseFormalParam();
        if (firstFormalParam == null){
            return null;
        }

        formalParams.add(firstFormalParam);

        while (!token.getType().equals("EOF_TOK")){

            if (token.getType().equals("PUNC_TOK") && token.getCharacters().equals(",")){

                token = lexer.GetNextToken();
                StatementNode formalParam = parseFormalParam();
                if (formalParam == null){
                    return null;
                }
                formalParams.add(formalParam);

            }else{
                break;
            }
        }

        return new FormalParamsNode(formalParams);
    }

    private StatementNode parseFunctionDecl(){
        String identifier = null;
        StatementNode formalParams = null;
        String type = null;
        StatementNode block = null;
        token = lexer.GetNextToken();

        if (!token.getType().equals("ID_TOK")){
            System.out.println();
            System.out.println("ERROR: Expected Identifier in Function Declaration on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        identifier = token.getCharacters();

        token = lexer.GetNextToken();
        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals("("))){
            System.out.println();
            System.out.println("ERROR: Expected '(' in Function Declaration on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(")"))){
            if (token.getType().equals("ID_TOK")){
                formalParams = parseFormalParams();
                if (formalParams == null){
                    return null;
                }
            }else {
                System.out.println();
                System.out.println("ERROR: Expected Identifier in Function Declaration on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
                System.out.println();
                return null;
            }
        }

        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(")"))){
            System.out.println();
            System.out.println("ERROR: Expected ')' in Function Declaration on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(":"))){
            System.out.println();
            System.out.println("ERROR: Expected ':' in Function Declaration on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        if (token.getType().equals("TYPE_TOK") || token.getType().equals("AUTO_TOK")){
            type = token.getCharacters();
        }else{
            System.out.println();
            System.out.println("ERROR: Expected Token Type on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        token = lexer.GetNextToken();
        if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals("{"))){
            System.out.println();
            System.out.println("ERROR: Expected '{' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }

        block = parseBlock();
        if (block == null){
            return null;
        }

        return new FunctionDeclNode(identifier, formalParams, type, block);
    }

    private StatementNode parseStatement(){
        if (token.getType().equals("LET_TOK")){
            StatementNode statementNode = parseVariableDecl();
            if (statementNode == null){
                return null;
            }

            if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(";"))){
                System.out.println();
                System.out.println("ERROR: Expected ';' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
                System.out.println();
                return null;
            }
            token = lexer.GetNextToken();
            return statementNode;

        } else if (token.getType().equals("ID_TOK")){
            StatementNode statementNode = parseAssignment();
            if (statementNode == null){
                return null;
            }

            if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(";"))){
                System.out.println();
                System.out.println("ERROR: Expected ';' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
                System.out.println();
                return null;
            }
            token = lexer.GetNextToken();
            return statementNode;

        } else if (token.getType().equals("PRINT_TOK")){
            StatementNode statementNode = parsePrintStatement();
            if (statementNode == null){
                return null;
            }

            if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(";"))){
                System.out.println();
                System.out.println("ERROR: Expected ';' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
                System.out.println();
                return null;
            }
            token = lexer.GetNextToken();
            return statementNode;

        } else if (token.getType().equals("IF_TOK")){
            StatementNode statementNode = parseIfStatement();
            if (statementNode == null){
                return null;
            }
            return statementNode;

        } else if (token.getType().equals("FOR_TOK")){
            StatementNode statementNode = parseForStatement();
            if (statementNode == null){
                return null;
            }
            return statementNode;

        } else if (token.getType().equals("WHILE_TOK")){
            StatementNode statementNode = parseWhileStatement();
            if (statementNode == null){
                return null;
            }
            return statementNode;

        } else if (token.getType().equals("RETURN_TOK")){
            StatementNode statementNode = parseReturnStatement();
            if (statementNode == null){
                return null;
            }

            if (!(token.getType().equals("PUNC_TOK") && token.getCharacters().equals(";"))){
                System.out.println();
                System.out.println("ERROR: Expected ';' on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
                System.out.println();
                return null;
            }
            token = lexer.GetNextToken();
            return statementNode;

        } else if (token.getType().equals("FF_TOK")){
            StatementNode statementNode = parseFunctionDecl();
            if (statementNode == null){
                return null;
            }
            return statementNode;

        } else if (token.getType().equals("PUNC_TOK") && token.getCharacters().equals("{")){
            StatementNode statementNode = parseBlock();
            if (statementNode == null){
                return null;
            }
            return statementNode;

        } else {
            System.out.println();
            System.out.println("ERROR: Unrecognised Statement on Line: " + token.getLine() + ". Token found : " + token.getCharacters());
            System.out.println();
            return null;
        }
    }


    public Node parse(){
        List<StatementNode> statements_list = new ArrayList<>();    //program is made up of a number of statements.
        token = lexer.GetNextToken();

        while (!token.getType().equals("EOF_TOK")){          //while the end of the file is not reached.
            StatementNode statementNode = parseStatement();
            if (statementNode == null){
               return null;
            }
            statements_list.add(statementNode);
        }

        ProgramNode programNode = new ProgramNode(statements_list);
        return programNode;
    }
}


