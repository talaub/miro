package com.sirweb.miro.parsing;

import com.sirweb.miro.ast.Element;
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

    public String consume (TokenType expectedType) throws MiroParserException {
        if (expectedType == tokenizer.nextTokenType())
            return tokenizer.getNext().getToken();
        else
            throw new MiroParserException("Unexpected token '"+tokenizer.getNext().getToken() + "' expected " + expectedType);
    }

    public void optional (TokenType optionalType) throws MiroParserException {
        if (tokenizer.nextTokenType() == optionalType)
            consume(optionalType);
    }

    public MiroValue parseValue () throws MiroParserException, MiroFuncParameterException, MiroUnimplementedFuncException {
        consumeWhitespaces();

        if (tokenizer.nextTokenType() == TokenType.C_R_TOKEN)
            return null;

        MultiValue multiValue = new MultiValue();

        do {
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

            consumeWhitespaces();

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

        stack.peek().addStatement(new MiroStatement(prependProperty + property, value));

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
}
