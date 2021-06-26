import java.util.ArrayList;
import java.util.List;

public class InterpreterExecutionVisitor implements Visitor {

    private SymbolTable symbolTable = new SymbolTable();
    private List<String> activeValues = new ArrayList<>(2);
    private List<String> params = new ArrayList<>(2);

    @Override
    public void visit(AdditiveOpNode node) {
        node.LHS.accept(this);
        String LHS_valueCopy = activeValues.get(0);
        activeValues.clear();
        node.RHS.accept(this);
        String RHS_valueCopy;
        if (activeValues.size() == 1){
            RHS_valueCopy = activeValues.get(0);
        }else {
            RHS_valueCopy = activeValues.get(1);
        }

        activeValues.clear();

        if (node.operator.equals("+")){
            float LHS_val = Float.parseFloat(LHS_valueCopy);
            float RHS_val = Float.parseFloat(RHS_valueCopy);
            float addition = LHS_val + RHS_val;
            String additionVal = Float.toString(addition);
            activeValues.add(additionVal);
        }else if (node.operator.equals("-")){
            float LHS_val = Float.parseFloat(LHS_valueCopy);
            float RHS_val = Float.parseFloat(RHS_valueCopy);
            float subtraction = LHS_val - RHS_val;
            String subVal = Float.toString(subtraction);
            activeValues.add(subVal);
        }else {
            boolean LHS_val = Boolean.parseBoolean(LHS_valueCopy);
            boolean RHS_val = Boolean.parseBoolean(RHS_valueCopy);
            boolean disjunction = LHS_val || RHS_val;
            String disVal = Boolean.toString(disjunction);
            activeValues.add(disVal);
        }
    }

    @Override
    public void visit(AssignmentNode node) {
        for (int i = symbolTable.activeScopes.size() - 1; i >= 0; --i){
            if (symbolTable.activeScopes.get(i).scope.containsKey(node.identifier)){
                node.expressionNode.accept(this);
                String[] values = symbolTable.activeScopes.get(i).scope.get(node.identifier);
                values[1] = activeValues.get(0);
                symbolTable.activeScopes.get(i).scope.replace(node.identifier, values);
                break;
            }
        }
        activeValues.clear();
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
        boolean value = node.polarity;
        String val;
        if (value){
            val = "true";
        }else{
            val = "false";
        }
        activeValues.add(val);
    }

    @Override
    public void visit(ExpressionNode node) {
        ///
    }

    @Override
    public void visit(FloatExpressionNode node) {
        float value = node.value;
        String val = Float.toString(value);
        activeValues.add(val);
    }

    @Override
    public void visit(FormalParamsNode node) {
        for (int i = 0; i < node.formalParams.size(); ++i){
            node.formalParams.get(i).accept(this);
        }
    }

    @Override
    public void visit(FormalParamNode node) {
        params.add(node.identifier);
    }

    @Override
    public void visit(ForStatementNode node) {
        Scope forScope = new Scope();
        symbolTable.pushActiveScope(forScope);

        if (node.variableDecl != null){
            node.variableDecl.accept(this);
        }

        node.expressionNode.accept(this);
        while (activeValues.get(0).equals("true")){
            activeValues.clear();
            node.block.accept(this);
            if (node.assignment != null){
                node.assignment.accept(this);
            }
            node.expressionNode.accept(this);
        }
        activeValues.clear();
        symbolTable.popUnActiveScope();
    }

    @Override
    public void visit(FunctionCallNode node) {
        String identifier = node.identifier;
        int paramSize = node.parameters.size();
        List<String> paramValues = new ArrayList<>(3);
        for (int i = 0; i < node.parameters.size(); ++i){
            node.parameters.get(i).accept(this);
            paramValues.add(activeValues.get(0));
            activeValues.clear();
        }

        for (int i = 0; i < symbolTable.declaredFunctions.size(); ++i){
            if (symbolTable.declaredFunctions.get(i)[0].equals(identifier) && symbolTable.declaredFunctions.get(i).length == paramSize+1){
                Scope functionCall = new Scope();
                symbolTable.pushActiveScope(functionCall);
                for (int j = 0; j < paramSize; ++j){

                    String paramIdentifier = symbolTable.declaredFunctions.get(i)[j+1];
                    String value = paramValues.get(j);
                    String type = null;

                    String[] values = {type, value};
                    symbolTable.insertIntoActiveScope(paramIdentifier, values);
                }
                symbolTable.functionBlocks.get(i).accept(this);
                symbolTable.popUnActiveScope();
                break;
            }
        }
    }

