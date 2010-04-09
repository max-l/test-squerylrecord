package test.model

import org.squeryl.Schema

object TestSchema extends Schema {
    val authors = table[Author]
    val books = table[Book]
    val publishers = table[Publisher]

  // this is a test schema, we can expose the power tools ! :
  override def drop = super.drop
}
