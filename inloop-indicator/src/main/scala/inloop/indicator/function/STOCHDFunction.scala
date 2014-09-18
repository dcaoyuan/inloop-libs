package inloop.indicator.function

import inloop.math.timeseries.Null
import inloop.math.timeseries.BaseTSer
import inloop.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class STOCHDFunction(_baseSer: BaseTSer, var period: Factor, var periodK: Factor, var periodD: Factor) extends Function(_baseSer) {

  val _stochK = TVar[Double]
  val _stochD = TVar[Double]

  override def set(args: Any*): Unit = {
    period = args(0).asInstanceOf[Factor]
    periodK = args(1).asInstanceOf[Factor]
    periodD = args(2).asInstanceOf[Factor]
  }

  protected def computeSpot(i: Int): Unit = {
    _stochK(i) = stochK(i, period, periodK)

    if (i < periodD.value - 1) {

      _stochD(i) = Null.Double

    } else {

      /** smooth stochK, periodD */
      _stochD(i) = ma(i, _stochK, periodD)

    }
  }

  def stochD(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _stochD(idx)
  }

}

