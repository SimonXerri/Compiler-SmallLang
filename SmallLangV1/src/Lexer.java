import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.Stack;
import java.util.List;
import java.util.Arrays;

public class Lexer {

    // Each number represents a state. State 16 is the error state.
    private int[][] transitionTable =   {{1,4,16,4,5,6,10,8,12,13,14,15,16},
                                        {1,16,2,16,16,16,16,16,16,16,16,16,16},
                                        {3,16,16,16,16,16,16,16,16,16,16,16,16},
                                        {3,16,16,16,16,16,16,16,16,16,16,16,16},
                                        {4,4,16,4,16,16,16,16,16,16,16,16,16},
                                        {16,16,16,16,16,16,16,16,16,16,16,16,16},
                                        {16,16,16,16,16,16,7,7,16,16,16,16,16},
                                        {16,16,16,16,16,16,16,16,16,16,16,16,16},
                                        {16,16,16,16,16,16,16,9,16,16,16,16,16},
                                        {16,16,16,16,16,16,16,16,16,16,16,16,16},
                                        {16,16,16,16,16,16,16,11,16,16,16,16,16},
                                        {16,16,16,16,16,16,16,16,16,16,16,16,16},
                                        {16,16,16,16,16,16,16,16,16,16,16,16,16},
                                        {16,16,16,16,16,16,16,16,16,16,16,16,16},
                                        {16,16,16,16,16,16,16,16,16,16,14,16,16},
                                        {16,16,16,16,16,16,16,16,16,16,16,16,16},
                                        {16,16,16,16,16,16,16,16,16,16,16,16,16}};
    private String file_name;       // name of the file containing the SmallLang Syntax
    private String file_content;    // content of the file
    private int pos = 0;            // index of the character being read from the file_content string
    private int pos_line = 0;       // position of token in line
    private int line = 1;           // line number

    public Lexer(String file_name){
        this.file_name = file_name;
        read_file(file_name);
    }

    public void read_file(String file_name){
        String file_content = "";
        try{
            File file = new File(file_name);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while((line = bufferedReader.readLine())!=null)
            {
                // removing single line comments
                int index = line.indexOf("//");
                if(index != -1){
                    int end = line.length();
                    String substring = line.substring(index, end);
                    line = line.replace(substring, "");
                }
                // removing comment blocks
                int index_block = line.indexOf("/*");
                if (index_block != -1){
                    int index_block_end = line.indexOf("*/");
                    if (index_block_end != -1) {
                        continue;
                    }
                    while((line = bufferedReader.readLine())!=null){
                        index_block_end = line.indexOf("*/");
                        if (index_block_end != -1){
                            break;
                        }
                    }
                    continue;
                }
                stringBuffer.append(line);
                stringBuffer.append("\n");
            }
            fileReader.close();
            file_content = stringBuffer.toString();
        }catch (IOException e){
            System.out.println("The Lexer did not find the file passed -> " + file_name);
            e.printStackTrace();
        }

        this.file_content = file_content;
    }

