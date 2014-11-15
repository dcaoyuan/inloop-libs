package inloopio.indicator.function

import inloopio.math.StatsFunctions
import inloopio.math.timeseries.Null
import inloopio.math.timeseries.TBaseSer
import inloopio.math.timeseries.TVar
import inloopio.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class MAXFunction(_baseSer: TBaseSer, var baseVar: TVar[Double], var period: Factor) extends Function(_baseSer) {

  final protected def imax(idx: Int, baseVar: TVar[Double], period: Double, prev: Double): Double = {
    StatsFunctions.imax(idx, baseVar.values, period.toInt, prev)
  }

  val _max = TVar[Double]()

  override def set(args: Any*): Unit = {
    baseVar = args(0).asInstanceOf[TVar[Double]]
    period = args(1).asInstanceOf[Factor]
  }

  protected def computeSpot(i: Int): Unit = {
    if (i < period.value - 1) {

      _max(i) = Null.Double

    } else {

      _max(i) = imax(i, baseVar, period.value, _max(i - 1))

    }
  }

  def max(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _max(idx)
  }

}

