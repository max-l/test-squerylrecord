package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{OptionalIntField, LongField, LongTypedField, StringField}
import net.liftweb.squerylrecord.KeyedRecord
import net.liftweb.squerylrecord.RecordTypeMode._
import org.squeryl.Query
import org.squeryl.annotations.Column

class Author private () extends Record[Author] with KeyedRecord[Long] {
    def meta = Author

    @Column(name="id")
    val idField = new LongField(this, 100)
    val name = new StringField(this, "")
    val age = new OptionalIntField(this)

    def books: Query[Book] = TestSchema.books.where(_.authorId === id)

    override def toString = "Author.createRecord.idField(" + idField.value + ", " + name.value + ", " + age.value + ")"
}

object Author extends Author with MetaRecord[Author]
