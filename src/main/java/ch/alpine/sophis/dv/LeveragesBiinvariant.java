// code by jph
package ch.alpine.sophis.dv;

import java.util.Objects;

import ch.alpine.sophis.math.Genesis;
import ch.alpine.sophus.math.api.Manifold;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.mat.gr.Mahalanobis;
import ch.alpine.tensor.nrm.NormalizeTotal;

/** bi-invariant
 * does not result in a symmetric distance matrix -> should not use for kriging
 * 
 * leverages distances are biinvariant
 * 
 * <p>computes form at given point based on points in sequence and returns
 * vector of evaluations dMah_x(p_i) of points in sequence.
 * 
 * <p>one evaluation of the leverages involves the computation of
 * <pre>
 * PseudoInverse[levers^T . levers]
 * </pre>
 * 
 * <p>References:
 * "Biinvariant Generalized Barycentric Coordinates on Lie Groups"
 * by Jan Hakenberg, 2020
 * 
 * "Biinvariant Distance Vectors"
 * by Jan Hakenberg, 2020 */
/* package */ class LeveragesBiinvariant extends BiinvariantBase implements Genesis {
  public LeveragesBiinvariant(Manifold manifold) {
    super(manifold);
  }

  @Override // from Biinvariant
  public Sedarim relative_distances(Tensor sequence) {
    Objects.requireNonNull(sequence);
    return point -> origin(manifold.exponential(point).vectorLog().slash(sequence));
  }

  @Override // from Biinvariant
  public Sedarim coordinate(ScalarUnaryOperator variogram, Tensor sequence) {
    return HsCoordinates.wrap(manifold, coordinate(variogram), sequence);
  }

  public Genesis coordinate(ScalarUnaryOperator variogram) {
    return new LeveragesGenesis(variogram);
  }

  @Override // from Biinvariant
  public Sedarim lagrainate(ScalarUnaryOperator variogram, Tensor sequence) {
    Objects.requireNonNull(variogram);
    Objects.requireNonNull(sequence);
    return point -> {
      Tensor levers = manifold.exponential(point).vectorLog().slash(sequence);
      Tensor target = NormalizeTotal.FUNCTION.apply(origin(levers).maps(variogram));
      return LagrangeCoordinates.of(levers, target);
    };
  }

  @Override // from Genesis
  public Tensor origin(Tensor levers) {
    return new Mahalanobis(levers).leverages_sqrt();
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("Leverages", manifold);
  }
}
