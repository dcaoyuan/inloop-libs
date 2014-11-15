package inloopio.math.timeseries

import inloopio.math.indicator.Factor
import inloopio.math.indicator.Function
import inloopio.math.indicator.Indicator
import scala.reflect.ClassTag

/**
 *
 * @author Caoyuan Deng
 */
trait TBaseSer extends TSer {

  def thing: Thing

  // --- Only TBaseSer can have methods that explictly add value
  def createOrReset(time: Long)
  def ++=[V <: TVal](values: Array[V]): TSer

  /**
   * @NOTICE we can only trust TBaseSer to translate row <-> time properly.
   */
  def indexOfTime(time: Long): Int
  def timeOfIndex(idx: Int): Long

  def timeOfRow(row: Int): Long
  def rowOfTime(time: Long): Int
  def lastOccurredRow: Int

  def toOnCalendarMode
  def toOnOccurredMode
  def isOnCalendarMode: Boolean

  def function[T <: Function: ClassTag](functionClass: Class[T], args: Any*): T
  def indicator[T <: Indicator: ClassTag](indicatorClass: Class[T], args: Factor*): T
}
