package inloop.indicator.function

import inloop.math.timeseries.Null
import inloop.math.timeseries.BaseTSer
import inloop.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class ADXRFunction extends Function {

  var periodDi, periodAdx: Factor = _

  val _adx = TVar[Double]()
  val _adxr = TVar[Double]()

  override def set(baseSer: BaseTSer, args: Any*): Unit = {
    super.set(baseSer)

    this.periodDi = args(0).asInstanceOf[Factor]
    this.periodAdx = args(1).asInstanceOf[Factor]
  }

  protected def computeSpot(i: Int) {
    _adx(i) = adx(i, periodDi, periodAdx)

    if (i < periodDi.value - 1 || i < periodAdx.value - 1) {

      _adxr(i) = Null.Double

    } else {

      val adx_i = _adx(i)
      val adx_j = _adx(i - periodAdx.value.toInt)

      _adxr(i) = (adx_i + adx_j) / 2f

    }
  }

  def adxr(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _adxr(idx)
  }

}

