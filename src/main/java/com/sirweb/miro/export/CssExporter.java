package com.sirweb.miro.export;

import com.sirweb.miro.ast.css.CssBlock;
import com.sirweb.miro.ast.css.CssStatement;
import com.sirweb.miro.ast.css.CssStylesheet;

import java.io.*;

public class CssExporter implements Exporter {
    private CssStylesheet stylesheet;
    private boolean minified = false;

    public CssExporter (CssStylesheet stylesheet, boolean minified) {
        this.stylesheet = stylesheet;
        this.minified = minified;
    }

    public CssExporter (CssStylesheet stylesheet) {
        this(stylesheet, false);
    }

    public String export () throws IOException {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        export(s);
        return s.toString();
    }

    public void export (OutputStream out) throws IOException {
        for (CssBlock block : stylesheet.getBlocks()) {
            if (minified)
                out.write(new String (block.getHeader() + "{").getBytes());
            else
                out.write(new String (block.getHeader() + " {\n").getBytes());

            for (CssStatement statement : block.getStatements())
                if (minified)
                    out.write(new String (statement.getProperty() + ":" + statement.getValue() + ";").getBytes());
                else
                    out.write(new String ("    "+statement.getProperty() + ": " + statement.getValue() + ";\n").getBytes());

            out.write(new String ("}"+(minified ? "":"\n\n")).getBytes());
        }
    }
}
