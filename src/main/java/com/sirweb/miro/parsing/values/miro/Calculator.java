package com.sirweb.miro.parsing.values.miro;

import com.sirweb.miro.exceptions.*;
import com.sirweb.miro.lexer.Token;
import com.sirweb.miro.lexer.TokenType;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import com.sirweb.miro.parsing.values.Unit;
import com.sirweb.miro.parsing.values.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Calculator implements MiroValue {

    @Override
    public Value callFunc(String functionName, List<MiroValue> parameters) throws MiroUnimplementedFuncException, MiroFuncParameterException {
        return null;
    }

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

    public Calculator (Parser parser) throws MiroException {
        this.parser = parser;
        this.tokenizer = parser.tokenizer();
        this.postfix = new ArrayList<>();
        parseCalculation();
    }

    public List<Object> getPostfix () { return postfix; }

    private void parseCalculation() throws MiroException {
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
                postfix.add(parser.parseValue());
            parser.consumeWhitespaces();
        } while (tokenizer.nextTokenType() != TokenType.C_R_TOKEN
                && tokenizer.nextTokenType() != TokenType.NEWLINE_TOKEN
                && tokenizer.nextTokenType() != TokenType.SEMICOLON_TOKEN
                && tokenizer.nextTokenType() != TokenType.COMMA_TOKEN
                && tokenizer.nextTokenType() != TokenType.C_C_TOKEN
                && tokenizer.nextTokenType() != TokenType.EOF
                && tokenizer.nextTokenType() != TokenType.MIRO_EXCLAMATION_TOKEN
                && tokenizer.nextTokenType() != TokenType.MIRO_DEBUG_TOKEN
                && tokenizer.nextTokenType() != TokenType.C_Q_TOKEN);
        while (!operators.isEmpty())
            postfix.add(operators.pop());

        parser.consumeNewlinesAndWhitespaces();

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
                        break;
                    case OR:
                        result = Or(val1, val2);
                        break;
                    case AND:
                        result = And(val1, val2);
                        break;
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
            if (val1.getUnit() == Unit.PERCENT
                    || val1.getUnit() == Unit.VW
                    || val1.getUnit() == Unit.VH
                    || ((Numeric) val2).getUnit() == Unit.PERCENT
                    || ((Numeric) val2).getUnit() == Unit.VW
                    || ((Numeric) val2).getUnit() == Unit.VH) {
                MultiValue parameters = new MultiValue();
                com.sirweb.miro.parsing.values.miro.List calculation = new com.sirweb.miro.parsing.values.miro.List();
                calculation.addValue(val1);
                calculation.addValue(new Ident("+"));
                calculation.addValue(val2);
                parameters.addValue(calculation);
                return new Function("calc", parameters);
            }
            else
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

    private Bool Or (MiroValue val1, MiroValue val2) {
        return new Bool(val1.getBoolean() || val2.getBoolean());
    }

    private Bool And (MiroValue val1, MiroValue val2) {
        return new Bool(val1.getBoolean() && val2.getBoolean());
    }

    @Override
    public boolean getBoolean() {
        return eval().getBoolean();
    }
}
