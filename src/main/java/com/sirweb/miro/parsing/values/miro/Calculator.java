package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.exceptions.MiroCalculationException;
import com.sirweb.miro.exceptions.MiroParserException;
import com.sirweb.miro.lexer.Token;
import com.sirweb.miro.lexer.TokenType;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Calculator implements MiroValue {

    public enum Operator {
        AND(1),
        OR(1),
        EQUALSEQUALS(2),
        GREATER(2),
        SMALLER(2),
        GREATER_EQUALS(2),
        SMALLER_EQUALS(2),
        PLUS(3),
        MINUS(3),
        MULTIPLY(4),
        DIVIDE(4);

        private final int precedence;

        Operator(final int newPrecedence) {
            precedence = newPrecedence;
        }

        public int getPrecedence() { return precedence; }

        public static Operator toOperator (String s) throws MiroCalculationException {
            switch (s) {
                case "&&":
                    return Operator.AND;
                case "||":
                    return Operator.OR;
                case "==":
                    return Operator.EQUALSEQUALS;
                case "+":
                    return Operator.PLUS;
                case "-":
                    return Operator.MINUS;
                case "*":
                    return Operator.MULTIPLY;
                case "/":
                    return Operator.DIVIDE;
                default:
                    throw new MiroCalculationException("Unknown operator '"+s+"'");

            }
        }
    }

    private Tokenizer tokenizer;
    private Parser parser;
    private List<Object> postfix;

    public Calculator (Parser parser) throws MiroParserException {
        this.parser = parser;
        this.tokenizer = parser.tokenizer();
        this.postfix = new ArrayList<>();
        parseCalculation();
    }
    private MiroValue parseValue () throws MiroParserException {
        parser.consumeWhitespaces();

        MultiValue multiValue = new MultiValue();

        do {
            int sizeBefore = multiValue.size();
            parser.consumeWhitespaces();
            if (tokenizer.nextTokenType() == TokenType.MIRO_IDENT_TOKEN) {
                Token token = tokenizer.getNext();
                MiroValue value = parser.findSymbol(token.getToken().substring(1));
                if (value == null)
                    throw new MiroParserException("Unknown variable '" + token.getToken() + "'");
                multiValue.addValue(value);
            } else if (tokenizer.nextTokenType() == TokenType.NUMBER_TOKEN
                    || tokenizer.nextTokenType() == TokenType.DIMENSION_TOKEN
                    || tokenizer.nextTokenType() == TokenType.PERCENTAGE_TOKEN) {
                multiValue.addValue(new Numeric(tokenizer.getNext()));
            } else if (tokenizer.nextTokenType() == TokenType.STRING_TOKEN) {
                multiValue.addValue(new StringValue(tokenizer.getNext()));
            } else if (tokenizer.nextTokenType() == TokenType.IDENT_TOKEN) {
                Token token = tokenizer.getNext();
                if (Color.knowsColor(token.getToken()))
                    multiValue.addValue(new Color(Color.getDefaultColorDictionary().get(token.getToken())));
                else
                    multiValue.addValue(new Ident(token));
            } else if (tokenizer.nextTokenType() == TokenType.O_R_TOKEN) {
                multiValue.addValue(new Calculator(parser).eval());
            } else if (tokenizer.nextTokenType() == TokenType.HASH_TOKEN)
                multiValue.addValue(new Color(tokenizer.getNext().getToken()));
            if (sizeBefore == multiValue.size()) {
                throw new MiroParserException("Could not parse value from token '" + tokenizer.getNext().getToken() + "'");
            }
            else
                parser.consumeWhitespaces();
        } while (tokenizer.nextTokenType() == TokenType.COMMA_TOKEN);

        return multiValue.size() > 1 ? multiValue : multiValue.get(0);
    }

    public List<Object> getPostfix () { return postfix; }

    private void parseCalculation() throws MiroParserException {
        Stack<Operator> operators = new Stack<>();
        parser.consumeWhitespaces();

        //parser.optional(TokenType.O_R_TOKEN);
        do {
            if (tokenizer.nextTokenType() == TokenType.ARITHMETIC_TOKEN) {
                Operator operator = Operator.toOperator(tokenizer.getNext().getToken());
                while (!operators.empty() &&
                        operators.peek().getPrecedence() > operator.getPrecedence()) {
                    postfix.add(operators.pop());
                }
                operators.push(operator);
            }
            else
                postfix.add(parseValue());
        } while (tokenizer.nextTokenType() != TokenType.C_R_TOKEN
                && tokenizer.nextTokenType() != TokenType.NEWLINE_TOKEN
                && tokenizer.nextTokenType() != TokenType.SEMICOLON_TOKEN
                && tokenizer.nextTokenType() != TokenType.COMMA_TOKEN
                && tokenizer.nextTokenType() != TokenType.C_C_TOKEN
                && tokenizer.nextTokenType() != TokenType.EOF);
        while (!operators.isEmpty())
            postfix.add(operators.pop());

        //parser.optional(TokenType.C_R_TOKEN);

    }

    public MiroValue eval () {
        Stack<MiroValue> operands = new Stack<>();
        int position = 0;

        if (operands.size() == 1)
            return operands.pop();

        do {
            if (getPostfix().get(position) instanceof MiroValue)
                operands.push((MiroValue) getPostfix().get(position));
            else {
                MiroValue val2 = operands.pop();
                MiroValue val1 = operands.pop();
                MiroValue result = null;
                Operator operator = (Operator) getPostfix().get(position);
                switch (operator) {
                    case PLUS:
                        result = add(val1, val2);
                        break;
                    case MULTIPLY:
                        result = multiply(val1, val2);
                }
                operands.push(result);
            }
        } while (++position < getPostfix().size());

        return operands.pop();
    }

    private MiroValue add (MiroValue val1, MiroValue val2) {
        if (val1 instanceof Numeric)
            return addNumeric((Numeric) val1, val2);
        if (val1 instanceof StringValue)
            return addString((StringValue) val1, val2);

        return null;
    }

    private MiroValue multiply (MiroValue val1, MiroValue val2) {
        if (val1 instanceof Numeric)
            return multiplyNumeric((Numeric) val1, val2);
        if (val1 instanceof StringValue)
            return addString((StringValue) val1, val2);

        return null;
    }

    private MiroValue addNumeric (Numeric val1, MiroValue val2) {
        if (val2 instanceof Numeric)
            return new Numeric(val1.getNormalizedValue() + ((Numeric)val2).getNormalizedValue(), val1.getUnit());
        if (val2 instanceof StringValue)
            return new StringValue(val1.toString() + ((StringValue) val2).getValue());

        return null;
    }

    private MiroValue addString (StringValue val1, MiroValue val2) {
        if (val2 instanceof Numeric)
            return new StringValue(val1.getValue() + ((Numeric) val2).toString());
        if (val2 instanceof StringValue)
            return new StringValue(val1.getValue() + ((StringValue) val2).getValue());

        return null;
    }

    private MiroValue multiplyNumeric (Numeric val1, MiroValue val2) {
        if (val2 instanceof Numeric)
            return new Numeric(val1.getNormalizedValue() * ((Numeric)val2).getNormalizedValue(), val1.getUnit());
        if (val2 instanceof StringValue) {
            String resultString = "";
            for (int i = 0; i < val1.getValue(); i++)
                resultString += ((StringValue) val2).getValue();
            return new StringValue(resultString);
        }

        return null;
    }
}
