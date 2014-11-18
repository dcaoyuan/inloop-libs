package inloopio.math.timeseries

/**
 * a value object with time field
 *
 * @note You can define method body in trait, but not fields, since those fields
 * may look like lots of $ connected string, and may break lots of libraries
 * which use refelction.
 * http://stackoverflow.com/questions/5203798/scala-mixing-traits-with-private-fields
 *
 * @author Caoyuan Deng
 */
class TVal extends Flag with Ordered[TVal] {

  private var _time: Long = _
  def time = _time
  def time_=(time: Long) {
    this._time = time
  }

  private var _lastModify: Long = _
  def lastModify = _lastModify
  def lastModify_=(time: Long) {
    this._lastModify = time
  }

  private var _flag: Int = 1 // dafault is closed
  def flag = _flag
  def flag_=(flag: Int) {
    this._flag = flag
  }
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
