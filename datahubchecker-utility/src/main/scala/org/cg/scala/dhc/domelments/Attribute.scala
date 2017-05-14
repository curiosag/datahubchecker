package org.cg.scala.dhc.domelments

import org.cg.scala.dhc.util.StringUtil

import scala.xml.Node

case class Attribute(val name: String, val transformation: Option[TransformationElement], val ln: String) {
  override def equals(o: scala.Any): Boolean = o.isInstanceOf[Attribute] && {
    val other = o.asInstanceOf[Attribute]
    name.equals(other.name) &&
      transformation.equals(other.transformation) &&
      ln.equals(other.ln)
  }

  override def toString: String = {
    s"""ATTRIBUTE $name ln $ln
        ${StringUtil.indent(transformation.getOrElse("").toString(), Const.Indent)}
     """.stripMargin
  }
}

object Attribute {

  def apply(a: Node): Attribute = {
    val name = (a \ "name").text
    val ln = (a \ "name" \ "@ln").text

    val transformation: Option[TransformationElement] = TransformationElement.get(a)

    Attribute(name, transformation, ln)
  }
}