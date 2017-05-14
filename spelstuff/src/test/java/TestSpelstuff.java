import org.cg.spelstuff.Spel;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.expression.spel.ast.PropertyOrFieldReference;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ssmertnig on 4/23/17.
 */

public class TestSpelstuff {

    private String splxpr = "resolve('ProductCanonical', 'integrationKey=' + resolve('ProductVariantCanonical', 'integrationKey='+#root.productvariantforeign)?.productForeign)?.purchaser";

    @Test
    public void filter() {
        List<PropertyOrFieldReference> actual = Spel.of(splxpr).filter(PropertyOrFieldReference.class);
        List<String> expected = new LinkedList<String>();
        expected.add("productvariantforeign");
        expected.add("productForeign");
        expected.add("purchaser");
        List<String> act = actual.stream().map(r -> r.getName()).collect(Collectors.toList());
        Assert.assertEquals(expected, act);
    }



}
