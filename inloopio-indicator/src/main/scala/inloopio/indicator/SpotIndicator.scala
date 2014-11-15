package inloopio.indicator

import inloopio.collection.ArrayList
import inloopio.math.timeseries.Null
import inloopio.math.timeseries.OhlcType
import inloopio.math.timeseries.TBaseSer
import inloopio.math.timeseries.TVar
import inloopio.math.indicator.Plot
import scala.collection.immutable
import scala.reflect.ClassTag

/**
 *
 * @author Caoyuan Deng
 */
abstract class SpotIndicator(_baseSer: TBaseSer) extends Indicator(_baseSer) with inloopio.math.indicator.SpotIndicator {

  def this() = this(null)

  /**
   * @todo Also override existsFromHead and existsFromTail?
   */
  override def exists(time: Long): Boolean = true

  override def computeFrom(fromTime: Long) {
    // do nothing
  }

  protected def compute(fromIdx: Int, size: Int) {
    // do nothing
  }

  def computeSpot(time: Long) {
    /** get baseIdx before preComputeFrom(), which may clear this data */
    val baseIdx = baseSer.indexOfOccurredTime(time)
    computeSpot(time, baseIdx)
  }

  /**
   * @param time
   * @param baseIdx   baseIdx may be < 0, means there is no timestamps for this
   *                  time yet, time could be future.
   */
  protected def computeSpot(time: Long, baseIdx: Int)

  object STVar {
    def apply[V: ClassTag](): TVar[V] = new SpotTVar[V]("", true, OhlcType.Close, Plot.None)
    def apply[V: ClassTag](name: String): TVar[V] = new SpotTVar[V](name, true, OhlcType.Close, Plot.None)
    def apply[V: ClassTag](name: String, plot: Plot): TVar[V] = new SpotTVar[V](name, true, OhlcType.Close, plot)
    def apply[V: ClassTag](name: String, ohlcType: OhlcType, plot: Plot): TVar[V] = new SpotTVar[V](name, true, ohlcType, plot)
    def apply[V: ClassTag](name: String, isInstant: Boolean, ohlcType: OhlcType, plot: Plot): TVar[V] = new SpotTVar[V](name, isInstant, ohlcType, plot)
  }

  final protected class SpotTVar[V: ClassTag](_name: String, _isInstant: Boolean, _ohlcType: OhlcType, _plot: Plot) extends AbstractInnerTVar[V](_name, _isInstant, _ohlcType, _plot) {

    private var timeToValue = immutable.TreeMap[Long, V]() // must sort by time

    def values: ArrayList[V] = {
      throw new UnsupportedOperationException()
    }

    def put(time: Long, value: V): Boolean = {
      timeToValue += time -> value
      true
    }

    def put(time: Long, fromHeadOrTail: Boolean, value: V): Boolean = {
      throw new UnsupportedOperationException("Can only be accessed via time.")
    }

    def apply(time: Long): V = {
      if (!timeToValue.contains(time)) {
        computeSpot(time)
      }
      timeToValue.getOrElse(time, Null.value)
    }

    def apply(time: Long, fromHeadOrTail: Boolean): V = {
      throw new UnsupportedOperationException("Can only be accessed via time.")
    }

    def update(time: Long, value: V) {
      timeToValue += time -> value
    }

    override def apply(idx: Int): V = {
      throw new UnsupportedOperationException("Can only be accessed via time.")
    }

    override def update(idx: Int, value: V) {
      throw new UnsupportedOperationException("Can only be accessed via time.")
    }

    override def reset(idx: Int) {
      throw new UnsupportedOperationException("Can only be accessed via time.")
    }

    override def reset(time: Long) {
      timeToValue -= time
    }

    def timesIterator: Iterator[Long] = timeToValue.keysIterator
    def valuesIterator: Iterator[V] = timeToValue.valuesIterator
  }
}

