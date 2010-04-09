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


    import TestSchema._

    transaction {
      val b = books.insert(new Book)

      //val sameB = books.where(b0 => b0.id === b.id)

      from(books)(b0 => {

        val ast = b0.id === b.id
                        
        where(ast)
        select(b0)
      })
    }
  }
}