// code by jph
package ch.alpine.sophis.fit;

import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.dv.Biinvariant;
import ch.alpine.sophis.dv.Biinvariants;
import ch.alpine.sophis.dv.Sedarim;
import ch.alpine.sophis.fit.HsWeiszfeldMethod;
import ch.alpine.sophis.fit.SpatialMedian;
import ch.alpine.sophis.fit.WeiszfeldMethod;
import ch.alpine.sophus.bm.LinearBiinvariantMean;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

class HsWeiszfeldMethodTest {
  @Test
  void testSimple() {
    RandomGenerator randomGenerator = new Random(3);
    SpatialMedian sm2 = new WeiszfeldMethod(Tolerance.CHOP);
    Distribution distribution = NormalDistribution.standard();
    Biinvariant biinvariant = Biinvariants.METRIC.ofSafe(RGroup.INSTANCE);
    for (int d = 2; d < 5; ++d)
      for (int n = 2; n < 20; n += 4) {
        Tensor sequence = RandomVariate.of(distribution, randomGenerator, n, d);
        Sedarim create = biinvariant.weighting(InversePowerVariogram.of(1), sequence);
        SpatialMedian sm1 = new HsWeiszfeldMethod(LinearBiinvariantMean.INSTANCE, create, Tolerance.CHOP);
        Tensor p1 = sm1.uniform(sequence).orElseThrow();
        Tensor p2 = sm2.uniform(sequence).orElseThrow();
        Chop._07.requireClose(p1, p2);
      }
  }
}
