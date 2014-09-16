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
class SUMFunction extends Function {
  final protected def isum(idx: Int, baseVar: TVar[Double], period: Double, prev: Double): Double = {
    StatsFunctions.isum(idx, baseVar.values, period.toInt, prev)
  }

  var period: Factor = _
  var baseVar: TVar[Double] = _

  val _sum = TVar[Double]()

  override def set(baseSer: BaseTSer, args: Any*): Unit = {
    super.set(baseSer)

    this.baseVar = args(0).asInstanceOf[TVar[Double]]
    this.period = args(1).asInstanceOf[Factor]
  }

  protected def computeSpot(i: Int): Unit = {
    if (i < period.value - 1) {

      _sum(i) = Null.Double

    } else {

      _sum(i) = isum(i, baseVar, period.value, _sum(i - 1))

    }
  }

  def sum(sessionId: Long, idx: Int): Double = {
    computeTo(sessionId, idx)

    _sum(idx)
  }

}
