package net.liftweb.squerylrecord

import java.util.Date
import net.liftweb.record.field.{
    BooleanTypedField, DoubleTypedField, IntTypedField, LongTypedField, StringTypedField
}
import org.squeryl.dsl.QueryDsl
import org.squeryl.internals.FieldReferenceLinker
import net.liftweb.record.{MetaRecord, Record}
import org.squeryl.customtypes.{FloatField, DateField, ByteField, CustomType}
import org.squeryl.dsl.ast.{SelectElement, SelectElementReference, ConstantExpressionNode}

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

    type ByteType = ByteField // FIXME
    type IntType = IntTypedField
    type StringType = StringTypedField
    type DoubleType = DoubleTypedField
    type FloatType = FloatField // FIXME
    type LongType = LongTypedField
    type BooleanType = BooleanTypedField
    type DateType = DateField // FIXME

    protected def mapByte2ByteType(i: Byte) = new ByteField(i)
    protected def mapInt2IntType(i: Int) = new UnownedIntField(i)
    protected def mapString2StringType(s: String) = new UnownedStringField(s)
    protected def mapDouble2DoubleType(d: Double) = new UnownedDoubleField(d)
    protected def mapFloat2FloatType(d: Float) = new FloatField(d)
    protected def mapLong2LongType(l: Long) = new UnownedLongField(l)
    protected def mapBoolean2BooleanType(b: Boolean) = new UnownedBooleanField(b)
    protected def mapDate2DateType(b: Date) = new DateField(b)

    protected implicit val sampleByte = mapByte2ByteType(0)
    protected implicit val sampleInt = new UnownedIntField(0)
    protected implicit val sampleString = new UnownedStringField("")
    protected implicit val sampleDouble = new UnownedDoubleField(0.0)
    protected implicit val sampleFloat:FloatField = mapFloat2FloatType(0.0F)
    protected implicit val sampleLong = new UnownedLongField(1)
    protected implicit val sampleBoolean = new UnownedBooleanField(false)
    protected implicit val sampleDate = mapDate2DateType(new Date)


    def createLeafNodeOfScalarIntType(i: IntTypedField) =
      FieldReferenceLinker.takeLastAccessedFieldReference match {
        case None =>
          new ConstantExpressionNode[IntType](i) with NumericalExpression[IntType]
        case Some(n:SelectElement) =>
          new SelectElementReference[IntType](n)(createOutMapperIntType) with  NumericalExpression[IntType]
      }

    def createLeafNodeOfScalarIntOptionType(i: Option[IntTypedField]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarStringType(s: StringTypedField) =
      FieldReferenceLinker.takeLastAccessedFieldReference match {
        case None =>
          new ConstantExpressionNode[StringType](s) with StringExpression[StringType]
        case Some(n:SelectElement) =>
          new SelectElementReference[StringType](n)(createOutMapperStringType) with  StringExpression[StringType]
      }

    def createLeafNodeOfScalarStringOptionType(s: Option[StringTypedField]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarDoubleType(i: DoubleTypedField) =          
      FieldReferenceLinker.takeLastAccessedFieldReference match {
        case None =>
          new ConstantExpressionNode[DoubleType](i) with NumericalExpression[DoubleType]
        case Some(n:SelectElement) =>
          new SelectElementReference[DoubleType](n)(createOutMapperDoubleType) with  NumericalExpression[DoubleType]
      }

    def createLeafNodeOfScalarDoubleOptionType(i: Option[DoubleTypedField]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarFloatType(i: FloatField) = error("not here yet")
    def createLeafNodeOfScalarFloatOptionType(i: Option[FloatField]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarLongType(i: LongTypedField) =
      FieldReferenceLinker.takeLastAccessedFieldReference match {
        case None =>
          new ConstantExpressionNode[LongType](i) with NumericalExpression[LongType]
        case Some(n:SelectElement) =>
          new SelectElementReference[LongType](n)(createOutMapperLongType) with  NumericalExpression[LongType]
      }

    def createLeafNodeOfScalarLongOptionType(l: Option[LongTypedField]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarBooleanType(i: BooleanTypedField) =
      FieldReferenceLinker.takeLastAccessedFieldReference match {
        case None =>
          new ConstantExpressionNode[BooleanType](i) with BooleanExpression[BooleanType]
        case Some(n:SelectElement) =>
          new SelectElementReference[BooleanType](n)(createOutMapperBooleanType) with  BooleanExpression[BooleanType]
      }


    def createLeafNodeOfScalarBooleanOptionType(i: Option[BooleanTypedField]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarDateType(i: DateField) = error("not here yet")
    def createLeafNodeOfScalarDateOptionType(i: Option[DateField]) = error("don't know what to do about Option types")

    
}

object RecordTypesMode extends RecordTypesMode
