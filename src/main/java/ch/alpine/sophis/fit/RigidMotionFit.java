// code by jph
package ch.alpine.sophis.fit;

import ch.alpine.sophus.bm.AffineVectorQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.pd.Orthogonalize;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.red.Times;

/** function computes the best-fitting rigid transformation that aligns
 * two sets of corresponding n elements from d-dimensional vector space.
 * 
 * Reference:
 * "Least-Squares Rigid Motion Using SVD"
 * Olga Sorkine-Hornung and Michael Rabinovich, 2016
 * 
 * rotation dot point plus translation == target
 * 
 * Reference:
 * "The Orthogonal Procrustes Problem"
 * by Gilbert Strang, p.257
 * 
 * Chatgpt uses the reference: "Kabsch algorithm"
 * 
 * @param rotation orthogonal matrix with dimension d x d and determinant +1
 * @param translation vector of length d */
public record RigidMotionFit(Tensor rotation, Tensor translation) implements TensorUnaryOperator {
  /** @param origin matrix of dimension n x d
   * @param target matrix of dimension n x d
   * @param weights vector of length n with entries that sum up to 1
   * @return
   * @throws Exception if total of weights does not equal 1 */
  public static RigidMotionFit of(Tensor origin, Tensor target, Tensor weights) {
    AffineVectorQ.INSTANCE.require(weights);
    return _of(origin, target, weights);
  }

  /** special case with weights == AveragingWeights.of(n)
   * 
   * @param origin matrix of dimension n x d
   * @param target matrix of dimension n x d
   * @return */
  public static RigidMotionFit of(Tensor origin, Tensor target) {
    Tensor pm = Mean.of(origin); // mean of origin coordinates
    Tensor qm = Mean.of(target); // mean of target coordinates
    Tensor xt = Tensor.of(origin.stream().map(pm.negate()::add)); // levers to origin coordinates
    Tensor yt = Tensor.of(target.stream().map(qm.negate()::add)); // levers to target coordinates
    Tensor rotation = Orthogonalize.usingSvd(Transpose.of(yt).dot(xt));
    return new RigidMotionFit(rotation, qm.subtract(rotation.dot(pm)));
  }

  // helper function
  private static RigidMotionFit _of(Tensor origin, Tensor target, Tensor weights) {
    Tensor pm = weights.dot(origin); // weighted mean of origin coordinates
    Tensor qm = weights.dot(target); // weighted mean of target coordinates
    Tensor xt = Tensor.of(origin.stream().map(pm.negate()::add)); // levers to origin coordinates
    Tensor yt = Tensor.of(target.stream().map(qm.negate()::add)); // levers to target coordinates
    Tensor rotation = Orthogonalize.usingSvd(Transpose.of(yt).dot(Times.of(weights, xt)));
    return new RigidMotionFit(rotation, qm.subtract(rotation.dot(pm)));
  }

  @Override
  public Tensor apply(Tensor point) {
    return rotation.dot(point).add(translation);
  }
}
