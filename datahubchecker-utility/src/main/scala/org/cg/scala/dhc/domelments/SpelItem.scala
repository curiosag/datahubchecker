package org.cg.scala.dhc.domelments

import java.util.Optional

import org.cg.spelstuff.{FilterResult, ParseResult, Spel}
import org.springframework.expression.spel.SpelNode

import scala.collection.JavaConverters._

case class FlatItemAttributeNSpelItem(refItemName: String, attributeName: String, ln: String, spelIdentifier: String)

case class ItemAttributeReference(val itemName: String, val attributeName: String)

/**
  * Created by ssmertnig on 5/1/17.
  */
class SpelItem(value: String, val error: Option[String], val identifiers: Seq[String], val itemReferences: Option[List[ItemAttributeReference]]) {
  override def equals(o: scala.Any): Boolean = o.isInstanceOf[SpelItem] && {
    val other = o.asInstanceOf[SpelItem]
    error.equals(other.error) &&
      identifiers.equals(other.identifiers)
  }

  override def toString: String = s"SPEL IDENT $value ${error.toString}"

}

object SpelItem {
  val SINGLE_NAME_REGEX = "^[A-Za-z0-9\\-_]*$".r()
  val RESOLVE = "resolve("

  def apply(value: String) = {

    if (SINGLE_NAME_REGEX.findFirstIn(value).isDefined)
      new SpelItem(value, None, Seq(value), None)
    else if (value.toLowerCase.indexOf(RESOLVE) >= 0)
      parseResolve(value)
    else {
      val filtered = Spel.of(value).filterPropertyOrFieldReference()
      if (filtered.parseError.isPresent)
        getParseErrorItem(value, filtered.parseError.get())
      else
        new SpelItem(value, None, filtered.fieldsOrProperties.asScala.toSeq.map(f => f.getName), None)
    }
  }

  def parseResolve(spelExpr: String) = {
    val parseResult = Spel.of(spelExpr).parse()
    if (parseResult.parseError.isPresent)
      getParseErrorItem(spelExpr, parseResult.parseError.get())
    else
      new SpelItem(spelExpr, None, Seq(), getReferencesFromSpel(spelExpr, parseResult.root))
  }

  def getReferencesFromSpel(spelExpr: String, root: Optional[SpelNode]) =
    if (!root.isPresent)
      None
    else
      Some(collectReferencesFromSpel(spelExpr, root.get(), List()))

  def getNestedResolveNode(spelExpr: String, node: SpelNode) =
    if (node.getChild(1).getClass.getName.equals("org.springframework.expression.spel.ast.OpPlus") && getValue(spelExpr, node.getChild(1).getChild(1)).startsWith(RESOLVE))
      Some(node.getChild(1).getChild(1))
    else
      None

  // see spel.gif in the project root for the ast structure dealt with here
  def collectReferencesFromSpel(spelExpr: String, node: SpelNode, list: List[ItemAttributeReference]): List[ItemAttributeReference] = {
    if (node.getChildCount != 2 || !getValue(spelExpr, node).startsWith(RESOLVE))
      list
    else {
      val attributeRef = getValue(spelExpr, node.getChild(1))
      if (node.getChild(0).getChildCount != 2)
        list
      else {
        val itemRefNode = node.getChild(0).getChild(0);
        val itemRef = getValue(spelExpr, itemRefNode);
        val itemAttributeRef = new ItemAttributeReference(itemRef, attributeRef)
        val nestedResolveNode = getNestedResolveNode(spelExpr, node.getChild(0))
        if (nestedResolveNode.isDefined)
          collectReferencesFromSpel(spelExpr, nestedResolveNode.get, itemAttributeRef :: list)
        else
          itemAttributeRef :: list
      }
    }

  }

  def getValue(spelExpr: String, node: SpelNode) = spelExpr.substring(node.getStartPosition, node.getEndPosition).replace("'", "")

  private def getParseErrorItem(value: String, error: String) = {
    new SpelItem(value, Some(error), Seq(), None)
  }

}

