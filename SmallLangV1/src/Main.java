public class Main {

    public static void main(String[] args) {
        String file_name = "test_program.txt";
        Lexer lexer = new Lexer(file_name);
        Parser parser = new Parser(lexer);
        Node node = parser.parse();
        ASTXMLGenerationVisitor astxmlGenerationVisitor = new ASTXMLGenerationVisitor();
        node.accept(astxmlGenerationVisitor);
        SemanticAnalysisVisitor semanticAnalysisVisitor = new SemanticAnalysisVisitor();
        node.accept(semanticAnalysisVisitor);
        InterpreterExecutionVisitor interpreterExecutionVisitor = new InterpreterExecutionVisitor();
        node.accept(interpreterExecutionVisitor);
    }
}
