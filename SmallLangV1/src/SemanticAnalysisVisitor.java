import java.util.ArrayList;
import java.util.List;

public class SemanticAnalysisVisitor implements Visitor {

    private SymbolTable symbolTable = new SymbolTable();
    private List<Integer> typeCheck = new ArrayList<>(2);   // contains the types of the LHS and RHS of an expression.
    private List<String[]> reassignCheck = new ArrayList<>(5);  // used in order to check whether a loop may be infinite.
    private int lastReturnType; //used in order to check the return type of a declared function.
    private boolean hasReturn; //used to check if a function has a return statement.
    private List<String> functionData = new ArrayList<>(5); // used to store the data of a function : identifier, param1, param2 , ...

    @Override
    public void visit(AdditiveOpNode node) {
        node.LHS.accept(this);
        int LHS_typeCopy = typeCheck.get(0);
        node.RHS.accept(this);
        int RHS_typeCopy;
        if (typeCheck.size() == 1){
            RHS_typeCopy = typeCheck.get(0);
        }else {
            RHS_typeCopy = typeCheck.get(1);
        }

        String type = null;

        if (node.operator.equals("+") || node.operator.equals("-")){
            type = symbolTable.lookUpReturnType(LHS_typeCopy, RHS_typeCopy, "typeTable_MDAS");
            if (type == null){
                System.out.println();
                System.out.println("ERROR: Incorrect types Added or Subtracted together. Only Integers and Floats can be Added or Subtracted.");
                System.out.println();
                System.exit(0);
            }
        }else {
            type = symbolTable.lookUpReturnType(LHS_typeCopy, RHS_typeCopy, "typeTable_AO");
            if (type == null){
                System.out.println();
                System.out.println("ERROR: Incorrect types OR together. Only Booleans can be OR.");
                System.out.println();
                System.exit(0);
            }
        }

        typeCheck.clear();
        if (type.equals("BOOL_LIT_TOK")){
            typeCheck.add(2);
        }else if (type.equals("FLOAT_TOK")){
            typeCheck.add(0);
        }else {
            typeCheck.add(1);
        }
    }

    @Override
    public void visit(AssignmentNode node) {
        String identifier = node.identifier;
        boolean variableDefined = symbolTable.lookUpVariable(identifier);
        if (!variableDefined){
            System.exit(0);
        }
        node.expressionNode.accept(this);
        String[] valuesPrev = symbolTable.lookUpType(identifier);
        String prevDeclType = valuesPrev[0];
        int prevDeclTypeInt = 0;
        if (prevDeclType.equals("int")){
            prevDeclTypeInt = 1;
        }else if(prevDeclType.equals("float")){
            prevDeclTypeInt = 0;
        }else {
            prevDeclTypeInt = 2;
        }

        if (prevDeclTypeInt != typeCheck.get(0)){
            System.out.println();
            System.out.println("ERROR: The expression being assigned to " + identifier + " does not have the same type as the identifier.");
            System.out.println();
            System.exit(0);
        }

        for(int i = 0; i < reassignCheck.size(); ++i){
            if (reassignCheck.get(i)[0].equals(identifier)){
                reassignCheck.get(i)[1] = "Assigned";
            }
        }

        typeCheck.clear();
    }

    @Override
    public void visit(BlockNode node) {
        String classCalling = new Exception().getStackTrace()[3].getClassName();    //Checking which class is calling the block
        if (classCalling.equals("WhileStatementNode") || classCalling.equals("IfStatementNode")|| classCalling.equals("ForStatementNode") || classCalling.equals("FunctionDeclNode")){
            //dont create new scope
            for(int i = 0; i < node.statements.size(); ++i){
                node.statements.get(i).accept(this);
            }
        }else{
            //create new scope
            Scope blockScope = new Scope();
            symbolTable.pushActiveScope(blockScope);
            for(int i = 0; i < node.statements.size(); ++i){
                node.statements.get(i).accept(this);
            }
            symbolTable.popUnActiveScope();
        }
    }

    @Override
    public void visit(BooleanExpressionNode node) {
        typeCheck.add(2);
    }

    @Override
    public void visit(ExpressionNode node) {
        ////
    }

    @Override
    public void visit(FloatExpressionNode node) {
        typeCheck.add(0);
    }

    @Override
    public void visit(FormalParamsNode node) {
        for (int i = 0; i < node.formalParams.size(); ++i){
            node.formalParams.get(i).accept(this);
        }
    }

