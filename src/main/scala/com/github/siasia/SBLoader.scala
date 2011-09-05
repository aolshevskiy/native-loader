import sbt._

import java.lang.reflect.{Method, Modifier}
import Modifier.{isPublic, isStatic}
import classpath.ClasspathUtilities
import java.io.File
import Keys._

object SBLoader extends Plugin {
	val sbLibs = SettingKey[Seq[File]]("sb-libs")

	class SBRun(instance: ScalaInstance, trapExit: Boolean, sbLibs: Seq[File]) extends ScalaRun
	{
		val singletonLoader = ClasspathUtilities.makeLoader(sbLibs, instance)
		/** Runs the class 'mainClass' using the given classpath and options using the scala runner.*/
		def run(mainClass: String, classpath: Seq[File], options: Seq[String], log: Logger) =	{
			log.info("Running " + mainClass + " " + options.mkString(" "))

			def execute = 
				try { run0(mainClass, classpath, options, log) }
				catch { case e: java.lang.reflect.InvocationTargetException => throw e.getCause }
			def directExecute = try { execute; None } catch { case e: Exception => log.trace(e); Some(e.toString) }

			if(trapExit) Run.executeTrapExit( execute, log ) else directExecute
		}
		private def run0(mainClassName: String, classpath: Seq[File], options: Seq[String], log: Logger)
		{
			log.debug("  Classpath:\n\t" + classpath.mkString("\n\t"))
			val loader = ClasspathUtilities.toLoader(classpath, singletonLoader)
			val main = getMainMethod(mainClassName, loader)
			invokeMain(loader, main, options)
		}
		private def invokeMain(loader: ClassLoader, main: Method, options: Seq[String])
		{
			val currentThread = Thread.currentThread
			val oldLoader = Thread.currentThread.getContextClassLoader()
			currentThread.setContextClassLoader(loader)
			try { main.invoke(null, options.toArray[String].asInstanceOf[Array[String]] ) }
			finally { currentThread.setContextClassLoader(oldLoader) }
		}
		def getMainMethod(mainClassName: String, loader: ClassLoader) =	{
			val mainClass = Class.forName(mainClassName, true, loader)
			val method = mainClass.getMethod("main", classOf[Array[String]])
			val modifiers = method.getModifiers
			if(!isPublic(modifiers)) throw new NoSuchMethodException(mainClassName + ".main is not public")
			if(!isStatic(modifiers)) throw new NoSuchMethodException(mainClassName + ".main is not static")
			method
		}
	}
	def runInit: Project.Initialize[ScalaRun] = (scalaInstance, trapExit, sbLibs) { (si, trap, sbLibs) => new SBRun(si, trap, sbLibs) }
	def sbLoaderSettings = Seq(
		sbLibs := Seq(),
		runner in run <<= runInit,
		runner in (Test, run) <<= runInit
	)
}
