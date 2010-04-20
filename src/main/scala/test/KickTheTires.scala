package test

import model.{Author, Book, TestSchema}
import org.squeryl.adapters.H2Adapter
import org.squeryl.{SessionFactory, Session}
import java.sql.SQLException
import org.squeryl.dsl.ast.TypedExpressionNode


object KickTheTires {

  def createH2TestConnection = {
    Session.create(
      java.sql.DriverManager.getConnection("jdbc:h2:~/test", "sa", ""),
      new H2Adapter
    )
  }

  def main(args : Array[String]) : Unit = {
    Class.forName("org.h2.Driver");
    
    SessionFactory.concreteFactory = Some(() => createH2TestConnection)

    import net.liftweb.squerylrecord.RecordTypesMode._    

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
    import net.liftweb.squerylrecord.RecordTypesMode._

    //Session.currentSession.setLogger(msg => println(msg))
    
    val kenFollet = new Author().age(59).name("Ken Follet")
    authors.insert(kenFollet)

    val alexandreDumas = new Author().age(70).name("Alexandre Dumas")
    authors.insert(alexandreDumas)

    val pillarsOfTherEarth = new Book().name("Pillars Of The Earth").authorId(kenFollet.id.value)
    books.insert(pillarsOfTherEarth)
    
    val laReineMargot = new Book().name("La Reine Margot").authorId(alexandreDumas.id.value)
    books.insert(laReineMargot)

    //commit the inserts, so we can inspect the DB if things go wrong :
    Session.currentSession.connection.commit
    
    val qLaReineLargot = from(books, authors)((b,a) =>
      where((a.name like "Alex%") and b.authorId === a.id)
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