    @Override
    public void visit(FunctionDeclNode node) {
        node.formalParams.accept(this);
        params.add(0, node.identifier);

        String[] functionData = params.toArray(new String[params.size()]);
        symbolTable.declaredFunctions.add(functionData);    // adding function identifier and param identifiers
        symbolTable.functionBlocks.add(node.block);         // adding function block
        params.clear();
    }

    @Override
    public void visit(IdentifierNode node) {
        String[] values = symbolTable.lookUpType(node.identifier_name);
        activeValues.add(values[1]);
    }

    @Override
    public void visit(IfStatementNode node) {
        Scope ifScope = new Scope();
        symbolTable.pushActiveScope(ifScope);
        node.condition.accept(this);

        if (activeValues.get(0).equals("true")){
            activeValues.clear();
            node.trueBlock.accept(this);
        }else{
            activeValues.clear();
            if (node.falseBlock != null){
                node.falseBlock.accept(this);
            }
        }

        symbolTable.popUnActiveScope();
    }

    @Override
    public void visit(IntegerExpressionNode node) {
        int value = node.value;
        String val = Integer.toString(value);
        activeValues.add(val);
    }

    @Override
    public void visit(MultiplicativeOpNode node) {
        node.LHS.accept(this);
        String LHS_valueCopy = activeValues.get(0);
        activeValues.clear();
        node.RHS.accept(this);
        String RHS_valueCopy;
        if (activeValues.size() == 1){
            RHS_valueCopy = activeValues.get(0);
        }else {
            RHS_valueCopy = activeValues.get(1);
        }

        activeValues.clear();

        if (node.operator.equals("*")){
            float LHS_val = Float.parseFloat(LHS_valueCopy);
            float RHS_val = Float.parseFloat(RHS_valueCopy);
            float multiplication = LHS_val * RHS_val;
            String multVal = Float.toString(multiplication);
            activeValues.add(multVal);
        }else if (node.operator.equals("/")){
            float LHS_val = Float.parseFloat(LHS_valueCopy);
            float RHS_val = Float.parseFloat(RHS_valueCopy);
            float division = LHS_val / RHS_val;
            String divVal = Float.toString(division);
            activeValues.add(divVal);
        }else {
            boolean LHS_val = Boolean.parseBoolean(LHS_valueCopy);
            boolean RHS_val = Boolean.parseBoolean(RHS_valueCopy);
            boolean conjunction = LHS_val && RHS_val;
            String conVal = Boolean.toString(conjunction);
            activeValues.add(conVal);
        }
    }

    @Override
    public void visit(PrintStatementNode node) {
        node.expression.accept(this);
        System.out.println(activeValues.get(0));
        activeValues.clear();
    }

