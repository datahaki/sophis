// code by jph
package ch.alpine.sophis.ref.d1h;

import java.io.Serializable;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;

public record HermiteLoConfig(Scalar lambda, Scalar mu) implements Serializable {
  public static final HermiteLoConfig STANDARD = new HermiteLoConfig( //
      RationalScalar.of(-1, 8), //
      RationalScalar.of(-1, 2));
  public static final HermiteLoConfig MANIFOLD = new HermiteLoConfig( //
      RationalScalar.of(-1, 5), //
      RationalScalar.of(9, 10));
}
