seq(scriptedSettings :_*)

scriptedBufferLog := false

sbtPlugin := true

organization := "com.github.siasia"

name := "sbloader"

version := "0.1-SNAPSHOT"

publishMavenStyle := true

publishTo := Some(Resolver.file("Local", Path.userHome / "projects" / "siasia.github.com" / "maven2" asFile)(Patterns(true, Resolver.mavenStyleBasePattern)))
		
