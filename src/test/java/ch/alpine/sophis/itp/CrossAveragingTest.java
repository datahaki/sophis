// code by jph
package ch.alpine.sophis.itp;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.dv.Biinvariant;
import ch.alpine.sophis.dv.Biinvariants;
import ch.alpine.sophis.dv.Sedarim;
import ch.alpine.sophus.hs.s.SnPhongMean;
import ch.alpine.sophus.hs.s.Sphere;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

class CrossAveragingTest {
  @Test
  void testSimplePD() throws ClassNotFoundException, IOException {
    Map<Biinvariants, Biinvariant> map = Biinvariants.all(Se2CoveringGroup.INSTANCE);
    for (Biinvariant biinvariant : map.values()) {
      Distribution distribution = NormalDistribution.standard();
      int n = 4 + ThreadLocalRandom.current().nextInt(4);
      Tensor sequence = RandomVariate.of(distribution, n, 3);
      Sedarim tensorUnaryOperator = Serialization.copy( //
          biinvariant.weighting(InversePowerVariogram.of(2), sequence));
      RandomSampleInterface randomSampleInterface = new Sphere(4);
      Tensor values = RandomSample.of(randomSampleInterface, n);
      Tensor point = RandomVariate.of(distribution, 3);
      // the use of snphong mean is not a mistake
      Tensor evaluate = new CrossAveraging(tensorUnaryOperator, SnPhongMean.INSTANCE, values).apply(point);
      VectorQ.requireLength(evaluate, 5);
    }
  }
}
