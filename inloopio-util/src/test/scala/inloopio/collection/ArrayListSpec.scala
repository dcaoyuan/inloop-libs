package inloopio.collection

import org.scalatest.BeforeAndAfterAll
import org.scalatest.Matchers
import org.scalatest.WordSpecLike
import scala.reflect.ClassTag

/**
 *
 * @author Caoyuan Deng
 */
class ArrayListSpec extends WordSpecLike with Matchers with BeforeAndAfterAll {

  "ArrayList" when {

    "issue SI-7268" should {
      "insert elements like" in {
        val si7268Tester = new SI7268Tester[Double]

        si7268Tester.insertAll(1.0)
        si7268Tester.values(0) should be(1.0)

        si7268Tester.insertOne(0.0)
        si7268Tester.values(0) should be(0.0)
        si7268Tester.values.size should be(2)

        //test.insertOk(1.0, 2.0)
        //test.insertOk(1.0)
        //test.insertFailed(1.0)

      }
    }

  }

  /**
   * https://issues.scala-lang.org/browse/SI-7268
   */
  class SI7268Tester[V: ClassTag] {
    val values = new ArrayList[V]()

    def insertOne(v: V) {
      values.insertOne(0, v)
      info(values.mkString(","))
    }

    def insertAll(v: V) {
      values.insertAll(0, Array(v))
      info(values.mkString(","))
    }

    def insertOk(v: V) {
      val xs = Array(v)
      values.insert(0, xs: _*)
      info(values.mkString(","))
    }

    def insertOk(v1: V, v2: V) {
      val xs = Array(v1, v2)
      values.insert(0, xs: _*)
      info(values.mkString(","))
    }

    def insertFailed(v: V) {
      // v will be boxed to java.lang.Object (java.lang.Double) due to ClassTag, 
      // then wrapped as Array[Object] and passed to function in scala.LowPriorityImplicits:
      //    implicit def genericWrapArray[T](xs: Array[T]): WrappedArray[T]
      // for insert(n: Int, elems: A*), and causes ArrayStoreException.
      values.insert(0, v)
      info(values.mkString(","))
    }

    def insertFailed(v1: V, v2: V) {
      values.insert(0, v1, v2)
      info(values.mkString(","))
    }
  }

}
