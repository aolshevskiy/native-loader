import org.freedesktop.dbus._

object Test {
	def main(args: Array[String]) {
		val conn = DBusConnection.getConnection(DBusConnection.SESSION)
		conn.disconnect()
	}
}
