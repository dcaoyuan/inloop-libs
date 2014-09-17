package inloop.indicator

import inloop.math.timeseries.BaseTSer
import inloop.math.timeseries.DefaultBaseTSer
import inloop.math.timeseries.TFreq
import inloop.math.timeseries.Thing

/**
 * @author Caoyuan Deng
 */
class FreeIndicator(val baseSer: BaseTSer, _thing: Thing, _freq: TFreq) extends DefaultBaseTSer(_thing, _freq)
    with inloop.math.indicator.Indicator {

  override def receive = super.receive orElse indicatorBehavior

  private var _identifier: Option[String] = None
  def identifier = _identifier
  def identifier_=(identifier: String) {
    _identifier = identifier match {
      case null | "" => None
      case _         => Some(identifier)
    }
  }

  /**
   * @param time to be computed from
   */
  def computeFrom(time: Long) {}
  def computedTime: Long = this.lastOccurredTime
}
