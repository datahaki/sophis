// code by jph
package ch.alpine.sophis.flow;

import java.io.Serializable;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;

/** Numerical Recipes 3rd Edition Section 17.3.1
 * 
 * @param n strictly positive */
public record ModifiedMidpointIntegrator(int n) implements Integrator, Serializable {
  public ModifiedMidpointIntegrator {
    Integers.requirePositive(n);
  }

  @Override
  public Tensor step(StateSpaceModel stateSpaceModel, Tensor x, Tensor u, Scalar h) {
    Scalar hn = h.divide(RealScalar.of(n));
    Tensor xm = x.add(stateSpaceModel.f(x, u).multiply(hn)); // line identical with MidpointIntegrator
    for (int m = 1; m < n; ++m) {
      Scalar _2h = hn.add(hn);
      Tensor x1 = x.add(stateSpaceModel.f(xm, u).multiply(_2h));
      x = xm;
      xm = x1;
    }
    return x.add(stateSpaceModel.f(xm, u).multiply(hn)); // TODO OWL ALG line almost identical with MidpointIntegrator !?
  }
}
