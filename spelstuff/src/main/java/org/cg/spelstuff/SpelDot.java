package org.cg.spelstuff;

import org.cg.scala.ast.*;
import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.LinkedList;
import java.util.List;

public class SpelDot {

    private final String spel;

    public static void main(String[] args) {
        if (args.length < 1)
            System.err.println("usage: SpelDot <spel expression>");
        else
            System.out.println(SpelDot.of(args[0]).dot());
    }

    private SpelDot(String spel) {
        this.spel = spel;
    }

    public String dot() {
        SpelNode ast = (new SpelExpressionParser()).parseRaw(spel).getAST();
        return new Ast2Dot(convert(ast)).get();
    }

    private Token spel2Token(SpelNode node) {
        return new Token(spel.substring(node.getStartPosition(), node.getEndPosition()) + " (" + node.getClass().getSimpleName() + ")");
    }

    private AstNode convert(SpelNode node) {
        if (node.getChildCount() == 0)
            return new AstTerminal(spel2Token(node));
        else
            return new AstNonTerminal(spel2Token(node), AstNodes.toScala(convertChildren(node)));
    }

    private List<AstNode> convertChildren(SpelNode node) {
        List<AstNode> result = new LinkedList<AstNode>();

        for (int i = 0; i < node.getChildCount(); i++)
            result.add(convert(node.getChild(i)));

        return result;
    }

    public static SpelDot of(String spel) throws SpelParseException {
        return new SpelDot(spel);
    }


}