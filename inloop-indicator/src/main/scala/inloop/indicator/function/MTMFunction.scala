package inloop.indicator.function

import inloop.math.timeseries.Null
import inloop.math.timeseries.BaseTSer
import inloop.math.timeseries.TVar
import inloop.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class MTMFunction(_baseSer: BaseTSer, var baseVar: TVar[Double], var period: Factor) extends Function(_baseSer) {

  val _mtm = TVar[Double]()

  override def set(args: Any*): Unit = {
    baseVar = args(0).asInstanceOf[TVar[Double]]
    period = args(1).asInstanceOf[Factor]
  }

  protected def computeSpot(i: Int): Unit = {
    if (i < period.value - 1) {

      _mtm(i) = Null.Double

    } else {

      _mtm(i) = (baseVar(i) / baseVar(i - period.value.toInt)) * 100f

    }
  }

  def mtm(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _mtm(idx)
  }

}

