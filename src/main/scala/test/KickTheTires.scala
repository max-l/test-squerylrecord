package test

import model.{Book, TestSchema}
import org.squeryl.adapters.H2Adapter
import org.squeryl.{SessionFactory, Session}
import java.sql.SQLException


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
    
    val b = books.insert(new Book)

    val q = from(books)(b0 => {
      where(b0.id === b.id)
      select(b0)
    })

    val sameB = q.single

    assert(sameB.id.value == b.id.value, "expected " + b.id + " got " + sameB.id)

    //val sameB = books.where(b0 => b0.id === b.id) this version still has a bug...
  }
}