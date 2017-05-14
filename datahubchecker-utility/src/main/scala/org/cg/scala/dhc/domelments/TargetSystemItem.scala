package org.cg.scala.dhc.domelments

/**
  * Created by ssmertnig on 4/28/17.
  */
case class TargetSystemItem(val item: String, val canonicalItemSource: String, dependencies: Seq[(String, String)], itemAttributes: Seq[TargetItemAttribute]){

}
