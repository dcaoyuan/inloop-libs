package inloopio.math.timeseries

import inloopio.actors.Reactor
import java.util.Calendar
import java.util.TimeZone
import scala.collection.mutable.WeakHashMap

/**
 * @Note to get this Combiner react to srcSer, it should be held as strong ref by some instances
 *
 * @author Caoyuan Deng
 */
class TSerCombiner(srcSer: TBaseSer, tarSer: TBaseSer, timeZone: TimeZone) extends Reactor {

  TSerCombiner.strongRefHolders.put(tarSer, this)

  def context = tarSer.context

  reactions += {
    case TSerEvent.Loaded(_, _, fromTime, _, _, _)   => compute(fromTime)
    case TSerEvent.Computed(_, _, fromTime, _, _, _) => compute(fromTime)
    case TSerEvent.Updated(_, _, fromTime, _, _, _)  => compute(fromTime)
    case TSerEvent.Cleared(_, _, fromTime, _, _, _)  => compute(fromTime)
  }
  listenTo(srcSer)

  private val cal = Calendar.getInstance(timeZone)
  private var tval: TVal = _

  def compute(fromTime: Long) {
    val cal = Calendar.getInstance(timeZone)
    cal.setTimeInMillis(fromTime)
    val roundedFromTime = tarSer.freq.round(fromTime, cal)
    cal.setTimeInMillis(roundedFromTime)
    val srcFromIdx = math.max(0, srcSer.timestamps.indexOrNextIndexOfOccurredTime(roundedFromTime))

    // --- begin combining

    val n = srcSer.size
    var i = srcFromIdx
    while (i < n) {
      val time_i = srcSer.timeOfIndex(i)
      if (time_i >= roundedFromTime) {
        val tval = tvalOf(time_i)

        val srcVars = srcSer.vars.iterator
        val tarVars = tarSer.vars.iterator
        /**
         * @TIPS
         * when combine, do adjust on source's value, then de adjust on combined quote data.
         * this will prevent bad high, open, and low into combined quote data:
         *
         * During the combining period, an adjust may happened, but we only record last
         * close_adj, the high, low, and open of the data before adjusted acutally may has
         * different scale close_adj, so must do adjust with its own close_adj firstly. then
         * use the last close_orj to de-adjust it.
         */
        if (tval.justOpen_?) {
          tval.unjustOpen_!

          while (srcVars.hasNext && tarVars.hasNext) {
            val svar = srcVars.next
            val tvar = tarVars.next
            svar.kind match {
              case TVarKind.Accumlate => tvar.updateByCasting(time_i, tvar.double(time_i) + svar.double(time_i))
              case _                  => tvar.updateByCasting(time_i, svar(time_i))
            }
          }

        } else {

          while (srcVars.hasNext && tarVars.hasNext) {
            val svar = srcVars.next
            val tvar = tarVars.next
            svar.kind match {
              case TVarKind.Accumlate => tvar.updateByCasting(time_i, tvar.double(time_i) + svar.double(time_i))
              case TVarKind.Open      => // do nothing
              case TVarKind.High      => tvar.updateByCasting(time_i, math.max(tvar.double(time_i), svar.double(time_i)))
              case TVarKind.Low       => tvar.updateByCasting(time_i, math.min(tvar.double(time_i), svar.double(time_i)))
              case TVarKind.Close     => tvar.updateByCasting(time_i, svar.double(time_i))
            }
          }

        }
      }
      i += 1
    }
  }

  def tvalOf(time: Long): TVal = {
    val rounded = tarSer.freq.round(time, cal)
    tval match {
      case one: TVal if one.time == rounded =>
        one
      case prevOneOrNull => // interval changes or null
        val newone = new TVal
        newone.time = rounded
        newone.unclosed_!
        newone.justOpen_!
        newone.fromMe_!

        tval = newone
        newone
    }
  }

  /**
   * This function keeps the adjusting linear according to a norm
   */
  private def linearAdjust(value: Double, prevNorm: Double, postNorm: Double): Double = {
    ((value - prevNorm) / prevNorm) * postNorm + postNorm
  }

  def dispose {}
}

object TSerCombiner {
  // Holding strong reference to ser combiner etc, see @TSerCombiner
  private val strongRefHolders = WeakHashMap[TSer, TSerCombiner]()
}

