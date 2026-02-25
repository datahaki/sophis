// code by jph
package ch.alpine.sophis.dv;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.api.Manifold;
import ch.alpine.sophus.hs.s.SnManifold;
import ch.alpine.sophus.hs.s.Sphere;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class LeveragesBiinvariantTest {
  @Test
  void testRn() {
    Tensor sequence = RandomVariate.of(UniformDistribution.unit(), 10, 3);
    Manifold manifold = RGroup.INSTANCE;
    LeveragesBiinvariant leveragesBiinvariant = new LeveragesBiinvariant(manifold);
    Sedarim w2 = leveragesBiinvariant.relative_distances(sequence);
    for (int count = 0; count < 10; ++count) {
      Tensor point = RandomVariate.of(UniformDistribution.unit(), 3);
      w2.sunder(point);
    }
  }

  @Test
  void testSn() {
    RandomSampleInterface randomSampleInterface = new Sphere(2);
    Tensor sequence = RandomSample.of(randomSampleInterface, 10);
    Manifold manifold = SnManifold.INSTANCE;
    LeveragesBiinvariant leveragesBiinvariant = new LeveragesBiinvariant(manifold);
    Sedarim w2 = leveragesBiinvariant.relative_distances(sequence);
    for (int count = 0; count < 10; ++count) {
      Tensor point = RandomSample.of(randomSampleInterface);
      w2.sunder(point);
    }
  }

  @Test
  void testSe2() {
    Distribution distribution = UniformDistribution.unit();
    Tensor sequence = RandomVariate.of(distribution, 10, 3);
    Manifold manifold = Se2Group.INSTANCE;
    LeveragesBiinvariant leveragesBiinvariant = new LeveragesBiinvariant(manifold);
    Sedarim w2 = leveragesBiinvariant.relative_distances(sequence);
    for (int count = 0; count < 10; ++count) {
      Tensor point = RandomVariate.of(distribution, 3);
      w2.sunder(point);
    }
  }
}