    @Override
    public void visit(FormalParamNode node) {
        String identifier = node.identifier;
        String values[] = {node.type, null};
        symbolTable.insertIntoActiveScope(identifier,values);   //storing the params in the current function scope.
        functionData.add(node.type);
    }

    @Override
    public void visit(ForStatementNode node) {
        Scope forScope = new Scope();
        reassignCheck.clear();
        symbolTable.pushActiveScope(forScope);
        if (node.variableDecl != null){
            node.variableDecl.accept(this);
        }
        node.expressionNode.accept(this);

        if (typeCheck.get(0) != 2){
            System.out.println();
            System.out.println("ERROR: The expression in the for loop should be of type Boolean.");
            System.out.println();
            System.exit(0);
        }

        typeCheck.clear();
        if (node.assignment != null){
            node.assignment.accept(this);
        }
        node.block.accept(this);

        boolean reassign = false;
        for(int i = 0; i < reassignCheck.size(); ++i){
            if (reassignCheck.get(i)[1].equals("Assigned")){
                reassign = true;
                break;
            }
        }
        if (!reassign){
            System.out.println();
            System.out.println("WARNING: For loop may create infinite loop if variable in condition is not updated!");
            System.out.println();
        }

        reassignCheck.clear();
        symbolTable.popUnActiveScope();
    }

    @Override
    public void visit(FunctionCallNode node) {
        //Scope functionCallScope = new Scope();
        //symbolTable.pushActiveScope(functionCallScope);
        List<Integer>copy_typeCheck = new ArrayList<>(5);
        for (int t = 0; t < typeCheck.size(); ++t){
            copy_typeCheck.add(typeCheck.get(t));
        }

        List<Integer> paramTypes = new ArrayList<>(5);
        typeCheck.clear();

        for (int i = 0; i < node.parameters.size(); ++i){
            node.parameters.get(i).accept(this);
            paramTypes.add(typeCheck.get(0));
            typeCheck.clear();
        }

        for (int p = 0; p < paramTypes.size(); ++p){
            if (paramTypes.get(p) == 0){
                functionData.add("float");
            }else if (paramTypes.get(p) == 1){
                functionData.add("int");
            }else{
                functionData.add("bool");
            }
        }

        typeCheck = copy_typeCheck;
        boolean f =false;
        String functionFoundReturn = null;
        for (int s = 0; s < symbolTable.declaredFunctions.size(); ++s){
            boolean found = true;

            if (symbolTable.declaredFunctions.get(s).length - 2 == functionData.size()){
                for (int z = 0; z < symbolTable.declaredFunctions.get(s).length; ++z){
                    if (z == 0 || z == 1){
                        continue;
                    }
                    if(!functionData.get(z-2).equals(symbolTable.declaredFunctions.get(s)[z])){
                        found = false;
                    }
                }
            }else{
                found = false;
            }
            if (found){
                f = true;
                functionFoundReturn = symbolTable.declaredFunctions.get(s)[1];
                break;
            }
        }

        if (!f){
            System.out.println();
            System.out.println("ERROR: The function " + node.identifier + " has not been defined or may have different parameters.");
            System.out.println();
            System.exit(0);
        }else{

            if (!functionFoundReturn.equals("auto")) {
                int returnTypeInt = 0;
                if (functionFoundReturn.equals("int")) {
                    returnTypeInt = 1;
                } else if (functionFoundReturn.equals("float")) {
                    returnTypeInt = 0;
                } else {
                    returnTypeInt = 2;
                }

                typeCheck.add(returnTypeInt);
            }
        }

        //symbolTable.popUnActiveScope();
        functionData.clear();
    }

