package inloopio.math.indicator

/**
 *
 * @author Caoyuan Deng
 */
trait SpotIndicator extends Indicator {
  def computeSpot(time: Long)
}
