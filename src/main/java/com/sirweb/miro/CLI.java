package com.sirweb.miro;

import com.sirweb.miro.exceptions.MiroException;
import com.sirweb.miro.util.Reader;
import org.apache.commons.cli.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class CLI {

    public static void main (String[] args) {

        Options options = new Options();
        options.addOption("out", true, "Sets the output filepath");
        options.addOption("h", "help", false, "Prints this help information");
        options.addOption("in", true, "Sets input filepath");
        CommandLineParser parser = new DefaultParser();

        String inFilePath = null;
        String outFilePath = null;

        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            if (line.hasOption("h")) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( "miro", options );
            }
            if (line.hasOption("in")) {
                String pathString = line.getOptionValue("in");
                if (pathString != null) {
                    inFilePath = pathString;
                }
                else
                    System.err.println("-in requires an argument");
            }
            else {
                System.err.println("Required parameter in not set");
            }
            if (line.hasOption("out")) {
                String portString = line.getOptionValue("out");
                if (portString != null) {

                    outFilePath = portString;

                }else
                    System.err.println("-out requires an argument");
            }
            else {
                System.err.println("Required parameter out not set");
            }
        }
        catch( ParseException exp ) {
            // oops, something went wrong
            exp.printStackTrace();
        }

        try {
            Miro miro = new Miro(new File(inFilePath), new File(outFilePath));
        } catch (MiroException e) {
            e.print();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
