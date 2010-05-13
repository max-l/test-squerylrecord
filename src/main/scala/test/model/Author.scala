package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{OptionalIntField, LongField, LongTypedField, StringField}
import net.liftweb.squerylrecord.KeyedRecord
import org.squeryl.Query
import org.squeryl.annotations.Column
import org.squeryl.PrimitiveTypeMode._

class Author extends Record[Author] with KeyedRecord[Long] {
    def meta = Author

    @Column(name="id")
    val idField = new LongField(this, 100)
    val name = new StringField(this, "")
    val age = new OptionalIntField(this)

    def books: Query[Book] = TestSchema.books.where(_.authorId.value === idField.value)
}

object Author extends Author with MetaRecord[Author] {
    def createRecord = new Author
}
