package bootstrap.liftweb

import scala.util.control.Exception.ultimately
import net.liftweb.common._
import net.liftweb.util.{LoanWrapper, Props}
import net.liftweb.http.{LiftRules, S}
import net.liftweb.mapper.{DB, DefaultConnectionIdentifier, StandardDBVendor}
import net.liftweb.sitemap.{Menu, SiteMap, Loc}
import net.liftweb.squerylrecord.SquerylRecord
import org.squeryl.adapters.H2Adapter
import org.squeryl.Session
import java.lang.annotation.Annotation
import Loc._
import net.liftweb.record.field._
import java.util.Calendar
import org.squeryl.annotations.Column
import java.sql.{ResultSet, Timestamp, Connection}
import net.liftweb.record.{TypedField, Record, BaseField, MetaRecord}
import java.lang.reflect.{Method, Field}
import org.squeryl.internals.{FieldMetaDataFactory, PosoMetaData, FieldMetaData}

/**
  * A class that's instantiated early and run.  It allows the application
  * to modify lift's environment
  */
class Boot {
  def boot {
    if (!DB.jndiJdbcConnAvailable_?)
      DB.defineConnectionManager(DefaultConnectionIdentifier,
        new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
                             Props.get("db.url") openOr "jdbc:h2:test",
                             Props.get("db.user"), Props.get("db.password")))

    SquerylRecord.init(() => new H2Adapter)

    FieldMetaData.factory = new RecordMetaDataFactoryOverride

      println("name of Author.name is " + test.model.Author.name.name)


    // where to search snippet
    LiftRules.addToPackages("test")

    // Build SiteMap
    val entries = Menu(Loc("Home", List("index"), "Home")) :: Nil
    LiftRules.setSiteMap(SiteMap(entries:_*))
    
    S.addAround(DB.buildLoanWrapper(DefaultConnectionIdentifier::Nil))
    S.addAround(new LoanWrapper {
        def apply[T](f: => T): T = ultimately(println(S.getAllNotices))(f)
    })
  }
}



/** FieldMetaDataFactory that allows Squeryl to use Records as model objects. */
class RecordMetaDataFactoryOverride extends FieldMetaDataFactory {
  /** Cache MetaRecords by the model object class (Record class) */
  private var metaRecordsByClass: Map[Class[_], MetaRecord[_]] = Map.empty

  /** Given a model object class (Record class) and field name, return the BaseField from the meta record */
  private def findMetaField(clasz: Class[_], name: String): BaseField = {
    def fieldFrom(mr: MetaRecord[_]): BaseField =
      mr.asInstanceOf[Record[_]].fieldByName(name) match {
        case Full(f: BaseField) => f
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

  /** Build a Squeryl FieldMetaData for a particular field in a Record */
  def build(parentMetaData: PosoMetaData[_], name: String,
            property: (Option[Field], Option[Method], Option[Method], Set[Annotation]),
            sampleInstance4OptionTypeDeduction: AnyRef, isOptimisticCounter: Boolean): FieldMetaData = {
    val metaField = findMetaField(parentMetaData.clasz, name)

    val (field, getter, setter, annotations) = property
    val colAnnotation = annotations.find(a => a.isInstanceOf[Column]).map(a => a.asInstanceOf[Column])

    val fieldsValueType = metaField match {
      case (_: BooleanTypedField)  => classOf[Boolean]
      case (_: DateTimeTypedField) => classOf[Timestamp]
      case (_: DoubleTypedField)   => classOf[Double]
      case (_: IntTypedField)      => classOf[Int]
      case (_: LongTypedField)     => classOf[Long]
      case (_: StringTypedField)   => classOf[String]
      case (_: EnumTypedField[_])   => classOf[Enumeration#Value]
      case _ => error("unsupported field type : " + metaField)
    }

    val overrideColLength = metaField match {
      case (stringTypedField: StringTypedField) => Some(stringTypedField.maxLength)
      case _ => None
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
      isOptimisticCounter,
      metaField) {

      override def length = overrideColLength getOrElse super.length

      private def fieldFor(o: AnyRef) = getter.get.invoke(o).asInstanceOf[TypedField[AnyRef]]

      override def setFromResultSet(target: AnyRef, rs: ResultSet, index: Int) =
        fieldFor(target).setFromAny(Box!!resultSetHandler(rs, index))

      override def get(o: AnyRef) = fieldFor(o).valueBox match {
        case Full(c: Calendar) => new Timestamp(c.getTime.getTime)
        case Full(other) => other
        case _ => null
      }
    }
  }
}