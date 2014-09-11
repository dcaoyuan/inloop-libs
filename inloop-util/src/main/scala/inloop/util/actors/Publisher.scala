package inloop.util.actors

import akka.actor.Actor
import akka.actor.ActorRef
import scala.collection.mutable
import scala.ref.Reference
import scala.ref.WeakReference

/**
 * <p>
 *    Notifies registered reactions when an event is published. Publishers are
 *    also reactors and listen to themselves per default as a convenience.
 *  </p>
 *  <p>
 *    In order to reduce memory leaks, reactions are weakly referenced by default,
 *    unless they implement <code>Reactions.StronglyReferenced</code>. That way,
 *    the lifetime of reactions are more easily bound to the registering object,
 *    which are reactors in common client code and hold strong references to their
 *    reactions. As a result, reactors can be garbage collected even though they
 *    still have reactions registered at some publisher, but not vice versa
 *    since reactors (strongly) reference publishers they are interested in.
 *  </p>
 */
sealed trait ListenerMessage
case class Listen(listener: ActorRef) extends ListenerMessage
case class Deafen(listener: ActorRef) extends ListenerMessage
case class WithListeners(f: (ActorRef) => Unit) extends ListenerMessage
trait StronglyReferenced

trait Publisher { _: Actor =>

  final lazy val listeners = new RefSet[ActorRef] {
    lazy val underlying = new mutable.HashSet[Reference[ActorRef]]
    protected def Ref(a: ActorRef) = a match {
      case a: StronglyReferenced => new StrongReference[ActorRef](a) with super.Ref[ActorRef]
      case _                     => new WeakReference[ActorRef](a, referenceQueue) with super.Ref[ActorRef]
    }
  }

  /**
   * Chain this into the receive function.
   *
   * {{{ def receive = listenerManagement orElse … }}}
   */
  protected def listenerManagement: Actor.Receive = {
    case Listen(l) => listeners += l
    case Deafen(l) => listeners -= l
    case WithListeners(f) =>
      f(self)
      val i = listeners.iterator
      while (i.hasNext) f(i.next)
  }

  /**
   * Notify all registered reactions.
   */
  def publish(msg: Any) {
    self ! msg
    val i = listeners.iterator
    while (i.hasNext) i.next ! msg
  }
}
