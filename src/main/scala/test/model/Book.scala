package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{IntField, StringField, LongField}
import net.liftweb.squerylrecord.RecordTypesMode._
import org.squeryl.KeyedEntity
import net.liftweb.squerylrecord.{SStringField, SIntField, SLongField}

class Book extends Record[Book] with KeyedEntity[SLongField] {
    def meta = Book

    val id = new SLongField(100)
    val name = new SStringField("")
    val publishedInYear = new SIntField(1990)

    val publisherId = new SLongField(0)

    val authorId = new SLongField(1234)
  
    //def author = TestSchema.authors.lookup(authorId : LongField[Author]) //(authorId, net.liftweb.squerylrecord.RecordTypesMode._)
    //def publisher = TestSchema.publishers.lookup(publisherId)

    def author = TestSchema.authors.where(a => a.id === authorId)  
  
    def publisher = TestSchema.publishers.where(p => p.id === publisherId)  
}

object Book extends Book with MetaRecord[Book] {
    def createRecord = new Book
}
