package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{IntField, LongField, LongTypedField, StringField}
import org.squeryl.PrimitiveTypeMode._

import org.squeryl.{KeyedEntity, Query}

class Author extends Record[Author] with KeyedEntity[Long] {
    def meta = Author

    val pk = new LongField(this, 100)
    def id = pk.value
    val name = new StringField(this, "")
    val age = new IntField(this, 1)

    def books: Query[Book] = TestSchema.books.where(_.authorId.value === pk.value)
}

object Author extends Author with MetaRecord[Author] {
    def createRecord = new Author
}
