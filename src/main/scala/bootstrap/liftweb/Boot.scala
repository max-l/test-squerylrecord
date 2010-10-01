package bootstrap.liftweb

import _root_.test.model.TestSchema
import scala.util.control.Exception.ultimately
import net.liftweb.common._
import net.liftweb.util.{LoanWrapper, Props}
import net.liftweb.http.{LiftRules, S}
import net.liftweb.mapper.{DB, DefaultConnectionIdentifier, StandardDBVendor}
import net.liftweb.sitemap.{Menu, SiteMap, Loc}
import net.liftweb.squerylrecord.SquerylRecord
import org.squeryl.adapters.H2Adapter
import org.squeryl.Session
import java.lang.annotation.Annotation
import Loc._
import net.liftweb.record.field._
import java.util.Calendar
import org.squeryl.annotations.Column
import java.sql.{ResultSet, Timestamp, Connection}
import net.liftweb.record.{TypedField, Record, BaseField, MetaRecord}
import java.lang.reflect.{Method, Field}
import org.squeryl.internals.{FieldMetaDataFactory, PosoMetaData, FieldMetaData}

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

    DB.use(DefaultConnectionIdentifier) { _ =>
      org.squeryl.Session.currentSession.setLogger(msg => msg)
      println("-------------------")
      TestSchema.drop
      println("+++++++++++++++++++")
      TestSchema.create
    }

    println("name of Author.name is " + test.model.Author.name.name)

    // where to search snippet
    LiftRules.addToPackages("test")

    // Build SiteMap
    val entries = Menu(Loc("Home", List("index"), "Home")) :: Nil
    LiftRules.setSiteMap(SiteMap(entries:_*))
    
    S.addAround(DB.buildLoanWrapper(DefaultConnectionIdentifier::Nil))
    S.addAround(new LoanWrapper {
        def apply[T](f: => T): T = ultimately(println(S.getAllNotices))(f)
    })
  }
}
