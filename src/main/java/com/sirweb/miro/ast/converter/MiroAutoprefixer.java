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
