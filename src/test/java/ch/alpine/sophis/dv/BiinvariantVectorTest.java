// code by jph
package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.hs.Manifold;
import ch.alpine.sophus.hs.s.SnManifold;
import ch.alpine.sophus.hs.s.Sphere;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.lie.Symmetrize;
import ch.alpine.tensor.mat.NullSpace;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.mat.PositiveDefiniteMatrixQ;
import ch.alpine.tensor.mat.PositiveSemidefiniteMatrixQ;
import ch.alpine.tensor.mat.SymmetricMatrixQ;
import ch.alpine.tensor.mat.gr.InfluenceMatrix;
import ch.alpine.tensor.mat.gr.InfluenceMatrixQ;
import ch.alpine.tensor.mat.pi.PseudoInverse;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.red.Trace;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Round;

class BiinvariantVectorTest {
  @Test
  void testSimpleR2() {
    Tensor sequence = RandomVariate.of(NormalDistribution.standard(), 10, 3);
    Tensor point = RandomVariate.of(NormalDistribution.standard(), 3);
    Manifold manifold = RGroup.INSTANCE;
    Tensor matrix = Tensor.of(sequence.stream().map(manifold.exponential(point)::log));
    Tensor nullsp = NullSpace.of(Transpose.of(matrix));
    OrthogonalMatrixQ.INSTANCE.require(nullsp);
    Chop._08.requireClose(PseudoInverse.of(nullsp), Transpose.of(nullsp));
  }

  private static Tensor _check(Manifold manifold, Tensor sequence, Tensor point) {
    Tensor V = manifold.exponential(point).log().slash(sequence);
    Tensor VT = Transpose.of(V);
    Tensor pinv = PseudoInverse.of(VT.dot(V));
    new SymmetricMatrixQ(Chop._04).require(pinv);
    Tensor sigma_inverse = Symmetrize.of(pinv);
    // ---
    Tensor H = V.dot(sigma_inverse.dot(VT)); // "hat matrix"
    new InfluenceMatrixQ(Chop._09).require(H);
    // ---
    Tensor traceh = Trace.of(H);
    Chop._07.requireClose(traceh, traceh.maps(Round.FUNCTION));
    // ---
    Tensor matrix = manifold.exponential(point).log().slash(sequence);
    InfluenceMatrix influenceMatrix = InfluenceMatrix.of(matrix);
    SymmetricMatrixQ.INSTANCE.require(influenceMatrix.matrix());
    Chop._08.requireClose(H, influenceMatrix.matrix());
    Tensor n = NullSpace.of(Transpose.of(V));
    Tensor M = influenceMatrix.residualMaker();
    new InfluenceMatrixQ(Chop._09).require(M);
    Chop._08.requireClose(M, Transpose.of(n).dot(n));
    // ---
    Tensor Xinv = PseudoInverse.of(V);
    Tensor p = V.dot(Xinv);
    Chop._08.requireClose(H, p);
    // ---
    Tensor d1 = influenceMatrix.leverages_sqrt();
    Tensor d2 = Tensor.of(influenceMatrix.matrix().stream().map(Vector2Norm::of));
    Chop._08.requireClose(d1, d2);
    return sigma_inverse;
  }

  @Test
  void testSe2CAnchorIsTarget() {
    Distribution distribution = UniformDistribution.of(-10, +10);
    RandomGenerator randomGenerator = new Random(3);
    Manifold manifold = Se2CoveringGroup.INSTANCE;
    for (int count = 4; count < 10; ++count) {
      Tensor sequence = RandomVariate.of(distribution, randomGenerator, count, 3);
      Tensor point = RandomVariate.of(distribution, randomGenerator, 3);
      Tensor sigma_inverse = _check(manifold, sequence, point);
      assertTrue(PositiveDefiniteMatrixQ.ofHermitian(sigma_inverse));
    }
  }

  @Test
  void testSe2AnchorIsTarget() {
    Distribution distribution = UniformDistribution.of(-10, +10);
    Manifold manifold = Se2Group.INSTANCE;
    for (int count = 4; count < 10; ++count) {
      Tensor sequence = RandomVariate.of(distribution, count, 3);
      Tensor point = RandomVariate.of(distribution, 3);
      Tensor sigma_inverse = _check(manifold, sequence, point);
      assertTrue(PositiveDefiniteMatrixQ.ofHermitian(sigma_inverse));
    }
  }

  @Test
  void testSnCAnchorIsTarget() {
    Manifold manifold = SnManifold.INSTANCE;
    for (int dimension = 2; dimension < 4; ++dimension) {
      RandomSampleInterface randomSampleInterface = new Sphere(dimension);
      for (int count = dimension + 1; count < 7; ++count) {
        Tensor sequence = RandomSample.of(randomSampleInterface, count);
        Tensor point = RandomSample.of(randomSampleInterface);
        Tensor sigma_inverse = _check(manifold, sequence, point);
        assertTrue(PositiveSemidefiniteMatrixQ.ofHermitian(sigma_inverse, Chop._08));
      }
    }
  }

  @Test
  void testNonPublic() {
    assertFalse(Modifier.isPublic(BiinvariantVector.class.getModifiers()));
  }
}
