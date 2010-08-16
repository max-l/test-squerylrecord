package bootstrap.liftweb

import net.liftweb.common._
import net.liftweb.util.Props
import net.liftweb.http.{LiftRules, S}
import net.liftweb.mapper.{DB, DefaultConnectionIdentifier, StandardDBVendor}
import net.liftweb.sitemap.{Menu, SiteMap, Loc}
import net.liftweb.squerylrecord.SquerylRecord
import org.squeryl.adapters.H2Adapter
import java.sql.Connection
import org.squeryl.Session

import Loc._

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?)
      DB.defineConnectionManager(DefaultConnectionIdentifier,
        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
                             Props.get("db.url") openOr "jdbc:h2:test",
                             Props.get("db.user"), Props.get("db.password")))

    SquerylRecord.init(() => new H2Adapter)

      println("name of Author.name is " + test.model.Author.name.name)


    // where to search snippet
    LiftRules.addToPackages("test")

    // Build SiteMap
    val entries = Menu(Loc("Home", List("index"), "Home")) :: Nil
    LiftRules.setSiteMap(SiteMap(entries:_*))
    
    S.addAround(DB.buildLoanWrapper(DefaultConnectionIdentifier::Nil))
  }
}

