package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{LongField, LongTypedField, StringField}
import net.liftweb.squerylrecord.PrimaryKeyField
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntity, Query}

class Publisher extends Record[Publisher] with KeyedEntity[Long] {
    def meta = Publisher

    val id = new LongField(this, 1) with PrimaryKeyField
    val name = new StringField(this, "")

    def books: Query[Book] = TestSchema.books.where(_.publisherId.value === id.value)
}

object Publisher extends Publisher with MetaRecord[Publisher] {
    def createRecord = new Publisher
}
