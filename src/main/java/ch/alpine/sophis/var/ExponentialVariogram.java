// code by jph
package ch.alpine.sophis.var;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.sca.Sign;
import ch.alpine.tensor.sca.exp.Exp;

/** <p>The input of the variogram has unit of a.
 * The output of the variogram has unit of b.
 * 
 * @param a positive */
public class ExponentialVariogram implements ScalarUnaryOperator {
  public static ScalarUnaryOperator of(Scalar a) {
    return Scalars.isZero(a) //
        ? ConstantOneVariogram.INSTANCE
        : new ExponentialVariogram(a);
  }

  /** @param a positive
   * @return */
  public static ScalarUnaryOperator of(Number a) {
    return of(RealScalar.of(a));
  }

  // ---
  private final Scalar a;

  private ExponentialVariogram(Scalar a) {
    this.a = Sign.requirePositive(a);
  }

  @Override
  public Scalar apply(Scalar r) {
    Sign.requirePositiveOrZero(r);
    return RealScalar.ONE.subtract(Exp.FUNCTION.apply(r.divide(a).negate()));
  }

  @Override // from Object
  public String toString() {
    return MathematicaFormat.concise("ExponentialVariogram", a);
  }
}
