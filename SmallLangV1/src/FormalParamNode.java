public class FormalParamNode extends StatementNode {

    String identifier;
    String type;

    public FormalParamNode(String identifier, String type){
        this.identifier = identifier;
        this.type = type;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
