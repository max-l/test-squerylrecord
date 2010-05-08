package test

import java.sql.SQLException
import org.squeryl.{SessionFactory, Session, View}
import org.squeryl.adapters.H2Adapter
import org.squeryl.internals.{FieldMetaData, PosoMetaData}
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

  def dumpPosoMetaData[T](what: View[T]): Unit = {
    val posoMetaData: PosoMetaData[T] = what.getClass.getMethod("posoMetaData").invoke(what).asInstanceOf[PosoMetaData[T]]
    println("PosoMetaData for " + posoMetaData.clasz + ", primaryKey = " + posoMetaData.primaryKey)
    for (fld <- posoMetaData.fieldsMetaData)
      println(" - " + fld.nameOfProperty + " fieldType=" + fld.fieldType +
              " isOption=" + fld.isOption + " isPrimaryKey=" + fld.isPrimaryKey)
  }

  def go {
    import TestSchema._
    import org.squeryl.PrimitiveTypeMode._

    dumpPosoMetaData(authors)
    dumpPosoMetaData(books)
    dumpPosoMetaData(publishers)

    //Session.currentSession.setLogger(msg => println(msg))
    
    val kenFollet = new Author().age(59).name("Ken Follet")
    authors.insert(kenFollet)

    val alexandreDumas = new Author().age(70).name("Alexandre Dumas")
    authors.insert(alexandreDumas)

    val pillarsOfTheEarth = new Book().name("Pillars Of The Earth").authorId(kenFollet.id.value)
    books.insert(pillarsOfTheEarth)
    
    val laReineMargot = new Book().name("La Reine Margot").authorId(alexandreDumas.id.value)
    books.insert(laReineMargot)

    //commit the inserts, so we can inspect the DB if things go wrong :
    Session.currentSession.connection.commit
    
    val qLaReineLargot = from(books, authors)((b,a) =>
      where((a.name.value like "Alex%") and b.authorId.value === a.id.value)
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
