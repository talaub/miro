package com.sirweb.miro.export;

import com.sirweb.miro.ast.css.*;

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
        for (CssElement element : stylesheet.getElements()) {

            if (element instanceof CssMediaQuery) {
                if (minified)
                    out.write(new String (element.getHeader() + "{").getBytes());
                else
                    out.write(new String (element.getHeader() + " {\n").getBytes());

                for (CssBlock block : ((CssMediaQuery) element).getBlocks())
                    exportBlock(out, block);

                out.write(new String ("}"+(minified ? "":"\n\n")).getBytes());
            }
            else
                exportBlock(out, (CssBlock) element);
        }
    }

    private void exportBlock (OutputStream out, CssBlock block) throws IOException {

        if (minified)
            out.write(new String (block.getHeader() + "{").getBytes());
        else
            out.write(new String (block.getHeader() + " {\n").getBytes());

        for (CssStatement statement : block.getStatements())
            if (minified)
                out.write(new String(statement.getProperty() + ":" + statement.getValue() + (statement.isImportant() ? " !important" : "") +  ";").getBytes());
            else
                out.write(new String("    " + statement.getProperty() + ": " + statement.getValue() + (statement.isImportant() ? " !important" : "") + ";\n").getBytes());
        out.write(new String ("}"+(minified ? "":"\n\n")).getBytes());
    }
}
