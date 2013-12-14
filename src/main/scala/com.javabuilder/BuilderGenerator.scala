package com.javabuilder

import java.lang.String
import java.io.{FileWriter, File}
import java.util
import scala.sys

object BuilderGenerator {
  var sourceDir: File = null

  def main(args: Array[String]) {
    if (args != null && args.length == 2) {
      sourceDir = new File(args(0))
      processFile(args(1))
    }
  }

  def processFile(arg: String) {
    if (arg.isEmpty || arg.contains("VersionedEntityImpl") || arg.contains("Serializable"))
      throw new IllegalArgumentException("The filename needs to be valid")
    val javaFiles = findFiles(_.getName endsWith arg)(sourceDir)
    print("files found " + javaFiles.length + " for the given pattern:" + arg + sys.props("line.separator"))
    javaFiles.foreach(e => applyMyLogic(e))
  }

  val classDefinition = "public class %sBuilder"
  val builderNameExpression = "%sBuilder"
  val itemOpenClass = "{"
  val itemCloseClass = "    }" + sys.props("line.separator")
  val returnThis = "        return this;"
  val methodSetter = "        this.instance.set%s(%s);" + sys.props("line.separator")
  var methodNamePattern = "    public %s with%s(%s %s)"
  var instancePattern = "    private %s instance;" + sys.props("line.separator") + sys.props("line.separator")
  var buildFunction = "    public %s build() {\n       return instance;\n    }" + sys.props("line.separator") + sys.props("line.separator")
  var constructorPattern = "    public %s() {\n        instance = new %s();\n    }" + sys.props("line.separator") + sys.props("line.separator")

  var source = null

  def getBaseClass(file: File): String = {
    var baseClass: String = null
    for (line <- scala.io.Source.fromFile(file).getLines()) {
      if (line.contains("public class") && line.contains("extends")) {
        baseClass = line.split("extends")(1).replace("{", "").trim + ".java"
        if (baseClass.contains("VersionedEntityImpl") || baseClass.contains("Serializable"))
          baseClass = ""
      }
    }
    baseClass
  }

  def gatherProperties(file: File, recursive: Boolean = false): util.ArrayList[JavaProp] = {
    val source = scala.io.Source.fromFile(file);
    var propList: util.ArrayList[JavaProp] = new util.ArrayList[JavaProp]();

    for (line <- source.getLines()) {
      line match {
        case x if x.contains("private") => (line: String) {
          val prop = new JavaProp(getVariableNameFromLine(line)(1), getVariableNameFromLine(line)(0))
          propList.add(prop)
          1
        }
        case _ =>
      }
    }
    propList
  }

  def getPackage(file: File): String = {
    val source = scala.io.Source.fromFile(file);
    var ret: String = null
    for (line <- source.getLines()) {
      line match {
        case x if x.contains("package") => ret = line
        case _ =>
      }
    }
    ret
  }

  def gatherImports(file: File, recursive: Boolean = false): util.ArrayList[String] = {
    val source = scala.io.Source.fromFile(file);
    var propList: util.ArrayList[String] = new util.ArrayList[String]();
    for (line <- source.getLines()) {
      line match {
        case x if x.contains("import") => propList.add(line)
        case _ =>
      }
    }
    propList
  }

  def applyMyLogic(file: File) {

    val listProperties: util.ArrayList[JavaProp] = gatherProperties(file)
    val classNameString = file.getName().toString().split('.')(0)
    val classBuilderName = String.format(builderNameExpression, classNameString);
    val builderFile = new FileWriter(file.getParent + "/" + String.format(builderNameExpression, classNameString) + ".java")
    val listOfImports: util.ArrayList[String] = gatherImports(file)
    val packageName: String = getPackage(file)
    var extendsFile: String = getBaseClass(file)
    if (!extendsFile.isEmpty)
      processFile(extendsFile)

    // write package
    builderFile.write(packageName + sys.props("line.separator") + sys.props("line.separator"))

    val arr: Array[String] = new Array[String](listOfImports.size());
    listOfImports.toArray(arr)
    arr.map((str: String) => builderFile.write(str + sys.props("line.separator")))
    builderFile.write(sys.props("line.separator"))

    //? imports
    //write class name
    builderFile.write(classDefinition.replace("%s", classNameString))
    if (!extendsFile.isEmpty) {
      builderFile.write(" extends " + stripExtention(extendsFile) + " ");
    }
    builderFile.write(itemOpenClass + sys.props("line.separator") + sys.props("line.separator"))

    //write instance
    builderFile.write(String.format(instancePattern, classNameString))
    //write constructor
    builderFile.write(String.format(constructorPattern, classBuilderName, classNameString))
    //builder write
    builderFile.write(String.format(buildFunction, classNameString))

    val it = listProperties.iterator();
    while (it.hasNext) {
      writeProperty(builderFile, it.next(), classBuilderName)
    }
    builderFile.write("}")
    builderFile.close
    print(classBuilderName + " was created")
  }

  def stripExtention(str: String): String = {
    val arr: Array[String] = str.split('.');
    arr(0)
  }

  def writeProperty(builderFile: FileWriter, prop: JavaProp, classBuilderName: String) {
    builderFile.write(String.format(methodNamePattern, classBuilderName, prop.propertyName.capitalize, prop.propertyType, prop.propertyName))
    builderFile.write(itemOpenClass + sys.props("line.separator"))
    builderFile.write(String.format(methodSetter, prop.propertyName.capitalize, prop.propertyName))
    builderFile.write(returnThis + sys.props("line.separator"))
    builderFile.write(itemCloseClass + sys.props("line.separator"))
  }

  def getVariableNameFromLine(s: String): Array[String] = {
    s.trim.replace(";", "").split(" ").slice(1, 3)
  }

  def findFiles(fileFilter: (File) => Boolean = (f) => true)(f: File): List[File] = {
    val source = f.list()
    val list = if (source == null) {
      Nil
    } else {
      source.toList.sorted
    }
    val visible = list.filter(_.charAt(0) != ".")
    val these = visible.map(new File(f, _))
    these.filter(fileFilter) ++ these.filter(_.isDirectory).flatMap(findFiles(fileFilter))
  }

}


