package test

import java.sql.SQLException
import org.squeryl.{SessionFactory, Session}
import org.squeryl.adapters.H2Adapter
import org.squeryl.internals.FieldMetaData
import net.liftweb.common.{Log4j, Loggable}
import net.liftweb.squerylrecord.RecordMetaDataFactory

import model.{Author, Book, TestSchema}

object KickTheTires extends Loggable {

  def createH2TestConnection = {
    val sess = Session.create(
      java.sql.DriverManager.getConnection("jdbc:h2:~/test", "sa", ""),
      new H2Adapter
    )
    sess.setLogger(s => logger.info(s))
    sess
  }

  def initDB = {
    Class.forName("org.h2.Driver");

    FieldMetaData.factory = new RecordMetaDataFactory
    
    SessionFactory.concreteFactory = Some(() => createH2TestConnection)
  }

  def main(args : Array[String]) : Unit = {
      
    Log4j.withDefault
    initDB

    import org.squeryl.PrimitiveTypeMode._

    transaction {
      try {
        TestSchema.drop // we normally *NEVER* do this !!
      }
      catch {
        case e:SQLException => println(" schema does not yet exist :" + e.getMessage)
      }
      TestSchema.create
    }

    transaction {
      go
    }
  }

  def go {
    import TestSchema._
    import org.squeryl.PrimitiveTypeMode._

    //Session.currentSession.setLogger(msg => println(msg))
    
    val kenFollet = new Author().age(59).name("Ken Follet")
    authors.insert(kenFollet)

    val alexandreDumas = new Author().age(70).name("Alexandre Dumas")
    authors.insert(alexandreDumas)

    val pillarsOfTherEarth = new Book().name("Pillars Of The Earth").authorId(kenFollet.pk.value)
    books.insert(pillarsOfTherEarth)
    
    val laReineMargot = new Book().name("La Reine Margot").authorId(alexandreDumas.pk.value)
    books.insert(laReineMargot)

    //commit the inserts, so we can inspect the DB if things go wrong :
    Session.currentSession.connection.commit
    
    val qLaReineLargot = from(books, authors)((b,a) =>
      where((a.name.value like "Alex%") and b.authorId.value === a.pk.value)
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
