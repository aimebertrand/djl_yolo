name := "djl_yolo"

version := "0.1"

scalaVersion := "2.13.6"
libraryDependencies ++= Seq(
  "ai.djl.pytorch" % "pytorch-engine" % "0.12.0",
  "ai.djl" % "api" % "0.12.0",
  "ai.djl.pytorch" % "pytorch-model-zoo" % "0.12.0",
  "ai.djl.pytorch" % "pytorch-native-cpu" % "1.8.1",
  "org.openpnp" % "opencv" % "4.5.1-2"
)