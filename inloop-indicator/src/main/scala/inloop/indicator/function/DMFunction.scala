package inloop.indicator.function

import inloop.math.timeseries.Null
import inloop.math.timeseries.BaseTSer

/**
 *
 * @author Caoyuan Deng
 */
class DMFunction extends Function {

  val _dmPlus = TVar[Double]()
  val _dmMinus = TVar[Double]()

  override def set(baseSer: BaseTSer, args: Any*): Unit = {
    super.set(baseSer)
  }

  protected def computeSpot(i: Int): Unit = {
    if (i == 0) {

      _dmPlus(i) = Null.Double
      _dmMinus(i) = Null.Double

    } else {

      if (H(i) > H(i - 1) && L(i) > L(i - 1)) {
        _dmPlus(i) = H(i) - H(i - 1)
        _dmMinus(i) = 0f
      } else if (H(i) < H(i - 1) && L(i) < L(i - 1)) {
        _dmPlus(i) = 0f
        _dmMinus(i) = L(i - 1) - L(i)
      } else if (H(i) > H(i - 1) && L(i) < L(i - 1)) {
        if (H(i) - H(i - 1) > L(i - 1) - L(i)) {
          _dmPlus(i) = H(i) - H(i - 1)
          _dmMinus(i) = 0f
        } else {
          _dmPlus(i) = 0f
          _dmMinus(i) = L(i - 1) - L(i)
        }
      } else if (H(i) < H(i - 1) && L(i) > L(i - 1)) {
        _dmPlus(i) = 0f
        _dmMinus(i) = 0f
      } else if (H(i) == H(i - 1) && L(i) == L(i - 1)) {
        _dmPlus(i) = 0f
        _dmMinus(i) = 0f
      } else if (L(i) > H(i - 1)) {
        _dmPlus(i) = H(i) - H(i)
        _dmMinus(i) = 0f
      } else if (H(i) < L(i - 1)) {
        _dmPlus(i) = 0f
        _dmMinus(i) = L(i - 1) - L(i)
      } else {
        _dmPlus(i) = 0f
        _dmMinus(i) = 0f
      }

    }
  }

  def dmPlus(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _dmPlus(idx)
  }

  def dmMinus(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _dmMinus(idx)
  }
}

