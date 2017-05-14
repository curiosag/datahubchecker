package org.cg.scala.dhc.domelments

import org.cg.scala.dhc.util.{FileInfo, StringUtil, xmlUtil}

import scala.collection.immutable.Seq
import scala.xml.Elem

/**
  * Created by ssmertnig on 4/21/17.
  */
abstract class Extension(val file: FileInfo, extensions: Array[Extension]) {

  protected val xml = xmlUtil.getXml(file)

  val dependenciesDefined = extractDependencyDefinitions(xml)

  val dependenciesResolved = resolveDependencies

  val dependenciesUnresolved = getDependenciesUnresolved

  def getItems(): List[Item]

  def getErrors(): List[String]

  val fileName = file.name

  private def resolveDependencies = {
    val pickDependency = (d: String) => extensions.find(ext => ext.fileName.startsWith(d))
    dependenciesDefined
      .filter(f => pickDependency(f).isDefined)
      .map(d => pickDependency(d).get)
  }

  /**
    *
    * @return Map[Itemname, Map[Attributename, Attribute]]
    **/
  protected def canonicalItemsToMap: Map[String, Map[String, Attribute]] = {
    val canonicalItems = dependenciesResolved
      .filter(c => c.fileName.endsWith(Const.canonicalExtension))
      .flatMap(d => d.getItems())

    canonicalItems
      .map(x => (x.itemType, attributesToMap(x.attributes))
      ).toMap
  }

  protected def attributesToMap(attributes: Set[Attribute]): Map[String, Attribute] =
    attributes.map(x => x.name -> x).toMap


  /**
    *
    * @param itemsWithCanonicalReference
    * @return Set[(Itemname, Attribute)]
    */
  protected def getItems2AttributesMapping(itemsWithCanonicalReference: Set[Item]): Set[(String, Attribute)] = {
    itemsWithCanonicalReference.flatMap(i => i.attributes.map(a => (i.itemType, a)))
  }

  protected def getErrors4ItemsWithoutCanonicalReference(itemsWithoutCanonicalReference: Seq[Item]) = {
    itemsWithoutCanonicalReference.map(i =>
      s"E01 Target item '${i.itemType}' at ln ${i.ln}: no canonical item for canonical item source found")
  }

  protected def itemAttributeMatches(a: (String, Attribute), c: Item) = {
    a._1.equals(c.itemType) && c.attributes.find(ca => a._2.name.equals(ca.name)).isDefined
  }

  protected def findMatchingCanonicalItems(canonicalItems: Set[Item], a: (String, Attribute)) = {
    canonicalItems.find(c => itemAttributeMatches(a, c)).isDefined
  }

  private def filterCandidateDependencies(f: FileInfo, dependenciesToResolve: Seq[String]) = {
    dependenciesToResolve.find(dep => f.canonicalPath.endsWith(dep)).isDefined
  }

  private def extractDependencyDefinitions(e: Elem) = {
    val extracted = e \ "dependencies" \ "dependency" \ "extension"
    extracted.map(_.text + Const.extensionSuffix)
  }


  protected def getErrorMsgs4SpelParsingErrors(spelItemsWithParseError: Set[(String, String, String)]) = {
    val spelItemsWithParseErrorErrors = spelItemsWithParseError.map(i =>
      s"E04 Target attribute '${i._1}' at ln ${i._2}: spel parse error : '${i._3}' ")
    spelItemsWithParseErrorErrors
  }

  private def getDependenciesUnresolved = {
    // strangely diff produced a false positive in case of documentsLogos-canonical-datahub-extension.xml
    val resolvedFileNames = dependenciesResolved.map(r => r.file.name)
    dependenciesDefined.filter(d => resolvedFileNames.forall(r => !r.endsWith(d)))
  }

  override def toString: String = {
    "\n" +
      s"""
         |EXTENSION ${file.name}
         |...dependencies defined ${StringUtil.indent(StringUtil.list2String(dependenciesDefined))}
         |...extensions unresolved ${StringUtil.indent(StringUtil.list2String(dependenciesUnresolved))}
         |...extensions resolved ${StringUtil.indent(StringUtil.list2String(dependenciesResolved.map(r => r.file.name)))}""".stripMargin +
      s"${StringUtil.indent(StringUtil.list2String(getItems()))}"
  }

}


object Extension {

  def apply(file: FileInfo, extensions: Array[Extension]): Extension = {
    val name = file.name

    if (name.endsWith(Const.rawExtension))
      RawExtension(file, extensions)
    else if (name.endsWith(Const.canonicalExtension))
      CanonicalExtension(file, extensions)
    else if (name.endsWith(Const.targetExtension))
      TargetExtension(file, extensions)
    else
      throw new IllegalArgumentException("can't check unknown file type " + name)
  }

}