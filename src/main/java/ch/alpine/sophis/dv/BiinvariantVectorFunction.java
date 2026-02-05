// code by jph
package ch.alpine.sophis.dv;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import ch.alpine.sophus.hs.Manifold;
import ch.alpine.sophus.math.api.TensorMetric;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.gr.InfluenceMatrix;

/** for Rn and Sn the frobenius distance results in identical coordinates as the 2-norm distance
 * 
 * however, for SE(2) the frobenius and 2-norm coordinates do not match!
 * 
 * Reference:
 * "Biinvariant Distance Vectors"
 * by Jan Hakenberg, 2020 */
/* package */ class BiinvariantVectorFunction implements Serializable {
  private final Manifold manifold;
  private final Tensor sequence;
  private final TensorMetric tensorMetric;
  private final Tensor[] influence;

  /** @param hsDesign
   * @param sequence
   * @param tensorMetric */
  public BiinvariantVectorFunction(Manifold manifold, Tensor sequence, TensorMetric tensorMetric) {
    this.manifold = manifold;
    this.sequence = sequence;
    this.tensorMetric = Objects.requireNonNull(tensorMetric);
    influence = sequence.stream() //
        .map(point -> manifold.exponential(point).log().slash(sequence)) //
        .map(InfluenceMatrix::of) //
        .map(InfluenceMatrix::matrix) //
        .toArray(Tensor[]::new);
  }

  /** @param point
   * @return biinvariant vector at given point of manifold */
  public BiinvariantVector biinvariantVector(Tensor point) {
    Tensor design = manifold.exponential(point).log().slash(sequence);
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(design);
    Tensor matrix = influenceMatrix.matrix();
    return new BiinvariantVector( //
        influenceMatrix, //
        Tensor.of(Arrays.stream(influence).map(x -> tensorMetric.distance(x, matrix))));
  }
}
