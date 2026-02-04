package ch.alpine.sophis.dv;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.dv.BarycentricCoordinate;
import ch.alpine.sophis.dv.HsCoordinates;
import ch.alpine.sophis.dv.MetricCoordinate;
import ch.alpine.sophis.dv.NormWeighting;
import ch.alpine.sophus.hs.HsDesign;
import ch.alpine.sophus.lie.sc.ScGroup;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.nrm.Vector1Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.ExponentialDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

class ScBiinvariantTest {
  public static final BarycentricCoordinate INSTANCE = new HsCoordinates( //
      new HsDesign(ScGroup.INSTANCE), //
      new MetricCoordinate(NormWeighting.of(Vector1Norm::of, InversePowerVariogram.of(1))));

  @Test
  void testSimple2() {
    Tensor sequence = Tensors.vector(2, 4).map(Tensors::of);
    Tensor target = Tensors.vector(1);
    Tensor weights = INSTANCE.weights(sequence, target);
    Tensor mean = ScGroup.INSTANCE.biinvariantMean().mean(sequence, weights);
    Chop._10.requireClose(target, mean);
  }

  @Test
  void testRandom() {
    for (int n = 4; n < 10; ++n) {
      Distribution distribution = ExponentialDistribution.of(1);
      Tensor sequence = RandomVariate.of(distribution, n, 1);
      Tensor target = Tensors.vector(1);
      Tensor weights = INSTANCE.weights(sequence, target);
      Tensor mean = ScGroup.INSTANCE.biinvariantMean().mean(sequence, weights);
      Chop._10.requireClose(target, mean);
    }
  }
}
