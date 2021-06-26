public class Token {

    private String type;
    private String characters;
    private float value;
    private int pos;
    private int line;

    public Token(String type, String characters, float value, int pos, int line){
        this.type = type;
        this.characters = characters;
        this.value = value;
        this.pos = pos;
        this.line = line;
    }

    public String getType() {
        return this.type;
    }

    public String getCharacters() {
        return this.characters;
    }

    public float getValue() {
        return this.value;
    }

    public int getPos() {
        return this.pos;
    }

    public int getLine() {
        return this.line;
    }

    public void printTokenData(){
        System.out.println("=======================================");
        System.out.println("Token Type: " + this.type);
        System.out.println("Token Characters: " + this.characters);
        System.out.println("Token Value: " + this.value);
        System.out.println("Token Position: " + this.pos);
        System.out.println("Token Line: " + this.line);
        System.out.println("=======================================");
    }
}
