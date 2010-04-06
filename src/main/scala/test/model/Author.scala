package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{IntField, LongField, StringField}
import net.liftweb.squerylrecord.RecordTypesMode._
import org.squeryl.{KeyedEntity, Query}

class Author extends Record[Author] with KeyedEntity[LongField[Author]] {
    def meta = Author

    val id = new LongField(this, 100) { }
    val name = new StringField(this, 100) { }
    val age = new IntField(this) { }

    def books: Query[Book] = TestSchema.books.where(_.authorId === id)
}

object Author extends Author with MetaRecord[Author] {
    def createRecord = new Author
}