    @Override
    public void visit(FunctionDeclNode node) {
        Scope functionDeclScope = new Scope();
        hasReturn = false;
        functionData.add(node.identifier);
        functionData.add(node.type);
        symbolTable.pushActiveScope(functionDeclScope);
        if (node.formalParams != null){
            node.formalParams.accept(this);
        }
        node.block.accept(this);

        if (!hasReturn){
            System.out.println();
            System.out.println("ERROR: The function: " + node.identifier + " has no return statement.");
            System.out.println();
            System.exit(0);
        }

        String returnType = node.type;
        if (!returnType.equals("auto")){
            int returnTypeInt = 0;
            if (returnType.equals("int")){
                returnTypeInt = 1;
            }else if(returnType.equals("float")){
                returnTypeInt = 0;
            }else {
                returnTypeInt = 2;
            }

            if (returnTypeInt != lastReturnType){
                System.out.println();
                System.out.println("ERROR: The returned expression does not match the return type in the function :" + node.identifier + ".");
                System.out.println();
                System.exit(0);
            }
        }else{
            String typeFunc = null;
            if (lastReturnType == 0){
                typeFunc = "float";
            }else if (lastReturnType == 1){
                typeFunc = "int";
            }else{
                typeFunc = "bool";
            }
            functionData.set(1, typeFunc);
        }

        for (int i = 0; i < symbolTable.declaredFunctions.size(); ++i){
            boolean difference = false;
            if (symbolTable.declaredFunctions.get(i).length == functionData.size()){
                for (int j = 0; j < functionData.size(); ++j){
                    if (j == 1){    //not comparing the return types
                        continue;
                    }

                    if (!symbolTable.declaredFunctions.get(i)[j].equalsIgnoreCase(functionData.get(j))){
                        difference = true;
                        break;
                    }
                }
            }else{
                difference = true;
            }

            if (!difference){
                System.out.println();
                System.out.println("ERROR: The declared function " + node.identifier + " is already defined with the same parameters!");
                System.out.println();
                System.exit(0);
            }
        }

        String[] function = functionData.toArray(new String[functionData.size()]);
        symbolTable.declaredFunctions.add(function);
        functionData.clear();
        symbolTable.popUnActiveScope();
    }

    @Override
    public void visit(IdentifierNode node) {
        String identifier = node.identifier_name;
        String[] infiniteCheck = {identifier, "None"};
        reassignCheck.add(infiniteCheck);
        boolean variableDefined = symbolTable.lookUpVariable(identifier);
        if (!variableDefined){
            System.exit(0);
        }else {
            String[] valuesPrev = symbolTable.lookUpType(identifier);
            String prevDeclType = valuesPrev[0];
            int prevDeclTypeInt = 0;
            if (prevDeclType.equals("int")){
                typeCheck.add(1);
            }else if(prevDeclType.equals("float")){
                typeCheck.add(0);
            }else {
                typeCheck.add(2);
            }
        }
    }

    @Override
    public void visit(IfStatementNode node) {
        Scope ifScope = new Scope();
        symbolTable.pushActiveScope(ifScope);
        node.condition.accept(this);

        if (typeCheck.get(0) != 2){
            System.out.println();
            System.out.println("ERROR: The expression in the if statement should be of type Boolean.");
            System.out.println();
            System.exit(0);
        }

        typeCheck.clear();
        node.trueBlock.accept(this);
        if (node.falseBlock != null){
            node.falseBlock.accept(this);
        }
        symbolTable.popUnActiveScope();
    }

    @Override
    public void visit(IntegerExpressionNode node) {
        typeCheck.add(1);
    }

    @Override
    public void visit(MultiplicativeOpNode node) {
        node.LHS.accept(this);
        int LHS_typeCopy = typeCheck.get(0);
        node.RHS.accept(this);
        int RHS_typeCopy;
        if (typeCheck.size() == 1){
            RHS_typeCopy = typeCheck.get(0);
        }else {
            RHS_typeCopy = typeCheck.get(1);
        }

        String type = null;

        if (node.operator.equals("*") || node.operator.equals("/")){
            type = symbolTable.lookUpReturnType(LHS_typeCopy, RHS_typeCopy, "typeTable_MDAS");

            if (type == null){
                System.out.println();
                System.out.println("ERROR: Incorrect types multiplied or divided. Only Integers and Floats can be multiplied or divided.");
                System.out.println();
                System.exit(0);
            }
        }else {
            type = symbolTable.lookUpReturnType(LHS_typeCopy, RHS_typeCopy, "typeTable_AO");

            if (type == null){
                System.out.println();
                System.out.println("ERROR: Incorrect types AND together. Only Booleans can be AND together.");
                System.out.println();
                System.exit(0);
            }
        }

        typeCheck.clear();
        if (type.equals("BOOL_LIT_TOK")){
            typeCheck.add(2);
        }else if (type.equals("FLOAT_TOK")){
            typeCheck.add(0);
        }else {
            typeCheck.add(1);
        }
    }

    @Override
    public void visit(PrintStatementNode node) {
        node.expression.accept(this);
        typeCheck.clear();
    }

