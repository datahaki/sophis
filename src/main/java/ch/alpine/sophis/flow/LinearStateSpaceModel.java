// code by jph
package ch.alpine.sophis.flow;

import java.io.Serializable;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.nrm.Matrix2Norm;

/** represents the standard state-space model with
 * state matrix a,
 * input matrix b,
 * output matrix c, and
 * transmission matrix d
 * 
 * Lipschitz L == Norm._2.ofMatrix(a) */
public record LinearStateSpaceModel(Tensor a, Tensor b, Tensor c, Tensor d) implements StateSpaceModel, Serializable {
  @Override
  public Tensor f(Tensor x, Tensor u) {
    return a.dot(x).add(b.dot(u));
  }

  public Scalar L() {
    return Matrix2Norm.bound(a);
  }

  public Tensor output(Tensor x, Tensor u) {
    return c.dot(x).add(d.dot(u));
  }
}
