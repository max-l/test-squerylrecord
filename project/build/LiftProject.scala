import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) {
    val ivyLocal = "Local Ivy Repository" at "file://"+Path.userHome+"/.ivy2/local"
    val mavenLocal = "Local Maven Repository" at "file://"+Path.userHome+"/.m2/repository"
    val databinder = "Databinder Repository" at "http://databinder.net/repo"
    //val scalatools_snapshot = "Scala Tools Snapshot" at "http://scala-tools.org/repo-snapshots/"
    //val scalatools_release = "Scala Tools Snapshot" at "http://scala-tools.org/repo-releases/"

    val liftVersion = "2.2-M1"

	override val jettyPort = 8081
	
    override def libraryDependencies = Set(
        "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
        "net.liftweb" %% "lift-record" % liftVersion % "compile->default",
        "net.liftweb" %% "lift-squeryl-record" % liftVersion % "compile->default",

        "org.squeryl" %% "squeryl" % "0.9.4-RC2-4lift2.1" % "compile->runtime",

        "joda-time" % "joda-time" % "1.6",
        "org.mortbay.jetty" % "jetty" % "6.1.22" % "test->default",
        "com.h2database" % "h2" % "1.2.121",
        "cglib" % "cglib-nodep" % "2.2",
        
        "log4j" % "log4j" % "1.2.16",
	"org.slf4j" % "slf4j-log4j12" % "1.6.1"
    ) ++ super.libraryDependencies
}
