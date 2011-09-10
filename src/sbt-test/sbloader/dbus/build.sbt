seq(sbLoaderSettings :_*)

sbLibs += file("/usr/share/java/dbus-java/dbus.jar")

unmanagedJars in Compile += file("/usr/share/java/dbus-java/dbus.jar")

taskTemporaryDirectory := file("/home/siasia/projects/sbloader/target/tmp")
