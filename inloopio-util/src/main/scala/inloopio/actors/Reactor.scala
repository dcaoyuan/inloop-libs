package inloopio.actors

import akka.actor.Actor
import akka.actor.ActorContext
import akka.actor.OneForOneStrategy
import akka.actor.Props
import akka.actor.SupervisorStrategy
import akka.event.Logging
import akka.event.LoggingAdapter
import inloopio.actors.Reactor.StronglyReferenced
import scala.collection.mutable
import scala.concurrent.duration._
import scala.ref.Reference
import scala.ref.WeakReference

/**
 * A Reactor is not an actor, but has an underlying actor.
 * The Reactor should be enclosed in an actor, i.e. implement context and log
 * @author Caoyuan Deng
 */
trait Reactor extends Publishablity {

  /**
   * If the context is passed from ourside, it should be accessed in the context's
   * self actor thread.
   */
  def context: ActorContext

  protected lazy val log: LoggingAdapter = Logging(context.system, this.getClass)

  /**
   * All reactions of this reactor.
   */
  protected val reactions: Reactions = new Reactions()

  private val underlyingActor = context.actorOf(Reactor.UnderlyingActor.props(reactions))

  /**
   * send message via undeylyingActor.
   */
  def !(msg: Any) = {
    underlyingActor ! msg
  }

  /**
   * Listen to the given publisher as long as <code>deafTo</code> isn't called for them.
   */
  def listenTo(ps: Publishablity*) = for (p <- ps) p.subscribe(this)

  /**
   * Installed reactions won't receive events from the given publisher anylonger.
   */
  def deafTo(ps: Publishablity*) = for (p <- ps) p.unsubscribe(this)
}

object Reactor {
  /**
   * A Reactor implementing this trait is strongly referenced in the reactor list
   */
  trait StronglyReferenced

  object UnderlyingActor {
    def props(reactions: Reactions) = Props(new UnderlyingActor(reactions))
  }

  final class UnderlyingActor(reactions: Reactions) extends Actor {
    override val supervisorStrategy =
      OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.minute) {
        case ReactionsException(msg, t) => SupervisorStrategy.Resume
      }

    def receive = {
      reactions
    }
  }
}

/**
 * <p>
 *    Notifies registered reactions when an event is published. Publishers are
 *    also reactors and listen to themselves per default as a convenience.
 *  </p>
 *  <p>
 *    In order to reduce memory leaks, reactors are weakly referenced by default,
 *    unless they implement <code>Reactor.StronglyReferenced</code>. That way,
 *    the lifetime of reactors are more easily bound to the registering object,
 *    which are reactors in common client code and hold strong references to their
 *    reactors. As a result, reactors can be garbage collected even though they
 *    still have reactors registered at some publisher, but not vice versa
 *    since reactors (strongly) reference publishers they are interested in.
 *  </p>
 */
private[actors] trait Publishablity {

  final lazy val listeners = new RefSet[Reactor] {
    val underlying = new mutable.HashSet[Reference[Reactor]]
    protected def Ref(a: Reactor) = a match {
      case a: StronglyReferenced => new StrongReference[Reactor](a) with super.Ref[Reactor]
      case _                     => new WeakReference[Reactor](a, referenceQueue) with super.Ref[Reactor]
    }
  }

  private[actors] def subscribe(listener: Reactor) {
    listeners += listener
  }

  private[actors] def unsubscribe(listener: Reactor) {
    listeners -= listener
  }

  /**
   * Notify all registered reactors.
   */
  def publish(e: Any) {
    listeners foreach { _ ! e }
  }

}

