// code by jph
package ch.alpine.sophis.dv;

import java.util.Objects;

import ch.alpine.sophus.hs.HsDesign;
import ch.alpine.sophus.math.Genesis;
import ch.alpine.tensor.Tensor;

public enum HsGenesis {
  ;
  /** @param hsDesign
   * @param genesis
   * @param sequence non-null
   * @return */
  public static Sedarim wrap(HsDesign hsDesign, Genesis genesis, Tensor sequence) {
    BarycentricCoordinate barycentricCoordinate = new HsCoordinates(hsDesign, genesis);
    Objects.requireNonNull(sequence);
    return point -> barycentricCoordinate.weights(sequence, point);
  }
}
