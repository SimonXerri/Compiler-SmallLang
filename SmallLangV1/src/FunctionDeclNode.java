
public class FunctionDeclNode extends StatementNode {

    String identifier;
    StatementNode formalParams;
    String type;
    StatementNode block;

    public FunctionDeclNode(String identifier, StatementNode formalParams, String type, StatementNode block){
        this.identifier = identifier;
        this.formalParams = formalParams;
        this.type = type;
        this.block = block;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
