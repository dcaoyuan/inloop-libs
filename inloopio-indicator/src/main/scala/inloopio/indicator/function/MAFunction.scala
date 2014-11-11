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
class MAFunction(_baseSer: BaseTSer, var baseVar: TVar[Double], var period: Factor) extends Function(_baseSer) {

  final protected def ima(idx: Int, baseVar: TVar[Double], period: Double, prev: Double): Double = {
    return StatsFunctions.ima(idx, baseVar.values, period.toInt, prev)
  }

  val _ma = TVar[Double]()

  override def set(args: Any*): Unit = {
    args match {
      case Seq(a0: TVar[_], a1: Factor) =>
        baseVar = a0.asInstanceOf[TVar[Double]]
        period = a1
    }
  }

  protected def computeSpot(i: Int): Unit = {
    if (i < period.value - 1) {

      _ma(i) = Null.Double

    } else {

      _ma(i) = ima(i, baseVar, period.value, _ma(i - 1))

    }
  }

  def ma(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _ma(idx)
  }

}

