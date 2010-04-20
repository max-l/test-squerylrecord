package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{LongField, LongTypedField, StringField}
import net.liftweb.squerylrecord.RecordTypesMode._
import net.liftweb.squerylrecord.SquerylField
import org.squeryl.{KeyedEntity, Query}

class Publisher extends Record[Publisher] with KeyedEntity[LongTypedField] {
    def meta = Publisher

    val id = new LongField(this, 1) with SquerylField
    val name = new StringField(this, "") with SquerylField

    def books: Query[Book] = TestSchema.books.where(_.publisherId === id)
}

object Publisher extends Publisher with MetaRecord[Publisher] {
    def createRecord = new Publisher
}
