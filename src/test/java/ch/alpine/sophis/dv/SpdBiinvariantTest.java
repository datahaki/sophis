// code by jph
package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.var.InversePowerVariogram;
import ch.alpine.sophus.bm.AffineVectorQ;
import ch.alpine.sophus.hs.spd.SpdManifold;
import ch.alpine.sophus.hs.spd.SpdNManifold;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.sca.Chop;

class SpdBiinvariantTest {
  public static BarycentricCoordinate[] list() {
    // return GbcHelper.barycentrics(SpdManifold.INSTANCE);
    return new BarycentricCoordinate[] { //
        new HsCoordinates(SpdManifold.INSTANCE, MetricCoordinate.of(InversePowerVariogram.of(1))), //
        new HsCoordinates(SpdManifold.INSTANCE, MetricCoordinate.of(InversePowerVariogram.of(2))), //
        // LeveragesCoordinate.slow(SpdManifold.INSTANCE, InversePowerVariogram.of(1)), //
        // LeveragesCoordinate.slow(SpdManifold.INSTANCE, InversePowerVariogram.of(2)), //
        UsanceCoordinate.of(SpdManifold.INSTANCE, InversePowerVariogram.of(1)), //
        UsanceCoordinate.of(SpdManifold.INSTANCE, InversePowerVariogram.of(2)), //
    };
  }

  @Test
  void testSimple() {
    Random random1 = new Random(3);
    int d = 2;
    int fail = 0;
    int len = 5 + random1.nextInt(3);
    SpdNManifold spdNManifold = new SpdNManifold(d);
    RandomSampleInterface rsi = spdNManifold.randomSampleInterface();
    Random randomGenerator = random1;
    Tensor sequence = RandomSample.of(rsi, randomGenerator, len);
    for (BarycentricCoordinate barycentricCoordinate : list())
      try {
        Tensor point = RandomSample.of(rsi, randomGenerator);
        Tensor weights = barycentricCoordinate.weights(sequence, point);
        AffineVectorQ.INSTANCE.require(weights); // , Chop._08);
        Tensor spd = SpdManifold.INSTANCE.biinvariantMean().mean(sequence, weights);
        Chop._08.requireClose(spd, point);
      } catch (Exception exception) {
        ++fail;
      }
    // System.out.println(fail);
    assertTrue(fail < 4);
  }

  @Test
  void testLagrangeProperty() {
    Random random = ThreadLocalRandom.current();
    int d = 2;
    int len = 5 + random.nextInt(3);
    SpdNManifold spdNManifold = new SpdNManifold(d);
    RandomSampleInterface rsi = spdNManifold.randomSampleInterface();
    Tensor sequence = RandomSample.of(rsi, len);
    for (BarycentricCoordinate barycentricCoordinate : list()) {
      int index = random.nextInt(sequence.length());
      Tensor point = sequence.get(index);
      Tensor weights = barycentricCoordinate.weights(sequence, point);
      AffineVectorQ.INSTANCE.require(weights); // , Chop._08);
      Tolerance.CHOP.requireClose(weights, UnitVector.of(len, index));
      Tensor spd = SpdManifold.INSTANCE.biinvariantMean().mean(sequence, weights);
      Tolerance.CHOP.requireClose(spd, point);
    }
  }
}
