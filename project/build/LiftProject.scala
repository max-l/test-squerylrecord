import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) {
    val databinder = "Databinder Repository" at "http://databinder.net/repo"
    val ivyLocal = "Local Ivy Repository" at "file://"+Path.userHome+"/.ivy2/local"
    val mavenLocal = "Local Maven Repository" at "file://"+Path.userHome+"/.m2/repository"
    //val scalatools_snapshot = "Scala Tools Snapshot" at "http://scala-tools.org/repo-snapshots/"
    //val scalatools_release = "Scala Tools Snapshot" at "http://scala-tools.org/repo-releases/"

    val liftVersion = "2.0-scala280-SNAPSHOT"

    override def libraryDependencies = Set(
        "net.liftweb" % "lift-webkit" % liftVersion % "compile->default",
        "net.liftweb" % "lift-record" % liftVersion % "compile->default",
        "net.liftweb" % "lift-squeryl-record" % liftVersion % "compile->default",
        "org.squeryl" % "squeryl_2.8.0.Beta1" % "0.9.4beta2" % "compile->runtime",
        "org.mortbay.jetty" % "jetty" % "6.1.22" % "test->default",
        "com.h2database" % "h2" % "1.2.121",
        "cglib" % "cglib-nodep" % "2.2"
    ) ++ super.libraryDependencies
}
