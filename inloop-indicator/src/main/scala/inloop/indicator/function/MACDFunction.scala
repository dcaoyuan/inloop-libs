package inloop.indicator.function

import inloop.math.timeseries.BaseTSer
import inloop.math.timeseries.TVar
import inloop.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class MACDFunction(_baseSer: BaseTSer) extends Function(_baseSer) {

  var periodSlow, periodFast: Factor = _
  var baseVar: TVar[Double] = _

  val _emaFast = TVar[Double]()
  val _emaSlow = TVar[Double]()

  val _macd = TVar[Double]()

  override def set(args: Any*): Unit = {
    baseVar = args(0).asInstanceOf[TVar[Double]]
    periodSlow = args(1).asInstanceOf[Factor]
    periodFast = args(2).asInstanceOf[Factor]
  }

  protected def computeSpot(i: Int): Unit = {
    _emaFast(i) = ema(i, baseVar, periodFast)
    _emaSlow(i) = ema(i, baseVar, periodSlow)

    _macd(i) = _emaFast(i) - _emaSlow(i)
  }

  def macd(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _macd(idx)
  }

}

