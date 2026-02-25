// code by jph
package ch.alpine.sophis.dv;

import java.io.Serializable;
import java.util.Objects;

import ch.alpine.sophus.api.Manifold;
import ch.alpine.sophus.api.TensorMetric;
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
  private final Tensor influences;

  /** @param hsDesign
   * @param sequence
   * @param tensorMetric */
  public BiinvariantVectorFunction(Manifold manifold, Tensor sequence, TensorMetric tensorMetric) {
    this.manifold = manifold;
    this.sequence = sequence;
    this.tensorMetric = Objects.requireNonNull(tensorMetric);
    influences = Tensor.of(sequence.stream() //
        .map(point -> manifold.exponential(point).vectorLog().slash(sequence)) //
        .map(InfluenceMatrix::of) //
        .map(InfluenceMatrix::matrix));
  }

  /** @param point
   * @return biinvariant vector at given point of manifold */
  public BiinvariantVector biinvariantVector(Tensor point) {
    Tensor levers = manifold.exponential(point).vectorLog().slash(sequence);
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(levers);
    Tensor matrix = influenceMatrix.matrix();
    return new BiinvariantVector( //
        influenceMatrix, //
        Tensor.of(influences.stream().map(x -> tensorMetric.distance(x, matrix))));
  }
}
