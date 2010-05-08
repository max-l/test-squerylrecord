package test.model

import net.liftweb.record.{MetaRecord, Record}
import net.liftweb.record.field.{LongField, LongTypedField, StringField}
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{KeyedEntity, Query}

class Publisher extends Record[Publisher] with KeyedEntity[Long] {
    def meta = Publisher

    val pk = new LongField(this, 1)
    def id = pk.value
    val name = new StringField(this, "")

    def books: Query[Book] = TestSchema.books.where(_.publisherId.value === pk.value)
}

object Publisher extends Publisher with MetaRecord[Publisher] {
    def createRecord = new Publisher
}
