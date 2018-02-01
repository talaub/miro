package com.sirweb.miro.lexer;

/**
 * The tokenizer for the Miro CSS proprocessor language
 * @author Tamino Laub
 */

import com.sirweb.miro.exceptions.MiroIndentationException;
import com.sirweb.miro.exceptions.MiroTokenizerException;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

    private List<Token> tokenstream;

    private String str;

    private int position = 0;

    private List<TokenData> tokenDatas;

    private String hexDigit = "([0-9]|[a-f]|[A-F])";
    private String escape = "(\\\\"+hexDigit+"){1,6}(\\ |\\t)?";

    private String ident = "((-)?("+escape+"|([a-zA-Z_\\.]))("+escape+"|([a-zA-Z0-9_\\-]))*)";

    private String string = "((\".*\")|(\'.*\'))";
    private String whitespace = "(\\ )";
    private String number = "((\\+|\\-)?((([0-9]*(\\.)([0-9]+))|([0-9]+))((e|E)(\\+|\\-)?[0-9]+)?))";
    private String newline = "(\\n|\\r\\n|\\r|\\f)";

    private Tokenizer (List<Token> tokenstream) {
        this.tokenstream = tokenstream;
        this.str = "";
    }

    public Tokenizer(String str) {
        this.str = str;
        tokenstream = new ArrayList<>();

        this.tokenDatas = new ArrayList<TokenData>();

        //tokenDatas.add(new TokenData(Pattern.compile("^()"), TokenType.));


        tokenDatas.add(new TokenData(Pattern.compile("^((/\\*(.|"+newline+")*\\*/)|(//.*))"), TokenType.COMMENT_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(url\\(("+whitespace+")*"+string+"("+whitespace+")*\\))"), TokenType.URL_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^("+string+")"), TokenType.STRING_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(,)"), TokenType.COMMA_TOKEN));


        tokenDatas.add(new TokenData(Pattern.compile("^(\\?"+ident+")"), TokenType.MIRO_CONDITIONAL_TOKEN));


        tokenDatas.add(new TokenData(Pattern.compile("^(\\!"+ident+")"), TokenType.MIRO_DEBUG_TOKEN));


        tokenDatas.add(new TokenData(Pattern.compile("^(\\&\\&)"), TokenType.ARITHMETIC_TOKEN));
        tokenDatas.add(new TokenData(Pattern.compile("^(\\|\\|)"), TokenType.ARITHMETIC_TOKEN));



        tokenDatas.add(new TokenData(Pattern.compile("^(\\&)"), TokenType.MIRO_AND_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(\\$\\{)"), TokenType.MIRO_INTERPOLATION_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(\\$"+ident+"\\()"), TokenType.MIRO_MIXIN_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(\\$"+ident+")"), TokenType.MIRO_IDENT_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^("+ident+")(\\-){2}"), TokenType.MIRO_NESTPROP_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(\\-\\>)"), TokenType.MIRO_ARROW_TOKEN));



        tokenDatas.add(new TokenData(Pattern.compile("^(<=)"), TokenType.ARITHMETIC_TOKEN));
        tokenDatas.add(new TokenData(Pattern.compile("^(>=)"), TokenType.ARITHMETIC_TOKEN));
        tokenDatas.add(new TokenData(Pattern.compile("^(!=)"), TokenType.ARITHMETIC_TOKEN));
        tokenDatas.add(new TokenData(Pattern.compile("^(>)"), TokenType.ARITHMETIC_TOKEN));
        tokenDatas.add(new TokenData(Pattern.compile("^(<)"), TokenType.ARITHMETIC_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(==)"), TokenType.EQUAL_EQUAL_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(~=)"), TokenType.INCLUDE_MATCH_TOKEN));
        tokenDatas.add(new TokenData(Pattern.compile("^(\\|=)"), TokenType.DASH_MATCH_TOKEN));
        tokenDatas.add(new TokenData(Pattern.compile("^(\\^=)"), TokenType.PREFIX_MATCH_TOKEN));
        tokenDatas.add(new TokenData(Pattern.compile("^(\\$=)"), TokenType.SUFFIX_MATCH_TOKEN));
        tokenDatas.add(new TokenData(Pattern.compile("^(\\*=)"), TokenType.SUBSTRING_MATCH_TOKEN));


        //tokenDatas.add(new TokenData(Pattern.compile("^("+newline+")"), TokenType.NEWLINE_TOKEN));

        //tokenDatas.add(new TokenData(Pattern.compile("^(\\t|((\\ ){4}))"), TokenType.MIRO_INDENT_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^("+whitespace+")"), TokenType.WHITESPACE_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^("+ident+"\\()"), TokenType.FUNCTION_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(\\$(_)?[a-zA-Z][a-zA-Z0-9]*)"), TokenType.MIRO_IDENT_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(@"+ident+")"), TokenType.AT_KEYWORD_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(#("+escape+"|([a-zA-Z0-9_\\-]))+)"), TokenType.HASH_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^("+number+"%)"), TokenType.PERCENTAGE_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^("+number+"(px|pX|Px|PX|em|EM|eM|Em|rem|REM|Rem|REm|rEM|rEm|reM|rEm|vw|VW|vW|Vw|vh|VH|vH|Vh|deg|Deg|DEg|DEG|dEG|deG|s|S|ms|MS|mS|Ms|pt|PT|pT|Pt|ex|Ex|eX|EX|cH|Ch|cH|CH|vmin|vmax|cm|CM|Cm|cM|in|In|iN|IN|mm|MM|mM|Mm|pc|Pc|pC|PC))"), TokenType.DIMENSION_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^("+number+")"), TokenType.NUMBER_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^("+ident+")"), TokenType.IDENT_TOKEN));


        tokenDatas.add(new TokenData(Pattern.compile("^(\\.)"), TokenType.MIRO_DOT_TOKEN));




        tokenDatas.add(new TokenData(Pattern.compile("^(\\()"), TokenType.O_R_TOKEN));
        tokenDatas.add(new TokenData(Pattern.compile("^(\\))"), TokenType.C_R_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(\\[)"), TokenType.O_Q_TOKEN));
        tokenDatas.add(new TokenData(Pattern.compile("^(\\])"), TokenType.C_Q_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(\\{)"), TokenType.O_C_TOKEN));
        tokenDatas.add(new TokenData(Pattern.compile("^(\\})"), TokenType.C_C_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(\\:)"), TokenType.COLON_TOKEN));
        tokenDatas.add(new TokenData(Pattern.compile("^(\\;)"), TokenType.SEMICOLON_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(\\=)"), TokenType.EQUAL_TOKEN));


        tokenDatas.add(new TokenData(Pattern.compile("^(\\~)"), TokenType.TILDE_TOKEN));

        tokenDatas.add(new TokenData(Pattern.compile("^(\\!)"), TokenType.MIRO_EXCLAMATION_TOKEN));

        for (String t : new String[] { "\\+", "\\-", "\\*", "/", "<", ">"}) {
            tokenDatas.add(new TokenData(Pattern.compile("^(" + t + ")"), TokenType.ARITHMETIC_TOKEN));
        }
    }

    public void tokenize () throws MiroIndentationException, MiroTokenizerException {
        int level = 0;

        while (!str.isEmpty()) {
            int beforeTokenCount = tokenstream.size();
            if (str.charAt(0) == '\n') {
                tokenstream.add(new Token("\n", TokenType.NEWLINE_TOKEN));
                str = str.substring(1);
                if (str.isEmpty())
                    break;
                int n = 0;
                while (str.charAt(0) == ' ') {
                    n++;
                    str = str.substring(1);
                }
                if (n % 4 != 0)
                    throw new MiroIndentationException();
                n /= 4;
                if (n > level) {
                    tokenstream.add(new Token("", TokenType.MIRO_INDENT_TOKEN));
                    level++;
                }
                while (n < level) {
                    tokenstream.add(new Token("", TokenType.MIRO_DEDENT_TOKEN));
                    level--;
                    if (level < n)
                        throw new MiroIndentationException();
                }
            }

            for (TokenData data : tokenDatas) {
                Matcher matcher = data.getPattern().matcher(str);

                if (matcher.find()) {

                    String token = matcher.group();

                    // String tokens have to be retokenized because they can be too large
                    if (data.getType() == TokenType.STRING_TOKEN) {
                        // Get what sign will end the string ('/")
                        char delimiter = token.charAt(0);
                        String stringToken = "" + delimiter;
                        boolean closed = false;
                        // Iterate until the delimiter was found
                        for (int i = 1; i < token.length(); i++) {
                            stringToken += token.charAt(i);
                            // If the delimiter was found, return the String as a StringToken
                            if (delimiter == token.charAt(i)) {
                                str = str.substring(i+1);
                                tokenstream.add(new Token(stringToken, data.getType()));
                                closed = true;
                                break;
                            }
                        }

                        // If the delimiter has not been found in the entire String, the String never was closed.
                        if (!closed)
                            throw new MiroTokenizerException("String opened but never closed");
                        else break;
                    }
                    else
                        str = matcher.replaceFirst("");

                    tokenstream.add(new Token(token, data.getType()));
                    break;

                }
            }

            if (beforeTokenCount == tokenstream.size())
                throw new MiroTokenizerException("Could not recognize token");

        }
    }

    public Token getNext () {

        return position < tokenstream.size() ? tokenstream.get(position++) : new Token("", TokenType.EOF);
    }

    public TokenType nextTokenType() {
        return position < tokenstream.size() ? tokenstream.get(position).getType() : TokenType.EOF;
    }

    public boolean lineOpensBlock () throws MiroTokenizerException, MiroIndentationException {
        Tokenizer tokenizer = this.clone();
        tokenizer.tokenize();

        while (tokenizer.nextTokenType() != TokenType.NEWLINE_TOKEN) {
            if (tokenizer.nextTokenType() == TokenType.EOF)
                return false;
            tokenizer.getNext();
        }
        tokenizer.getNext();
        return tokenizer.nextTokenType() == TokenType.MIRO_INDENT_TOKEN;

    }

    public TokenType getNextTokenTypeNotWhitespaceOrNewline () throws MiroTokenizerException, MiroIndentationException {
        Tokenizer tokenizer = this.clone();
        tokenizer.tokenize();
        while (tokenizer.nextTokenType() == TokenType.NEWLINE_TOKEN
                || tokenizer.nextTokenType() == TokenType.WHITESPACE_TOKEN)
            tokenizer.getNext();
        return tokenizer.nextTokenType();
    }

    public Tokenizer clone () {
        return new Tokenizer(tokenstream.subList(position, tokenstream.size()));
    }
}
