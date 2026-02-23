// code by jph
package ch.alpine.sophis.decim;

import java.io.Serializable;

import ch.alpine.sophus.math.api.LineDistance;
import ch.alpine.sophus.math.api.TensorDistance;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Max;

public record SymmetricLineDistance(LineDistance lineDistance) implements LineDistance, Serializable {
  @Override // from LineDistance
  public TensorDistance distanceToLine(Tensor beg, Tensor end) {
    return new NormImpl( //
        lineDistance.distanceToLine(beg, end), //
        lineDistance.distanceToLine(end, beg));
  }

  private record NormImpl(TensorDistance tensorNorm1, TensorDistance tensorNorm2) //
      implements TensorDistance {
    @Override
    public Scalar distance(Tensor index) {
      return Max.of( //
          tensorNorm1.distance(index), //
          tensorNorm2.distance(index));
    }
  }
}
