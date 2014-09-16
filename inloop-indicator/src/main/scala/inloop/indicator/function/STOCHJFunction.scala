package inloop.indicator.function

import inloop.math.timeseries.BaseTSer
import inloop.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class STOCHJFunction(_baseSer: BaseTSer) extends Function(_baseSer) {

  var period, periodK, periodD: Factor = _

  val _stochK = TVar[Double]()
  val _stochD = TVar[Double]()

  val _stochJ = TVar[Double]()

  override def set(args: Any*): Unit = {
    period = args(0).asInstanceOf[Factor]
    periodK = args(1).asInstanceOf[Factor]
    periodD = args(2).asInstanceOf[Factor]
  }

  protected def computeSpot(i: Int): Unit = {
    _stochK(i) = stochK(i, period, periodK)
    _stochD(i) = stochD(i, period, periodK, periodD)

    _stochJ(i) = _stochD(i) + 2 * (_stochD(i) - _stochK(i))
  }

  def stochJ(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _stochJ(idx)
  }

}

