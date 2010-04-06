package net.liftweb.squerylrecord

import java.util.Date
import net.liftweb.record.Record
import net.liftweb.record.field.{BooleanField, DoubleField, IntField, LongField, StringField}
import org.squeryl.dsl.QueryDsl
import org.squeryl.dsl.ast.{SelectElementReference, ConstantExpressionNode}
import org.squeryl.internals.FieldReferenceLinker

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

    type ByteType = Nothing //ByteField[_]
    type IntType = IntField[_]
    type StringType = StringField[_]
    type DoubleType = DoubleField[_]
    type FloatType = Nothing //FloatField[_]
    type LongType = LongField[_]
    type BooleanType = BooleanField[_]
    type DateType = Nothing //DateField[_]

    // ick nulls
    protected def mapByte2ByteType(i: Byte) = error("not here yet")
    protected def mapInt2IntType(i: Int) = new IntField(null, i)
    protected def mapString2StringType(s: String) = new StringField(null, s)
    protected def mapDouble2DoubleType(d: Double) = new DoubleField(null, d)
    protected def mapFloat2FloatType(d: Float) = error("not here yet")
    protected def mapLong2LongType(l: Long) = new LongField(null, l)
    protected def mapBoolean2BooleanType(b: Boolean) = new BooleanField(null, b)
    protected def mapDate2DateType(b: Date) = error("not here yet")

    protected implicit val sampleByte = null
    protected implicit val sampleInt = new IntField(null, 0)
    protected implicit val sampleString = new StringField(null, "")
    protected implicit val sampleDouble = new DoubleField(null, 0.0)
    protected implicit val sampleFloat = null
    protected implicit val sampleLong = new LongField(null, 1)
    protected implicit val sampleBoolean = new BooleanField(null, false)
    protected implicit val sampleDate = null


    def createLeafNodeOfScalarIntType(i: IntField[_]) =
        new SelectElementReference[IntType](FieldReferenceLinker.takeLastAccessedFieldReference.get)(createOutMapperIntType) with NumericalExpression[IntType]
    def createLeafNodeOfScalarIntOptionType(i: Option[IntField[_]]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarStringType(s: StringField[_]) =
        new SelectElementReference[StringType](FieldReferenceLinker.takeLastAccessedFieldReference.get)(createOutMapperStringType) with StringExpression[StringType]
    def createLeafNodeOfScalarStringOptionType(s: Option[StringField[_]]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarDoubleType(i: DoubleField[_]) =
        new SelectElementReference[DoubleType](FieldReferenceLinker.takeLastAccessedFieldReference.get)(createOutMapperDoubleType) with  NumericalExpression[DoubleType]
    def createLeafNodeOfScalarDoubleOptionType(i: Option[DoubleField[_]]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarFloatType(i: Nothing /*FloatField*/) = error("not here yet")
    def createLeafNodeOfScalarFloatOptionType(i: Option[Nothing/*FloatField*/]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarLongType(i: LongField[_]) =
        new SelectElementReference[LongType](FieldReferenceLinker.takeLastAccessedFieldReference.get)(createOutMapperLongType) with  NumericalExpression[LongType]
    def createLeafNodeOfScalarLongOptionType(l: Option[LongField[_]]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarBooleanType(i: BooleanField[_]) =
        new SelectElementReference[BooleanType](FieldReferenceLinker.takeLastAccessedFieldReference.get)(createOutMapperBooleanType) with  BooleanExpression[BooleanType]

    def createLeafNodeOfScalarBooleanOptionType(i: Option[BooleanField[_]]) = error("don't know what to do about Option types")

    def createLeafNodeOfScalarDateType(i: Nothing /*DateField*/) = error("not here yet")
    def createLeafNodeOfScalarDateOptionType(i: Option[Nothing/*DateField*/]) = error("don't know what to do about Option types")

    
}

object RecordTypesMode extends RecordTypesMode
