package net.liftweb.squerylrecord

import java.lang.reflect.{Method, Field}
import java.lang.annotation.Annotation
import net.liftweb.record.field._
import org.squeryl.annotations.Column
import org.squeryl.internals.{FieldMetaData, PosoMetaData, FieldMetaDataFactory}
import java.sql.ResultSet
import net.liftweb.record.{TypedField, BaseField}

class RecordMetaDataFactory extends FieldMetaDataFactory {

  def build(parentMetaData: PosoMetaData[_], name: String, property: (Option[Field], Option[Method], Option[Method], Set[Annotation]), sampleInstance4OptionTypeDeduction: AnyRef, isOptimisticCounter: Boolean) = {
    
    val field  = property._1
    val getter = property._2
    val setter = property._3
    val annotations = property._4
    val colAnnotation = annotations.find(a => a.isInstanceOf[Column]).map(a => a.asInstanceOf[Column])

    val recordFieldType = field.get.getType

    val fieldsValueType =
      if(classOf[LongField[_]].isAssignableFrom(recordFieldType))
        classOf[Long]
      else if(classOf[StringField[_]].isAssignableFrom(recordFieldType))
        classOf[String]
      else if(classOf[IntField[_]].isAssignableFrom(recordFieldType))
        classOf[Int]
      else
        error("unsupported field type : " + recordFieldType.getName)


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
        recordField.get(record).asInstanceOf[TypedField[_]]      

      override def setFromResultSet(target: AnyRef, rs: ResultSet, index: Int) = {
        
        val v = resultSetHandler(rs, index)
        _typedField(target).setFromAny(v)
      }

      override def get(o: AnyRef) = {
        val v = _typedField(o).value
        if(v == null)
          null
        else
          v.asInstanceOf[AnyRef]
      }
    }
  }
}