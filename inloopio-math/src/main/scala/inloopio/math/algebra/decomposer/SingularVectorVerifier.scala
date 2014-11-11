package inloopio.math.algebra.decomposer

import inloopio.math.algebra.Vector
import inloopio.math.algebra.VectorIterable

trait SingularVectorVerifier {
  def verify(eigenMatrix: VectorIterable, vector: Vector): EigenStatus
}
