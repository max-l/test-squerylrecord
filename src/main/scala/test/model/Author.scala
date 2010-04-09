package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{IntField, LongField, StringField}
import net.liftweb.squerylrecord.RecordTypesMode._
import org.squeryl.{KeyedEntity, Query}
import net.liftweb.squerylrecord.{SLongField, SStringField, SIntField}

class Author extends Record[Author] with KeyedEntity[SLongField] {
    def meta = Author

    val id = new SLongField(100)
    val name = new SStringField("")
    val age = new SIntField(1)

    def books: Query[Book] = TestSchema.books.where(_.authorId === id)
}

object Author extends Author with MetaRecord[Author] {
    def createRecord = new Author
}
