package org.cg.scala.dhc.domelments

/**
  * Created by ssmertnig on 4/21/17.
  */
object Const {
  val Indent: Int = 3

  val extensionSuffix = "-datahub-extension.xml"
  val rawExtension = "-raw" + extensionSuffix
  val canonicalExtension = "-canonical" + extensionSuffix
  val targetExtension = "-target" + extensionSuffix

  val attributeNotFound = new Attribute("", Option.empty, "")
  val itemNotFound = new Item("", Set(), "", Option.empty, Seq())
}
