package com.sirweb.miro.ast;

import com.sirweb.miro.parsing.values.miro.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public abstract class Element {
    private List<Block> blocks;
    private List<Statement> statements;
    private List<ImportRule> importRules;
    private SymbolTable symbolTable;

    public Element () {
        this.blocks = new ArrayList<>();
        this.statements = new ArrayList<>();
        this.symbolTable = new SymbolTable();
        this.importRules = new ArrayList<>();
    }

    public Iterable<Block> getBlocks () { return blocks; }
    public void addBlock (Block block) { blocks.add(block); }

    public SymbolTable symbolTable() {
        return symbolTable;
    }

    public Iterable<Statement> getStatements () { return statements; }
    public void addStatement (Statement statement) { statements.add(statement); }
    public boolean hasStatements () { return !statements.isEmpty(); }

    public void addImportRule(ImportRule rule) { importRules.add(rule); }
    public Iterable<ImportRule> getImportRules () { return importRules; }

    public void replaceSymbolTable (SymbolTable newSymbolTable) { this.symbolTable = newSymbolTable; }
}
