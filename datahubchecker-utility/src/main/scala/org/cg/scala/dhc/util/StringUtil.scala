package org.cg.scala.dhc.util

import org.cg.scala.dhc.domelments.Const

/**
  * Created by ssmertnig on 4/28/17.
  */
object StringUtil {

  def indent(s: String, n: Int): String = {
    val patch = " " * n
    patch + s.replaceAll("\n", patch + "\n")
  }

  def indent (s: String): String = indent(s, Const.Indent)

  def list2String[T](s: Seq[T]) = s.map(e => e.toString()).mkString

  def list2String[T](s: Set[T]) = s.map(e => e.toString()).mkString

}
