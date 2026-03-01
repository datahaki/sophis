// code by jph
package ch.alpine.sophis.math.bij;

import java.io.Serializable;

import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.lie.rot.RotationMatrix;

/** the term "family" conveys the meaning that the rotation
 * depends on a single parameter, for instance time
 * 
 * @param function that maps to angle */
public record So2Family(ScalarUnaryOperator function) implements R2RigidFamily, Serializable {
  @Override // from BijectionFamily
  public TensorUnaryOperator forward(Scalar scalar) {
    Scalar angle = function.apply(scalar);
    Tensor matrix = RotationMatrix.of(angle);
    return matrix::dot;
  }

  @Override // from BijectionFamily
  public TensorUnaryOperator inverse(Scalar scalar) {
    Scalar angle = function.apply(scalar);
    Tensor matrix = RotationMatrix.of(angle.negate());
    return matrix::dot;
  }

  @Override // from RigidFamily
  public Tensor forward_se2(Scalar scalar) {
    Scalar angle = function.apply(scalar);
    return Se2Matrix.of(Array.zeros(2).append(angle));
  }
}
