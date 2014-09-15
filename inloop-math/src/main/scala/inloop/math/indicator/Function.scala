package inloop.math.indicator

import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level
import java.util.logging.Logger
import inloop.math.timeseries.BaseTSer
import inloop.math.timeseries.TSer

object Function {
  private val log = Logger.getLogger(this.getClass.getName)

  private val idToFunction = new ConcurrentHashMap[Id[_ <: Function], Function](8, 0.9f, 1)

  def idOf[T <: Function](klass: Class[T], baseSer: BaseTSer, args: Any*) = Id[T](klass, baseSer, args: _*)

  def apply[T <: Function](klass: Class[T], baseSer: BaseTSer, args: Any*): T = {
    // TODO make function actors as children of baseSer actor?
    val id = idOf(klass, baseSer, args: _*)
    idToFunction.get(id) match {
      case null =>
        /** if got none from idToFunction, try to create new one */
        try {
          val function = klass.newInstance
          /** don't forget to call set(baseSer, args) immediatley */
          function.set(baseSer, args: _*)
          idToFunction.putIfAbsent(id, function)
          function
        } catch {
          case ex: Throwable => log.log(Level.SEVERE, ex.getMessage, ex); null.asInstanceOf[T]
        }
      case x => x.asInstanceOf[T]
    }
  }

  def releaseAll() {
    idToFunction.clear
  }
}

trait Function extends TSer {

  /**
   * set the function's arguments.
   * @param baseSer, the ser that this function is based, ie. used to compute
   */
  def set(baseSer: BaseTSer, args: Any*)

  /**
   * This method will compute from computedIdx <b>to</b> idx.
   *
   * and AbstractIndicator.compute(final long begTime) will compute <b>from</b>
   * begTime to last data
   *
   * @param sessionId, the sessionId usally is controlled by outside caller,
   *        such as an indicator
   * @param idx, the idx to be computed to
   */
  def computeTo(sessionId: Long, idx: Int)
}
