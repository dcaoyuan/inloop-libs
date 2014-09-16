package inloop.indicator.function

import inloop.math.timeseries.BaseTSer
import inloop.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class WMSFunction(_baseSer: BaseTSer) extends Function(_baseSer) {

  var period: Factor = _

  val _wms = TVar[Double]()

  override def set(args: Any*): Unit = {
    period = args(0).asInstanceOf[Factor]
  }

  protected def computeSpot(i: Int): Unit = {
    val h_max_i = max(i, H, period)
    val l_min_i = min(i, L, period)

    _wms(i) = 100 - (C(i) - l_min_i) / (h_max_i - l_min_i) * 100f
  }

  def wms(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _wms(idx)
  }

}

