package inloopio.math.indicator

import inloopio.math.timeseries.TVar
import inloopio.math.signal.Signal

/**
 * @author Caoyuan Deng
 */
trait SignalIndicator extends Indicator {
  def signalVar: TVar[List[Signal]]
}
