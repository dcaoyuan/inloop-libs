package inloop.math.indicator

import inloop.math.timeseries.TVar
import inloop.math.signal.Signal

/**
 * @author Caoyuan Deng
 */
trait SignalIndicator extends Indicator {
  def signalVar: TVar[List[Signal]]
}
