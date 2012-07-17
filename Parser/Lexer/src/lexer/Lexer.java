package lexer;


/**
 *  The Lexer class is responsible for scanning the source file
 *  which is a stream of characters and returning a stream of 
 *  tokens; each token object will contain the string (or access
 *  to the string) that describes the token along with an
 *  indication of its location in the source program to be used
 *  for error reporting; we are tracking line numbers; white spaces
 *  are space, tab, newlines
 */
public class Lexer {
    static String lineCode=""; //will contain code from simple.x
    private boolean atEOF = false;
    private char ch;     // next character to process
    private SourceReader source;
    // positions in line of current token
    private int startPosition, endPosition;

    public Lexer(String sourceFile) throws Exception {
        new TokenType();  // init token table
        source = new SourceReader(sourceFile);
        ch = source.read();
    }

    
    public static void main(String args[]) {
        
        if(args.length > 0){ //protects against out of bounds error
        Token tok;
        
        try {
            Lexer lex = new Lexer(args[0]);
            java.lang.Integer i;//= new java.lang.Integer(0)//java.lang.Integer(1); //aditional line traking
            int iVal=0;
            while (true) {
                tok = lex.nextToken();
                tok.print();
                
                i= tok.getLineNumber();
                //concatenating code into static string, must be done because instance of Lexer expeires outside of
                //stupid try catch statement
                if(i > iVal){
                    iVal= i;
                    lineCode+= i.toString()+". " + lex.getListElement(i-1)+ "\n"; 
                
                }
            }
            
                                  
            
        } catch (Exception e) {
            
//            for(int i=0; i < lex.getListSize(); i++){
//                System.out.println((i+1)+ lex.getListElement(i));
//            
//            }
        }
        
        System.out.println();
        System.out.println(lineCode);
        }
        
        
    }

    /**
     *  newIdTokens are either ids or reserved words; new id's will be inserted
     *  in the symbol table with an indication that they are id's
     *  @param id is the String just scanned - it's either an id or reserved word
     *  @param startPosition is the column in the source file where the token begins
     *  @param endPosition is the column in the source file where the token ends
     *  @return the Token; either an id or one for the reserved words
     */
    public Token newIdToken(String id, int startPosition, int endPosition) {
        return new Token(startPosition, endPosition, source.getLineno(),
                Symbol.symbol(id, Tokens.Identifier));
    }

    /**
     *  number tokens are inserted in the symbol table; we don't convert the 
     *  numeric strings to numbers until we load the bytecodes for interpreting;
     *  this ensures that any machine numeric dependencies are deferred
     *  until we actually run the program; i.e. the numeric constraints of the
     *  hardware used to compile the source program are not used
     *  @param number is the int String just scanned
     *  @param startPosition is the column in the source file where the int begins
     *  @param endPosition is the column in the source file where the int ends
     *  @return the int Token
     */
    public Token newNumberToken(String number, int startPosition, int endPosition) {
        return new Token(startPosition, endPosition, source.getLineno(),
                Symbol.symbol(number, Tokens.INTeger));
    }

    /**
     *  build the token for operators (+ -) or separators (parens, braces)
     *  filter out comments which begin with two slashes
     *  @param s is the String representing the token
     *  @param startPosition is the column in the source file where the token begins
     *  @param endPosition is the column in the source file where the token ends
     *  @return the Token just found
     */
    public Token makeToken(String s, int startPosition, int endPosition) {
        if (s.equals("//")) {  // filter comment
            try {
                int oldLine = source.getLineno(); //make note of commented line #
                do {                              //since the rest of line is commented...
                    ch = source.read();
                } while (oldLine == source.getLineno());//keep reading chars from commented line untel at next line number
            } catch (Exception e) {
                atEOF = true;
            }
            return nextToken();
        }
        Symbol sym = Symbol.symbol(s, Tokens.BogusToken); // be sure it's a valid token
        if (sym == null) {
            System.out.println("******** illegal character: " + s);
            atEOF = true;
            return nextToken();  //because atEOF==true, this call exits stuff out.
        }
        return new Token(startPosition, endPosition, source.getLineno(), sym);
    }

    /**
     *  @return the next Token found in the source file
     */
    public Token nextToken() { // ch is always the next char to process
        if (atEOF) {
            if (source != null) {
                source.close();
                source = null;
            }
            return null;
        }
        try {
            while (Character.isWhitespace(ch)) {  // scan past whitespace
                ch = source.read();
            }
        } catch (Exception e) {
            atEOF = true;
            return nextToken();
        }
        startPosition = source.getPosition();  //upon reading whitespace. Orient oneself with positions
        endPosition = startPosition - 1;

        //***CHECKS FOR ID************************************
        if (Character.isJavaIdentifierStart(ch)) {
            // return tokens for ids and reserved words
            String id = "";
            try {
                do {
                    endPosition++;
                    id += ch;
                    ch = source.read();                      //while isJavaIdentifier, read and concatenate ids & reserved words
                } while (Character.isJavaIdentifierPart(ch));
            } catch (Exception e) {
                atEOF = true;
            }
            return newIdToken(id, startPosition, endPosition);  //calls & returns newNumberToken
        }
        //***CHECKS FOR INT
        if (Character.isDigit(ch)) {
            // return number tokens
            String number = "";
            try {
                do {
                    endPosition++;
                    number += ch;
                    ch = source.read();          // <------
                } while (Character.isDigit(ch)); //read while is digit & concatenate
            } catch (Exception e) {
                atEOF = true;
            }
            return newNumberToken(number, startPosition, endPosition);  //calls & returns newNumberToken method
        }

        // At this point the only tokens to check for are one or two
        // characters; we must also check for comments that begin with
        // 2 slashes
        String charOld = "" + ch; //charOld is incase the next charactater doesn't get returned from symbol as valid
        String op = charOld;
        Symbol sym;
        try {
            endPosition++;
            ch = source.read();
            op += ch;
            // check if valid 2 char operator; if it's not in the symbol
            // table then don't insert it since we really have a one char
            // token
            sym = Symbol.symbol(op, Tokens.BogusToken);
            if (sym == null) {  // it must be a one char token
                return makeToken(charOld, startPosition, endPosition);  //makes a one char token
            }
            endPosition++;//increment since we've determined its two char
            ch = source.read();  //for whitespace?
            return makeToken(op, startPosition, endPosition); //makes two char token provided not one char token
        } catch (Exception e) {
        }
        atEOF = true;
        if (startPosition == endPosition) {
            op = charOld;
        }
        return makeToken(op, startPosition, endPosition);
    }
    
    //********************************************
    public String getListElement(int element){
        return source.getListElement(element);
    }
    
//    public int getListSize(){
//        return source.getListSize();
//    }
}