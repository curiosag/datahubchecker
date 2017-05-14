package org.cg.scala.dhc.domelments

import org.cg.scala.dhc.util.StringUtil

import scala.xml.Node

/**
  * Created by ssmertnig on 4/22/17.
  */


case class Item(val itemType: String, val attributes: Set[Attribute], val ln: String, val canonicalItemSource: Option[String], val dependencies: Seq[String]) {
  override def equals(o: scala.Any): Boolean =
    o.isInstanceOf[Item] && {
      val other = o.asInstanceOf[Item]
      itemType.equals(other.itemType) &&
        attributes.equals(other.attributes) &&
        canonicalItemSource.equals(other.canonicalItemSource)
      ln.equals(other.ln)
    }

  override def toString: String =
    s"\nITEM($itemType, ln $ln, CanonicalSource: ${canonicalItemSource.getOrElse("()")})\n" +
           StringUtil.indent(StringUtil.list2String(attributes)) +
        s"item dependencies: ${StringUtil.list2String(dependencies)}"
}

object Item {

  def apply(item: Node): Item = {
    val itemType = (item \ "type").text
    val ln = ((item \ "type") \ "@ln").text
    val attributes = (item \ "attributes" \ "attribute").map(a => Attribute(a)).toSet

    def canoicalSourceNode = (item \ "canonicalItemSource")

    val canonicalItemSource =
      if (canoicalSourceNode.isEmpty)
        Option.empty
      else
        Option(canoicalSourceNode.text)

    val dependencies = (item \ "dependencies" \ "dependency").map(d => d.text)

    Item(itemType, attributes, ln, canonicalItemSource, dependencies)
  }


}
