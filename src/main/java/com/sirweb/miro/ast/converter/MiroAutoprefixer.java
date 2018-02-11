package com.sirweb.miro.ast.converter;

import com.sirweb.miro.ast.Block;
import com.sirweb.miro.ast.Element;
import com.sirweb.miro.ast.Statement;
import com.sirweb.miro.ast.miro.MiroBlock;
import com.sirweb.miro.ast.miro.MiroStatement;
import com.sirweb.miro.ast.miro.MiroStylesheet;
import com.sirweb.miro.parsing.values.miro.MiroValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MiroAutoprefixer {
    private MiroStylesheet stylesheet;
    private Map<String, List<String>> prefixedProperties;

    public MiroAutoprefixer (MiroStylesheet stylesheet) {
        this.stylesheet = stylesheet;

        prefixedProperties = new HashMap<>();

        prefixedProperties.put("transition", new ArrayList<>());
        prefixedProperties.get("transition").add("moz");
        prefixedProperties.get("transition").add("webkit");

        prefixedProperties.put("background-clip", new ArrayList<>());
        prefixedProperties.get("background-clip").add("webkit");

        prefixedProperties.put("box-reflection", new ArrayList<>());
        prefixedProperties.get("box-reflection").add("webkit");

        prefixedProperties.put("filter", new ArrayList<>());
        prefixedProperties.get("filter").add("webkit");

        prefixedProperties.put("font-feature-settings", new ArrayList<>());
        prefixedProperties.get("font-feature-settings").add("webkit");
        prefixedProperties.get("font-feature-settings").add("moz");

        prefixedProperties.put("word-break", new ArrayList<>());
        prefixedProperties.get("word-break").add("ms");

        prefixedProperties.put("hyphens", new ArrayList<>());
        prefixedProperties.get("hyphens").add("webkit");
        prefixedProperties.get("hyphens").add("moz");
        prefixedProperties.get("hyphens").add("ms");

        prefixedProperties.put("mask-image", new ArrayList<>());
        prefixedProperties.get("mask-image").add("webkit");

        prefixedProperties.put("column-count", new ArrayList<>());
        prefixedProperties.get("column-count").add("webkit");
        prefixedProperties.get("column-count").add("moz");

        prefixedProperties.put("column-gap", new ArrayList<>());
        prefixedProperties.get("column-gap").add("webkit");
        prefixedProperties.get("column-gap").add("moz");

        prefixedProperties.put("column-rule", new ArrayList<>());
        prefixedProperties.get("column-rule").add("webkit");
        prefixedProperties.get("column-rule").add("moz");

        prefixedProperties.put("object-fit", new ArrayList<>());
        prefixedProperties.get("object-fit").add("o");

        prefixedProperties.put("flow-from", new ArrayList<>());
        prefixedProperties.get("flow-from").add("webkit");
        prefixedProperties.get("flow-from").add("ms");

        prefixedProperties.put("flow-into", new ArrayList<>());
        prefixedProperties.get("flow-into").add("webkit");
        prefixedProperties.get("flow-into").add("ms");

        prefixedProperties.put("transform", new ArrayList<>());
        prefixedProperties.get("transform").add("webkit");
        prefixedProperties.get("transform").add("ms");

        prefixedProperties.put("appearance", new ArrayList<>());
        prefixedProperties.get("appearance").add("webkit");
        prefixedProperties.get("appearance").add("moz");


    }

    public void prefix () {
        for (Element element : stylesheet.getBlocks())
            prefixElement(element);
    }

    private void prefixElement (Element element) {
        List<MiroStatement> newStatements = new ArrayList<>();
        for (Statement statement : element.getStatements()) {
            if (prefixedProperties.containsKey(statement.getProperty())) {
                for (String prefix : prefixedProperties.get(statement.getProperty()))
                    newStatements.add(new MiroStatement("-"+prefix+"-"+statement.getProperty(), (MiroValue) statement.getValue()));
            }
        }

        for (Statement statement : newStatements)
            element.addStatement(statement);

        for (Element elem : element.getBlocks())
            prefixElement(elem);
    }


}
