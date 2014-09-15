package inloop.indicator.function

import inloop.math.timeseries.Null
import inloop.math.timeseries.BaseTSer
import inloop.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class DXFunction extends Function {

  var period: Factor = _

  val _diPlus = TVar[Double]()
  val _diMinus = TVar[Double]()

  val _dx = TVar[Double]()

  override def set(baseSer: BaseTSer, args: Any*): Unit = {
    super.set(baseSer)

    this.period = args(0).asInstanceOf[Factor]
  }

  protected def computeSpot(i: Int): Unit = {
    if (i < period.value - 1) {

      _diPlus(i) = diPlus(i, period)
      _diMinus(i) = diMinus(i, period)

      _dx(i) = Null.Double

    } else {

      _diPlus(i) = diPlus(i, period)
      _diMinus(i) = diMinus(i, period)

      val diPlus_i = _diPlus(i)
      val diMinus_i = _diMinus(i)

      val dx_i = if (diPlus_i + diMinus_i == 0) 0f else math.abs(diPlus_i - diMinus_i) / (diPlus_i + diMinus_i) * 100f

      _dx(i) = dx_i
    }
  }

  def dx(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _dx(idx)
  }

}

