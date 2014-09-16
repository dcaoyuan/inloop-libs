package inloop.indicator.function

import inloop.math.timeseries.Null
import inloop.math.timeseries.BaseTSer
import inloop.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class STOCHKFunction(_baseSer: BaseTSer) extends Function(_baseSer) {

  var period, periodK: Factor = _

  val _elementK = TVar[Double]()

  val _stochK = TVar[Double]()

  override def set(args: Any*): Unit = {
    period = args(0).asInstanceOf[Factor]
    periodK = args(1).asInstanceOf[Factor]
  }

  protected def computeSpot(i: Int): Unit = {
    if (i < period.value - 1) {

      _elementK(i) = Null.Double

      _stochK(i) = Null.Double

    } else {

      val h_max_i = max(i, H, period)
      val l_min_i = min(i, L, period)

      _elementK(i) = (C(i) - l_min_i) / (h_max_i - l_min_i) * 100f

      /** smooth elementK, periodK */
      _stochK(i) = ma(i, _elementK, periodK)

    }
  }

  def stochK(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _stochK(idx)
  }

}

