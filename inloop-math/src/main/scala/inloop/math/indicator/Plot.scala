package inloop.math.indicator

/**
 * enum for defining chart style that could be used for Plottable
 *
 * @author Caoyuan Deng
 */
abstract class Plot
object Plot {
  case object None extends Plot
  case object Line extends Plot
  case object Stick extends Plot
  case object Dot extends Plot
  case object Shade extends Plot
  case object Profile extends Plot
  case object Quote extends Plot
  case object Volume extends Plot
  case object Signal extends Plot
  case object Zigzag extends Plot
  case object Info extends Plot
}

