package inloop.math.timeseries

/**
 * a value object with time field
 *
 * @note You can define method body in trait, but not fields, since those field
 * may look like lots of $ connected string, and may break lots of libraries
 * which use refelction.
 *
 * @author Caoyuan Deng
 */
trait TVal extends Ordered[TVal] {
  def time: Long
  def time_=(time: Long)

  def compare(that: TVal): Int = {
    if (time > that.time) {
      1
    } else if (time < that.time) {
      -1
    } else {
      0
    }
  }
}

