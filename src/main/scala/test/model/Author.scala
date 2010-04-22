package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{IntField, LongField, LongTypedField, StringField}
import net.liftweb.squerylrecord.RecordTypesMode._

import org.squeryl.{KeyedEntity, Query}

class Author extends Record[Author] with KeyedEntity[LongTypedField] {
    def meta = Author

    val id = new LongField(this, 100)
    val name = new StringField(this, "")
    val age = new IntField(this, 1)

    def books: Query[Book] = TestSchema.books.where(_.authorId === id)
}

object Author extends Author with MetaRecord[Author] {
    def createRecord = new Author
}
