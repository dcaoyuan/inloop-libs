package inloop.indicator.function

import inloop.math.StatsFunctions
import inloop.math.timeseries.BaseTSer
import inloop.math.timeseries.TVar
import inloop.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class EMAFunction(_baseSer: BaseTSer, var baseVar: TVar[Double], var period: Factor) extends Function(_baseSer) {

  final protected def iema(idx: Int, var1: TVar[Double], period: Double, prev: Double): Double = {
    StatsFunctions.iema(idx, var1.values, period.toInt, prev)
  }

  val _ema = TVar[Double]()

  override def set(args: Any*): Unit = {
    baseVar = args(0).asInstanceOf[TVar[Double]]
    period = args(1).asInstanceOf[Factor]
  }

  protected def computeSpot(i: Int): Unit = {
    if (i == 0) {

      _ema(i) = baseVar(i)

    } else {

      _ema(i) = iema(i, baseVar, period.value, _ema(i - 1))

    }
  }

  def ema(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _ema(idx)
  }

}