    @Override
    public void visit(RelationalOpNode node) {
        node.LHS.accept(this);
        String LHS_valueCopy = activeValues.get(0);
        activeValues.clear();
        node.RHS.accept(this);
        String RHS_valueCopy;
        if (activeValues.size() == 1){
            RHS_valueCopy = activeValues.get(0);
        }else {
            RHS_valueCopy = activeValues.get(1);
        }

        activeValues.clear();

        if (node.operator.equals("<")){
            float LHS_val = Float.parseFloat(LHS_valueCopy);
            float RHS_val = Float.parseFloat(RHS_valueCopy);
            boolean lessThan = LHS_val < RHS_val;
            String ltVal = Boolean.toString(lessThan);
            activeValues.add(ltVal);
        }else if (node.operator.equals(">")){
            float LHS_val = Float.parseFloat(LHS_valueCopy);
            float RHS_val = Float.parseFloat(RHS_valueCopy);
            boolean greaterThan = LHS_val > RHS_val;
            String gtVal = Boolean.toString(greaterThan);
            activeValues.add(gtVal);
        }else if (node.operator.equals("==")){
            if (LHS_valueCopy.equals("true") || LHS_valueCopy.equals("false")){
                boolean LHS_val = Boolean.parseBoolean(LHS_valueCopy);
                boolean RHS_val = Boolean.parseBoolean(RHS_valueCopy);
                boolean equalTo = LHS_val == RHS_val;
                String etVal = Boolean.toString(equalTo);
                activeValues.add(etVal);
            }else{
                float LHS_val = Float.parseFloat(LHS_valueCopy);
                float RHS_val = Float.parseFloat(RHS_valueCopy);
                boolean equalTo = LHS_val == RHS_val;
                String etVal = Boolean.toString(equalTo);
                activeValues.add(etVal);
            }
        }else if (node.operator.equals("<>")){
            if (LHS_valueCopy.equals("true") || LHS_valueCopy.equals("false")){
                boolean LHS_val = Boolean.parseBoolean(LHS_valueCopy);
                boolean RHS_val = Boolean.parseBoolean(RHS_valueCopy);
                boolean notEqualTo = LHS_val != RHS_val;
                String neVal = Boolean.toString(notEqualTo);
                activeValues.add(neVal);
            }else{
                float LHS_val = Float.parseFloat(LHS_valueCopy);
                float RHS_val = Float.parseFloat(RHS_valueCopy);
                boolean notEqualTo = LHS_val != RHS_val;
                String neVal = Boolean.toString(notEqualTo);
                activeValues.add(neVal);
            }
        }else if (node.operator.equals("<=")){
            float LHS_val = Float.parseFloat(LHS_valueCopy);
            float RHS_val = Float.parseFloat(RHS_valueCopy);
            boolean lessThanEqual = LHS_val <= RHS_val;
            String leVal = Boolean.toString(lessThanEqual);
            activeValues.add(leVal);
        }else {
            float LHS_val = Float.parseFloat(LHS_valueCopy);
            float RHS_val = Float.parseFloat(RHS_valueCopy);
            boolean greaterThanEqual = LHS_val >= RHS_val;
            String geVal = Boolean.toString(greaterThanEqual);
            activeValues.add(geVal);
        }
    }

    @Override
    public void visit(ReturnStatementNode node) {
        node.expressionNode.accept(this);
    }

    @Override
    public void visit(StatementNode node) {
        ///
    }

    @Override
    public void visit(VariableDeclerationNode node) {
        String identifier = node.identifier;
        String type = node.type;
        node.expression.accept(this);
        String value = activeValues.get(0);
        String[] values = {type, value};
        symbolTable.insertIntoActiveScope(identifier,values);
        activeValues.clear();
    }

    @Override
    public void visit(UnaryExpressionNode node) {
        node.expressionNode.accept(this);

        if (activeValues.get(0).equals("true")){
            activeValues.set(0, "false");
        }else{
            activeValues.set(0, "true");
        }
    }

    @Override
    public void visit(WhileStatementNode node) {
        Scope whileScope = new Scope();
        symbolTable.pushActiveScope(whileScope);
        node.expressionNode.accept(this);
        while (activeValues.get(0).equals("true")){
            activeValues.clear();
            node.block.accept(this);
            node.expressionNode.accept(this);
        }
        activeValues.clear();
        symbolTable.popUnActiveScope();
    }

    @Override
    public void visit(ProgramNode node) {
        if (node != null){
            System.out.println();
            System.out.println("Program Output: ");
            System.out.println();
            Scope globalScope = new Scope();
            symbolTable.pushActiveScope(globalScope);

            for(int i = 0; i < node.statements_list.size(); ++i){
                node.statements_list.get(i).accept(this);
            }
        } else{
            System.out.println();
            System.out.println("ERROR: Unable to do Interpreter Execution Pass since an error occurred in the generation of the AST.");
            System.out.println();
        }
    }
}
