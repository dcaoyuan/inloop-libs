package inloop.math.algebra.decomposer.hebbian

import inloop.collection.ArrayList
import inloop.math.algebra.DenseVector
import inloop.math.algebra.Matrix
import inloop.math.algebra.Vector
import inloop.math.algebra.decomposer.EigenStatus

class TrainingState(var currentEigens: Matrix, var trainingProjections: Matrix) {

  var numEigensProcessed = 0
  var currentEigenValues: ArrayList[Double] = _
  var trainingIndex = 0
  var helperVector: Vector = DenseVector(currentEigens.numRows)
  var isFirstPass = true
  var statusProgress = new ArrayList[EigenStatus]()
  var activationNumerator = 0.0
  var activationDenominatorSquared = 0.0

  def mostRecentEigen: Vector = {
    currentEigens.viewRow(numEigensProcessed - 1)
  }

  def currentTrainingProjection: Vector = {
    if (trainingProjections.viewRow(trainingIndex) == null) {
      trainingProjections.assignRow(trainingIndex, DenseVector(currentEigens.numCols))
    }
    trainingProjections.viewRow(trainingIndex)
  }
}
