package org.cg.spelstuff;

import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParseResult {
	public final Optional<SpelNode> root;
	public final Optional<String> parseError;

	public ParseResult(Optional<SpelNode> root, Optional<String> parseError){
		this.root = root;
		this.parseError = parseError;
	}
}
