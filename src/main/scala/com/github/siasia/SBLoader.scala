package com.github.siasia

import sbt._

import java.lang.reflect.{Method, Modifier}
import Modifier.{isPublic, isStatic}
import classpath.ClasspathUtilities
import java.io.File
import Keys._

object SBLoader extends Plugin {
	val sbLibs = SettingKey[Seq[File]]("sb-libs")

	private var singletonLoader: ClassLoader = null

	def scalaInstanceSetting(version: String, launcher: xsbti.Launcher, sbLibs: Seq[File]) = {
		val provider = launcher.getScala(version)		
		if(singletonLoader == null)
			singletonLoader = ClasspathUtilities.toLoader(sbLibs ,provider.loader)
		new ScalaInstance(version, singletonLoader, provider.libraryJar, provider.compilerJar, (provider.jars.toSet - provider.libraryJar - provider.compilerJar).toSeq, None)
	}
		

	def sbLoaderSettings = Seq(
		sbLibs := Seq(),
		scalaInstance <<= (appConfiguration, scalaVersion, sbLibs) map { (app, version, sbLibs) =>
			val launcher = app.provider.scalaProvider.launcher
			scalaInstanceSetting(version, launcher, sbLibs)
		}
	)
}
