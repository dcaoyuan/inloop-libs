package inloop.indicator

/**
 *
 * @author Caoyuan Deng
 */
import inloop.math.timeseries.BaseTSer

class VOLIndicator(_baseSer: BaseTSer) extends Indicator(_baseSer) {
  sname = "VOL"
  lname = "Volume"

  val period1 = Factor("Period Short", 5)
  val period2 = Factor("Period Mediaum", 10)

  val vol = TVar[Double]("VOL", Plot.Volume)
  val ma1 = TVar[Double]("MA1", Plot.Line)
  val ma2 = TVar[Double]("MA2", Plot.Line)

  protected def compute(fromIdx: Int, size: Int) {
    var i = fromIdx
    while (i < size) {
      vol(i) = V(i)
      ma1(i) = ma(i, V, period1)
      ma2(i) = ma(i, V, period2)
      i += 1
    }
  }

}
