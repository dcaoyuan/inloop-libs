package inloopio.indicator.function

import inloopio.math.timeseries.Null
import inloopio.math.timeseries.TBaseSer

/**
 *
 * @author Caoyuan Deng
 */
class TRFunction(_baseSer: TBaseSer) extends Function(_baseSer) {

  val _tr = TVar[Double]()

  override def set(args: Any*): Unit = {
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

