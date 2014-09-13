package inloop.indicator.function

import inloop.math.timeseries.BaseTSer

/**
 *
 * @author Caoyuan Deng
 */
class OBVFunction extends Function {

  val _obv = TVar[Double]()

  override def set(baseSer: BaseTSer, args: Any*): Unit = {
    super.set(baseSer)
  }

  protected def computeSpot(i: Int): Unit = {
    if (i == 0) {

      _obv(i) = 0f

    } else {

      if (C(i) > C(i - 1)) {
        _obv(i) = _obv(i - 1) + V(i)
      } else if (C(i) < C(i - 1)) {
        _obv(i) = _obv(i - 1) - V(i)
      } else {
        /** C(i) == C(i - 1) */
        _obv(i) = _obv(i - 1)
      }

    }
  }

  def obv(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _obv(idx)
  }

}

