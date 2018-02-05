package com.sirweb.miro;

import com.sirweb.miro.ast.converter.MiroToCssConverter;
import com.sirweb.miro.ast.css.CssStylesheet;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.export.CssExporter;
import com.sirweb.miro.export.Exporter;
import com.sirweb.miro.lexer.Tokenizer;
import com.sirweb.miro.parsing.Parser;
import com.sirweb.miro.util.Reader;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class Miro {
    private MiroStylesheet stylesheet;
    public Miro (String content) throws MiroException {
        Tokenizer tokenizer = new Tokenizer(content);
        tokenizer.tokenize();
        Parser parser = new Parser(tokenizer);
        this.stylesheet = parser.parse();
    }

    public Miro (File file) throws MiroException {
        this(new Reader(file.getAbsolutePath()).read());
    }

    public String toCss () throws IOException {
        MiroToCssConverter converter = new MiroToCssConverter(stylesheet);
        CssStylesheet cssStylesheet = converter.convert();
        Exporter exporter = new CssExporter(cssStylesheet);
        //PrintWriter pw = new PrintWriter(new File("/Users/taminolaub/Desktop/test.css"));
        //PrintStream ps = new PrintStream(new File("/Users/taminolaub/Desktop/test.css"));
        //exporter.export(ps);
        //ps.close();
        return exporter.export();
    }
}
