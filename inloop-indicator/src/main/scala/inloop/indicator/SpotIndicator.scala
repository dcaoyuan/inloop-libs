package inloop.indicator

import inloop.collection.ArrayList
import inloop.math.timeseries.BaseTSer
import inloop.math.timeseries.Null
import inloop.math.timeseries.TVar
import inloop.math.indicator.Plot
import scala.collection.immutable
import scala.reflect.ClassTag

/**
 *
 * @author Caoyuan Deng
 */
abstract class SpotIndicator(_baseSer: BaseTSer) extends Indicator(_baseSer) with inloop.math.indicator.SpotIndicator {

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
    def apply[V: ClassTag](): TVar[V] = new SpotTVar[V]("", true, Plot.None)
    def apply[V: ClassTag](name: String): TVar[V] = new SpotTVar[V](name, true, Plot.None)
    def apply[V: ClassTag](name: String, plot: Plot): TVar[V] = new SpotTVar[V](name, true, plot)
    def apply[V: ClassTag](name: String, isInstant: Boolean, plot: Plot): TVar[V] = new SpotTVar[V](name, isInstant, plot)
  }

  final protected class SpotTVar[V: ClassTag](_name: String, _isInstant: Boolean, _plot: Plot) extends AbstractInnerTVar[V](_name, _isInstant, _plot) {

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

