package lexer;

import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.exceptions.MiroIndentationException;
import com.sirweb.miro.exceptions.MiroTokenizerException;
import com.sirweb.miro.lexer.Token;
import com.sirweb.miro.lexer.TokenType;
import com.sirweb.miro.lexer.Tokenizer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TokenizerTest {
    @Test
    public void testIndent () throws MiroException {
        String code = "\n    \n        \n    $test";
        Tokenizer tokenizer = new Tokenizer(code);
        tokenizer.tokenize();
        assertEquals(TokenType.NEWLINE_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.MIRO_INDENT_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.NEWLINE_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.MIRO_INDENT_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.NEWLINE_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.MIRO_DEDENT_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.MIRO_IDENT_TOKEN, tokenizer.getNext().getType());

    }

    @Test(expected = MiroIndentationException.class)
    public void testIndentException () throws MiroException {
        String code = "\n    \n       \n    $test";
        Tokenizer tokenizer = new Tokenizer(code);
        tokenizer.tokenize();

    }


    @Test(expected = MiroTokenizerException.class)
    public void testUnknownTokenException () throws MiroException {
        String code = "%test";
        Tokenizer tokenizer = new Tokenizer(code);
        tokenizer.tokenize();
    }

    @Test(expected = MiroTokenizerException.class)
    public void unclosedString () throws MiroException {
        String code = "'test";
        Tokenizer tokenizer = new Tokenizer(code);
        tokenizer.tokenize();
    }

    @Test
    public void testEof () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("$code");
        tokenizer.tokenize();

        assertEquals(TokenType.MIRO_IDENT_TOKEN, tokenizer.getNext().getType());
        assertEquals(TokenType.EOF, tokenizer.getNext().getType());
        assertEquals(TokenType.EOF, tokenizer.getNext().getType());
        assertEquals(TokenType.EOF, tokenizer.getNext().getType());
    }

    @Test
    public void testStrings () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("'Test1''Test2'");
        tokenizer.tokenize();

        Token first = tokenizer.getNext();
        Token second = tokenizer.getNext();
        assertEquals(TokenType.STRING_TOKEN, first.getType());
        assertEquals(TokenType.STRING_TOKEN, second.getType());
        assertEquals("'Test1'", first.getToken());
        assertEquals("'Test2'", second.getToken());
    }

    @Test
    public void testMultilinecomment () throws MiroException {
        Tokenizer tokenizer = new Tokenizer("before/* This\nis\na\nmultiline\n   comment :)*/after");
        tokenizer.tokenize();

        Token first = tokenizer.getNext();
        Token second = tokenizer.getNext();
        assertEquals(TokenType.IDENT_TOKEN, first.getType());
        assertEquals(TokenType.IDENT_TOKEN, second.getType());
        assertEquals("before", first.getToken());
        assertEquals("after", second.getToken());
    }
}
