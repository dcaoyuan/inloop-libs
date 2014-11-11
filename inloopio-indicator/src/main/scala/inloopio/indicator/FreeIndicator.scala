package inloopio.indicator

import inloopio.math.timeseries.BaseTSer
import inloopio.math.timeseries.DefaultBaseTSer
import inloopio.math.timeseries.TFreq
import inloopio.math.timeseries.Thing

/**
 * @author Caoyuan Deng
 */
class FreeIndicator(val baseSer: BaseTSer, _thing: Thing, _freq: TFreq) extends DefaultBaseTSer(_thing, _freq)
    with inloopio.math.indicator.Indicator {

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
