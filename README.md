This is an utility to create builders, the usage is quite straightforward
scala:run -DaddArgs or modify the pom adding arguments into the laucher
otherwise
arg 1 = source of the .class files, can be the root of the project where the class is located
arg 2 = target class for which one needs to create the builder
Arguments are delimitated by | character

The utility will create builders for parent object too.

scala:run -DaddArgs="mysourcefolder"|"MyClass.java"