package com.javabuilder

import org.scalatest.FunSuite
import java.io.File


class BuilderGeneratorFunSuiteTest extends FunSuite {

  test("test if the stripExtention function works correctly") {
    assert(BuilderGenerator.stripExtention("Test.java") == "Test")
  }

  test("make sure the variables are correctly read from the sourceclass") {
    val variable = BuilderGenerator.getVariableNameFromLine("    private String entityName;")
    assert(variable(0) == "String")
    assert(variable(1) == "entityName")
  }

  test("find files function works correctly") {
    var files: scala.collection.immutable.List[File] = BuilderGenerator.findFiles(_.getName endsWith "TestMock.java")(new File("."))
    assume(files.length == 1)
  }

  test("make sure the properties of the class are correctly read") {
    val props: java.util.ArrayList[JavaProp] = BuilderGenerator.gatherProperties(
      BuilderGenerator.findFiles(_.getName endsWith "TestMock.java")(new File("."))(0)
    )
    assume(props.size() == 4)
    assume(props.get(0).propertyName == "entityAuditId")
    assume(props.get(0).propertyType == "long")
  }

  test("make sure imports are correctly pikedup"){
    val props: java.util.ArrayList[String] = BuilderGenerator.gatherImports(
      BuilderGenerator.findFiles(_.getName endsWith "TestMock.java")(new File("."))(0)
    )
    assume(props.size() == 1)
  }

}
