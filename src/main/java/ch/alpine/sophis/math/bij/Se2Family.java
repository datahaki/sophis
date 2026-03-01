// code by jph
package ch.alpine.sophis.math.bij;

import java.io.Serializable;

import ch.alpine.sophus.lie.se2.Se2ForwardAction;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.lie.rot.RotationMatrix;

/** the term "family" conveys the meaning that the rigid transformation
 * depends on a single parameter, for instance time
 * 
 * @param function maps a {@link Scalar} to a vector {px, py, angle}
 * that represents the {@link Se2Bijection} */
public record Se2Family(ScalarTensorFunction function) implements R2RigidFamily, Serializable {
  /** @param center
   * @param rotation
   * @return */
  public static R2RigidFamily rotationAround(Tensor center, ScalarUnaryOperator rotation) {
    return new Se2Family(time -> {
      Scalar theta = rotation.apply(time);
      return center.subtract(RotationMatrix.of(theta).dot(center)).append(theta);
    });
  }

  @Override // from BijectionFamily
  public TensorUnaryOperator forward(Scalar scalar) {
    return new Se2ForwardAction(function.apply(scalar));
  }

  @Override // from BijectionFamily
  public TensorUnaryOperator inverse(Scalar scalar) {
    return new Se2InverseAction(function.apply(scalar));
  }

  @Override // from RigidFamily
  public Tensor forward_se2(Scalar scalar) {
    return Se2Matrix.of(function.apply(scalar));
  }
}
