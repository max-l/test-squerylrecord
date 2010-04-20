package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{IntField, StringField, LongField, LongTypedField}
import net.liftweb.squerylrecord.RecordTypesMode._
import net.liftweb.squerylrecord.SquerylField
import org.squeryl.KeyedEntity

class Book extends Record[Book] with KeyedEntity[LongTypedField] {
    def meta = Book

    val id = new LongField(this, 100) with SquerylField
    val name = new StringField(this, "") with SquerylField
    val publishedInYear = new IntField(this, 1990) with SquerylField

    val publisherId = new LongField(this, 0) with SquerylField

    val authorId = new LongField(this, 1234) with SquerylField
  
    def author = TestSchema.authors.lookup(authorId: LongTypedField) //(authorId, net.liftweb.squerylrecord.RecordTypesMode._)
    //def publisher = TestSchema.publishers.lookup(publisherId)

    //def author = TestSchema.authors.where(a => a.id === authorId)  
  
    def publisher = TestSchema.publishers.where(p => p.id === publisherId)  
}

object Book extends Book with MetaRecord[Book] {
  def createRecord = new Book  
}
