package org.cg.scala.dhc.domelments

import org.cg.scala.dhc.util.StringUtil

import scala.xml.{Node, NodeSeq}

case class TransformationElement(rawSource: Option[RawSource], val spelItem: SpelItem, val expressionLn: String) {
  override def equals(o: scala.Any): Boolean = o.isInstanceOf[TransformationElement] && {
    val other = o.asInstanceOf[TransformationElement]
    rawSource.equals(other.rawSource) &&
      spelItem.equals(other.spelItem) &&
      expressionLn.equals(other.expressionLn)
  }

  override def toString: String = s"TRANSFORMATION ln $expressionLn spel ${spelItem.toString} expr $spelItem " +
   "\n" + StringUtil.indent(rawSource.getOrElse("").toString, Const.Indent)

}

object TransformationElement {

  private def apply(t: Node): TransformationElement = {
    val a = t.toString()

    if (!(t \ "rawSource").isEmpty)
      fromRawItemDefinition(t)
    else if (!(t \ "transformationExpression").isEmpty)
      fromTargetItemDefinition(t)
    else throw new IllegalArgumentException("can't retrieve transformation expression from " + t.toString())
  }

  def fromTargetItemDefinition(t: Node) = {
    val transformationNode = (t \ "transformationExpression")
    val expression = transformationNode.text
    val expressionLn = (transformationNode \ "@ln").text

    TransformationElement(Option.empty, SpelItem(expression), expressionLn)
  }

  private def fromRawItemDefinition(t: Node) = {
    val expressionNode = (t \ "expression")

    val expression = expressionNode.text
    val expressionLn = (expressionNode \ "@ln").text

    TransformationElement(Option(RawSource(t)), SpelItem(expression), expressionLn)
  }


  def get(a: Node) = {
    val transformationNode = {
      val t = (a \ "transformations" \ "transformation")
      if (!t.isEmpty)
        t
      else {
        if (!(a \ "transformationExpression").isEmpty)
          a
        else NodeSeq.Empty
      }
    }

    val sa = a.toString()

    val transformations = transformationNode.map(x => {
      TransformationElement(x)
    })

    val transformation = if (transformations.isEmpty) Option.empty else Option(transformations.head)
    transformation
  }
}


