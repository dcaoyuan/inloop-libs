package inloop.indicator.function

import inloop.math.timeseries.Null
import inloop.math.timeseries.BaseTSer
import inloop.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class ADXFunction extends Function {

  var periodDi, periodAdx: Factor = _

  val _dx = TVar[Double]()

  val _adx = TVar[Double]()

  override def set(baseSer: BaseTSer, args: Any*): Unit = {
    super.set(baseSer, Nil)
    args match {
      case Seq(a0: Factor, a1: Factor) =>
        this.periodDi = a0
        this.periodAdx = a1
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

