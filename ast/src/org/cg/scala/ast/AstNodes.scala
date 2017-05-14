package org.cg.scala.ast
import scala.collection.JavaConverters._
/**
  * Created by ssmertnig on 5/3/17.
  */
class Token(val token: String)
{
  def value() = token
}

case class Id(override val token: String) extends Token(token)

sealed abstract class AstNode

case class AstStructuralNonTerminal(name: String, children: List[AstNode]) extends AstNode
case class AstNonTerminal(symbol: Token, children: List[AstNode]) extends AstNode
case class AstTerminal(symbol: Token) extends AstNode

object AstNodes{
  def toScala(list: java.util.List[AstNode]): List[AstNode] = list.asScala.toList
}
