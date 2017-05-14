package org.cg.scala.dhc.domelments

import org.cg.scala.dhc.util.{FileInfo, StringUtil}

/**
  *   Raw item                                Raw to Canonical map                                                       Referenced Canonical item in canonical.xml
  *   type maps to rawSource and
  *   attribute name maps to expression
  *
  *  <item>                                   <item>                                                                    <item>
  *    <type>AdvantageRaw</type> ...... != .. <type>AdvantageTypeCanonical</type> ......... = ..................        <type>AdvantageTypeCanonical</type>
  *       <attributes>      .                  <attributes>                                                                <attributes>
  *                         .                    <attribute>                                                                 <attribute>
  *                         .                      <name>code</name>               ......... = ..................               <name>code</name>
  *                         ......                 <transformations>
  *         <attribute>          .                   <transformation>
  *                              ......... = ......   <rawSource>AdvantageRaw</rawSource>
  *            <name>smthgRaw</name> ..... = ......   <expression spel="true">advantageType == null...</expression>
  *         <attribute>                             </transformation>
  *                                               </transformations>
  *                                             </attribute>
  *                                           </attributes>
  *                                         </item>
  *
  *
  */

//@formatter:on

case class RawExtension(override val file: FileInfo, val extensions: Array[Extension])
  extends Extension(file, extensions) {


  override def getItems(): List[Item] = {
    val items = (xml \ "rawItems" \ "item")

    items.map(i => Item(i)).toList
  }

  def getR2cItems(): List[Item] = {
    val items = (xml \ "canonicalItems" \ "item")

    items.map(i => Item(i)).toList
  }

  override def toString: String = {
    super.toString +
      s"RAW TO CANONICAL MAPPING\n" + s"${StringUtil.indent(StringUtil.list2String(getR2cItems()))}"
  }

  override def getErrors(): List[String] = {
    getErrorsFromRawToCanonicalDefinitions
  }


  def getRawItemAttributeMap(raw: List[Item]) =
    raw
      .map(x => (x.itemType, attributesToMap(x.attributes))
      ).toMap


  private def getErrorsFromRawToCanonicalDefinitions = {
    // abbreviation 'r2c' always refers to the raw to canonical items definition in raw.xml coming after raw items

    val raw = getItems()
    val rawItemAttributeMap: Map[String, Map[String, Attribute]] = getRawItemAttributeMap(raw)

    val flatR2cItemNAttributeRefs = getFlatR2cItemNAttributeRefs

    val flatR2cInvalidItemRefs =
    flatR2cItemNAttributeRefs
      .filter(i => rawItemAttributeMap.get(i.rawSource).isEmpty)

    val flatR2cInvalidAttributeRefs =
      flatR2cItemNAttributeRefs
        .filter(i => rawItemAttributeMap.get(i.rawSource).isEmpty ||
          rawItemAttributeMap(i.rawSource).get(i.spel).isEmpty)

    getErrorMsgs4InvalidItemRefs(flatR2cInvalidItemRefs) ++
      getErrorMsgs4SpelParsingErrors(getFlatR2cSpelParsingErrors.toSet) ++
      getErrorsMsgs4InvalidAttributeRefs(flatR2cInvalidAttributeRefs.toSet)

  }

  private def getR2cAttributesWithTransformation = getR2cItems()
    .flatMap(i => i.attributes)
    .filter(i => i.transformation.isDefined);


  def getFlatR2cSpelParsingErrors = {
    for (attribute <- getR2cAttributesWithTransformation;
         spelError <- attribute.transformation.get.spelItem.error
         if attribute.transformation.isDefined && attribute.transformation.get.spelItem.error.isDefined
    ) yield (attribute.name, attribute.ln, spelError)
  }

  private def getFlatR2cItemNAttributeRefs = {
    for (attribute <- getR2cAttributesWithTransformation;
         spel <- attribute.transformation.get.spelItem.identifiers

         if attribute.transformation.isDefined && attribute.transformation.get.rawSource.isDefined

    ) yield
      R2cDefinitionData(
        attribute.name,
        attribute.ln,
        attribute.transformation.get.rawSource.get.rawSource,
        spel)
  }

  def getErrorMsgs4InvalidItemRefs(info: List[R2cDefinitionData]) =
    info.map(i => s"E07 rawSource '${i.rawSource}' not found in canonical attribute ${i.attribute} at ln ${i.ln}")

  def getErrorsMsgs4InvalidAttributeRefs(info: Set[R2cDefinitionData]) =
    info.map(i => s"E08 no raw attribute found for spel element '${i.spel}', defined in canonical attribute ${i.attribute} at ln ${i.ln}")

}
