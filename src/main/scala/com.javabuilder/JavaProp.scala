package com.javabuilder

class JavaProp {

  var _propertyName: String = null;
  var _propertyType: String = null;

  def propertyName = _propertyName
  def propertyType = _propertyType

  def this(propName:String, propType:String) {
    this()
    this._propertyName = propName.replace(";", "")
    this._propertyType = propType
  }

}
