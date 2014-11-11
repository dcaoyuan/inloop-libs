package inloopio.math.algebra.decomposer.hebbian

import inloopio.math.algebra.Vector

trait EigenUpdater {
  def update(pseudoEigen: Vector, trainingVector: Vector, currentState: TrainingState)
}
