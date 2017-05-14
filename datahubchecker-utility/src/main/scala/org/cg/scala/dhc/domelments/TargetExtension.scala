package org.cg.scala.dhc.domelments

import org.cg.scala.dhc.util.FileInfo

/**
  * Created by ssmertnig on 4/28/17.
  */
case class TargetExtension(override val file: FileInfo, val extensions: Array[Extension])
  extends Extension(file, extensions) {

  override def getItems(): List[Item] = {
    val systems = (xml \ "targetSystems" \ "targetSystem")
    val items = systems.flatMap(s => s \ "targetItems" \ "item")

    items.map(i => Item(i)).toList
  }


  override def getErrors(): List[String] = {
    val items = getItems()

    /*  Canonical            <->        Target
         Itemname                     item/canonicalItemSource
         Attributename                item/attribute/transformationExpression (spel)/spel items
    */
    val canonicalItemAttributeMap = canonicalItemsToMap
    val itemsWithoutCanonicalReference = items.filter(i => canonicalItemAttributeMap.get(i.canonicalItemSource.getOrElse("")).isEmpty)
    val itemsWithCanonicalReference = items.toSet -- itemsWithoutCanonicalReference.toSet

    val attributeNSpelItems = flattenAttributesNSpelItems(itemsWithCanonicalReference)
    val spelItemsWithParseError = getSpelItemsWithParseError(attributeNSpelItems)

    val canonicalItemRefsFromSpelExpr = flattenItemAttributeNSpelItemSubStructure(attributeNSpelItems).toList
    val invalidCanonicalItemRefsFromSpelExpr = canonicalItemRefsFromSpelExpr.filter(i => canonicalItemAttributeMap.get(i.refItemName).isEmpty)
    val validCanonicalItemRefsFromSpelExpr = canonicalItemRefsFromSpelExpr.toSet -- invalidCanonicalItemRefsFromSpelExpr


    val scanonicalItemRefsFromSpelExpr = canonicalItemRefsFromSpelExpr.toString()
    val sinvalidCanonicalItemRefsFromSpelExpr = invalidCanonicalItemRefsFromSpelExpr.toString()


    val flatAttributeNSpelItems = flattenItemAttributeNSpelItem(attributeNSpelItems) ++ validCanonicalItemRefsFromSpelExpr
    val spelItemsWithoutRefAttributes = flatAttributeNSpelItems.filter(a => canonicalItemAttributeMap(a.refItemName).get(a.spelIdentifier).isEmpty)

    (getErrors4ItemsWithoutCanonicalReference(itemsWithoutCanonicalReference)
      ++ getErrorMsgs4SpelParsingErrors(spelItemsWithParseError)
      ++ getErrors4MissingRawRefAttribute(spelItemsWithoutRefAttributes)
      ++ getErrors4InvalidCanonicalItemRefsFromSpelExpr(invalidCanonicalItemRefsFromSpelExpr))
      .toList
  }

  protected def getSpelItemsWithParseError(attributeNSpelItems: Set[(Item, Attribute, SpelItem)]) = {
    attributeNSpelItems
      .filter { case (_, att, spelItem) => spelItem.error.isDefined }
      .map { case (_, att, spelItem) => (att.name, att.ln, spelItem.error.get) }
  }

  protected def flattenAttributesNSpelItems(itemsWithCanonicalReference: Set[Item]) = {
    for (item <- itemsWithCanonicalReference;
         attribute <- item.attributes;
         transformation <- attribute.transformation
    ) yield (item, attribute, transformation.spelItem)
  }

  val SRC_MISSING = "Canonical item source missing"

  protected def flattenItemAttributeNSpelItem(attributeNSpelItems: Set[(Item, Attribute, SpelItem)]) = {
    attributeNSpelItems
      .filter { case (_, _, spelItemCheck) => spelItemCheck.error.isEmpty }
      .flatMap { case (item, att, spelItem) =>
        spelItem.identifiers
          .map(spelIdentifier => FlatItemAttributeNSpelItem(item.canonicalItemSource.getOrElse(SRC_MISSING), att.name, att.ln, spelIdentifier))
      }
  }

  protected def flattenItemAttributeNSpelItemSubStructure(attributeNSpelItems: Set[(Item, Attribute, SpelItem)]) = {
    attributeNSpelItems
      .filter { case (_, _, spelItemCheck) => spelItemCheck.itemReferences.isDefined }
      .flatMap { case (item, att, spelItem) =>
        spelItem.itemReferences.get
          .map(itemRef => FlatItemAttributeNSpelItem(itemRef.itemName, att.name, att.ln, itemRef.attributeName))
      }
  }

  protected def getErrors4MissingRawRefAttribute(spelItemsWithoutRefAttributes: Set[FlatItemAttributeNSpelItem]) = {
    spelItemsWithoutRefAttributes.map(i =>
      s"E02 Target attribute '${i.attributeName}' at ln ${i.ln}: no canonical reference for identifier '${i.spelIdentifier}' found")
  }

  private def getErrors4InvalidCanonicalItemRefsFromSpelExpr(invalidCanonicalItemRefsFromSpelExpr: List[FlatItemAttributeNSpelItem]) =
    invalidCanonicalItemRefsFromSpelExpr.map(i =>
      s"E01 Target item '${i.refItemName}' in spel expression for attribute '${i.attributeName}' at ln ${i.ln}: no canonical item found")

}
