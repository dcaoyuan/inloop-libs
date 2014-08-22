package inloop.math.algebra.decomposer.hebbian

import inloop.math.algebra.Vector

trait EigenUpdater {
  def update(pseudoEigen: Vector, trainingVector: Vector, currentState: TrainingState)
}
