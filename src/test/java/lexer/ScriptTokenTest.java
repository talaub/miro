package lexer;

import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.lexer.TokenType;
import com.sirweb.miro.lexer.Tokenizer;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScriptTokenTest {

    @Test
    public void testAssign () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$test=15;$new = $test");
        tokenizer.tokenize();

        assertEquals(TokenType.MIRO_IDENT_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.EQUAL_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.NUMBER_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.SEMICOLON_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.MIRO_IDENT_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.WHITESPACE_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.EQUAL_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.WHITESPACE_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.MIRO_IDENT_TOKEN, tokenizer.getNext().getType());
    }

    @Test
    public void testFunctioncall () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$function($test, 15)");
        tokenizer.tokenize();

        assertEquals(TokenType.MIRO_MIXIN_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.MIRO_IDENT_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.COMMA_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.WHITESPACE_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.NUMBER_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.C_R_TOKEN, tokenizer.getNext().getType());
    }
}
