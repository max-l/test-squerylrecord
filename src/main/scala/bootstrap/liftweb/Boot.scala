package bootstrap.liftweb

import _root_.net.liftweb.common._
import _root_.net.liftweb.util._
import _root_.net.liftweb.http._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import Helpers._
import org.squeryl.adapters.H2Adapter
import java.sql.Connection
import org.squeryl.Session

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("test")

    // Build SiteMap
    val entries = Menu(Loc("Home", List("index"), "Home")) :: Nil
    LiftRules.setSiteMap(SiteMap(entries:_*))


    org.squeryl.SessionFactory.externalTransactionManagementAdapter = Some(() =>
      org.squeryl.Session.create(
        _grabJdbcConnectionFromCurrentThread,
        new H2Adapter
      )
    )
  }

  //TODO: implement
  private def _grabJdbcConnectionFromCurrentThread: Connection =
    error("implement me : obtain the current thread's JDBC connection from Lift")

  //TODO: call this with a 'end of transaction' Lift callback 
  private def _callWhenLiftClosesItsTransaction =
    Session.cleanupResources
}

