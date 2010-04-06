package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{LongField, StringField}
import net.liftweb.squerylrecord.RecordTypesMode._
import org.squeryl.{KeyedEntity, Query}

class Publisher extends Record[Publisher] with KeyedEntity[LongField[Publisher]] {
    def meta = Publisher

    val id = new LongField(this) { }
    val name = new StringField(this, 100) { }

    def books: Query[Book] = TestSchema.books.where(_.publisherId === id)
}

object Publisher extends Publisher with MetaRecord[Publisher] {
    def createRecord = new Publisher
}
