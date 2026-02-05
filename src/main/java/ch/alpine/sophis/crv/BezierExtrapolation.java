// code by jph
package ch.alpine.sophis.crv;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.itp.BezierFunction;
import ch.alpine.tensor.itp.BinaryAverage;

/** extrapolation by evaluating the Bezier curve defined by n number of
 * control points at parameter value n / (n - 1) */
public record BezierExtrapolation(BinaryAverage binaryAverage) implements TensorUnaryOperator {
  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    int n = tensor.length();
    return new BezierFunction(binaryAverage, tensor).apply(RationalScalar.of(n, n - 1));
  }
}
