package inloopio.indicator.function

import inloopio.math.timeseries.Null
import inloopio.math.timeseries.TBaseSer
import inloopio.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class ADXFunction(_baseSer: TBaseSer, var periodDi: Factor, var periodAdx: Factor) extends Function(_baseSer) {

  val _dx = TVar[Double]()

  val _adx = TVar[Double]()

  override def set(args: Any*): Unit = {
    args match {
      case Seq(a0: Factor, a1: Factor) =>
        periodDi = a0
        periodAdx = a1
    }
  }

  protected def computeSpot(i: Int): Unit = {
    _dx(i) = dx(i, periodDi)

    if (i < periodDi.value - 1 || i < periodAdx.value - 1) {

      _adx(i) = Null.Double

    } else {

      _adx(i) = ma(i, _dx, periodAdx)

    }
  }

  def adx(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _adx(idx)
  }

}

