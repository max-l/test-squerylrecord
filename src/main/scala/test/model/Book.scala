package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{IntField, StringField, LongField}
import net.liftweb.squerylrecord.RecordTypesMode._
import org.squeryl.KeyedEntity

class Book extends Record[Book] with KeyedEntity[LongField[Book]] {
    def meta = Book

    val id = new LongField(this, 100) { }
    val name = new StringField(this, 100) { }
    val publishedInYear = new IntField(this) { }
    val authorId = new LongField(this) { }
    val publisherId = new LongField(this) { }

    def author = TestSchema.authors.lookup(authorId)
    def publisher = TestSchema.publishers.lookup(publisherId)
}

object Book extends Book with MetaRecord[Book] {
    def createRecord = new Book
}
