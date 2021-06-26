# Compiler-SmallLang

Designed and implemented a compiler made up of a lexer, parser, and interpreter in **Java** for an expression based, strongly typed programming language called SmallLang. 
The Compiler carries support for semantic analysis and smart detection of incorrect code handling. 

**Below is the EBNF rules for SmallLang:**

〈Letter 〉 ::= [A-Za-z]

〈Digit〉 ::= [0-9]

〈Type〉 ::= ‘float’ | ‘int’ | ‘bool’

〈Auto〉 ::= ‘auto’

〈BooleanLiteral〉 ::= ‘true’ | ‘false’

〈IntegerLiteral〉 ::= 〈Digit〉 { 〈Digit〉 }

〈FloatLiteral〉 ::= 〈Digit〉 { 〈Digit〉 } ‘.’ 〈Digit〉 { 〈Digit〉 }

〈Literal〉 ::= 〈BooleanLiteral〉| 〈IntegerLiteral〉| 〈FloatLiteral〉
      
〈Identifier 〉 ::= ( ‘ ’ | 〈Letter 〉 ) { ‘ ’ | 〈Letter 〉 | 〈Digit〉 }

〈MultiplicativeOp〉 ::= ‘*’ | ‘/’ | ‘and’

〈AdditiveOp〉 ::= ‘+’ | ‘-’ | ‘or’

〈RelationalOp〉 ::= ‘<’ | ‘>’ | ‘==’ | ‘<>’ | ‘<=’ | ‘>=’

〈ActualParams〉 ::= 〈Expression〉 { ‘,’ 〈Expression〉 }

〈FunctionCall〉 ::= 〈Identifier 〉 ‘(’ [ 〈ActualParams〉 ] ‘)’

〈SubExpression〉 ::= ‘(’ 〈Expression〉 ‘)’

〈Unary〉 ::= ( ‘-’ | ‘not’ ) 〈Expression〉

〈Factor 〉 ::= 〈Literal〉| 〈Identifier 〉| 〈FunctionCall〉| 〈SubExpression〉| 〈Unary〉
                              
〈Term〉 ::= 〈Factor 〉 { 〈MultiplicativeOp〉 〈Factor 〉 }

〈SimpleExpression〉 ::= 〈Term〉 { 〈AdditiveOp〉 〈Term〉 }

〈Expression〉 ::= 〈SimpleExpression〉 { 〈RelationalOp〉 〈SimpleExpression〉 }

〈Assignment〉 ::= 〈Identifier 〉 ‘=’ 〈Expression〉

〈VariableDecl〉 ::= ‘let’ 〈Identifier 〉 ‘:’ ( 〈Type〉 | 〈Auto〉 ) ‘=’ 〈Expression〉

〈PrintStatement〉 ::= ‘print’ 〈Expression〉

〈RtrnStatement〉 ::= ‘return’ 〈Expression〉

〈IfStatement〉 ::= ‘if’ ‘(’ 〈Expression〉 ‘)’ 〈Block〉 [ ‘else’ 〈Block〉 ]

〈ForStatement〉 ::= ‘for’ ‘(’ [ 〈VariableDecl〉 ] ’;’ 〈Expression〉 ’;’ [ 〈Assignment〉 ] ‘)’ 〈Block〉

〈WhileStatement〉 ::= ‘while’ ‘(’ 〈Expression〉 ‘)’ 〈Block〉

〈FormalParam〉 ::= 〈Identifier 〉 ‘:’ 〈Type〉

〈FormalParams〉 ::= 〈FormalParam〉 { ‘,’ 〈FormalParam〉 }

〈FunctionDecl〉 ::= ‘ff’ 〈Identifier 〉 ‘(’ [ 〈FormalParams〉 ] ‘)’ ‘:’ ( 〈Type〉 | 〈Auto〉 ) 〈Block〉

〈Statement〉 ::= 〈VariableDecl〉 ‘;’ | 〈Assignment〉 ‘;’ | 〈PrintStatement〉 ‘;’ | 〈IfStatement〉| 〈ForStatement〉| 〈WhileStatement〉| 〈RtrnStatement〉 ‘;’ | 〈FunctionDecl〉| 〈Block〉               
                  
〈Block〉 ::= ‘{’ { 〈Statement〉 } ‘}’

〈Program〉 ::= { 〈Statement〉 }

  
  
