package org.cg.scala.dhc

import java.io.File

import org.cg.scala.dhc.util.{FileInfo, FileUtil}
import org.cg.scala.dhc.domelments.{Const, Extension}

import scala.collection.mutable.ArrayBuffer
import scala.util.matching.Regex

class DatahubChecker(val path: String) {

  val fileRegex = "(-raw|-canonical|-target)-datahub-extension\\.xml\\z"

  val errorFilters: Array[Regex] = getErrorFilters()

  def check(): Array[String] = {
    val (extensions, files, hasChanged) = resolve(path, Array(), Array())
    printErrors(extensions)
  }

  def checkAgain(): Unit = {
    reResolve(500, Array(), Array())
  }

  def getErrorFilters() = {
    val file = new File(path + "/datahubchecker.errorfilters")
    if (file.exists()) {
      FileUtil.fileToString(file).split('\n').map(filter => filter.r)
    }
    else
      Array[Regex]()
  }

  private def reResolve(interval: Int, recentExtension: Array[Extension], recentFiles: Array[FileInfo]): Unit = {
    val (extensions, files, hasChanged) = resolve(path, recentExtension, recentFiles)

    if (hasChanged) {
      val errors = printErrors(extensions)
      if (errors.length > 0)
        println("\n==================================================================================================================")
    }

    if (trySleep(interval))
      reResolve(interval, extensions, files)
    else
      Unit
  }

  private def trySleep(interval: Int) = try {
    Thread.sleep(interval)
    true
  } catch {
    case e: InterruptedException => false
  }

  private def notify(extension: Extension) = {
    print(".")
    extension
  }

  private def resolve(path: String, recentExtension: Array[Extension], recentFiles: Array[FileInfo]) = {
    val pool = FileUtil.recursiveListFileInfos(path, fileRegex).filter(f => f.canonicalPath.contains("/src/"))

    val newOrChangedFiles: Array[FileInfo] = getNewOrChangedFiles(pool, recentFiles)

    val isCanonical = (f: FileInfo) => f.name.endsWith(Const.canonicalExtension)
    val canonicalFilesToResolve = newOrChangedFiles.filter(f => isCanonical(f))
    val otherFilesToResolve = newOrChangedFiles.filter(f => !isCanonical(f))

    val extensionsUnchanged = recentExtension.filter(e => !newOrChangedFiles.find(r => r.name.equals(e.fileName)).isDefined)
    val canonicalExtensionsResolved = canonicalFilesToResolve.map(f => notify(Extension(f, Array())))
    val otherExtensionsResolved = otherFilesToResolve.map(f => notify(Extension(f, canonicalExtensionsResolved ++ extensionsUnchanged)))

    val result = (extensionsUnchanged ++ canonicalExtensionsResolved ++ otherExtensionsResolved, pool, newOrChangedFiles.length > 0)
    result;
  }

  private def getNewOrChangedFiles(pool: Array[FileInfo], recentFiles: Array[FileInfo]) = {
    val namesEqual = (p: FileInfo, r: FileInfo) => p.canonicalPath.equals(r.canonicalPath)
    val newFiles = pool.filter(p => recentFiles.find(r => namesEqual(p, r)).isEmpty)
    val changedFiles = pool.filter(p => recentFiles.find(r => namesEqual(p, r) && r.lastChanged != p.lastChanged).isDefined)
    val filesToResolve = newFiles ++ changedFiles
    filesToResolve
  }

  private def getErrors(extensions: Seq[Extension]) = {
    val targets = extensions.filter(e => !e.fileName.endsWith(Const.canonicalExtension))
    val errors = targets
      .flatMap(t => t.getErrors().map(error => s"\n${t.fileName} ${error}"))
      .filter((error => !errorFilters.find(re => re.findFirstIn(error).isDefined).isDefined))
    errors.toArray
  }

  private def printErrors(extensions: Seq[Extension]) = {
    val errors = getErrors(extensions)
    errors.foreach(e => println(e))
    errors
  }

  private def printFileDependencies(fileDependencies: Array[Extension]) = {
    println()
    fileDependencies.foreach(l => {
      printDependencies(l, 0)
      println()
    })
  }

  private def printDependencies(dependency: Extension, level: Int): Unit = {
    if (level == 0 || dependency.dependenciesDefined.size > 0)
      println(blanks(level + 1) + "Deps of " + dependency.file.canonicalPath)

    dependency.dependenciesResolved.foreach(l => println(blanks(level + 1 * 2) + l.file.canonicalPath))
    printUnresolvedDependencies(dependency.dependenciesUnresolved, level);

    dependency.dependenciesResolved.foreach(d => printDependencies(d, level + 1))
  }

  private def printUnresolvedDependencies(unresolved: Seq[String], level: Int): Unit = {
    unresolved.foreach(u => println(blanks(level + 1 * 2) + "CAN'T RESOLVE " + u))
  }

  private def blanks(i: Int) = List.fill(i * 3)(" ").mkString

}


object datahubchecker {
  def main(args: Array[String]): Unit = {
    if (args.length == 0)
      print("usage: datahubchecker path")
    else
      new DatahubChecker(args.head).checkAgain()
    //printFileDependencies(extensions.toArray)
  }

}

