package inloop.math.timeseries.datasource

import inloop.math.timeseries.TFreq
import inloop.math.timeseries.BaseTSer
import inloop.math.timeseries.descriptor.Content
import inloop.util.actors.Publisher

/**
 *
 * @author Caoyuan Deng
 */
trait SerProvider extends Publisher {
  type T <: BaseTSer
  type C <: DataContract[_]

  /**
   * Load sers, can be called to load ser whenever
   * If there is already a dataServer is running and not finished, don't load again.
   * @return boolean: if run sucessfully, ie. load begins, return true, else return false.
   */
  def loadSer(ser: T): Boolean
  def putSer(ser: T)
  def resetSers

  def uniSymbol: String
  def uniSymbol_=(symbol: String)

  def name: String

  def stopAllDataServer

  def serOf(freq: TFreq): Option[T]

  def description: String
  def description_=(description: String)

  /**
   * The content of each symbol should be got automatailly from PersistenceManager.restoreContent
   * and keep it there without being refered to another one, so, we only give getter without setter.
   */
  def content: Content

  /**
   * A helper method which can be overridden to get another ser provider from symbol
   */
  def serProviderOf(uniSymbol: String): Option[SerProvider]
}

