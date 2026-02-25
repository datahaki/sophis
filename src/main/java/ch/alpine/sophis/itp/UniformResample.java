// code by jph
package ch.alpine.sophis.itp;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.sophis.ref.d1.CurveSubdivision;
import ch.alpine.sophus.api.TensorMetric;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Append;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.api.ScalarTensorFunction;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.itp.BinaryAverage;
import ch.alpine.tensor.red.Total;
import ch.alpine.tensor.sca.Round;
import ch.alpine.tensor.sca.Sign;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Resampling.html">Resampling</a> */
public class UniformResample implements CurveSubdivision, Serializable {
  /** @param tensorMetric
   * @param binaryAverage
   * @param spacing positive
   * @return */
  public static CurveSubdivision of(TensorMetric tensorMetric, BinaryAverage binaryAverage, Scalar spacing) {
    return new UniformResample( //
        Objects.requireNonNull(tensorMetric), //
        Objects.requireNonNull(binaryAverage), //
        Sign.requirePositive(spacing));
  }

  // ---
  private final TensorUnaryOperator adjacentDistances;
  private final BinaryAverage binaryAverage;
  private final Scalar spacing;

  private UniformResample(TensorMetric tensorMetric, BinaryAverage binaryAverage, Scalar spacing) {
    adjacentDistances = AdjacentDistances.of(tensorMetric);
    this.binaryAverage = binaryAverage;
    this.spacing = spacing;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    return string(Append.of(tensor, tensor.get(0)));
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    Tensor distances = adjacentDistances.apply(tensor);
    ScalarTensorFunction scalarTensorFunction = ArcLengthParameterization.of(distances, binaryAverage, tensor);
    Scalar length = Total.ofVector(distances);
    int n = Round.intValueExact(length.divide(spacing));
    return Tensor.of(Subdivide.of(0, 1, n).stream() //
        .limit(n) //
        .map(Scalar.class::cast) //
        .map(scalarTensorFunction));
  }
}
