# Local dependencies

Place your MySQL Connector/J JAR here and rename the local copy to:

```text
mysql-connector-j.jar
```

The Eclipse classpath refers to this relative filename. The original development environment used Connector/J 9.7.0.

Keep the JavaFX SDK outside the repository and define an Eclipse user library named `JavaFX21`, or use its `lib` directory as the Java module path. The original environment used JavaFX SDK 21.0.11.

JAR files are ignored by Git. They are not included in this source package.
