package inloop.math.timeseries

import inloop.math.indicator.Plot
import scala.collection.mutable

/**
 *
 * @author Caoyuan Deng
 */
class ThingSer(_thing: Thing, _freq: TFreq) extends DefaultBaseTSer(_thing, _freq) {

  private var _shortName: String = thing.identifier
  private var _isAdjusted: Boolean = false

  val open = TVar[Double]("O", Plot.OHLC)
  val high = TVar[Double]("H", Plot.OHLC)
  val low = TVar[Double]("L", Plot.OHLC)
  val close = TVar[Double]("C", Plot.OHLC)
  val volume = TVar[Double]("V", Plot.Volume)
  val amount = TVar[Double]("A", Plot.Volume)
  val isClosed = TVar[Boolean]("E")

  override val exportableVars = List(open, high, low, close, volume, amount)
}
