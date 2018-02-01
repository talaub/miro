package com.sirweb.miro;

import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.util.Reader;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;

public class CLI {

    public static void main (String[] args) {
        String code = "$m = 5px\np\n    margin-left $m\n    &.class\n        font-size 20em + 16px\n        position absolute\ndiv\n    position 7;top $m + 3\n    content 3 * 'Hallo'";
        Reader reader = new Reader("/Users/taminolaub/Desktop/test.miro");
        code = reader.read();
        System.out.println(code);
        System.out.println("------------------------");
        try {
            System.out.println(new Miro(code).toCss());
        } catch (IOException | MiroException e) {
            e.printStackTrace();
        }
    }
}
