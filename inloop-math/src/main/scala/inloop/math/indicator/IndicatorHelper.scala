package inloop.math.indicator

import inloop.math.timeseries.TSerEvent
import inloop.math.timeseries.BaseTSer
import inloop.util.actors.Reactions

/**
 * A helper class to implement most of the Indicator methods, it can be used
 * by indicator etc.
 *
 * @param baseSer:Ser base series to compute resultSer
 * @param resultSer:Indicatoe result series to be computed
 *
 * @author Caoyuan Deng
 */
trait IndicatorHelper { self: Indicator =>

  /**
   * preComputeFrom will set and backup the context before computeFrom(long begTime):
   * begTime, begIdx etc.
   *
   *
   * @return fromTime
   */
  private var fromTime: Long = _ // used by postComputeFrom only

  private var baseSerReaction: Reactions.Reaction = _
  // remember event's callback to be forwarded in postCompute()
  private var baseSerEventCallBack: TSerEvent.Callback = _

  protected def createBaseSerReaction(baseSer: BaseTSer) = {
    /**
     * The ser is a result computed from baseSer, so should follow the baseSeries' data changing:
     * 1. In case of series is the same as baseSeries, should respond to
     *    Computed event of baseSeries.
     * 2. In case of series is not the same as baseSeries, should respond to
     *    Loaded, Refresh and Updated event of baseSeries.
     */
    baseSerReaction = {
      case TSerEvent.Loaded(_, _, fromTime, toTime, _, callback) =>
        self.computeFrom(fromTime)
        baseSerEventCallBack = callback
      case TSerEvent.Refresh(_, _, fromTime, toTime, _, callback) =>
        self.computeFrom(fromTime)
        baseSerEventCallBack = callback
      case TSerEvent.Updated(_, _, fromTime, toTime, _, callback) =>
        self.computeFrom(fromTime)
        baseSerEventCallBack = callback
      case TSerEvent.Computed(src, _, fromTime, toTime, _, callback) if (src eq baseSer) && (src ne this) =>
        /**
         * If the resultSer is the same as baseSer (such as QuoteSer),
         * the baseSer will fire an event when compute() finished,
         * then run to here, this may cause a dead loop. So, FinishedComputing
         * should not react when self eq baseSer
         */
        self.computeFrom(fromTime)
        baseSerEventCallBack = callback
      case TSerEvent.Cleared(src, _, fromTime, toTime, _, callback) if (src eq baseSer) && (src ne this) =>
        self.clear(fromTime)
        baseSerEventCallBack = callback
    }

    baseSerReaction
  }

  def preComputeFrom(fromTime: Long): Int = {
    assert(this.baseSer != null, "base series not set!")

    val timestamps = self.timestamps

    val (fromTime1, fromIdx, mayNeedToValidate) = if (fromTime <= 0) {
      (fromTime, 0, true)
    } else {
      if (fromTime < self.computedTime) {
        // * the timestamps <-> items map may not be validate now, should validate it first
        val fromTimeX = fromTime
        // * indexOfOccurredTime always returns physical index, so don't worry about isOncalendarTime
        val fromIdxX = math.max(timestamps.indexOfOccurredTime(fromTimeX), 0) // should not less then 0
        (fromTimeX, fromIdxX, true)
      } else if (fromTime > self.computedTime) {
        // * if begTime > computedTime, re-compute from computedTime
        val fromTimeX = self.computedTime
        // * indexOfOccurredTime always returns physical index, so don't worry about isOncalendarTime
        val fromIdxX = math.max(timestamps.indexOfOccurredTime(fromTimeX), 0) // should not less then 0
        (fromTimeX, fromIdxX, timestamps.size > self.size)
      } else {
        // * begTime == computedTime
        // * if begTime > computedTime, re-compute from computedTime
        val fromTimeX = self.computedTime
        // * indexOfOccurredTime always returns physical index, so don't worry about isOncalendarTime
        val fromIdxX = math.max(timestamps.indexOfOccurredTime(fromTimeX), 0) // should not less then 0
        (fromTimeX, fromIdxX, false)
      }
    }

    if (this ne baseSer) {
      self.validate
    }

    //        if (mayNeedToValidate) {
    //            self.validate
    //        }

    this.fromTime = fromTime1

    //println(resultSer.freq + resultSer.shortDescription + ": computed time=" + computedTime + ", begIdx=" + begIdx)
    /**
     * should re-compute series except it's also the baseSer:
     * @TODO
     * Do we really need clear it from begTime, or just from computed time after computing ?
     */
    //        if (resultSer != baseSer) {
    //            /** in case of resultSer == baseSer, do this will also clear baseSer */
    //            resultSer.clear(fromTime);
    //        }

    fromIdx
  }

  def postComputeFrom {
    // construct resultSer's change event, forward baseTSerEventCallBack
    self.publish(TSerEvent.Computed(self,
      null,
      fromTime,
      self.computedTime,
      null,
      baseSerEventCallBack))
  }

  def dispose {
    if (baseSerReaction != null) {
      baseSer.reactions -= baseSerReaction
    }
  }

}
