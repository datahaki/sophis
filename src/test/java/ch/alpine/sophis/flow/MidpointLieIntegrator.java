// code by jph
package ch.alpine.sophis.flow;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.sophus.api.TangentSpace;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

public class MidpointLieIntegrator implements TimeIntegrator, Serializable {
  /** @param lieGroup
   * @param exponential
   * @return */
  public static TimeIntegrator of(LieGroup lieGroup, TangentSpace exponential) {
    return new MidpointLieIntegrator( //
        Objects.requireNonNull(lieGroup), //
        Objects.requireNonNull(exponential));
  }

  // ---
  private final LieGroup lieGroup;
  private final TangentSpace exponential;

  private MidpointLieIntegrator(LieGroup lieGroup, TangentSpace exponential) {
    this.lieGroup = lieGroup;
    this.exponential = exponential;
  }

  @Override // from Integrator
  public Tensor step(StateSpaceModel stateSpaceModel, Tensor x0, Tensor u, Scalar _2h
  // Flow flow, Tensor x0, Scalar _2h
  ) {
    Scalar h = _2h.multiply(Rational.HALF);
    Tensor xm = lieGroup.combine(x0, exponential.exp(stateSpaceModel.f(x0, u).multiply(h)));
    return /**/ lieGroup.combine(x0, exponential.exp(stateSpaceModel.f(xm, u).multiply(_2h))); // 2h
  }
}
