package inloop.math.algebra.decomposer

import inloop.math.algebra.Vector
import inloop.math.algebra.VectorIterable

trait SingularVectorVerifier {
  def verify(eigenMatrix: VectorIterable, vector: Vector): EigenStatus
}
