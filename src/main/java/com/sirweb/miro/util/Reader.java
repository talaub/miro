package com.sirweb.miro.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Tamino Laub
 */
public class Reader {
    private String content = "";
    public Reader (String filepath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                content += line + "\n";

            }

        }catch(IOException e) { e.printStackTrace(); }

    }

    public String read () { return content; }

}