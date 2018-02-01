package com.sirweb.miro.ast;

import com.sirweb.miro.parsing.values.miro.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public abstract class Element {
    private List<Block> blocks;
    private List<Statement> statements;
    private SymbolTable symbolTable;

    public Element () {
        this.blocks = new ArrayList<>();
        this.statements = new ArrayList<>();
        this.symbolTable = new SymbolTable();
    }

    public Iterable<Block> getBlocks () { return blocks; }
    public void addBlock (Block block) { blocks.add(block); }

    public SymbolTable symbolTable() {
        return symbolTable;
    }

    public Iterable<Statement> getStatements () { return statements; }
    public void addStatement (Statement statement) { statements.add(statement); }
    public boolean hasStatements () { return !statements.isEmpty(); }
}
