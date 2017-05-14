package org.cg.scala.dhc.domelments

import org.cg.scala.dhc.util.FileInfo

/**
  * Created by ssmertnig on 4/28/17.
  */
case class CanonicalExtension(override val file: FileInfo, val extensions: Array[Extension])
  extends Extension(file, extensions) {

  override def getItems(): List[Item] = {
    val items = (xml \ "canonicalItems" \ "item")

    items.map(i => Item(i)).toList
  }

  override def getErrors(): List[String] = List()
}