    public Token GetNextToken(){
        Integer[] final_states = new Integer[]{1,3,4,5,6,7,8,9,10,11,12,13,14,15};
        Character[] punctuation = new Character[]{';', ':', ',', '(', ')', '{', '}'};
        Character[] multiplicativeOp = new Character[]{'*', '/'};
        Character[] additiveOp = new Character[]{'+', '-'};

        List<Integer> list_final = Arrays.asList(final_states);
        List<Character> list_punctuation = Arrays.asList(punctuation);
        List<Character> list_multiplicativeOp = Arrays.asList(multiplicativeOp);
        List<Character> list_additiveOp = Arrays.asList(additiveOp);

        String currentWord = "";
        Stack stack = new Stack();
        int state = 0;
        String lexeme = "";
        stack.clear();
        stack.push(-1);  // pushing bad. -1 == bad

        // while state is not equal to Se
        while(state != 16){
            if(this.pos == file_content.length()){
                return new Token("EOF_TOK", "EOF", 0, this.pos, this.line);
            }

            char nextChar = file_content.charAt(this.pos);
            this.pos++;
            this.pos_line++;
            lexeme += nextChar;

            if(list_final.contains(state)){
                stack.clear();
            }

            stack.push(state);
            boolean digit_type = Character.isDigit(nextChar);
            boolean letter_type = Character.isLetter(nextChar);
            int float_type = Character.compare('.', nextChar);
            int underscore_type = Character.compare('_', nextChar);
            int space_type = Character.compare(' ', nextChar);
            int tab_type = Character.compare('\t', nextChar);

            if(digit_type){
                state = transitionTable[state][0];
            }else if (letter_type){
                state = transitionTable[state][1];
            } else if (float_type == 0){
                state = transitionTable[state][2];
            } else if (underscore_type == 0){
                state = transitionTable[state][3];
            } else if (list_punctuation.contains(nextChar)){
                state = transitionTable[state][4];
            } else if (nextChar == '<'){
                state = transitionTable[state][5];
            } else if (nextChar == '>'){
                state = transitionTable[state][6];
            } else if (nextChar == '='){
                state = transitionTable[state][7];
            } else if (list_multiplicativeOp.contains(nextChar)){
                state = transitionTable[state][8];
            } else if (list_additiveOp.contains(nextChar)){
                state = transitionTable[state][9];
            } else if ((space_type == 0 ) || (tab_type == 0)){
                state = transitionTable[state][10];
            } else if (nextChar == '\n'){
                state = transitionTable[state][11];
            } else{
                state = 16;
            }
        }

        while(!list_final.contains(state) && state != -1){
            state = (int)stack.pop();
            if (!lexeme.equals("")){
                lexeme = lexeme.substring(0, lexeme.length()-1);
            }
            pos--;
        }

        if(list_final.contains(state)){
            if (lexeme.charAt(0) == ' '){
                return GetNextToken();
            } else if (lexeme.equals("\n")){
                this.line++;
                this.pos_line = 0;
                return GetNextToken();
            } else if (lexeme.equals(";")){
                return new Token("PUNC_TOK", ";", 0, this.pos_line, this.line);
            } else if (lexeme.equals(":")){
                return new Token("PUNC_TOK", ":", 0, this.pos_line, this.line);
            } else if (lexeme.equals(",")){
                return new Token("PUNC_TOK", ",", 0, this.pos_line, this.line);
            } else if (lexeme.equals("(")){
                return new Token("PUNC_TOK", "(", 0, this.pos_line, this.line);
            } else if (lexeme.equals(")")){
                return new Token("PUNC_TOK", ")", 0, this.pos_line, this.line);
            } else if (lexeme.equals("{")){
                return new Token("PUNC_TOK", "{", 0, this.pos_line, this.line);
            } else if (lexeme.equals("}")){
                return new Token("PUNC_TOK", "}", 0, this.pos_line, this.line);
            } else if (lexeme.equals("*")){
                return new Token("MULTI_OP_TOK", "*", 0, this.pos_line, this.line);
            } else if (lexeme.equals("/")){
                return new Token("MULTI_OP_TOK", "/", 0, this.pos_line, this.line);
            } else if (lexeme.equals("and")){
                return new Token("MULTI_OP_TOK", "and", 0, this.pos_line, this.line);
            } else if (lexeme.equals("+")){
                return new Token("ADD_OP_TOK", "+", 0, this.pos_line, this.line);
            } else if (lexeme.equals("-")){
                return new Token("ADD_OP_TOK", "-", 0, this.pos_line, this.line);
            } else if (lexeme.equals("or")){
                return new Token("ADD_OP_TOK", "or", 0, this.pos_line, this.line);
            }  else if (lexeme.equals("<")){
                return new Token("REL_OP_TOK", "<", 0, this.pos_line, this.line);
            } else if (lexeme.equals(">")){
                return new Token("REL_OP_TOK", ">", 0, this.pos_line, this.line);
            } else if (lexeme.equals("==")){
                return new Token("REL_OP_TOK", "==", 0, this.pos_line, this.line);
            } else if (lexeme.equals("<>")){
                return new Token("REL_OP_TOK", "<>", 0, this.pos_line, this.line);
            } else if (lexeme.equals("<=")){
                return new Token("REL_OP_TOK", "<=", 0, this.pos_line, this.line);
            } else if (lexeme.equals(">=")){
                return new Token("REL_OP_TOK", ">=", 0, this.pos_line, this.line);
            } else if (lexeme.equals("auto")){
                return new Token("AUTO_TOK", "auto", 0, this.pos_line, this.line);
            } else if (lexeme.equals("float")){
                return new Token("TYPE_TOK", "float", 0, this.pos_line, this.line);
            } else if (lexeme.equals("int")){
                return new Token("TYPE_TOK", "int", 0, this.pos_line, this.line);
            } else if (lexeme.equals("bool")){
                return new Token("TYPE_TOK", "bool", 0, this.pos_line, this.line);
            } else if (lexeme.equals("true")){
                return new Token("BOOL_LIT_TOK", "true", 0, this.pos_line, this.line);
            } else if (lexeme.equals("false")){
                return new Token("BOOL_LIT_TOK", "false", 0, this.pos_line, this.line);
            } else if(lexeme.equals("=")){
                return new Token("ASSIGN_TOK", "=", 0, this.pos_line, this.line);
            } else if(lexeme.equals("let")){
                return new Token("LET_TOK", "let", 0, this.pos_line, this.line);
            } else if(lexeme.equals("print")){
                return new Token("PRINT_TOK", "print", 0, this.pos_line, this.line);
            } else if(lexeme.equals("return")){
                return new Token("RETURN_TOK", "return", 0, this.pos_line, this.line);
            } else if(lexeme.equals("if")){
                return new Token("IF_TOK", "if", 0, this.pos_line, this.line);
            } else if(lexeme.equals("else")){
                return new Token("ELSE_TOK", "else", 0, this.pos_line, this.line);
            } else if(lexeme.equals("for")){
                return new Token("FOR_TOK", "for", 0, this.pos_line, this.line);
            } else if(lexeme.equals("while")){
                return new Token("WHILE_TOK", "while", 0, this.pos_line, this.line);
            } else if(lexeme.equals("not")){
                return new Token("NOT_TOK", "not", 0, this.pos_line, this.line);
            } else if(lexeme.equals("ff")){
                return new Token("FF_TOK", "ff", 0, this.pos_line, this.line);
            } else if(Character.isLetter(lexeme.charAt(0)) || lexeme.charAt(0) == '_'){
                return new Token("ID_TOK", lexeme, 0, this.pos_line, this.line);
            } else if(Character.isDigit(lexeme.charAt(0)) && lexeme.contains(".")){
                return new Token("FLOAT_TOK", lexeme, Float.parseFloat(lexeme), this.pos_line, this.line);
            } else if(Character.isDigit(lexeme.charAt(0)) && !lexeme.contains(".")){
                return new Token("INT_TOK", lexeme, Float.parseFloat(lexeme), this.pos_line, this.line);
            }
            return new Token("SYN_ERR_TOK", "Syntax Error", -1, this.pos_line, this.line);
        }else{
            return new Token("SYN_ERR_TOK", "Syntax Error", -1, this.pos_line, this.line);
        }
    }
}
