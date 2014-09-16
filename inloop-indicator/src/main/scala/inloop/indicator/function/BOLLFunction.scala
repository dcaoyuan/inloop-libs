package inloop.indicator.function

import inloop.math.timeseries.Null
import inloop.math.timeseries.BaseTSer
import inloop.math.timeseries.TVar
import inloop.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
class BOLLFunction(_baseSer: BaseTSer) extends Function(_baseSer) {

  var period, alpha: Factor = _
  var baseVar: TVar[Double] = _

  val _bollMiddle = TVar[Double]()
  val _bollUpper = TVar[Double]()
  val _bollLower = TVar[Double]()

  override def set(args: Any*): Unit = {
    baseVar = args(0).asInstanceOf[TVar[Double]]
    period = args(1).asInstanceOf[Factor]
    alpha = args(2).asInstanceOf[Factor]
  }

  protected def computeSpot(i: Int): Unit = {
    if (i < period.value - 1) {

      _bollMiddle(i) = Null.Double
      _bollUpper(i) = Null.Double
      _bollLower(i) = Null.Double

    } else {

      val ma_i = ma(i, baseVar, period)
      val standard_deviation_i = stdDev(i, baseVar, period)

      _bollMiddle(i) = ma_i
      _bollUpper(i) = ma_i + alpha.value * standard_deviation_i
      _bollLower(i) = ma_i - alpha.value * standard_deviation_i

    }
  }

  def bollMiddle(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _bollMiddle(idx)
  }

  def bollUpper(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _bollUpper(idx)
  }

  def bollLower(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _bollLower(idx)
  }

}

