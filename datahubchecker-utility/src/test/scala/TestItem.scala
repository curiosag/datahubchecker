
import org.cg.scala.dhc.domelments._
import org.scalatest.FlatSpec


/**
  * Created by ssmertnig on 4/22/17.
  */
class TestItem extends FlatSpec {


  val rawItem = <item>
    <type ln="1">CategoryRaw</type>
    <description>Raw representation of a category from PIM</description>
    <attributes>
      <attribute>
        <name ln="2">code</name>
      </attribute>
      <attribute>
        <name ln="3">name</name>
      </attribute>

    </attributes>
  </item>

  "" should "raw Item"  in {

    val item = Item(rawItem)
    val expectedAttributes = Set(new Attribute("code", Option.empty, "2"), new Attribute("name", Option.empty, "3"))
    val expected = new Item("CategoryRaw", expectedAttributes, "1", Option.empty, Seq())

    assert(item.equals(expected))
  }

  val canonicalItem = <item>
    <type ln="1">CategoryCanonical</type>
    <description>Canonical representation of a PIM category</description>
    <status>ACTIVE</status>
    <attributes>
      <attribute>
        <name ln="2">code</name>
        <model>
          <type>String</type>
          <primaryKey>true</primaryKey>
        </model>
      </attribute>
      <attribute>
        <name ln="3">name</name>
        <model>
          <localizable>true</localizable>
          <type>String</type>
        </model>
      </attribute>
    </attributes>
  </item>

  "" should "canonical Item" in {
    val item = Item(canonicalItem)
    val expectedAttributes = Set(new Attribute("code", Option.empty, "2"), new Attribute("name", Option.empty, "3"))
    val expected = new Item("CategoryCanonical", expectedAttributes, "1", Option.empty, Seq())


    assert(item.equals(expected))
  }

  val targetItem =
    <item>
      <type ln="1">MediaLogoMetaInfoTarget</type>
      <exportCode>MediaLogoMetaInfo</exportCode>
      <description>PCM representation of a media logo meta info</description>
      <updatable>true</updatable>
      <dependencies>
        <dependency ln="2">LogoMetaInfoAssignmentTarget</dependency>
      </dependencies>
      <canonicalItemSource>LogoMetaInfoAssignmentCanonical</canonicalItemSource>
      <attributes>
        <attribute>
          <name ln="3">logoAssignment</name>
          <transformationExpression ln="4">integrationKey</transformationExpression>
        </attribute>
        <attribute>
          <name ln="5">logoDummy</name>
          <transformationExpression ln="6">parentCodes.isEmpty() ? null : integrationKey.iterator().next()</transformationExpression>
        </attribute>
      </attributes>
    </item>

  "" should "target Item be" in {
    val item = Item(targetItem)
    val expectedTransformationElementlogoAssignment = Option(TransformationElement(Option.empty, SpelItem("integrationKey"), "4"))
    val expectedTransformationElementDummy1 = Option(TransformationElement(Option.empty, SpelItem("integrationKey"), "6"))
    val expectedTransformationElementDummy2 = Option(TransformationElement(Option.empty, SpelItem("parentCodes"), "6"))

    val expectedAttributes = Set(new Attribute("logoAssignment", expectedTransformationElementlogoAssignment, "3"),
      new Attribute("logoAssignment", expectedTransformationElementDummy1, "3"),
      new Attribute("logoAssignment", expectedTransformationElementDummy2, "3"))

    val expected = new Item("MediaLogoMetaInfoTarget", expectedAttributes, "1", Option("LogoMetaInfoAssignmentCanonical"), Seq("LogoMetaInfoAssignmentTarget"))


    assert(item.equals(expected))

  }

  val targetItemWithSpelError =
    <item>
      <type ln="1">MediaLogoMetaInfoTarget</type>
      <exportCode>MediaLogoMetaInfo</exportCode>
      <description>PCM representation of a media logo meta info</description>
      <updatable>true</updatable>
      <dependencies>
        <dependency ln="2">LogoMetaInfoAssignmentTarget</dependency>
      </dependencies>
      <canonicalItemSource>LogoMetaInfoAssignmentCanonical</canonicalItemSource>
      <attributes>
        <attribute>
          <name ln="3">logoDummy</name>
          <transformationExpression ln="4">parentCodes.isEmpty() ?)) null : integrationKey.iterator().next()</transformationExpression>
        </attribute>
      </attributes>
    </item>

  "target Item of erroneous spel" should "be" in {
    val item = Item(targetItemWithSpelError)

    val expr = "parentCodes.isEmpty() ?)) null : integrationKey.iterator().next()"
    val exprErrorMsg="Expression [parentCodes.isEmpty() ?)) null : integrationKey.iterator().next()] @23: EL1043E: Unexpected token. Expected 'colon(:)' but was 'rparen())'"
    val expectedSpelItem = new SpelItem(expr, Some(exprErrorMsg), Seq(), None)
    val expectedTransformationElement = Option(TransformationElement(Option.empty, expectedSpelItem, "4"))

    val expectedAttributes = Set(
      new Attribute("logoDummy", expectedTransformationElement, "3"))

    val expected = new Item("MediaLogoMetaInfoTarget", expectedAttributes, "1", Option("LogoMetaInfoAssignmentCanonical"), Seq("LogoMetaInfoAssignmentTarget"))

    assert(item.equals(expected))

  }

  val rawToCanonicalItem =
    <item>
      <type ln="1">ItemStatusCanonical</type>
      <attributes>
        <attribute>
          <name ln="2">statusCode</name>
          <transformations>
            <transformation>
              <rawSource ln="3">CategoryRaw</rawSource>
              <expression spel="true" ln="4">T(org.apache.commons.lang3.StringUtils).isEmpty(statusCode) ? null : statusCode.toUpperCase()</expression>
            </transformation>
          </transformations>
        </attribute>
      </attributes>
    </item>

   "" should "raw to canonical be" in {
    val item = Item(rawToCanonicalItem)
    val raw = Option(RawSource("CategoryRaw", "3"))
    val expectedTransformation = Option(TransformationElement(raw, SpelItem("T(org.apache.commons.lang3.StringUtils).isEmpty(statusCode) ? null : statusCode.toUpperCase()"), "4"))
    val expectedAttributes = Set(new Attribute("statusCode", expectedTransformation, "2"))
    val expected = new Item("ItemStatusCanonical", expectedAttributes, "1", Option.empty, Seq())

    assert(item.equals(expected))

  }


}
