package inloopio.actors

import akka.actor.Actor.Receive
import scala.collection.mutable.Buffer
import scala.collection.mutable.ListBuffer

/**
 * Used by Reactor to let clients register custom event reactions.
 */
final class Reactions extends Receive {
  private val parts: Buffer[Receive] = new ListBuffer[Receive]

  def isDefinedAt(e: Any) = parts.exists(_ isDefinedAt e)

  def apply(e: Any) {
    for (p <- parts if p isDefinedAt e) {
      try {
        p(e)
      } catch {
        case ex: Throwable => throw ReactionsException(e, ex)
      }
    }
  }

  /**
   * Add a receive.
   */
  def +=(r: Receive): this.type = { parts += r; this }

  /**
   * Remove the given receive.
   */
  def -=(r: Receive): this.type = { parts -= r; this }

}

case class ReactionsException(actorMessage: Any, originalException: Throwable) extends RuntimeException("Exception due to message")
