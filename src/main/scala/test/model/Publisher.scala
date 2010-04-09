package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{LongField, StringField}
import net.liftweb.squerylrecord.RecordTypesMode._
import org.squeryl.{KeyedEntity, Query}
import net.liftweb.squerylrecord.{SStringField, SLongField}

class Publisher extends Record[Publisher] with KeyedEntity[SLongField] {
    def meta = Publisher

    val id = new SLongField(1)
    val name = new SStringField("")

    def books: Query[Book] = TestSchema.books.where(_.publisherId === id)
}

object Publisher extends Publisher with MetaRecord[Publisher] {
    def createRecord = new Publisher
}
