// code by jph
package ch.alpine.sophis.crv.clt;

import ch.alpine.tensor.Scalar;

@FunctionalInterface
/* package */ interface ClothoidPartial {
  /** @param t
   * @return approximate integration of Exp[ i*Polynomial({c0, c1, c2}) ] on [0, t] */
  Scalar il(Scalar t);
}
