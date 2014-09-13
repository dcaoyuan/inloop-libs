package inloop.indicator.function

import inloop.math.StatsFunctions
import inloop.math.timeseries.Null
import inloop.math.timeseries.BaseTSer
import inloop.math.timeseries.TVar
import inloop.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class MAFunction extends Function {
  final protected def ima(idx: Int, baseVar: TVar[Double], period: Double, prev: Double): Double = {
    return StatsFunctions.ima(idx, baseVar.values, period.toInt, prev)
  }

  var period: Factor = _
  var baseVar: TVar[Double] = _

  val _ma = TVar[Double]()

  override def set(baseSer: BaseTSer, args: Any*): Unit = {
    super.set(baseSer)
    args match {
      case Seq(a0: TVar[Double], a1: Factor) =>
        this.baseVar = a0
        this.period = a1
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

