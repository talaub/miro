package com.sirweb.miro;

import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.util.Reader;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;

public class CLI {

    public static void main (String[] args) {
        String code = "$pad = [\n        5px,\n        10px,\n        20px,\n        50px\n    ]\n\ndiv\n    padding $pad.get(1) + $pad.get(3)\n    \n    &.has-padding\n        padding $pad";
        //Reader reader = new Reader("/Users/taminolaub/Desktop/test.miro");
        //code = reader.read();
        System.out.println(code);
        System.out.println("------------------------");
        try {
            System.out.println(new Miro(code).toCss());
        } catch (IOException | MiroException e) {
            e.printStackTrace();
        }
    }
}
