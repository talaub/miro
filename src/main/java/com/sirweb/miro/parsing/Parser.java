package com.sirweb.miro.parsing;

import com.sirweb.miro.ast.Element;
import com.sirweb.miro.ast.css.CssStylesheet;
import com.sirweb.miro.ast.miro.MiroBlock;
import com.sirweb.miro.ast.miro.MiroMediaQuery;
import com.sirweb.miro.ast.miro.MiroStatement;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.exceptions.MiroFuncParameterException;
import com.sirweb.miro.exceptions.MiroParserException;
import com.sirweb.miro.exceptions.MiroUnimplementedFuncException;
import com.sirweb.miro.lexer.Token;
import com.sirweb.miro.lexer.TokenType;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.values.Value;
import com.sirweb.miro.parsing.values.miro.*;
import com.sirweb.miro.util.Reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class Parser {
    private Tokenizer tokenizer;
    private MiroStylesheet root;
    private Stack<Element> stack;

    public Parser (Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public Tokenizer tokenizer() {
        return tokenizer;
    }

    public void consumeWhitespaces () {
        while (tokenizer.nextTokenType() == TokenType.WHITESPACE_TOKEN)
            tokenizer.getNext();
    }

    public void consumeNewlines () {
        while (tokenizer.nextTokenType() == TokenType.NEWLINE_TOKEN)
            tokenizer.getNext();
    }

    public void consumeNewlinesAndWhitespaces () {
        while (tokenizer.nextTokenType() == TokenType.WHITESPACE_TOKEN
                || tokenizer.nextTokenType() == TokenType.NEWLINE_TOKEN
                || tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN
                || tokenizer.nextTokenType() == TokenType.MIRO_INDENT_TOKEN)
            tokenizer.getNext();
    }

    public String consume (TokenType expectedType) throws MiroParserException {
        if (expectedType == tokenizer.nextTokenType())
            return tokenizer.getNext().getToken();
        else
            throw new MiroParserException("Unexpected token '"+tokenizer.getNext().getToken() + "' expected " + expectedType);
    }

    public boolean optional (TokenType optionalType) throws MiroParserException {
        if (tokenizer.nextTokenType() == optionalType) {
            consume(optionalType);
            return true;
        }
        return false;
    }

    public MiroValue parseValue () throws MiroParserException, MiroFuncParameterException, MiroUnimplementedFuncException {
        consumeWhitespaces();

        if (tokenizer.nextTokenType() == TokenType.C_R_TOKEN)
            return null;

        MultiValue multiValue = new MultiValue();

        do {

            consumeNewlinesAndWhitespaces();
            optional(TokenType.COMMA_TOKEN);

            consumeNewlinesAndWhitespaces();

            MiroValue parsedValue = null;
            int sizeBefore = multiValue.size();
            consumeWhitespaces();

            if (tokenizer.nextTokenType() == TokenType.MIRO_IDENT_TOKEN) {
                Token token = tokenizer.getNext();
                parsedValue = findSymbol(token.getToken().substring(1));
                if (parsedValue == null)
                    throw new MiroParserException("Unknown variable '" + token.getToken() + "'");
            } else if (tokenizer.nextTokenType() == TokenType.NUMBER_TOKEN
                    || tokenizer.nextTokenType() == TokenType.DIMENSION_TOKEN
                    || tokenizer.nextTokenType() == TokenType.PERCENTAGE_TOKEN) {
                parsedValue = new Numeric(tokenizer.getNext());
            } else if (tokenizer.nextTokenType() == TokenType.STRING_TOKEN) {
                parsedValue = new StringValue(tokenizer.getNext());
            } else if (tokenizer.nextTokenType() == TokenType.IDENT_TOKEN) {
                Token token = tokenizer.getNext();
                if (Color.knowsColor(token.getToken()))
                    parsedValue = new Color(Color.getDefaultColorDictionary().get(token.getToken()));
                else
                    parsedValue = new Ident(token);
            } else if (tokenizer.nextTokenType() == TokenType.O_R_TOKEN) {
                parsedValue = new Calculator(this).eval();
            } else if (tokenizer.nextTokenType() == TokenType.HASH_TOKEN)
                parsedValue = new Color(tokenizer.getNext().getToken());
            else if (tokenizer.nextTokenType() == TokenType.FUNCTION_TOKEN) {
                String functionName = consume(TokenType.FUNCTION_TOKEN);
                functionName = functionName.substring(0, functionName.length() - 1);

                consumeWhitespaces();
                MiroValue parsedParameter = parseValue();
                consumeWhitespaces();
                consume(TokenType.C_R_TOKEN);

                if (!(parsedParameter instanceof MultiValue)) {
                    MultiValue mv = new MultiValue();
                    mv.addValue(parsedParameter);
                    parsedParameter = mv;
                }

                parsedValue = new Function(functionName, (MultiValue) parsedParameter);

            }
            else if (tokenizer.nextTokenType() == TokenType.O_Q_TOKEN) {
                parsedValue = new com.sirweb.miro.parsing.values.miro.List();
                consume(TokenType.O_Q_TOKEN);
                consumeNewlinesAndWhitespaces();
                if (tokenizer.nextTokenType() != TokenType.C_Q_TOKEN) {
                    MiroValue content = parseValue();
                    if (content instanceof MultiValue)
                        for (MiroValue value : ((MultiValue) content).getValues())
                            ((com.sirweb.miro.parsing.values.miro.List) parsedValue).addValue(value);
                    else
                        ((com.sirweb.miro.parsing.values.miro.List) parsedValue).addValue(content);
                }
                consume(TokenType.C_Q_TOKEN);
            }

            if (parsedValue == null)
                throw new MiroParserException("Could not parse value from token '" + tokenizer.getNext().getToken() + "'");

            while (tokenizer.nextTokenType() == TokenType.FUNCTION_TOKEN) {
                String funcName = consume(TokenType.FUNCTION_TOKEN);
                funcName = funcName.substring(1, funcName.length() - 1);
                MiroValue parameterValue = parseValue();
                List<MiroValue> parameters = new ArrayList<>();
                if (parameterValue == null) {}
                else if (parameterValue instanceof MultiValue) {
                    for (int i = 0; i < ((MultiValue) parameterValue).size(); i++)
                        parameters.add(((MultiValue) parameterValue).get(i));
                }
                else {
                    parameters.add(parameterValue);
                }

                consume(TokenType.C_R_TOKEN);
                parsedValue = (MiroValue) parsedValue.callFunc(funcName, parameters);
            }

            multiValue.addValue(parsedValue);

        } while (tokenizer.nextTokenType() == TokenType.COMMA_TOKEN);

        return multiValue.size() > 1 ? multiValue : multiValue.get(0);
    }

    public MiroStylesheet parse () throws MiroException {
        stack = new Stack<>();
        parseStylesheet();
        return root;
    }

    private void parseStylesheet () throws MiroException {
        root = new MiroStylesheet();
        stack.push(root);

        parseBlockContent();
    }

    private void parseScript () throws MiroParserException {
        if (tokenizer.nextTokenType() == TokenType.MIRO_IDENT_TOKEN)
            parseScriptAssignment();
        optional(TokenType.SEMICOLON_TOKEN);
    }

    private MiroValue findSymbol (String symbolName) {
        Stack<Element> tmpStack = (Stack<Element>) stack.clone();
        while (!tmpStack.empty()) {
            if (tmpStack.peek().symbolTable().hasSymbol(symbolName))
                return tmpStack.peek().symbolTable().getSymbol(symbolName);
            tmpStack.pop();
        }
        return null;
    }

    private void parseScriptAssignment () throws MiroParserException {
        String assignIdent = consume(TokenType.MIRO_IDENT_TOKEN).substring(1);
        consumeWhitespaces();

        consume(TokenType.EQUAL_TOKEN);

        consumeWhitespaces();

        MiroValue value = new Calculator(this).eval();

        stack.peek().symbolTable().setSymbol(assignIdent, value);
    }

    private void parseCss () throws MiroException {
        if (tokenizer.nextTokenType() == TokenType.AT_KEYWORD_TOKEN)
            parseAtExpression();
        else if (tokenizer.lineOpensBlock()) {
            if (tokenizer.nextTokenType() == TokenType.MIRO_NESTPROP_TOKEN)
                parseNestProp();
            else
                parseCssBlock();
        }
        else
            parseCssStatement("");
    }

    private void parseCssBlock () throws MiroException {
        String header = "";

        do {
            if (tokenizer.nextTokenType() == TokenType.MIRO_INTERPOLATION_TOKEN) {
                consume(TokenType.MIRO_INTERPOLATION_TOKEN);
                consumeWhitespaces();
                header += new Calculator(this).eval();
                consumeWhitespaces();
                consume(TokenType.C_C_TOKEN);
            }
            else
                header += tokenizer.getNext().getToken();
        } while (tokenizer.nextTokenType() != TokenType.NEWLINE_TOKEN);

        MiroBlock block = new MiroBlock(header);

        stack.peek().addBlock(block);
        stack.push(block);

        consumeNewlines();
        consume(TokenType.MIRO_INDENT_TOKEN);
        parseBlockContent();
        if (tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN)
            consume(TokenType.MIRO_DEDENT_TOKEN);
        else
            consume(TokenType.EOF);

        if (tokenizer.getNextTokenTypeNotWhitespaceOrNewline() != TokenType.MIRO_INDENT_TOKEN)
            stack.pop();
    }

    private void parseBlockContent () throws MiroException {
        while (tokenizer.nextTokenType() != TokenType.EOF
                && tokenizer.nextTokenType() != TokenType.MIRO_DEDENT_TOKEN) {
            consumeWhitespaces();
            consumeNewlines();
            if (tokenizer.nextTokenType() == TokenType.MIRO_IDENT_TOKEN)
                parseScript();
            else
                parseCss();
            consumeWhitespaces();
            consumeNewlines();
        }
    }

    private void parseCssStatement (String prependProperty) throws MiroParserException {
        String property = consume(TokenType.IDENT_TOKEN);
        consumeWhitespaces();
        optional(TokenType.COLON_TOKEN);
        consumeWhitespaces();

        MiroValue value = new Calculator(this).eval();


        consumeWhitespaces();

        boolean important = false;

        if (optional(TokenType.MIRO_EXCLAMATION_TOKEN))
            important = true;
        else if (tokenizer.nextTokenType() == TokenType.MIRO_DEBUG_TOKEN)
            if ("!important".equals(tokenizer.getNext().getToken().toLowerCase()))
                important = true;


        stack.peek().addStatement(new MiroStatement(prependProperty + property, value, important));
        consumeWhitespaces();

        optional(TokenType.SEMICOLON_TOKEN);
        consumeWhitespaces();
        optional(TokenType.NEWLINE_TOKEN);

    }

    private void parseNestProp () throws MiroParserException {
        String nest = consume(TokenType.MIRO_NESTPROP_TOKEN);
        consumeNewlines();
        consume(TokenType.MIRO_INDENT_TOKEN);
        nest = nest.substring(0, nest.length() - 1);

        while (tokenizer.nextTokenType() != TokenType.EOF
                && tokenizer.nextTokenType() != TokenType.MIRO_DEDENT_TOKEN) {
            consumeWhitespaces();
            consumeNewlines();
            if (tokenizer.nextTokenType() == TokenType.MIRO_IDENT_TOKEN)
                parseScript();
            else
                parseCssStatement(nest);
            consumeWhitespaces();
            consumeNewlines();
        }
        consumeNewlines();
        optional(TokenType.MIRO_DEDENT_TOKEN);
    }

    private void parseAtExpression () throws MiroException {
        String tokenString = tokenizer.getNext().getToken().substring(1).toLowerCase();

        switch (tokenString) {
            case "media":
                parseMediaQuery();
                break;
            case "if":
                parseMediaIf();
                break;
            case "use":
                parseUse();
                break;
        }
    }

    private void parseMediaQuery () throws MiroException {
        String mediaString = "";

        while (tokenizer.nextTokenType() != TokenType.NEWLINE_TOKEN
                && tokenizer.nextTokenType() != TokenType.EOF) {
            if (tokenizer.nextTokenType() == TokenType.MIRO_INTERPOLATION_TOKEN) {
                consume(TokenType.MIRO_INTERPOLATION_TOKEN);

                Calculator calculator = new Calculator(this);
                mediaString += calculator.eval().toString();

                consume(TokenType.C_C_TOKEN);
            }
            else {
                mediaString += tokenizer.getNext().getToken();
            }
        }

        MiroBlock block = new MiroMediaQuery(mediaString);

        stack.peek().addBlock(block);
        stack.push(block);

        consumeNewlines();
        consume(TokenType.MIRO_INDENT_TOKEN);
        parseBlockContent();
        if (tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN)
            consume(TokenType.MIRO_DEDENT_TOKEN);
        else
            consume(TokenType.EOF);
        stack.pop();
    }

    private void parseMediaIf () throws MiroException {
        String mediaString = "";
        do {
            consumeWhitespaces();
            mediaString += parseMediaIfArgument();

            consumeWhitespaces();

            if (tokenizer.nextTokenType() == TokenType.COLON_TOKEN) break;

            if (tokenizer.nextTokenType() == TokenType.IDENT_TOKEN) {
                Token currentToken = tokenizer.getNext();
                if ("and".equals(currentToken.getToken()))
                    mediaString += " and ";
                else if ("or".equals(currentToken.getToken()))
                    mediaString += ", ";
                else
                    throw new MiroParserException("Unexpected token " + currentToken + " expected 'and', 'or', '||', '&&'");
            }
            else if(tokenizer.nextTokenType() == TokenType.ARITHMETIC_TOKEN) {

                Token currentToken = tokenizer.getNext();
                if ("&&".equals(currentToken.getToken()))
                    mediaString += " and ";
                else if ("||".equals(currentToken.getToken()))
                    mediaString += ", ";
                else
                    throw new MiroParserException("Unexpected token " + currentToken + " expected 'and', 'or', '||', '&&'");
            }
            else
                throw new MiroParserException("Unexpected token " + tokenizer.getNext().getToken() + " expected 'and', 'or', '||', '&&'");

            consumeWhitespaces();

        } while (tokenizer.nextTokenType() != TokenType.COLON_TOKEN);

        consume(TokenType.COLON_TOKEN);

        MiroBlock block = new MiroMediaQuery(mediaString);

        stack.peek().addBlock(block);
        stack.push(block);

        consumeNewlines();
        consume(TokenType.MIRO_INDENT_TOKEN);
        parseBlockContent();
        if (tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN)
            consume(TokenType.MIRO_DEDENT_TOKEN);
        else
            consume(TokenType.EOF);
        stack.pop();

    }

    private String parseMediaIfArgument () throws MiroException {
        consume(TokenType.O_R_TOKEN);
        consumeWhitespaces();
        String property = consume(TokenType.IDENT_TOKEN);

        consumeWhitespaces();

        String comparisonOperator = null;

        if (tokenizer.nextTokenType() == TokenType.ARITHMETIC_TOKEN)
            comparisonOperator = consume(TokenType.ARITHMETIC_TOKEN);
        else if (tokenizer.nextTokenType() == TokenType.EQUAL_EQUAL_TOKEN)
            comparisonOperator = consume(TokenType.EQUAL_EQUAL_TOKEN);
        else if (tokenizer.nextTokenType() == TokenType.IDENT_TOKEN) {
            comparisonOperator = consume(TokenType.IDENT_TOKEN);
            if ("is".equals(comparisonOperator))
                comparisonOperator = "==";
        }
        else
            throw new MiroParserException("Unknown comparison operator "+tokenizer.getNext().getToken());

        consumeWhitespaces();

        MiroValue value = parseValue();
        consume(TokenType.C_R_TOKEN);

        if ("width".equals(property) || "device-width".equals(property) || "device-height".equals(property) || "color".equals(property) || "color-index".equals(property) || "monochrome".equals(property) || "grid".equals(property)) {
            if (!(value instanceof Numeric))
                throw new MiroParserException("Media-if property '"+property+"' only takes values of type Number");

            if ("<=".equals(comparisonOperator))
                return "(max-"+property+": " + value.toString() + ")";
            else if (">=".equals(comparisonOperator))
                return "(min-"+property+": " + value.toString() + ")";
            else if ("<".equals(comparisonOperator))
                return "(max-"+property+": " + (((Numeric) value).getValue() - 1) + ((Numeric) value).getUnit() + ")";
            else if (">".equals(comparisonOperator))
                return "(min-"+property+": " + (((Numeric) value).getValue() + 1) + ((Numeric) value).getUnit() + ")";
            else if ("==".equals(comparisonOperator))
                return "("+property+": " + value.toString() + ")";
            else
                throw new MiroParserException("Media-if property '"+property+"' cannot deal with comparison operator '"+comparisonOperator+"'");


        }
        else if ("orientation".equals(property) || "light-level".equals(property) || "pointer".equals(property)) {
            if (!(value instanceof Ident))
                throw new MiroException("Media-if property '"+property+"' only takes values of type Ident");

            if ("==".equals(comparisonOperator))
                return "("+property+": " + value.toString() + ")";
            else
                throw new MiroException("Media-if property '"+property+"' cannot deal with comparison operator '"+comparisonOperator+"'");

        }
        else if ("media".equals(property)) {
            if (!(value instanceof Ident))
                throw new MiroException("Media-if property 'media' only takes values of type Ident");

            if ("==".equals(comparisonOperator))
                return value.toString();
            else if ("has".equals(comparisonOperator))
                return "(" + value.toString()  + ")";
            else
                throw new MiroParserException("Media-if property 'media' cannot deal with comparison operator '"+comparisonOperator+"'");

        }
        else
            throw new MiroParserException("Unknown Media-if property '" + property + "'");
    }

    private void parseUse () throws MiroException {
        consumeWhitespaces();

        String filePath;

        filePath = consume(TokenType.STRING_TOKEN);
        filePath = filePath.substring(1, filePath.length() - 1);

        String fileContent = new Reader(filePath).read();

        Tokenizer tokenizer = new Tokenizer(fileContent);
        tokenizer.tokenize();
        Parser miroParser = new Parser(tokenizer);
        MiroStylesheet miroStylesheet = miroParser.parse();

        SymbolTable st = miroStylesheet.symbolTable();

        for (String symbol : st.getSymbols())
            stack.peek().symbolTable().setSymbol(symbol, st.getSymbol(symbol));

        optional(TokenType.SEMICOLON_TOKEN);

    }
}
