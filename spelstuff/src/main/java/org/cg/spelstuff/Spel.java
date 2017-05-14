package org.cg.spelstuff;

import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.SpelParseException;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * Created by ssmertnig on 4/23/17.
 */
public class Spel {

    private final String spel;

    private Spel(String spel) {
        this.spel = spel;
    }

    public FilterResult filterPropertyOrFieldReference() {
        try {
            Optional<String> opt = Optional.empty();
            return new FilterResult(filter(PropertyOrFieldReference.class), opt);
        } catch (SpelParseException e) {
            return new FilterResult(new LinkedList<PropertyOrFieldReference>(), Optional.of(e.getMessage()));
        }
    }

    public <T extends SpelNode> List<T> filter(Class<T> type) throws SpelParseException {
        if (type == null)
            throw new IllegalArgumentException("type is null");

        List<T> result = new LinkedList<T>();

        collect(type, (new SpelExpressionParser()).parseRaw(spel).getAST(), result);
        return result;
    }

    private <T extends SpelNode> void collect(Class<T> type, SpelNode currentNode, List<T> basket) {
        if (type.isAssignableFrom(currentNode.getClass()))
            basket.add(type.cast(currentNode));

        for (int i = 0; i < currentNode.getChildCount(); i++)
            collect(type, currentNode.getChild(i), basket);

    }

    public ParseResult parse() {
        try {
            return new ParseResult(Optional.of((new SpelExpressionParser()).parseRaw(spel).getAST()), Optional.empty());
        } catch (SpelParseException e) {
            return new ParseResult(Optional.empty(), Optional.of(e.getMessage()));
        }
    }

    public static Spel of(String spel) {
        return new Spel(spel);
    }
}
