package inloopio.math.timeseries

sealed trait OhlcType
object OhlcType {
  case object Open extends OhlcType
  case object High extends OhlcType
  case object Low extends OhlcType
  case object Close extends OhlcType
}
