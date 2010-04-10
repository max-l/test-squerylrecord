package net.liftweb.squerylrecord

import java.util.Date
import net.liftweb.record.field.{BooleanField, DoubleField, IntField, LongField, StringField}
import org.squeryl.dsl.QueryDsl
import org.squeryl.internals.FieldReferenceLinker
import net.liftweb.record.{MetaRecord, Record}
import org.squeryl.customtypes.{FloatField, DateField, ByteField, CustomType}
import org.squeryl.dsl.ast.{SelectElement, SelectElementReference, ConstantExpressionNode}

trait DummyRecord extends Record[DummyRecord] {
  def meta: MetaRecord[DummyRecord] = null
}

object DummyRecord extends DummyRecord

class SLongField(v: Long) extends LongField[DummyRecord](DummyRecord) with CustomType {
  set(v)
  def wrappedValue: Any = value
}

class SIntField(v: Int) extends IntField[DummyRecord](DummyRecord) with CustomType {
  set(v)
  def wrappedValue: Any = value
}

class SStringField(v: String) extends StringField[DummyRecord](DummyRecord, 128) with CustomType {
  set(v)
  def wrappedValue: Any = value
}

class SDoubleField(v: Double) extends DoubleField[DummyRecord](DummyRecord) with CustomType {
  set(v)
  def wrappedValue: Any = value
}

class SBooleanField(v: Boolean) extends BooleanField[DummyRecord](DummyRecord) with CustomType {
  set(v)
  def wrappedValue: Any = value
}

trait RecordTypesMode extends QueryDsl {

    implicit def createConstantNodeOfScalarIntType(i: Int) =
        new ConstantExpressionNode[Int](i) with NumericalExpression[Int]

    implicit def createConstantNodeOfScalarStringType(s: String) =
        new ConstantExpressionNode[String](s, true) with StringExpression[String]

    implicit def createConstantNodeOfScalarDoubleType(i: Double) =
        new ConstantExpressionNode[Double](i) with NumericalExpression[Double]

    implicit def createConstantNodeOfScalarFloatType(i: Float) =
        new ConstantExpressionNode[Float](i) with NumericalExpression[Float]

    implicit def createConstantNodeOfScalarLongType(i: Long) =
        new ConstantExpressionNode[Long](i) with NumericalExpression[Long]

    implicit def createConstantNodeOfScalarBooleanType(i: Boolean) =
        new ConstantExpressionNode[Boolean](i) with NonNumericalExpression[Boolean]

    type ByteType = ByteField
    type IntType = SIntField
    type StringType = SStringField
    type DoubleType = SDoubleField
    type FloatType = FloatField
    type LongType = SLongField
    type BooleanType = SBooleanField
    type DateType = DateField

    // ick nulls
    protected def mapByte2ByteType(i: Byte) = new ByteField(i)
    protected def mapInt2IntType(i: Int) = new SIntField(i)
    protected def mapString2StringType(s: String) = new SStringField(s)
    protected def mapDouble2DoubleType(d: Double) = new SDoubleField(d)
    protected def mapFloat2FloatType(d: Float) = new FloatField(d)
    protected def mapLong2LongType(l: Long) = new SLongField(l)
    protected def mapBoolean2BooleanType(b: Boolean) = new SBooleanField(b)
    protected def mapDate2DateType(b: Date) = new DateField(b)

    protected implicit val sampleByte = mapByte2ByteType(0)
    protected implicit val sampleInt = new SIntField(0)
    protected implicit val sampleString = new SStringField("")
    protected implicit val sampleDouble = new SDoubleField(0.0)
    protected implicit val sampleFloat:FloatField = mapFloat2FloatType(0.0F)
    protected implicit val sampleLong = new SLongField(1)
    protected implicit val sampleBoolean = new SBooleanField(false)
    protected implicit val sampleDate = mapDate2DateType(new Date)


    def createLeafNodeOfScalarIntType(i: SIntField) =
      FieldReferenceLinker.takeLastAccessedFieldReference match {
        case None =>
          new ConstantExpressionNode[IntType](i) with NumericalExpression[IntType]
        case Some(n:SelectElement) =>
          new SelectElementReference[IntType](n)(createOutMapperIntType) with  NumericalExpression[IntType]
      }

    def createLeafNodeOfScalarIntOptionType(i: Option[SIntField]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarStringType(s: SStringField) =
      FieldReferenceLinker.takeLastAccessedFieldReference match {
        case None =>
          new ConstantExpressionNode[StringType](s) with StringExpression[StringType]
        case Some(n:SelectElement) =>
          new SelectElementReference[StringType](n)(createOutMapperStringType) with  StringExpression[StringType]
      }

    def createLeafNodeOfScalarStringOptionType(s: Option[SStringField]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarDoubleType(i: SDoubleField) =          
      FieldReferenceLinker.takeLastAccessedFieldReference match {
        case None =>
          new ConstantExpressionNode[DoubleType](i) with NumericalExpression[DoubleType]
        case Some(n:SelectElement) =>
          new SelectElementReference[DoubleType](n)(createOutMapperDoubleType) with  NumericalExpression[DoubleType]
      }

    def createLeafNodeOfScalarDoubleOptionType(i: Option[SDoubleField]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarFloatType(i: FloatField) = error("not here yet")
    def createLeafNodeOfScalarFloatOptionType(i: Option[FloatField]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarLongType(i: SLongField) =
      FieldReferenceLinker.takeLastAccessedFieldReference match {
        case None =>
          new ConstantExpressionNode[LongType](i) with NumericalExpression[LongType]
        case Some(n:SelectElement) =>
          new SelectElementReference[LongType](n)(createOutMapperLongType) with  NumericalExpression[LongType]
      }

    def createLeafNodeOfScalarLongOptionType(l: Option[SLongField]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarBooleanType(i: SBooleanField) =
      FieldReferenceLinker.takeLastAccessedFieldReference match {
        case None =>
          new ConstantExpressionNode[BooleanType](i) with BooleanExpression[BooleanType]
        case Some(n:SelectElement) =>
          new SelectElementReference[BooleanType](n)(createOutMapperBooleanType) with  BooleanExpression[BooleanType]
      }


    def createLeafNodeOfScalarBooleanOptionType(i: Option[SBooleanField]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarDateType(i: DateField) = error("not here yet")
    def createLeafNodeOfScalarDateOptionType(i: Option[DateField]) = error("don't know what to do about Option types")

    
}

object RecordTypesMode extends RecordTypesMode
