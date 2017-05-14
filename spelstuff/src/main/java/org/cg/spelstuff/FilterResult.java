package org.cg.spelstuff;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.expression.spel.SpelNode;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;

public class FilterResult {
	public final List<PropertyOrFieldReference> fieldsOrProperties = new ArrayList<PropertyOrFieldReference>();
	public final Optional<String> parseError;
	
	public FilterResult(List<PropertyOrFieldReference> list, Optional<String> parseError){
		this.fieldsOrProperties.addAll(list);
		this.parseError = parseError;
	}
}
