// code by jph
package ch.alpine.sophis.dv;

import ch.alpine.sophus.hs.Manifold;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;

/** list of biinvariant weightings and barycentric coordinates regardless whether a
 * biinvariant metric exists on the manifold.
 * 
 * <p>Reference:
 * "Biinvariant Distance Vectors"
 * by Jan Hakenberg, 2020 */
public interface Biinvariant {
  Manifold manifold();

  /** @param sequence
   * @return operator that maps a point to a vector of relative distances to the elements in the given sequence */
  Sedarim relative_distances(Tensor sequence);

  /** @param variogram
   * @param sequence
   * @return distance vector with entries subject to given variogram */
  Sedarim var_dist(ScalarUnaryOperator variogram, Tensor sequence);

  /** @param variogram
   * @param sequence
   * @return distance vector with entries subject to given variogram normalized to sum up to 1 */
  Sedarim weighting(ScalarUnaryOperator variogram, Tensor sequence);

  /** @param variogram
   * @param sequence
   * @return operator that provides barycentric coordinates */
  Sedarim coordinate(ScalarUnaryOperator variogram, Tensor sequence);

  /** barycentric coordinate solution of Lagrange multiplier system
   * 
   * @param variogram
   * @param sequence
   * @return operator that provides barycentric coordinates */
  Sedarim lagrainate(ScalarUnaryOperator variogram, Tensor sequence);
}
