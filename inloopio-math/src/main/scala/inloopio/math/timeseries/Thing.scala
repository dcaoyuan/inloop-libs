package inloopio.math.timeseries

import akka.actor.Actor
import inloopio.math.timeseries.datasource.DataContract
import inloopio.math.timeseries.descriptor.Content
import inloopio.util.actors.Publisher

/**
 *
 * @author Caoyuan Deng
 */
trait Thing extends Actor with Publisher {
  type T <: BaseTSer
  type C <: DataContract[_]

  def identifier: String
  def identifier_=(identifier: String)

  def name: String

  def description: String
  def description_=(description: String)

  def serOf(freq: TFreq): Option[T]

  /**
   * Load sers, can be called to load ser whenever
   * If there is already a dataServer is running and not finished, don't load again.
   * @return boolean: if run sucessfully, ie. load begins, return true, else return false.
   */
  def loadSer(ser: T): Boolean
  def putSer(ser: T)
  def resetSers

  def stopAllDataServer

  /**
   * The content of each symbol should be got automatailly from PersistenceManager.restoreContent
   * and keep it there without being refered to another one, so, we only give getter without setter.
   */
  def content: Content

  /**
   * A helper method which can be overridden to get another ser provider from identifier
   */
  def thingOf(identifier: String): Option[Thing]
}

