// code by jph
package ch.alpine.sophis.gbc.d2;

import java.util.Objects;

import ch.alpine.sophis.api.Genesis;
import ch.alpine.sophis.crv.d2.alg.OriginEnclosureQ;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorScalarFunction;
import ch.alpine.tensor.chq.FiniteTensorQ;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;

/** for k == 0 the coordinates are identical to three-point coordinates with mean value as barycenter
 * 
 * mean value coordinates are C^\infty and work for non-convex polygons
 * 
 * Reference:
 * "Iterative coordinates"
 * by Chongyang Deng, Qingjun Chang, Kai Hormann, 2020 */
public record IterativeCoordinateLevel(Genesis genesis, Chop chop, int max) implements TensorScalarFunction {
  public IterativeCoordinateLevel {
    Objects.requireNonNull(genesis);
    Objects.requireNonNull(chop);
    Integers.requirePositiveOrZero(max);
  }

  @Override
  public Scalar apply(Tensor levers) {
    if (OriginEnclosureQ.INSTANCE.test(levers)) {
      Tensor scaling = InverseNorm.INSTANCE.origin(levers);
      if (FiniteTensorQ.of(scaling)) {
        Tensor normalized = Times.of(scaling, levers);
        int depth = 0;
        while (depth < max) {
          Tensor weights = genesis.origin(normalized);
          if (weights.stream().map(Scalar.class::cast).map(chop).allMatch(Sign::isPositiveOrZero))
            return RealScalar.of(depth);
          Tensor midpoints = Adds.forward(normalized);
          normalized = Times.of(InverseNorm.INSTANCE.origin(midpoints), midpoints);
          ++depth;
        }
        return RealScalar.of(depth);
      }
    }
    return DoubleScalar.INDETERMINATE;
  }
}
