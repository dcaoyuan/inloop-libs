package inloop.indicator

import inloop.math.timeseries.BaseTSer
import inloop.math.timeseries.DefaultBaseTSer
import inloop.math.timeseries.Thing
import inloop.math.timeseries.TFreq

/**
 * @author Caoyuan Deng
 */
class FreeIndicator(_thing: Thing, _freq: TFreq) extends DefaultBaseTSer(_thing, _freq)
    with inloop.math.indicator.Indicator {

  private var _baseSer: BaseTSer = _

  private var _identifier: Option[String] = None

  override def receive = listenerManagement orElse indicatorBehavior

  def identifier = _identifier
  def identifier_=(identifier: String) {
    _identifier = identifier match {
      case null | "" => None
      case _         => Some(identifier)
    }
  }

  def set(baseSer: BaseTSer) {
    _baseSer = baseSer
    if (baseSer != null) {
      super.set(baseSer.freq)
    }
  }

  def baseSer: BaseTSer = _baseSer
  def baseSer_=(baseSer: BaseTSer) {
    set(baseSer)
  }

  /**
   * @param time to be computed from
   */
  def computeFrom(time: Long) {}
  def computedTime: Long = this.lastOccurredTime

  def dispose {}

}
