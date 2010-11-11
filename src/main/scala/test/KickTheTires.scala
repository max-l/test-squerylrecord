package test

import java.sql.SQLException
import java.util.Calendar
import org.squeryl.adapters.H2Adapter
import net.liftweb.common.{Empty, Log4j, Loggable}
import net.liftweb.mapper.{DB, DefaultConnectionIdentifier, StandardDBVendor}
import model.{Author, Book, TestSchema}
import org.squeryl.dsl.ast.SelectElementReference
import org.squeryl.internals.{FieldReferenceLinker, FieldMetaData}
import net.liftweb.squerylrecord.{RecordTypeMode, SquerylRecord}
import net.liftweb.record.{MandatoryTypedField, OptionalTypedField, TypedField}
import net.liftweb.record.field.{EnumField, EnumTypedField}

object KickTheTires extends Loggable {
  def main(args : Array[String]) : Unit = {
      
    Log4j.withConfig(Log4j.defaultProps.replace("INFO", "DEBUG"))

    val dbVendor = new StandardDBVendor("org.h2.Driver", "jdbc:h2:test", Empty, Empty)
    try {
      DB.defineConnectionManager(DefaultConnectionIdentifier, dbVendor)
      SquerylRecord.init(() => new H2Adapter)

      import net.liftweb.squerylrecord.RecordTypeMode._

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
    import net.liftweb.squerylrecord.RecordTypeMode._
    import test.model._

    val now = Calendar.getInstance

    org.squeryl.Session.currentSession.setLogger(println(_))

    val kenFollet = Author.createRecord.age(59).name("Ken Follet").birthday(Some(now))
    authors.insert(kenFollet)

    //println("---->" + now.getTime)

    val alexandreDumas = Author.createRecord.age(70).name("Alexandre Dumas")
    authors.insert(alexandreDumas)

    val pillarsOfTheEarth = Book.createRecord.name("Pillars Of The Earth").authorId(kenFollet.id).genre(Genre.Novel)
    books.insert(pillarsOfTheEarth)
    
    val laReineMargot = Book.createRecord.name("La Reine Margot").authorId(alexandreDumas.id).genre(Genre.Novel)
    books.insert(laReineMargot)
    
    //commit the inserts, so we can inspect the DB if things go wrong :
    DB.currentConnection.foreach(_.connection.commit)

    // check that the field name is set after loading an object:
    val loaded = books.lookup(laReineMargot.id)
    assert(loaded.get.name.name == laReineMargot.name.name, "Field names do not match: " + loaded.get.name.name +
      " and " + laReineMargot.name.name)


    val c1 = Calendar.getInstance
    c1.setTime(now.getTime)
    c1.add(Calendar.HOUR_OF_DAY, -1)
    val c2 = Calendar.getInstance
    c2.setTime(now.getTime)
    c2.add(Calendar.HOUR_OF_DAY, 1)

    println("====>"+c1.getTime)
    println("====>"+c2.getTime)

//    val qKenFolletByBirthday = from(authors)(a => where(a.birthday between(Some(c1.getTime), Some(c2.getTime))) select(a))
//    assert(qKenFolletByBirthday.single.name.value == "Ken Follet")
    
    val qLaReineLargot = from(books, authors)((b,a) =>
      where((a.name.value like "Alex%") and b.authorId === a.id)
      select(b)
    )

    //println(qLaReineLargot.statement)

    val zBook = qLaReineLargot.single

    assert(zBook.name.value == "La Reine Margot")
    println(qLaReineLargot.single.name)

    val alex = zBook.author.get

    assert(alex.name.value == "Alexandre Dumas")

    val option70 = from(authors)(a=>
      where(a.id === alex.id)
      select(&(a.age))
    )

    assert(option70.single.get == 70)

//    val g:Genre#Value = Genre.Novel
    val novels = from(books)(b =>
      where(b.genre === Genre.Novel)
      select(b)
      orderBy(b.id)
    ).toList

    val s1 = novels.map(b => b.id).toSet
    val s2 = Set(pillarsOfTheEarth.id, laReineMargot.id)

    assert(s1 == s2)


    val b0  = books.where(_.id === laReineMargot.id).single

    assert(b0.genre.get == Genre.Novel)

    b0.genre(Genre.Culinary)

    books.update(b0)

    val b1  = books.where(_.id === laReineMargot.id).single

    assert(b1.genre.get == Genre.Culinary)    
  }  
}
