package org.cg.scala.dhc.domelments

import scala.xml.Node

/**
  * Created by ssmertnig on 4/22/17.
  */

case class RawSource(val rawSource: String, val rawSourceLn: String) {
  override def equals(o: scala.Any): Boolean = o.isInstanceOf[RawSource] && {
    val other = o.asInstanceOf[RawSource]
    rawSource.equals(other.rawSource) &&
      rawSourceLn.equals(other.rawSourceLn)
  }

  override def toString: String = s"RAW SOURCE $rawSource ln $rawSourceLn"
};

object RawSource {
  def apply(t: Node): RawSource = {
    val rawSource = (t \ "rawSource").text
    val rawSourceLn = (t \ "rawSource" \ "@ln").text
    new RawSource(rawSource, rawSourceLn)
  }
}