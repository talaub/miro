package com.sirweb.miro.parsing;

import com.sirweb.miro.ast.Block;
import com.sirweb.miro.ast.Element;
import com.sirweb.miro.ast.ImportRule;
import com.sirweb.miro.ast.Statement;
import com.sirweb.miro.ast.miro.*;
import com.sirweb.miro.exceptions.*;
import com.sirweb.miro.lexer.Token;
import com.sirweb.miro.lexer.TokenType;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.values.Unit;
import com.sirweb.miro.parsing.values.miro.*;
import com.sirweb.miro.util.Reader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Parser {
    private Tokenizer tokenizer;
    private MiroStylesheet root;
    private Stack<Element> stack;
    private SymbolTable globals;
    private boolean inCollection = false;
    private String filePath = "";

    public Parser (Tokenizer tokenizer) { this(tokenizer, "/"); }

    public Parser (Tokenizer tokenizer, String filePath) {
        this.tokenizer = tokenizer;
        this.globals = new SymbolTable();
        this.filePath = filePath;
    }

    public void setGlobal (String name, MiroValue value) {
        globals.setSymbol(name, value);
    }

    public void setGlobal (MiroMixin mixin) {
        globals.addMixin(mixin);
    }

    public SymbolTable getFullSymbolTable () {
        SymbolTable sm = new SymbolTable();

        Stack<Element> tmpStack = (Stack<Element>) stack.clone();
        while (!tmpStack.empty()) {
            Element element = tmpStack.pop();

            for (String key : element.symbolTable().getSymbols()) {
                if (!sm.hasSymbol(key))
                    sm.setSymbol(key, element.symbolTable().getSymbol(key));
            }
        }
        return sm;
    }

    public Tokenizer tokenizer() {
        return tokenizer;
    }

    public void consumeWhitespaces () {
        while (tokenizer.nextTokenType() == TokenType.WHITESPACE_TOKEN)
            tokenizer.getNext();
    }

    public void consumeBlock () {
        int indents = 0;
        while (true) {
            if (tokenizer.nextTokenType() == TokenType.MIRO_INDENT_TOKEN)
                indents++;
            else if (tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN) {
                if (indents == 0)
                    return;
                else
                    indents--;
            }
            else if (tokenizer.nextTokenType() == TokenType.EOF)
                return;
            tokenizer.getNext();
        }
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

    public MiroValue parseValue () throws MiroException {
        return parseValue(false);
    }

    public MiroValue parseValue (boolean quitAtComma) throws MiroException {
        consumeWhitespaces();

        if (tokenizer.nextTokenType() == TokenType.C_R_TOKEN)
            return null;

        MultiValue multiValue = new MultiValue();

        TokenType delimiter = null;

        do {
            consumeWhitespaces();consumeNewlines();
            if (!inCollection && tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN)
                break;

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
                else if ("TRUE".equals(token.getToken()) || "FALSE".equals(token.getToken()))
                    parsedValue = new Bool("TRUE".equals(token.getToken()) ? true : false);
                else
                    parsedValue = new Ident(token);
            } else if (tokenizer.nextTokenType() == TokenType.O_R_TOKEN) {
                parsedValue = new Calculator(this, true).eval();
            } else if (tokenizer.nextTokenType() == TokenType.HASH_TOKEN)
                parsedValue = new Color(tokenizer.getNext().getToken());
            else if (tokenizer.nextTokenType() == TokenType.URL_TOKEN)
                parsedValue = new Url(tokenizer.getNext());
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

                if ("rgb".equals(((Function) parsedValue).getName())) {
                    for (MiroValue val : ((MultiValue) parsedParameter).getValues())
                        if (!(val instanceof Numeric))
                            throw new MiroParserException("Cannot create Color from " + val.getClass().getSimpleName());
                    parsedValue = new Color((Numeric) ((MultiValue) parsedParameter).get(0), (Numeric) ((MultiValue) parsedParameter).get(1), (Numeric) ((MultiValue) parsedParameter).get(2), new Numeric(255, Unit.NONE));
                }
                else if ("rgba".equals(((Function) parsedValue).getName())) {
                    for (MiroValue val : ((MultiValue) parsedParameter).getValues())
                        if (!(val instanceof Numeric))
                            throw new MiroParserException("Cannot create Color from " + val.getClass().getSimpleName());
                    parsedValue = new Color((Numeric) ((MultiValue) parsedParameter).get(0), (Numeric) ((MultiValue) parsedParameter).get(1), (Numeric) ((MultiValue) parsedParameter).get(2), (Numeric) ((MultiValue) parsedParameter).get(3));
                }
            }
            else if (tokenizer.nextTokenType() == TokenType.O_Q_TOKEN) {
                parsedValue = new com.sirweb.miro.parsing.values.miro.List();
                inCollection = true;
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
                consumeNewlinesAndWhitespaces();
                consume(TokenType.C_Q_TOKEN);
                inCollection = false;
            }
            else if (tokenizer.nextTokenType() == TokenType.O_C_TOKEN) {
                parsedValue = new Dictionary();

                consume(TokenType.O_C_TOKEN);
                inCollection = true;
                while (tokenizer.nextTokenType() != TokenType.C_C_TOKEN) {
                    consumeNewlinesAndWhitespaces();
                    String key = consume(TokenType.IDENT_TOKEN);
                    consumeNewlinesAndWhitespaces();
                    consume(TokenType.COLON_TOKEN);
                    consumeNewlinesAndWhitespaces();
                    MiroValue value = parseValue(true);
                    consumeNewlinesAndWhitespaces();
                    optional(TokenType.COMMA_TOKEN);
                    consumeNewlinesAndWhitespaces();
                    ((Dictionary) parsedValue).setValue(key, value);
                }
                consume(TokenType.C_C_TOKEN);
                inCollection = false;
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

            if (delimiter == null) {
                consumeWhitespaces();
                if (tokenizer.nextTokenType() == TokenType.STRING_TOKEN
                        || tokenizer.nextTokenType() == TokenType.IDENT_TOKEN
                        || tokenizer.nextTokenType() == TokenType.NUMBER_TOKEN
                        || tokenizer.nextTokenType() == TokenType.DIMENSION_TOKEN
                        || tokenizer.nextTokenType() == TokenType.MIRO_IDENT_TOKEN
                        || tokenizer.nextTokenType() == TokenType.O_C_TOKEN
                        || tokenizer.nextTokenType() == TokenType.O_R_TOKEN
                        || tokenizer.nextTokenType() == TokenType.O_Q_TOKEN
                        || tokenizer.nextTokenType() == TokenType.URL_TOKEN
                        || tokenizer.nextTokenType() == TokenType.PERCENTAGE_TOKEN
                        || tokenizer.nextTokenType() == TokenType.FUNCTION_TOKEN) {
                    delimiter = TokenType.WHITESPACE_TOKEN;
                    tokenizer.pushBack();
                } else
                    delimiter = TokenType.COMMA_TOKEN;
            }

            if (delimiter == TokenType.COMMA_TOKEN)
                consumeWhitespaces();

            if (quitAtComma && tokenizer.nextTokenType() == TokenType.COMMA_TOKEN)
                return parsedValue;


        } while (tokenizer.nextTokenType() == delimiter);

        if (delimiter == TokenType.WHITESPACE_TOKEN) {
            com.sirweb.miro.parsing.values.miro.List res = new com.sirweb.miro.parsing.values.miro.List();

            for (MiroValue v : multiValue.getValues())
                res.addValue(v);
            return res;
        }
        else
            return multiValue.size() > 1 ? multiValue : multiValue.get(0);
    }

    public MiroStylesheet parse () throws MiroException {
        stack = new Stack<>();
        parseStylesheet();
        return root;
    }

    public MiroValue parseFunction () throws MiroException {
        return null;
    }

    private void parseStylesheet () throws MiroException {
        root = new MiroStylesheet();
        //for (String key : globals.getSymbols())
         //   root.symbolTable().setSymbol(key, globals.getSymbol(key));
        stack.push(root);

        parseBlockContent();
    }

    private void parseScript () throws MiroException {
        if (tokenizer.nextTokenType() == TokenType.MIRO_IDENT_TOKEN) {
            parseScriptAssignment();
            optional(TokenType.SEMICOLON_TOKEN);
        }
        else if (tokenizer.nextTokenType() == TokenType.IDENT_TOKEN) {
            if ("if".equals(tokenizer.nextTokenString()))
                parseScriptIf();
            else if ("for".equals(tokenizer.nextTokenString()))
                parseScriptFor();
        }
    }

    private MiroValue findSymbol (String symbolName) {
        Stack<Element> tmpStack = (Stack<Element>) stack.clone();
        while (!tmpStack.empty()) {
            if (tmpStack.peek().symbolTable().hasSymbol(symbolName))
                return tmpStack.peek().symbolTable().getSymbol(symbolName);
            tmpStack.pop();
        }
        if (globals.hasSymbol(symbolName))
            return globals.getSymbol(symbolName);
        return null;
    }

    private MiroMixin findMixin (String mixinName) {
        Stack<Element> tmpStack = (Stack<Element>) stack.clone();
        while (!tmpStack.empty()) {
            if (tmpStack.peek().symbolTable().hasMixin(mixinName))
                return tmpStack.peek().symbolTable().getMixin(mixinName);
            tmpStack.pop();
        }
        if (globals.hasMixin(mixinName))
            return globals.getMixin(mixinName);
        return null;
    }

    private void parseScriptAssignment () throws MiroException {
        String assignIdent = consume(TokenType.MIRO_IDENT_TOKEN).substring(1);
        consumeWhitespaces();

        consume(TokenType.EQUAL_TOKEN);

        consumeWhitespaces();

        MiroValue value = new Calculator(this).eval();

        stack.peek().symbolTable().setSymbol(assignIdent, value);
    }

    private void parseCss () throws MiroException {
        if (tokenizer.nextTokenType() == TokenType.MIRO_MIXIN_TOKEN) {
            if (tokenizer.lineOpensBlock())
                parseMixinDeclaration();
            else
                parseMixinCall();
        } else if (tokenizer.nextTokenType() == TokenType.AT_KEYWORD_TOKEN)
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
        consumeWhitespaces();
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
            if (tokenizer.nextTokenType() == TokenType.MIRO_IDENT_TOKEN
                    || (tokenizer.nextTokenType() == TokenType.IDENT_TOKEN && ("if".equals(tokenizer.nextTokenString()) || "for".equals(tokenizer.nextTokenString()))))
                parseScript();
            else
                parseCss();
            consumeWhitespaces();
            consumeNewlines();
        }
    }

    private void parseCssStatement (String prependProperty) throws MiroException {
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

    private void parseNestProp () throws MiroException {
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
            case "import":
                parseImport();
                break;
            case "color":
            case "string":
            case "stringvalue":
            case "bool":
            case "boolean":
            case "ident":
            case "function":
            case "numeric":
            case "list":
            case "dictionary":
            case "url":
                parseValueExtension(tokenString);
                break;
            default:
                parseUnknownAtRule();
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

    private void parseImport () throws MiroException {
        consumeWhitespaces();

        Calculator calculator = new Calculator(this);
        MiroValue urlValue = calculator.eval();

        boolean isMiroImport = false;
        if (urlValue instanceof StringValue) {
            if (((StringValue) urlValue).getValue().endsWith(".miro"))
                isMiroImport = true;
            else
                stack.peek().addImportRule(new MiroImportRule(urlValue));
        }
        else if (urlValue instanceof Url) {
            if (((Url) urlValue).getUrl().endsWith(".miro"))
                isMiroImport = true;
            else
                stack.peek().addImportRule(new MiroImportRule(urlValue));
        }
        else
            throw new MiroImportException("Cannot import file from value type " + urlValue.getClass().getSimpleName());

        if (isMiroImport) {
            String importUrl = (urlValue instanceof StringValue) ? ((StringValue) urlValue).getValue() : ((Url) urlValue).getUrl();

            if (!(importUrl.startsWith("/")
                || importUrl.startsWith("http"))) {
                String[] partElems = filePath.split("/");
                for (int i = partElems.length - 2; i >= 0; i--)
                    importUrl = partElems[i] + "/" + importUrl;
            }

            File importFile = new File(importUrl);
            if (!(importFile.exists() && !importFile.isDirectory()))
                throw new MiroImportException("The specified file " + importUrl + " could not be found");

            Tokenizer tempTokenizer = new Tokenizer(new Reader(importUrl).read());
            tempTokenizer.tokenize();

            SymbolTable fullSymbols = getFullSymbolTable();

            Parser tempParser = new Parser(tempTokenizer);
            for (String symbol : fullSymbols.getSymbols())
                tempParser.setGlobal(symbol, fullSymbols.getSymbol(symbol));
            for (MiroMixin mixin : fullSymbols.getMixins())
                tempParser.setGlobal(mixin);

            MiroStylesheet tempStylesheet = tempParser.parse();

            for (String symbol : tempStylesheet.symbolTable().getSymbols())
                stack.peek().symbolTable().setSymbol(symbol, tempStylesheet.symbolTable().getSymbol(symbol));

            for (MiroMixin mixin : tempStylesheet.symbolTable().getMixins())
                stack.peek().symbolTable().addMixin(mixin);

            for (Block block : tempStylesheet.getBlocks())
                stack.peek().addBlock(block);
            for (Statement statement : tempStylesheet.getStatements())
                stack.peek().addStatement(statement);
            for (ImportRule importRule : tempStylesheet.getImportRules())
                stack.peek().addImportRule(importRule);


        }

        optional(TokenType.SEMICOLON_TOKEN);

    }

    private void parseUnknownAtRule () throws MiroException {
        String atRule = tokenizer.nextTokenString().substring(1);
        if (tokenizer.lineOpensBlock())
            parseUnknownAtBlock();
    }

    private void parseUnknownAtBlock () throws MiroException {
        String header = "";
        tokenizer.pushBack();
        do
            header += consume(tokenizer.nextTokenType());
        while (tokenizer.nextTokenType() != TokenType.NEWLINE_TOKEN);

        consume(TokenType.NEWLINE_TOKEN);
        consume(TokenType.MIRO_INDENT_TOKEN);

        MiroBlock block = new MiroBlock(header);

        stack.peek().addBlock(block);
        stack.push(block);

        parseBlockContent();

        if (tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN)
            consume(TokenType.MIRO_DEDENT_TOKEN);
        else
            consume(TokenType.EOF);
        stack.pop();

    }

    private void parseMixinDeclaration () throws  MiroException {
        String declarationString = consume(TokenType.MIRO_MIXIN_TOKEN);
        String mixinName = declarationString.substring(1, declarationString.length() - 1);
        MiroMixin mixin = new MiroMixin(mixinName);
        consumeWhitespaces();

        boolean startDefaultValues = false;
        while (tokenizer.nextTokenType() != TokenType.C_R_TOKEN) {
            consumeWhitespaces();
            MiroMixinParameter parameter = parseMixinDeclarationParameter();

            if (startDefaultValues && parameter.getDefaultValue() == null)
                throw new MiroMixinException("Seperate parameters with and without default values");

            if (parameter.getDefaultValue() != null)
                startDefaultValues = true;
            mixin.addParameter(parameter);
            consumeWhitespaces();
            optional(TokenType.COMMA_TOKEN);
            consumeWhitespaces();
        }
        consume(TokenType.C_R_TOKEN);
        consume(TokenType.NEWLINE_TOKEN);
        consume(TokenType.MIRO_INDENT_TOKEN);

        int indents = 0;
        do {
            if (tokenizer.nextTokenType() == TokenType.MIRO_INDENT_TOKEN)
                indents++;
            else if (tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN)
                indents--;
            mixin.addContent(tokenizer.getNext());
        } while (!(indents == 0 && tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN) && !(tokenizer.nextTokenType() == TokenType.EOF));

        if (tokenizer.nextTokenType() != TokenType.EOF)
            consume(TokenType.MIRO_DEDENT_TOKEN);

        stack.peek().symbolTable().addMixin(mixin);
    }

    private MiroMixinParameter parseMixinDeclarationParameter () throws MiroException {
        consumeWhitespaces();
        String name = consume(TokenType.IDENT_TOKEN);
        MiroValue defaultValue = null;
        consumeWhitespaces();

        if (tokenizer.nextTokenType() == TokenType.EQUAL_TOKEN) {
            consume(TokenType.EQUAL_TOKEN);
            consumeWhitespaces();
            defaultValue = parseValue(true);
            consumeWhitespaces();
        }

        return new MiroMixinParameter(name, defaultValue);
    }

    private void parseMixinCall () throws MiroException {
        String callString = consume(TokenType.MIRO_MIXIN_TOKEN);
        String mixinName = callString.substring(1, callString.length() - 1);

        MiroValue parameterValue = parseValue();
        consume(TokenType.C_R_TOKEN);

        MiroMixin mixin = findMixin(mixinName);

        if (mixin == null)
            throw new MiroParserException("Unknown mixin with name '"+mixinName+"'");

        int parameterCount;

        if (parameterValue instanceof MultiValue)
            parameterCount = ((MultiValue) parameterValue).size();
        else if (parameterValue == null)
            parameterCount = 0;
        else
            parameterCount = 1;

        List<MiroMixinParameter> neededParameters = new ArrayList<>();

        for (MiroMixinParameter param : mixin.getParameters())
            if (param.getDefaultValue() == null)
                neededParameters.add(param);

        if (parameterCount < neededParameters.size())
            throw new MiroMixinException("Mixin " + mixinName + " takes " + mixin.getParameterCount() + " parameters but " + parameterCount + " were passed");

        List<MiroValue> parameterValueList = new ArrayList<>();


        int i = 0;
        if (parameterCount == 1) {
            parameterValueList.add(parameterValue);
            i++;
        }
        else if (parameterCount > 1) {

            for (MiroValue v : ((MultiValue) parameterValue).getValues()) {
                parameterValueList.add(v);
                i++;
            }
        }
        for (; i < mixin.getParameterCount(); i++)
            parameterValueList.add(mixin.getParameter(i).getDefaultValue());

        Tokenizer mixinTokenizer = new Tokenizer(mixin.getContent());
        Parser mixinParser = new Parser(mixinTokenizer);

        SymbolTable fullSymbols = getFullSymbolTable();

        for (String key : fullSymbols.getSymbols())
            mixinParser.setGlobal(key, fullSymbols.getSymbol(key));

        int paramIndex = 0;
        for (MiroMixinParameter param : mixin.getParameters()) {
            mixinParser.setGlobal(param.getName(), parameterValueList.get(paramIndex));
            paramIndex++;
        }

        MiroStylesheet mixinStylesheet = mixinParser.parse();

        for (Statement statement : mixinStylesheet.getStatements())
            stack.peek().addStatement(statement);


        for (Block block : mixinStylesheet.getBlocks())
            stack.peek().addBlock(block);

    }

    private void parseScriptIf () throws MiroException {
        consume(TokenType.IDENT_TOKEN);
        consumeWhitespaces();
        Calculator conditionCalculator = new Calculator(this);
        MiroValue condition = conditionCalculator.eval();

        consumeWhitespaces();
        consume(TokenType.COLON_TOKEN);
        consume(TokenType.NEWLINE_TOKEN);
        consume(TokenType.MIRO_INDENT_TOKEN);

        if (condition.getBoolean())
            parseBlockContent();
        else
            consumeBlock();

        consumeNewlines();

        if (tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN)
            consume(TokenType.MIRO_DEDENT_TOKEN);
        else
            consume(TokenType.EOF);

    }

    private void parseScriptFor () throws MiroException {
        consume(TokenType.IDENT_TOKEN);
        consumeWhitespaces();
        String key = consume(TokenType.IDENT_TOKEN);
        String value = null;
        consumeWhitespaces();
        if (optional(TokenType.COMMA_TOKEN)) {
            consumeWhitespaces();
            value = consume(TokenType.IDENT_TOKEN);
            consumeWhitespaces();
        }
        String operation = consume(TokenType.IDENT_TOKEN);
        Calculator vCalc = new Calculator(this);
        MiroValue objectValue = vCalc.eval();
        consume(TokenType.COLON_TOKEN);

        consume(TokenType.NEWLINE_TOKEN);
        consume(TokenType.MIRO_INDENT_TOKEN);

        List<Token> forContent = new ArrayList<>();

        int indents = 0;
        do {
            if (tokenizer.nextTokenType() == TokenType.MIRO_INDENT_TOKEN)
                indents++;
            else if (tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN)
                indents--;
            forContent.add(tokenizer.getNext());
        } while (!(indents == 0 && tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN) && !(tokenizer.nextTokenType() == TokenType.EOF));

        if (tokenizer.nextTokenType() != TokenType.EOF)
            consume(TokenType.MIRO_DEDENT_TOKEN);


        if ("in".equals(operation)) {
            if (objectValue instanceof com.sirweb.miro.parsing.values.miro.List) {
                int index = 0;
                for (MiroValue currentValue : ((com.sirweb.miro.parsing.values.miro.List) objectValue).getValues()) {
                    Tokenizer forTokenizer = new Tokenizer(forContent);
                    Parser forParser = new Parser(forTokenizer);

                    SymbolTable fullSymbols = getFullSymbolTable();

                    for (String symbol : fullSymbols.getSymbols())
                        forParser.setGlobal(symbol, fullSymbols.getSymbol(symbol));
                    for (MiroMixin mixin : fullSymbols.getMixins())
                        forParser.setGlobal(mixin);

                    if (value == null)
                        forParser.setGlobal(key, currentValue);
                    else {
                        forParser.setGlobal(key, new Numeric(index, Unit.NONE));
                        forParser.setGlobal(value, currentValue);
                    }

                    MiroStylesheet forStylesheet = forParser.parse();

                    for (Statement statement : forStylesheet.getStatements())
                        stack.peek().addStatement(statement);


                    for (Block block : forStylesheet.getBlocks())
                        stack.peek().addBlock(block);

                    for (ImportRule importRule : forStylesheet.getImportRules())
                        stack.peek().addImportRule(importRule);

                    for (String symbol : forStylesheet.symbolTable().getSymbols())
                        stack.peek().symbolTable().setSymbol(symbol, forStylesheet.symbolTable().getSymbol(symbol));
                    index++;
                }
            }
            else if (objectValue instanceof StringValue) {
                for (int index = 0; index < ((StringValue) objectValue).getValue().length(); index++) {
                    Tokenizer forTokenizer = new Tokenizer(forContent);
                    Parser forParser = new Parser(forTokenizer);

                    SymbolTable fullSymbols = getFullSymbolTable();

                    for (String symbol : fullSymbols.getSymbols())
                        forParser.setGlobal(symbol, fullSymbols.getSymbol(symbol));
                    for (MiroMixin mixin : fullSymbols.getMixins())
                        forParser.setGlobal(mixin);

                    if (value == null)
                        forParser.setGlobal(key, new StringValue(((StringValue) objectValue).getValue().charAt(index) + ""));
                    else {
                        forParser.setGlobal(key, new Numeric(index, Unit.NONE));
                        forParser.setGlobal(value, new StringValue(((StringValue) objectValue).getValue().charAt(index) + ""));
                    }

                    MiroStylesheet forStylesheet = forParser.parse();

                    for (Statement statement : forStylesheet.getStatements())
                        stack.peek().addStatement(statement);

                    for (ImportRule importRule : forStylesheet.getImportRules())
                        stack.peek().addImportRule(importRule);

                    for (Block block : forStylesheet.getBlocks())
                        stack.peek().addBlock(block);

                    for (String symbol : forStylesheet.symbolTable().getSymbols())
                        stack.peek().symbolTable().setSymbol(symbol, forStylesheet.symbolTable().getSymbol(symbol));
                }
            }
            else if (objectValue instanceof  Dictionary) {
                for (String keyIdent : ((Dictionary) objectValue).getKeys()) {
                    Tokenizer forTokenizer = new Tokenizer(forContent);
                    Parser forParser = new Parser(forTokenizer);

                    SymbolTable fullSymbols = getFullSymbolTable();

                    for (String symbol : fullSymbols.getSymbols())
                        forParser.setGlobal(symbol, fullSymbols.getSymbol(symbol));
                    for (MiroMixin mixin : fullSymbols.getMixins())
                        forParser.setGlobal(mixin);

                    if (value == null) {
                        com.sirweb.miro.parsing.values.miro.List valList = new com.sirweb.miro.parsing.values.miro.List();
                        valList.addValue(new Ident(keyIdent));
                        valList.addValue(((Dictionary) objectValue).get(keyIdent));
                        forParser.setGlobal(key, valList);
                    }
                    else {
                        forParser.setGlobal(key, new Ident(keyIdent));
                        forParser.setGlobal(value, ((Dictionary) objectValue).get(keyIdent));
                    }

                    MiroStylesheet forStylesheet = forParser.parse();

                    for (Statement statement : forStylesheet.getStatements())
                        stack.peek().addStatement(statement);

                    for (ImportRule importRule : forStylesheet.getImportRules())
                        stack.peek().addImportRule(importRule);

                    for (Block block : forStylesheet.getBlocks())
                        stack.peek().addBlock(block);

                    for (String symbol : forStylesheet.symbolTable().getSymbols())
                        stack.peek().symbolTable().setSymbol(symbol, forStylesheet.symbolTable().getSymbol(symbol));
                }
            }
            else
                throw new MiroParserException("for ... in cannot be applied to value of type "+operation.getClass().getSimpleName());
        }
        else if ("to".equals(operation)) {
            if (value != null)
                throw new MiroParserException("for " + key + ", "+value+" to is not defined. for ... to ... only takes one index");
            if (!(objectValue instanceof Numeric))
                throw new MiroParserException("for ... to ... can only be applied to Numerics");
            if (((Numeric) objectValue).getUnit() != Unit.NONE)
                throw new MiroParserException("for ... to ... can only be applied to Numerics without a unit");

            for (int index = 0; index < ((Numeric) objectValue).getNormalizedValue(); index++) {
                Tokenizer forTokenizer = new Tokenizer(forContent);
                Parser forParser = new Parser(forTokenizer);

                SymbolTable fullSymbols = getFullSymbolTable();

                for (String symbol : fullSymbols.getSymbols())
                    forParser.setGlobal(symbol, fullSymbols.getSymbol(symbol));
                for (MiroMixin mixin : fullSymbols.getMixins())
                    forParser.setGlobal(mixin);

                    forParser.setGlobal(key, new Numeric(index, Unit.NONE));
                MiroStylesheet forStylesheet = forParser.parse();

                for (Statement statement : forStylesheet.getStatements())
                    stack.peek().addStatement(statement);

                for (ImportRule importRule : forStylesheet.getImportRules())
                    stack.peek().addImportRule(importRule);

                for (Block block : forStylesheet.getBlocks())
                    stack.peek().addBlock(block);

                for (String symbol : forStylesheet.symbolTable().getSymbols())
                    stack.peek().symbolTable().setSymbol(symbol, forStylesheet.symbolTable().getSymbol(symbol));
            }

        }
        else
            throw new MiroParserException("for ... " + operation + " ... is an unknown operation");
    }

    private void parseValueExtension (String extened) throws MiroException {
        optional(TokenType.COLON_TOKEN);
        consume(TokenType.NEWLINE_TOKEN);
        consume(TokenType.MIRO_INDENT_TOKEN);
        while (tokenizer.nextTokenType() != TokenType.MIRO_DEDENT_TOKEN
                && tokenizer.nextTokenType() != TokenType.EOF) {
            switch(extened) {
                case "color":
                    Color.addFunc(parseValueFunctionDeclaration());
                    break;
                case "bool":
                case "boolean":
                    Bool.addFunc(parseValueFunctionDeclaration());
                    break;
            }
        }
        if (tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN)
            consume(TokenType.MIRO_DEDENT_TOKEN);
        else
            consume(TokenType.EOF);
    }

    private MiroFunc parseValueFunctionDeclaration () throws MiroException {
        consumeNewlinesAndWhitespaces();
        String ident = consume(TokenType.IDENT_TOKEN);
        if (!"func".equals(ident))
            throw new MiroParserException("Unexpected ident " + ident + " expected func");
        consumeWhitespaces();
        String functionName = consume(TokenType.FUNCTION_TOKEN);
        functionName = functionName.substring(0, functionName.length()-1);
        MiroFunc func = new MiroFunc(functionName);

        boolean startDefaultValues = false;
        while (tokenizer.nextTokenType() != TokenType.C_R_TOKEN) {
            consumeWhitespaces();
            MiroFuncParameter parameter = parseMiroFuncDeclarationParameter();

            if (startDefaultValues && parameter.getDefaultValue() == null)
                throw new MiroMixinException("Seperate parameters with and without default values");

            if (parameter.getDefaultValue() != null)
                startDefaultValues = true;
            func.addParameter(parameter);
            consumeWhitespaces();
            optional(TokenType.COMMA_TOKEN);
            consumeWhitespaces();
        }


        consume(TokenType.C_R_TOKEN);
        consumeWhitespaces();
        consume(TokenType.COLON_TOKEN);
        consumeWhitespaces();
        consume(TokenType.NEWLINE_TOKEN);
        consume(TokenType.MIRO_INDENT_TOKEN);

        int indents = 0;
        do {
            if (tokenizer.nextTokenType() == TokenType.MIRO_INDENT_TOKEN)
                indents++;
            else if (tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN)
                indents--;
            func.addContent(tokenizer.getNext());
        } while (!(indents == 0 && tokenizer.nextTokenType() == TokenType.MIRO_DEDENT_TOKEN) && !(tokenizer.nextTokenType() == TokenType.EOF));

        if (tokenizer.nextTokenType() != TokenType.EOF)
            consume(TokenType.MIRO_DEDENT_TOKEN);


        return func;
    }

    private MiroFuncParameter parseMiroFuncDeclarationParameter () throws MiroException {
        consumeWhitespaces();
        String name = consume(TokenType.IDENT_TOKEN);
        MiroValue defaultValue = null;
        consumeWhitespaces();

        if (tokenizer.nextTokenType() == TokenType.EQUAL_TOKEN) {
            consume(TokenType.EQUAL_TOKEN);
            consumeWhitespaces();
            defaultValue = parseValue(true);
            consumeWhitespaces();
        }

        return new MiroFuncParameter(name, defaultValue);
    }
}
