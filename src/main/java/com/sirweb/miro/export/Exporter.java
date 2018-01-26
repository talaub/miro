package com.sirweb.miro.export;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public interface Exporter {

    String export () throws IOException;

    void export (OutputStream out) throws IOException;
}
