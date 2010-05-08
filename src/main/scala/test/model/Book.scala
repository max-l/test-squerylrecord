package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{IntField, StringField, LongField, LongTypedField}
import org.squeryl.PrimitiveTypeMode._

import org.squeryl.KeyedEntity

class Book extends Record[Book] with KeyedEntity[Long] {
    def meta = Book

    val pk = new LongField(this, 100)
    def id = pk.value
    val name = new StringField(this, "")
    val publishedInYear = new IntField(this, 1990)

    val publisherId = new LongField(this, 0)

    val authorId = new LongField(this, 1234)
  
    def author = TestSchema.authors.lookup(authorId.value)
    //def publisher = TestSchema.publishers.lookup(publisherId)

    //def author = TestSchema.authors.where(a => a.id === authorId)  
  
    def publisher = TestSchema.publishers.where(p => p.pk.value === publisherId.value)
}

object Book extends Book with MetaRecord[Book] {
  def createRecord = new Book  
}
