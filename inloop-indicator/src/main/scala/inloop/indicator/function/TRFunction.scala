package inloop.indicator.function

import inloop.math.timeseries.Null
import inloop.math.timeseries.BaseTSer

/**
 *
 * @author Caoyuan Deng
 */
class TRFunction extends Function {

  val _tr = TVar[Double]()

  override def set(baseSer: BaseTSer, args: Any*): Unit = {
    super.set(baseSer)
  }

  protected def computeSpot(i: Int): Unit = {
    if (i == 0) {

      _tr(i) = Null.Double

    } else {

      val tr_tmp = math.max(H(i) - L(i), math.abs(H(i) - C(i - 1)))
      _tr(i) = math.max(tr_tmp, math.abs(L(i) - C(i - 1)))

    }
  }

  def tr(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _tr(idx)
  }

}

