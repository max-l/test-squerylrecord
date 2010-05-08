package net.liftweb.squerylrecord

import java.lang.reflect.{Method, Field}
import java.lang.annotation.Annotation
import java.sql.ResultSet
import java.util.{Calendar, Date}
import net.liftweb.common.{Box, Full}
import net.liftweb.record.{MetaRecord, Record, TypedField}
import net.liftweb.record.field._
import org.squeryl.annotations.Column
import org.squeryl.internals.{FieldMetaData, PosoMetaData, FieldMetaDataFactory}
import scala.collection.immutable.Map

class RecordMetaDataFactory extends FieldMetaDataFactory {

  private var metaRecordsByClass: Map[Class[_], MetaRecord[_]] = Map.empty

  private def findMetaField(clasz: Class[_], name: String): TypedField[_] = {
    def fieldFrom(mr: MetaRecord[_]): TypedField[_] =
      mr.asInstanceOf[Record[_]].fieldByName(name) match {
        case Full(f: TypedField[_]) => f
        case Full(_) => error("field " + name + " in Record metadata for " + clasz + " is not a TypedField")
        case _ => error("failed to find field " + name + " in Record metadata for " + clasz)
      }

    metaRecordsByClass get clasz match {
      case Some(mr) => fieldFrom(mr)
      case None =>
        try {
          val rec = clasz.newInstance.asInstanceOf[Record[_]]
          val mr = rec.meta
          metaRecordsByClass = metaRecordsByClass updated (clasz, mr)
          fieldFrom(mr)
        } catch {
          case ex => error("failed to find MetaRecord for " + clasz + " due to exception " + ex.toString)
        }
    }
  }

  def build(parentMetaData: PosoMetaData[_], name: String, property: (Option[Field], Option[Method], Option[Method], Set[Annotation]), sampleInstance4OptionTypeDeduction: AnyRef, isOptimisticCounter: Boolean) = {
    val metaField = findMetaField(parentMetaData.clasz, name)

    val (field, getter, setter, annotations) = property
    val colAnnotation = annotations.find(a => a.isInstanceOf[Column]).map(a => a.asInstanceOf[Column])

    val fieldsValueType = metaField match {
      case (_: BooleanTypedField)  => classOf[Boolean]
      case (_: DateTimeTypedField) => classOf[Date]
      case (_: DoubleTypedField)   => classOf[Double]
      case (_: IntTypedField)      => classOf[Int]
      case (_: LongTypedField)     => classOf[Long]
      case (_: StringTypedField)   => classOf[String]
      case _ => error("unsupported field type : " + metaField)
    }

    new FieldMetaData(
      parentMetaData,
      name,
      fieldsValueType, // if isOption, this fieldType is the type param of Option, i.e. the T in Option[T]
      fieldsValueType, //in primitive type mode fieldType == wrappedFieldType, in custom type mode wrappedFieldType is the 'real' type, i.e. the (primitive) type that jdbc understands
      None, //val customTypeFactory: Option[AnyRef=>Product1[Any]],
      metaField.optional_?,
      getter,
      setter,
      field,
      colAnnotation,
      isOptimisticCounter) {

      private def fieldFor(o: AnyRef) = getter.get.invoke(o).asInstanceOf[TypedField[_]]

      override def setFromResultSet(target: AnyRef, rs: ResultSet, index: Int) =
        fieldFor(target).setFromAny(Box!!resultSetHandler(rs, index))

      override def get(o: AnyRef) = fieldFor(o).valueBox match {
        case Full(c: Calendar) => c.getTime
        case Full(other) => other.asInstanceOf[AnyRef]
        case _ => null
      }
    }
  }
}
