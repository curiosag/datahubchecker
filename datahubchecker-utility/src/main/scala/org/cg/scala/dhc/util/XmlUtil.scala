package org.cg.scala.dhc.util

import java.io.File

/**
  * Created by ssmertnig on 4/22/17.
  */
object xmlUtil {

  def getXml(file: FileInfo) = XML.loadString(XmlAnnotator.annotate(FileUtil.fileToString(new File(file.canonicalPath))))

}
