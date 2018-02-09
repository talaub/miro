package com.sirweb.miro.ast.miro;

import com.sirweb.miro.lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class MiroMixin {
     private List<MiroMixinParameter> parameters;
     private String name;
     private List<Token> content;

     public MiroMixin (String name) {
         this.name = name;
         this.parameters = new ArrayList<>();
         content = new ArrayList<>();
     }

     public String getName () { return name; }

     public Iterable<MiroMixinParameter> getParameters () { return parameters; }

     public boolean hasParameter (String name) {
         return getParameter(name) != null;
     }

     public MiroMixinParameter getParameter (String name) {
         for (MiroMixinParameter param : getParameters())
             if (param.getName().equals(name))
                 return param;
         return null;
     }

     public void addParameter (MiroMixinParameter parameter) { parameters.add(parameter); }

     public List<Token> getContent () { return content; }

     public void addContent (Token tok) { content.add(tok); }

     public int getParameterCount () { return parameters.size(); }

     public MiroMixinParameter getParameter (int i) { return parameters.get(i); }
}
