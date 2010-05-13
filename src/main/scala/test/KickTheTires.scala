package test

import java.sql.SQLException
import org.squeryl.adapters.H2Adapter
import net.liftweb.common.{Empty, Log4j, Loggable}
import net.liftweb.mapper.{DB, DefaultConnectionIdentifier, StandardDBVendor}
import net.liftweb.squerylrecord.SquerylRecord

import model.{Author, Book, TestSchema}

object KickTheTires extends Loggable {
  def main(args : Array[String]) : Unit = {
      
    Log4j.withConfig(Log4j.defaultProps.replace("INFO", "DEBUG"))

    val dbVendor = new StandardDBVendor("org.h2.Driver", "jdbc:h2:test", Empty, Empty)
    try {
      DB.defineConnectionManager(DefaultConnectionIdentifier, dbVendor)
      SquerylRecord.init(() => new H2Adapter)

      import org.squeryl.PrimitiveTypeMode._

      DB.use(DefaultConnectionIdentifier) { _ =>
        try {
          TestSchema.drop // we normally *NEVER* do this !!
        } catch {
          case e:SQLException => println(" schema does not yet exist :" + e.getMessage)
        }
        TestSchema.create
      }

      DB.use(DefaultConnectionIdentifier) { _ =>
        go
      }
    } finally {
      dbVendor.closeAllConnections_!
    }
  }

  def go {
    import TestSchema._
    import org.squeryl.PrimitiveTypeMode._
    
    val kenFollet = new Author().age(59).name("Ken Follet")
    authors.insert(kenFollet)

    val alexandreDumas = new Author().age(70).name("Alexandre Dumas")
    authors.insert(alexandreDumas)

    val pillarsOfTheEarth = new Book().name("Pillars Of The Earth").authorId(kenFollet.idField.value)
    books.insert(pillarsOfTheEarth)
    
    val laReineMargot = new Book().name("La Reine Margot").authorId(alexandreDumas.idField.value)
    books.insert(laReineMargot)

    //commit the inserts, so we can inspect the DB if things go wrong :
    DB.currentConnection.foreach(_.connection.commit)
    
    val qLaReineLargot = from(books, authors)((b,a) =>
      where((a.name.value like "Alex%") and b.authorId.value === a.idField.value)
      select(b)
    )

    //println(qLaReineLargot.statement)

    val zBook = qLaReineLargot.single

    assert(zBook.name.value == "La Reine Margot")
    println(qLaReineLargot.single.name.value)

    val alex = zBook.author.get

    assert(alex.name.value == "Alexandre Dumas")
  }  
}
