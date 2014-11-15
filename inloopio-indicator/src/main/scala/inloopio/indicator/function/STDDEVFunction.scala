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
class STDDEVFunction(_baseSer: TBaseSer, var baseVar: TVar[Double], var period: Factor) extends Function(_baseSer) {

  final protected def stdDev(idx: Int, baseVar: TVar[Double], period: Double): Double = {
    val begIdx = idx - period.toInt + 1
    val endIdx = idx

    StatsFunctions.stdDev(baseVar.values, begIdx, endIdx)
  }

  val _stdDev = TVar[Double]()

  override def set(args: Any*): Unit = {
    baseVar = args(0).asInstanceOf[TVar[Double]]
    period = args(1).asInstanceOf[Factor]
  }

  protected def computeSpot(i: Int): Unit = {
    if (i < period.value - 1) {

      _stdDev(i) = Null.Double

    } else {

      _stdDev(i) = stdDev(i, baseVar, period.value)

    }
  }

  def stdDev(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _stdDev(idx)
  }

}

