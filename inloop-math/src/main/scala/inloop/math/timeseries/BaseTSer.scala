package inloop.math.timeseries

/**
 *
 * @author Caoyuan Deng
 */
import inloop.math.timeseries.datasource.SerProvider

trait BaseTSer extends TSer {

  def serProvider: SerProvider

  // --- Only BaseTSer can have methods that explictly add value
  def createOrReset(time: Long)
  def ++=[V <: TVal](values: Array[V]): TSer

  /**
   * @NOTICE we can only trust BaseTSer to translate row <-> time properly.
   */
  def indexOfTime(time: Long): Int
  def timeOfIndex(idx: Int): Long

  def timeOfRow(row: Int): Long
  def rowOfTime(time: Long): Int
  def lastOccurredRow: Int

  def toOnCalendarMode
  def toOnOccurredMode
  def isOnCalendarMode: Boolean
}

