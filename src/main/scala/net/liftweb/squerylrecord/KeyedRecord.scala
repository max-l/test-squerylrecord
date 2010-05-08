package net.liftweb.squerylrecord {

import net.liftweb.record.MandatoryTypedField
import org.squeryl.BaseKeyedEntity

trait KeyedRecord[K] extends BaseKeyedEntity[K] {
  def id: MandatoryTypedField[K]
  def primaryKeyValue = id.value
}

}
