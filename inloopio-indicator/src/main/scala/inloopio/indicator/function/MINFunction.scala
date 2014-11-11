package inloopio.indicator.function

import inloopio.math.StatsFunctions
import inloopio.math.timeseries.Null
import inloopio.math.timeseries.BaseTSer
import inloopio.math.timeseries.TVar
import inloopio.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class MINFunction(_baseSer: BaseTSer, var baseVar: TVar[Double], var period: Factor) extends Function(_baseSer) {

  final protected def imin(idx: Int, baseVar: TVar[Double], period: Double, prev: Double): Double = {
    StatsFunctions.imin(idx, baseVar.values, period.toInt, prev)
  }

  val _min = TVar[Double]()

  override def set(args: Any*): Unit = {
    baseVar = args(0).asInstanceOf[TVar[Double]]
    period = args(1).asInstanceOf[Factor]
  }

  protected def computeSpot(i: Int): Unit = {
    if (i < period.value - 1) {

      _min(i) = Null.Double

    } else {

      _min(i) = imin(i, baseVar, period.value, _min(i - 1))

    }
  }

  def min(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _min(idx)
  }

}

