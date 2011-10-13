package com.github.siasia

import sbt._
import Keys._

import classpath.ClasspathUtilities

object NativeLoader extends Plugin {
	lazy val natives = SettingKey[Seq[File]]("natives")
	lazy val nativeLoader = AttributeKey[ClassLoader]("native-loader")

	def createNativeLoader(state: State, version: String, launcher: xsbti.Launcher, natives: Seq[File], nativeTemp: File) = {
		val provider = launcher.getScala(version)		
		state.put(nativeLoader, ClasspathUtilities.toLoader(natives, provider.loader, Map(), nativeTemp))
	}

	def scalaInstanceTask = (state, scalaVersion, appConfiguration) map {
		(state, version, app) =>
		val provider = app.provider.scalaProvider.launcher.getScala(version)
		val loader = state.get(nativeLoader).get
		new ScalaInstance(version, loader, provider.libraryJar, provider.compilerJar, (provider.jars.toSet - provider.libraryJar - provider.compilerJar).toSeq, None)
	}
	
	def nativeSettings = Seq(
		onLoad in GlobalScope <<= (onLoad in GlobalScope, scalaVersion, appConfiguration, natives, taskTemporaryDirectory) {
			(onLoad, version, app, natives, temp) =>
			(state) =>
				val launcher = app.provider.scalaProvider.launcher
				onLoad(createNativeLoader(state, version, launcher, natives, temp))
		},
		scalaInstance <<= scalaInstanceTask,
		unmanagedJars in Compile <++= (natives) map (ns => ns)
	)
}
