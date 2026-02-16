// code by jph
package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.sophus.lie.se2.Se2RandomSample;
import ch.alpine.sophus.math.api.Manifold;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.SquareMatrixQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.nrm.Matrix2Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;

class HarborBiinvariantTest {
  /** @param manifold
   * @param sequence
   * @return */
  public static BiinvariantVectorFunction norm2(Manifold manifold, Tensor sequence) {
    return new BiinvariantVectorFunction(manifold, sequence, (x, y) -> Matrix2Norm.of(x.subtract(y)));
  }

  @Test
  void testRn() {
    Distribution distribution = UniformDistribution.of(Clips.absolute(10));
    Manifold manifold = RGroup.INSTANCE;
    int length = 4 + ThreadLocalRandom.current().nextInt(6);
    Tensor sequence = RandomVariate.of(distribution, length, 3);
    Tensor point = RandomVariate.of(distribution, 3);
    BiinvariantVectorFunction d1 = new HarborBiinvariant(manifold).biinvariantVectorFunction(sequence);
    BiinvariantVectorFunction d2 = norm2(manifold, sequence);
    BiinvariantVectorFunction d3 = new CupolaBiinvariant(manifold).biinvariantVectorFunction(sequence);
    BiinvariantVector v1 = d1.biinvariantVector(point);
    BiinvariantVector v2 = d2.biinvariantVector(point);
    BiinvariantVector v3 = d3.biinvariantVector(point);
    Chop._10.requireClose(v1.weighting(s -> s), v2.weighting(s -> s));
    assertEquals(v1.vector().length(), v3.vector().length());
  }

  @Test
  void testSe2C() {
    Distribution distribution = UniformDistribution.of(Clips.absolute(10));
    Manifold manifold = Se2CoveringGroup.INSTANCE;
    int length = 4 + ThreadLocalRandom.current().nextInt(4);
    Tensor sequence = RandomVariate.of(distribution, length, 3);
    Tensor point = RandomVariate.of(distribution, 3);
    BiinvariantVectorFunction d1 = new HarborBiinvariant(manifold).biinvariantVectorFunction(sequence);
    BiinvariantVectorFunction d2 = norm2(manifold, sequence);
    BiinvariantVectorFunction d3 = new CupolaBiinvariant(manifold).biinvariantVectorFunction(sequence);
    BiinvariantVector v1 = d1.biinvariantVector(point);
    BiinvariantVector v2 = d2.biinvariantVector(point);
    BiinvariantVector v3 = d3.biinvariantVector(point);
    assertEquals(v1.vector().length(), v2.vector().length());
    assertEquals(v1.vector().length(), v3.vector().length());
  }

  @Test
  void testRandom() {
    Manifold manifold = Se2CoveringGroup.INSTANCE;
    Distribution distributiox = NormalDistribution.of(0, 0.1);
    Distribution distribution = NormalDistribution.of(0, 0.1);
    Map<Biinvariants, Biinvariant> map = Biinvariants.all(manifold);
    for (Biinvariant biinvariant : map.values())
      for (int n = 4; n < 10; ++n) {
        Tensor points = RandomVariate.of(distributiox, n, 3);
        Tensor xya = RandomVariate.of(distribution, 3);
        Tensor distances = biinvariant.relative_distances(points).sunder(xya);
        Tensor shift = RandomVariate.of(distribution, 3);
        for (TensorUnaryOperator tensorMapping : BiinvariantCheck.of(Se2CoveringGroup.INSTANCE, shift))
          Chop._05.requireClose(distances, //
              biinvariant.relative_distances( //
                  Tensor.of(points.stream().map(tensorMapping))).sunder(tensorMapping.apply(xya)));
      }
  }

  @Test
  void testSymmetric() {
    Tensor sequence = RandomSample.of(Se2RandomSample.of(NormalDistribution.standard()), 15);
    Map<Biinvariants, Biinvariant> map = Biinvariants.kriging(Se2CoveringGroup.INSTANCE);
    for (Biinvariant biinvariant : map.values()) {
      Sedarim sedarim = biinvariant.relative_distances(sequence);
      Tensor matrix = Tensor.of(sequence.stream().map(sedarim::sunder));
      SquareMatrixQ.INSTANCE.require(matrix);
      SymmetricMatrixQ.INSTANCE.require(matrix);
    }
  }

  @Test
  void testNonPublic() {
    assertFalse(Modifier.isPublic(HarborBiinvariant.class.getModifiers()));
  }
}