    @Override
    public void visit(RelationalOpNode node) {
        node.LHS.accept(this);
        int LHS_typeCopy = typeCheck.get(0);
        node.RHS.accept(this);
        int RHS_typeCopy;
        if (typeCheck.size() == 1){
            RHS_typeCopy = typeCheck.get(0);
        }else {
            RHS_typeCopy = typeCheck.get(1);
        }

        String type = null;

        if (node.operator.equals("<") || node.operator.equals(">") || node.operator.equals("<=") || node.operator.equals(">=")){
            type = symbolTable.lookUpReturnType(LHS_typeCopy, RHS_typeCopy, "typeTable_REL");
            if (type == null) {
                System.out.println();
                System.out.println("ERROR: Incorrect types Related together.");
                System.out.println();
                System.exit(0);
            }
        }else {
            type = symbolTable.lookUpReturnType(LHS_typeCopy, RHS_typeCopy, "typeTable_EN");
            if (type == null) {
                System.out.println();
                System.out.println("ERROR: Incorrect types Related together.");
                System.out.println();
                System.exit(0);
            }
        }

        typeCheck.clear();
        if (type.equals("BOOL_LIT_TOK")){
            typeCheck.add(2);
        }else if (type.equals("FLOAT_TOK")){
            typeCheck.add(0);
        }else {
            typeCheck.add(1);
        }
    }

    @Override
    public void visit(ReturnStatementNode node) {
        node.expressionNode.accept(this);
        lastReturnType = typeCheck.get(0);
        hasReturn = true;
        typeCheck.clear();
    }

    @Override
    public void visit(StatementNode node) {
        ////
    }

    @Override
    public void visit(VariableDeclerationNode node) {
        String declaredType = node.type;
        int declType;

        if (declaredType.equals("auto")){
            String identifier = node.identifier;
            node.expression.accept(this);
            String type = null;
            if (typeCheck.get(0) == 0){
                type = "float";
            }else if (typeCheck.get(0) == 1){
                type = "int";
            }else {
                type = "bool";
            }

            String[] values = new String[2];
            values[0] = type;
            values[1] = null;   // Value is not used in the Semantic Analysis stage.
            boolean declarationRepeat = symbolTable.lookUpDeclaration(identifier);
            if (declarationRepeat){
                symbolTable.insertIntoActiveScope(identifier,values);
                typeCheck.clear();
            }else {
                System.exit(0);
            }

        }else{
            String identifier = node.identifier;
            String[] values = new String[2];
            values[0] = node.type;
            values[1] = null;   // Value is not used in the Semantic Analysis stage.

            boolean declarationRepeat = symbolTable.lookUpDeclaration(identifier);
            if (declarationRepeat){
                symbolTable.insertIntoActiveScope(identifier,values);
                node.expression.accept(this);
            }else {
                System.exit(0);
            }

            if (declaredType.equals("int")){
                declType = 1;
            }else if(declaredType.equals("float")){
                declType = 0;
            }else {
                declType = 2;
            }

            if (typeCheck.get(0) == declType){
                typeCheck.clear();
            }else {
                System.out.println();
                System.out.println("ERROR: The type declared in the variable declaration for " + node.identifier + " does not match the type of the expression.");
                System.out.println();
                System.exit(0);
            }
        }

    }

    @Override
    public void visit(UnaryExpressionNode node) {
        node.expressionNode.accept(this);

        if (typeCheck.get(0) != 2){
            System.out.println();
            System.out.println("ERROR: The unary expression should be of type Boolean.");
            System.out.println();
            System.exit(0);
        }
    }

    @Override
    public void visit(WhileStatementNode node) {
        Scope whileScope = new Scope();
        reassignCheck.clear();
        symbolTable.pushActiveScope(whileScope);
        node.expressionNode.accept(this);

        if (typeCheck.get(0) != 2){
            System.out.println();
            System.out.println("ERROR: The expression in the while loop should be of type Boolean.");
            System.out.println();
            System.exit(0);
        }
        typeCheck.clear();
        node.block.accept(this);

        boolean reassign = false;
        for(int i = 0; i < reassignCheck.size(); ++i){
            if (reassignCheck.get(i)[1].equals("Assigned")){
                reassign = true;
                break;
            }
        }
        if (!reassign){
            System.out.println();
            System.out.println("WARNING: While loop may create infinite loop if variable in condition is not updated!");
            System.out.println();
        }

        reassignCheck.clear();
        symbolTable.popUnActiveScope();
    }

    @Override
    public void visit(ProgramNode node) {
        if (node != null){
            Scope globalScope = new Scope();
            symbolTable.pushActiveScope(globalScope);

            for(int i = 0; i < node.statements_list.size(); ++i){
                node.statements_list.get(i).accept(this);
            }
        } else{
            System.out.println();
            System.out.println("ERROR: Unable to do Semantic Analysis since an error occurred in the generation of the AST.");
            System.out.println();
        }
    }
}
