package net.liftweb.squerylrecord

import java.lang.reflect.{Method, Field}
import java.lang.annotation.Annotation
import java.sql.ResultSet
import java.util.Date
import net.liftweb.common.Box
import net.liftweb.record.{TypedField, BaseField}
import net.liftweb.record.field._
import org.squeryl.annotations.Column
import org.squeryl.internals.{FieldMetaData, PosoMetaData, FieldMetaDataFactory}

class RecordMetaDataFactory extends FieldMetaDataFactory {

  def build(parentMetaData: PosoMetaData[_], name: String, property: (Option[Field], Option[Method], Option[Method], Set[Annotation]), sampleInstance4OptionTypeDeduction: AnyRef, isOptimisticCounter: Boolean) = {
    
    val (field, getter, setter, annotations) = property
    val colAnnotation = annotations.find(a => a.isInstanceOf[Column]).map(a => a.asInstanceOf[Column])

    val recordFieldType = field.get.getType

    val fieldsValueType = 
        if (classOf[BooleanTypedField].isAssignableFrom(recordFieldType)) classOf[Boolean]
        else if (classOf[DateTimeTypedField].isAssignableFrom(recordFieldType)) classOf[Date]
        else if (classOf[DoubleTypedField].isAssignableFrom(recordFieldType)) classOf[Double]
        else if (classOf[IntTypedField].isAssignableFrom(recordFieldType)) classOf[Int]
        else if (classOf[LongTypedField].isAssignableFrom(recordFieldType)) classOf[Long]
        else if (classOf[StringTypedField].isAssignableFrom(recordFieldType)) classOf[String]
        else error("unsupported field type : " + recordFieldType.getName)


    new FieldMetaData(
      parentMetaData,
      name,
      fieldsValueType, // if isOption, this fieldType is the type param of Option, i.e. the T in Option[T]
      fieldsValueType, //in primitive type mode fieldType == wrappedFieldType, in custom type mode wrappedFieldType is the 'real' type, i.e. the (primitive) type that jdbc understands
      None, //val customTypeFactory: Option[AnyRef=>Product1[Any]],
      false, //val isOption: Boolean,
      getter,
      setter,
      field,
      colAnnotation,
      isOptimisticCounter) {

      //records are always fields 
      val recordField = field.get

      private def _typedField(record: AnyRef) =
        recordField.get(record).asInstanceOf[TypedField[AnyRef]]

      override def setFromResultSet(target: AnyRef, rs: ResultSet, index: Int) = {        
        val v = resultSetHandler(rs, index)

          println("!!!!!!!!!! setFromResultSet(" + target.toString + ", rs, " + index + ") v=" + v.toString)

        _typedField(target).setFromAny(Box!!v)
      }

      override def get(o: AnyRef) = _typedField(o).valueBox openOr null
    }
  }
}
