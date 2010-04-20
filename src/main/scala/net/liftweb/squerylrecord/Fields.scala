package net.liftweb.squerylrecord

import net.liftweb.record.TypedField
import net.liftweb.record.field.{BooleanTypedField, DoubleTypedField, IntTypedField, LongTypedField, StringTypedField}
import org.squeryl.customtypes.CustomType

trait SquerylField extends CustomType {
    self: TypedField[_] =>
    
    def wrappedValue: Any = valueBox openOr null
}

class UnownedIntField(v: Int) extends IntTypedField with SquerylField { set(v) }
class UnownedStringField(v: String) extends StringTypedField with SquerylField {
    set(v)
    protected val maxLength = Integer.MAX_VALUE
}
class UnownedDoubleField(v: Double) extends DoubleTypedField with SquerylField { set(v) }
class UnownedLongField(v: Long) extends LongTypedField with SquerylField { set(v) }
class UnownedBooleanField(v: Boolean) extends BooleanTypedField with SquerylField { set(v) }
