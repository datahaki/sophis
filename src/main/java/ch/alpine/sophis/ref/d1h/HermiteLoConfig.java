// code by jph
package ch.alpine.sophis.ref.d1h;

import java.io.Serializable;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;

public record HermiteLoConfig(Scalar lambda, Scalar mu) implements Serializable {
  public static final HermiteLoConfig STANDARD = new HermiteLoConfig( //
      Rational.of(-1, 8), //
      Rational.of(-1, 2));
  public static final HermiteLoConfig MANIFOLD = new HermiteLoConfig( //
      Rational.of(-1, 5), //
      Rational.of(9, 10));
}
