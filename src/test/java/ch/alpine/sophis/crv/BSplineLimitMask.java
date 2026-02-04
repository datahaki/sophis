// code by jph
package ch.alpine.sophis.crv;

import java.util.function.Function;

import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.ext.Integers;

/** function defined for positive odd integers
 * 
 * Example:
 * BSplineLimitMask[ 1 ] == {1}
 * BSplineLimitMask[ 3 ] == {1/6, 2/3, 1/6}
 * BSplineLimitMask[ 5 ] == {1/120, 13/60, 11/20, 13/60, 1/120} */
public enum BSplineLimitMask implements Function<Integer, Tensor> {
  FUNCTION;

  @Override
  public Tensor apply(Integer degree) {
    int extent = (Integers.requireOdd(degree) - 1) / 2;
    return Range.of(extent + 1, degree + extent + 1) //
        .map(GeodesicBSplineFunction.of(RGroup.INSTANCE, degree, UnitVector.of(2 * degree + 1, degree)));
  }
}
