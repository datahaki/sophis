// code by jph
package ch.alpine.sophis.hull.d3;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;

enum StaticHelper {
  ;
  /** Precision of a double. */
  static final Scalar DOUBLE_PREC = RealScalar.of(Math.nextUp(1.0) - 1.0);
  static final Scalar QUADRO_PREC = DOUBLE_PREC.add(DOUBLE_PREC);
  static final Scalar _3_PREC = QUADRO_PREC.add(DOUBLE_PREC);
}
