package inloop.math.indicator

import javax.swing.Action
import inloop.util.ServiceLoader

/**
 *
 * @author  Caoyuan Deng
 * @version 1.0, December 11, 2006, 10:20 PM
 * @since   1.0.4
 */
object IndicatorDescriptorActionFactory {
  private lazy val instance = ServiceLoader.load(classOf[IndicatorDescriptorActionFactory]).iterator.next

  def apply(): IndicatorDescriptorActionFactory = {
    instance
  }
}

trait IndicatorDescriptorActionFactory {
  def createActions(descriptor: IndicatorDescriptor): Array[Action]
}

