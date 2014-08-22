package inloop.math.indicator

import inloop.collection.ArrayList
import inloop.math.PersistenceManager
import inloop.math.timeseries.BaseTSer
import inloop.math.timeseries.TFreq
import inloop.math.timeseries.descriptor.Descriptor

/**
 *
 * @author Caoyuan Deng
 */
class IndicatorDescriptor($serviceClassName: => String, $freq: => TFreq, $factors: => Array[Factor], $active: => Boolean) extends Descriptor[Indicator]($serviceClassName, $freq, $active) {

  def this() = this(null, TFreq.DAILY, Array[Factor](), false)

  val folderName = "Indicators"

  private var _factors = new ArrayList[Factor] ++= $factors

  private var _uniSymbol: Option[String] = None

  /**
   * You specify the indictor is from another symbols
   */
  def uniSymbol = _uniSymbol
  def uniSymbol_=(uniSymbol: String) {
    _uniSymbol = uniSymbol match {
      case null | "" => None
      case _         => Some(uniSymbol)
    }
  }

  override def set(serviceClassName: String, freq: TFreq): Unit = {
    super.set(serviceClassName, freq)

    setFacsToDefault
  }

  def factors: Array[Factor] = _factors.toArray
  def factors_=(factors: Array[Factor]) {
    /**
     * @NOTICE:
     * always create a new copy of in factors to seperate the factors of this
     * and that transfered in (we don't know who transfer it in, so, be more
     * carefule is always good)
     */
    val mySize = this._factors.length
    if (factors != null) {
      var i = -1
      while ({ i += 1; i < factors.length }) {
        val newFac = factors(i).clone
        if (i < mySize) {
          this._factors(i) = newFac
        } else {
          this._factors += newFac
        }
      }
    } else {
      this._factors.clear
    }
  }

  override def displayName: String = {
    val name = lookupServiceTemplate(classOf[Indicator], "Indicators") match {
      case Some(tpInstance) => tpInstance.shortName
      case None             => serviceClassName
    }

    Indicator.displayName(name, factors)
  }

  /**
   * @NOTICE
   * Here we get a new indicator instance by searching DefaultFileSystem(on NetBeans).
   * This is because that this instance may from other modules (i.e. SolarisIndicator),
   * it may not be seen from this module. Actually we should not set dependency on
   * those added-on modules.
   * @param baseSer for indicator
   */
  override protected def createServiceInstance(args: Any*): Option[Indicator] = args match {
    case Seq(baseSerx: BaseTSer) => lookupServiceTemplate(classOf[Indicator], "Indicators") match {
      case Some(indx) =>
        // is this indicator from another symbol ?
        val baseSer = (
          for (
            s <- uniSymbol if s != baseSerx.serProvider.uniSymbol;
            p <- baseSerx.serProvider.serProviderOf(s);
            b <- p.serOf(baseSerx.freq)
          ) yield b) getOrElse baseSerx

        val instance = if (factors.length == 0) {
          // this means this indicatorDescritor's factors may not be set yet, so set a default one now
          val instancex = Indicator(indx.getClass.asInstanceOf[Class[Indicator]], baseSer)
          factors = instancex.factors
          instancex
        } else {
          // should set facs here, because it's from one that is stored in xml
          Indicator(indx.getClass.asInstanceOf[Class[Indicator]], baseSer, factors: _*)
        }

        Option(instance)
      case None => None
    }
    case _ => None
  }

  def setFacsToDefault {
    val defaultFacs = PersistenceManager().defaultContent.lookupDescriptor(
      classOf[IndicatorDescriptor], serviceClassName, freq) match {
        case None => lookupServiceTemplate(classOf[Indicator], "Indicators") match {
          case None    => None
          case Some(x) => Some(x.factors)
        }
        case Some(defaultDescriptor) => Some(defaultDescriptor.factors)
      }

    defaultFacs foreach { x => factors = x }
  }

}

