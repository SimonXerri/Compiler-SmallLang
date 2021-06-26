import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SymbolTable {

    Stack<Scope> activeScopes = new Stack<>();
    List<String[]> declaredFunctions = new ArrayList<>(3);  //stores the function data.
    List<StatementNode> functionBlocks = new ArrayList<>(3);    //stores the function blocks.

    // Matrix to check the return type when Multiplication, Division, Addition, and Subtraction is done.
    String[][] typeTable_MDAS = {{"FLOAT_TOK", "FLOAT_TOK", null},  //float
                                 {"FLOAT_TOK", "INT_TOK", null},    //int
                                 {null, null, null}};               //bool

    // Matrix to check the return type when AND or OR is done.
    String[][] typeTable_AO = {{null, null, null},              //float
                               {null, null, null},              //int
                               {null, null, "BOOL_LIT_TOK"}};   //bool

    // Matrix to check the return type when '==' or '<>' is done.
    String[][] typeTable_EN = {{"BOOL_LIT_TOK", "BOOL_LIT_TOK", null}, //float
                               {"BOOL_LIT_TOK", "BOOL_LIT_TOK", null}, //int
                               {null, null, "BOOL_LIT_TOK"}};          //bool

    // Matrix to check the return type when '<', '>', '<=', or '>=' is done
    String[][] typeTable_REL = {{"BOOL_LIT_TOK", "BOOL_LIT_TOK", null}, //float
                                {"BOOL_LIT_TOK", "BOOL_LIT_TOK", null}, //int
                                {null, null, null}};                    //bool

    public void pushActiveScope(Scope scope){
        activeScopes.push(scope);
    }

    public void popUnActiveScope(){
        activeScopes.pop();
    }

    public void insertIntoActiveScope(String identifier, String[] values){
        int size = activeScopes.size();
        activeScopes.get(size-1).scope.put(identifier, values);
    }

    public boolean lookUpDeclaration(String identifier){    //Function used to check that there aren't repeated declarations in the same scope
        int size = activeScopes.size();

        if (size != 0){
            if (activeScopes.get(size-1).scope.containsKey(identifier)){
                System.out.println();
                System.out.println("ERROR: The identifier " + identifier + " is already defined in the scope.");
                System.out.println();
                return false;
            }
        }
        return true;
    }

    public boolean lookUpVariable(String identifier){     //Function used to check that any variables used to assign are already declared
        int size = activeScopes.size();
        boolean found = false;
        for (int i = 0; i < size; ++i){
            if (activeScopes.get(i).scope.containsKey(identifier)){
                found = true ;
            }
        }

        if (!found){
            System.out.println();
            System.out.println("ERROR: The identifier " + identifier + " has not been defined yet.");
            System.out.println();
        }
        return found;
    }

    public String[] lookUpType(String identifier){           //Function used to find the type and values of the variables
        int size = activeScopes.size();
        for (int i = size-1; i >= 0; --i){
            if (activeScopes.get(i).scope.containsKey(identifier)){
                return activeScopes.get(i).scope.get(identifier);
            }
        }
        return null;
    }

    // 0 = float, 1 = int, 2 = boolean
    public String lookUpReturnType(int LHS, int RHS, String typeTable){     //Function used to find the return type when an operation is carried out.
        if (typeTable.equals("typeTable_MDAS")){
            return typeTable_MDAS[LHS][RHS];
        }else if (typeTable.equals("typeTable_AO")){
            return typeTable_AO[LHS][RHS];
        }else if (typeTable.equals("typeTable_EN")){
            return typeTable_EN[LHS][RHS];
        }else{
            return typeTable_REL[LHS][RHS];
        }
    }

}
