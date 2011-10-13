sbtPlugin := true

organization := "com.github.siasia"

name := "native-loader"

version := "0.1"

publishMavenStyle := true

publishTo := Some(Resolver.file("Local", Path.userHome / "projects" / "siasia.github.com" / "maven2" asFile)(Patterns(true, Resolver.mavenStyleBasePattern)))
		
