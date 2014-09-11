package inloop.math.timeseries

import inloop.math.indicator.Plottable
import inloop.collection.ArrayList
import scala.reflect.ClassTag

/**
 * A horizontal view of Ser. It's a reference of one of the field vars.
 * V is the type of value
 *
 * @author Caoyuan Deng
 */
abstract class TVar[V: ClassTag] extends Plottable {
  private val nullVal = Null.value[V]

  def name: String
  def name_=(name: String)

  def timestamps: TStamps

  /**
   * @return Is it an instant variable if true, or an accumulate variable if false.
   */
  def isInstant: Boolean

  /**
   * Append or insert value at time
   */
  def put(time: Long, value: V): Boolean
  def put(time: Long, fromHeadOrTail: Boolean, value: V): Boolean

  def apply(time: Long): V
  def apply(time: Long, fromHeadOrTail: Boolean): V

  def update(time: Long, value: V)

  def values: ArrayList[V]

  def timesIterator: Iterator[Long]
  def valuesIterator: Iterator[V]

  /**
   * This method will never return null, return a nullValue at least.
   */
  def apply(idx: Int): V = {
    if (idx >= 0 && idx < values.size) {
      values(idx) match {
        case null  => nullVal
        case value => value
      }
    } else nullVal
  }

  def update(idx: Int, value: V) {
    if (idx >= 0 && idx < values.size) {
      values(idx) = value
    } else {
      assert(false, "TVar.update(index, value): this index's value of Var did not be holded yet: " +
        "idx=" + idx + ", value size=" + values.size + ", timestamps size=" + timestamps.size)
    }
  }

  final def putNull(time: Long): Boolean = put(time, nullVal)
  final def putNull(idx: Int): Boolean = put(idx, nullVal)

  /** reset to nullValue */
  def reset(time: Long) = update(time, nullVal)
  /** reset to nullValue */
  def reset(idx: Int) = update(idx, nullVal)

  def toArray(fromTime: Long, toTime: Long): Array[V] = {
    try {
      timestamps.readLock.lock

      val frIdx = timestamps.indexOrNextIndexOfOccurredTime(fromTime)
      val toIdx = timestamps.indexOrPrevIndexOfOccurredTime(toTime)
      val len = toIdx - frIdx + 1
      val values1 = new Array[V](len)

      values.copyToArray(values1, frIdx, len)
      values1

    } finally {
      timestamps.readLock.unlock
    }
  }

  def toArrayWithTime(fromTime: Long, toTime: Long): (Array[Long], Array[V]) = {
    try {
      timestamps.readLock.lock

      val frIdx = timestamps.indexOrNextIndexOfOccurredTime(fromTime)
      val toIdx = timestamps.indexOrPrevIndexOfOccurredTime(toTime)
      val len = toIdx - frIdx + 1
      val times1 = new Array[Long](len)
      val values1 = new Array[V](len)

      timestamps.copyToArray(times1, frIdx, len)
      values.copyToArray(values1, frIdx, len)
      (times1, values1)

    } finally {
      timestamps.readLock.unlock
    }
  }

  def toDoubleArray: Array[Double] = {
    val length = size
    val result = new Array[Double](length)

    if (length > 0 && apply(0).isInstanceOf[Number]) {
      var i = 0
      while (i < length) {
        result(i) = apply(i).asInstanceOf[Number].doubleValue
        i += 1
      }
    }

    result
  }

  def float(time: Long): Float = toFloat(apply(time))
  def float(idx: Int): Float = toFloat(apply(idx))

  def double(time: Long): Double = toDouble(apply(time))
  def double(idx: Int): Double = toDouble(apply(idx))

  def toFloat(v: V): Float = {
    v match {
      case null      => Null.Float
      case x: Byte   => x.toFloat
      case x: Short  => x.toFloat
      case x: Char   => x.toFloat
      case x: Int    => x.toFloat
      case x: Long   => x.toFloat
      case x: Float  => x
      case x: Double => x.toFloat
      case x: Number => x.floatValue
      case x: AnyRef =>
        assert(false, "Why you get here(TVar.float) ? " +
          v + " Check your code and give me Float instead of float: " +
          x.asInstanceOf[AnyRef].getClass.getCanonicalName)
        Null.Float
    }
  }

  def toDouble(v: V): Double = {
    v match {
      case null      => Null.Double
      case x: Byte   => x.toDouble
      case x: Short  => x.toDouble
      case x: Char   => x.toDouble
      case x: Int    => x.toDouble
      case x: Long   => x.toDouble
      case x: Float  => x.toDouble
      case x: Double => x
      case x: Number => x.doubleValue
      case x: AnyRef =>
        assert(false, "Why you get here(TVar.double) ? " +
          v + " Check your code and give me Double instead of double: " +
          x.asInstanceOf[AnyRef].getClass.getCanonicalName)
        Null.Double
    }
  }

  /**
   * Clear values that >= fromIdx
   */
  def clear(fromIdx: Int): Unit = {
    if (fromIdx < 0) {
      return
    }
    var i = values.size - 1
    while (i >= fromIdx) {
      values.remove(i)
      i += 1
    }
  }

  def size: Int = values.size

  /**
   * All instances of TVar or extended classes will be equals if they have the
   * same values, this prevent the duplicated manage of values.
   */
  override def equals(o: Any): Boolean = {
    o match {
      case x: TVar[_] => this.values eq x.values
      case _          => false
    }
  }

  /**
   * All instances of TVar or extended classes use identityHashCode as hashCode
   */
  private val _hashCode = System.identityHashCode(this)
  override def hashCode: Int = _hashCode

  override def toString = name
}
