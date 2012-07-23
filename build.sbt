name := "id3iconv"

version := "1.0"

autoScalaLibrary := false

crossPaths := false

mainClass in (Compile, run) := Some("net.zhoufeng.ID3iconv")

mainClass in (Compile, packageBin) := Some("net.zhoufeng.ID3iconv")

libraryDependencies += "commons-io" % "commons-io" % "2.4"