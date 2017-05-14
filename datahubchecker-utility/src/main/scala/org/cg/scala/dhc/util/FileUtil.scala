package org.cg.scala.dhc.util

import java.io.{ByteArrayOutputStream, File, FileInputStream}

import scala.util.matching.Regex
import scala.xml.Elem
import scala.xml.factory.XMLLoader
import javax.xml.parsers.SAXParser

object XML extends XMLLoader[Elem] {
  override def parser: SAXParser = {
    val f = javax.xml.parsers.SAXParserFactory.newInstance()
    f.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
    f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    f.newSAXParser()
  }
}

object FileUtil {

  def recursiveListFileInfos(baseDir: String, regex: String): Array[FileInfo] =
    recursiveListFiles(baseDir, regex).map(f => new FileInfo(f.getName, f.getCanonicalPath, f.lastModified()))

  def recursiveListFiles(baseDir: String, regex: String): Array[File] = recursiveListFiles(new File(baseDir), new Regex(regex))

  def recursiveListFiles(f: File, r: Regex): Array[File] = {
    val these = f.listFiles
    if (these != null) {
      val good = these.filter(f => r.findFirstIn(f.getName).isDefined)
      good ++ these.filter(_.isDirectory).flatMap(recursiveListFiles(_, r))
    }
    else
      new Array[File](0)
  }

  def fileToString(file: File) = {
    val inStream = new FileInputStream(file)
    val outStream = new ByteArrayOutputStream
    try {
      var reading = true
      while (reading) {
        inStream.read() match {
          case -1 => reading = false
          case c => outStream.write(c)
        }
      }
      outStream.flush()
    }
    finally {
      inStream.close()
    }
    new String(outStream.toByteArray())
  }

}
