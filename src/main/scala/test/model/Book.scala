package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{IntField, StringField, LongField, LongTypedField}
import net.liftweb.squerylrecord.KeyedRecord
import net.liftweb.squerylrecord.RecordTypeMode._
import org.squeryl.annotations.Column

class Book private () extends Record[Book] with KeyedRecord[Long] {
    def meta = Book

    @Column(name="id")
    val idField = new LongField(this, 100)
    val name = new StringField(this, "")
    val publishedInYear = new IntField(this, 1990)

    val publisherId = new LongField(this, 0)

    val authorId = new LongField(this, 1234)
  
    def author = TestSchema.authors.lookup(authorId.value)
    //def publisher = TestSchema.publishers.lookup(publisherId)

    //def author = TestSchema.authors.where(a => a.id === authorId)
  
    def publisher = TestSchema.publishers.where(p => p.id === publisherId)
}

object Book extends Book with MetaRecord[Book]
