package inloop.indicator

import inloop.math.timeseries.BaseTSer
import inloop.math.timeseries.Null
import inloop.math.timeseries.ThingSer

/**
 *
 * @author Caoyuan Deng
 */
class CompareIndicator(_baseSer: BaseTSer) extends Indicator(_baseSer) {

  var serToBeCompared: ThingSer = _

  val begPosition = Factor("Begin of Time Frame", 0L)
  val endPosition = Factor("End of Time Frame", 0L)
  val maxValue = Factor("Max Value", Double.MinValue)
  val minValue = Factor("Min Value", Double.MaxValue)

  var open = TVar[Double]("O", Plot.OHLC)
  var high = TVar[Double]("H", Plot.OHLC)
  var low = TVar[Double]("L", Plot.OHLC)
  var close = TVar[Double]("C", Plot.OHLC)
  var volume = TVar[Double]("V", Plot.Volume)

  protected def compute(begIdx: Int, size: Int) {
    /** camparing base point is the value of begin time (the most left on screen */

    /** always compute from most left position on screen */
    val begPos = begPosition.value.toInt //math.min((int)begPosition.value(), begIdx);
    val endPos = endPosition.value.toInt //math.min((int)endPosition.value(),   _dataSize - 1);

    val baseTSer = baseSer.asInstanceOf[ThingSer]
    /** get first value of baseSer in time frame, it will be the comparing base point */
    var baseNorm = Null.Double
    var row = begPosition.value.toInt
    var end = endPosition.value.toInt
    var break = false
    while (row <= end & !break) {
      val baseTime = baseTSer.timeOfRow(row)
      if (baseTSer.exists(baseTime)) {
        baseNorm = baseTSer.close(baseTime)
        break = true
      }

      row += 1
    }

    if (Null.is(baseNorm)) {
      return
    }

    var compareNorm = Null.Double
    /**
     * !NOTICE
     * we only calculate this indicator's value for a timeSet showing in screen,
     * instead of all over the time frame of baseSer, thus, we use
     * this time set for loop instead of the usaully usage in other indicators:
     *        for (int i = fromIndex; i < size(); i++) {
     *            ....
     *        }
     *
     * Think about it, when the baseSer updated, we should re-calculate
     * all Ser instead from fromIndex.
     */
    var i = begPos
    while (i <= endPos) {
      if (i < begPosition.value) {
        /** don't calulate those is less than beginPosition to got a proper compareBeginValue */
      } else {

        val time = baseSer.asInstanceOf[BaseTSer].timeOfRow(i)

        /**
         * !NOTICE:
         * we should fetch serToBeCompared by time instead by position which may
         * not sync with baseSer.
         */
        if (serToBeCompared.exists(time)) {
          /** get first value of serToBeCompared in time frame */
          if (Null.is(compareNorm)) {
            compareNorm = serToBeCompared.close(time)
          }

          if (exists(time)) {
            open(time) = linearAdjust(serToBeCompared.open(time), compareNorm, baseNorm)
            high(time) = linearAdjust(serToBeCompared.high(time), compareNorm, baseNorm)
            low(time) = linearAdjust(serToBeCompared.low(time), compareNorm, baseNorm)
            close(time) = linearAdjust(serToBeCompared.close(time), compareNorm, baseNorm)
          }
        }
      }

      i += 1
    }
  }

  /**
   * This function keeps the adjusting linear according to a norm
   */
  private def linearAdjust(value: Double, prevNorm: Double, postNorm: Double): Double = {
    ((value - prevNorm) / prevNorm) * postNorm + postNorm
  }

}
