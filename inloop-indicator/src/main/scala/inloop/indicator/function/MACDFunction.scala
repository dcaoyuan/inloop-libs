package inloop.indicator.function

import inloop.math.timeseries.BaseTSer
import inloop.math.timeseries.TVar
import inloop.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class MACDFunction extends Function {

  var periodSlow, periodFast: Factor = _
  var baseVar: TVar[Double] = _

  val _emaFast = TVar[Double]()
  val _emaSlow = TVar[Double]()

  val _macd = TVar[Double]()

  override def set(baseSer: BaseTSer, args: Any*): Unit = {
    super.set(baseSer)

    this.baseVar = args(0).asInstanceOf[TVar[Double]]
    this.periodSlow = args(1).asInstanceOf[Factor]
    this.periodFast = args(2).asInstanceOf[Factor]
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

