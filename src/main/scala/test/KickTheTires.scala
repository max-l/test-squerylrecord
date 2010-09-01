package test

import java.sql.SQLException
import org.squeryl.adapters.H2Adapter
import net.liftweb.common.{Empty, Log4j, Loggable}
import net.liftweb.mapper.{DB, DefaultConnectionIdentifier, StandardDBVendor}
import model.{Author, Book, TestSchema}
import bootstrap.liftweb.RecordMetaDataFactoryOverride
import org.squeryl.dsl.ast.SelectElementReference
import org.squeryl.internals.{FieldReferenceLinker, FieldMetaData}
import org.squeryl.dsl.EnumExpression
import net.liftweb.squerylrecord.{RecordTypeMode, RecordTypeModeBase, SquerylRecord}
import net.liftweb.record.{MandatoryTypedField, OptionalTypedField, TypedField}
import net.liftweb.record.field.{EnumField, EnumTypedField}

object KickTheTires extends Loggable {
  def main(args : Array[String]) : Unit = {
      
    Log4j.withConfig(Log4j.defaultProps.replace("INFO", "DEBUG"))

    val dbVendor = new StandardDBVendor("org.h2.Driver", "jdbc:h2:test", Empty, Empty)
    try {
      DB.defineConnectionManager(DefaultConnectionIdentifier, dbVendor)
      SquerylRecord.init(() => new H2Adapter)
      FieldMetaData.factory = new RecordMetaDataFactoryOverride

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

  trait RecordTypeModeBaseOverride4MandatoryEnums extends RecordTypeMode {

    implicit def enum2EnumExpr(l: EnumTypedField[Enumeration]) = {
      val n = FieldReferenceLinker.takeLastAccessedFieldReference.get
      new SelectElementReference[Enumeration#Value](n)(n.createEnumerationMapper) with  EnumExpression[Enumeration#Value]
    }



//    implicit def enum2EnumExpr[E <: Enumeration](l: EnumField[_,E]) = {
//      val n = FieldReferenceLinker.takeLastAccessedFieldReference.get
//      new SelectElementReference[Enumeration#Value](n)(n.createEnumerationMapper) with  EnumExpression[E#Value]
//    }
  }  

  object RecordTypeModeBaseOverride4OptionalEnums extends RecordTypeModeBaseOverride4MandatoryEnums {

    implicit def enum2OptionEnumExpr(l: OptionalTypedField[Enumeration#Value]) = {
      val n = FieldReferenceLinker.takeLastAccessedFieldReference.get
      new SelectElementReference[Option[Enumeration#Value]](n)(n.createEnumerationOptionMapper) with  EnumExpression[Option[Enumeration#Value]]
    }
  }

  def go {
    import TestSchema._
    //import net.liftweb.squerylrecord.RecordTypeMode._
    import RecordTypeModeBaseOverride4OptionalEnums._
    import test.model._
    
    val kenFollet = new Author().age(59).name("Ken Follet")
    authors.insert(kenFollet)

    val alexandreDumas = new Author().age(70).name("Alexandre Dumas")
    authors.insert(alexandreDumas)

    val pillarsOfTheEarth = new Book().name("Pillars Of The Earth").authorId(kenFollet.id).genre(Genre.Novel)
    books.insert(pillarsOfTheEarth)
    
    val laReineMargot = new Book().name("La Reine Margot").authorId(alexandreDumas.id).genre(Genre.Novel)
    books.insert(laReineMargot)

    //commit the inserts, so we can inspect the DB if things go wrong :
    DB.currentConnection.foreach(_.connection.commit)
    
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

    val b0  = books.where(_.id === laReineMargot.id).single
    b0.genre(Genre.Culinary)

    books.update(b0)

    val b1  = books.where(_.id === laReineMargot.id).single

    assert(b1.genre.get == Genre.Culinary)
//    val g:Genre#Value = Genre.Novel
//    val novels = from(books)(b =>
//      where({
//
////        val a1 = b.genre : TypedField[Enumeration#Value]
////        val a2 = a1 : EnumExpression[Enumeration#Value]
//
//        val r = b.genre === Genre.Novel
//        r
//      })
//      select(b.id)
//    )
//
//    assert(novels.map(_.id).toSet == Set(pillarsOfTheEarth.id, laReineMargot.id))
  }  
}
