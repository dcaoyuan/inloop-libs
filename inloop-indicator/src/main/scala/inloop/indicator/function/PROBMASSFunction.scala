package inloop.indicator.function

import inloop.math.StatsFunctions
import inloop.math.timeseries.BaseTSer
import inloop.math.timeseries.TVar
import inloop.math.indicator.Factor

/**
 *
 * @author Caoyuan Deng
 */
final class PROBMASSFunction(_baseSer: BaseTSer) extends Function(_baseSer) {

  protected def probMass(idx: Int, baseVar: TVar[Double], period: Double, nInterval: Double): Array[Array[Double]] = {
    val begIdx = idx - period.intValue + 1
    val endIdx = idx

    StatsFunctions.probMass(baseVar.values, begIdx, endIdx, nInterval.intValue)
  }

  protected def probMass(idx: Int, baseVar: TVar[Double], weight: TVar[Double], period: Double, nInterval: Double): Array[Array[Double]] = {
    val begIdx = idx - period.intValue + 1
    val endIdx = idx

    StatsFunctions.probMass(baseVar.values, weight.values, begIdx, endIdx, nInterval.intValue)
  }

  var period: Factor = _
  var nInterval: Factor = _

  var baseVar: TVar[Double] = _
  var weight: TVar[Double] = _

  /**
   * as this function do not remember previous valus, do not need a Var as probMass
   */
  var _probMass: Array[Array[Double]] = _

  override def set(args: Any*): Unit = {
    args match {
      case Seq(a0: TVar[Double], a1: TVar[Double], a2: Factor, a3: Factor) =>
        baseVar = a0
        weight.equals(a1)
        period.equals(a2)
        nInterval.equals(a3)
    }
  }

  protected def computeSpot(i: Int): Unit = {
    if (weight == null) {

      _probMass = probMass(i, baseVar, period.value, nInterval.value);

    } else {

      _probMass = probMass(i, baseVar, weight, period.value, nInterval.value);

    }
  }

  /**
   * override compute(int), this function is not dependent on previous values
   */
  def compute(idx: Int): Unit = {
    computeSpot(idx)
  }

  def probMass(sessionId: Long, idx: Int): Array[Array[Double]] = {
    compute(idx)

    _probMass
  }

}

