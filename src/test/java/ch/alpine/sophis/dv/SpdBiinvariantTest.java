package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.alpine.sophus.hs.spd.Spd0RandomSample;
import ch.alpine.sophus.hs.spd.SpdManifold;
import ch.alpine.sophus.lie.so.SoNGroup;
import ch.alpine.sophus.math.AffineQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.BasisTransform;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.pdf.c.TriangularDistribution;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

class SpdBiinvariantTest {
  public static BarycentricCoordinate[] list() {
    // return GbcHelper.barycentrics(SpdManifold.INSTANCE);
    return new BarycentricCoordinate[] { //
        new HsCoordinates(SpdManifold.INSTANCE, MetricCoordinate.of(InversePowerVariogram.of(1))), //
        new HsCoordinates(SpdManifold.INSTANCE, MetricCoordinate.of(InversePowerVariogram.of(2))), //
        // LeveragesCoordinate.slow(SpdManifold.INSTANCE, InversePowerVariogram.of(1)), //
        // LeveragesCoordinate.slow(SpdManifold.INSTANCE, InversePowerVariogram.of(2)), //
        LeveragesCoordinate.of(SpdManifold.INSTANCE, InversePowerVariogram.of(1)), //
        LeveragesCoordinate.of(SpdManifold.INSTANCE, InversePowerVariogram.of(2)), //
    };
  }

  @Disabled
  @Test
  void testSimple() {
    Random random1 = new Random(3);
    int d = 2;
    int fail = 0;
    int len = 5 + random1.nextInt(3);
    RandomSampleInterface rsi = new Spd0RandomSample(d, NormalDistribution.standard());
    Random randomGenerator = random1;
    Tensor sequence = RandomSample.of(rsi, randomGenerator, len);
    for (BarycentricCoordinate barycentricCoordinate : list())
      try {
        Tensor point = RandomSample.of(rsi, randomGenerator);
        Tensor weights = barycentricCoordinate.weights(sequence, point);
        AffineQ.INSTANCE.requireMember(weights); // , Chop._08);
        Tensor spd = SpdManifold.INSTANCE.biinvariantMean().mean(sequence, weights);
        Chop._08.requireClose(spd, point);
      } catch (Exception exception) {
        ++fail;
      }
    // System.out.println(fail);
    assertTrue(fail < 4);
  }

  @Disabled
  @Test
  void testLagrangeProperty() {
    Random random = ThreadLocalRandom.current();
    int d = 2;
    int len = 5 + random.nextInt(3);
    RandomSampleInterface rsi = new Spd0RandomSample(d, NormalDistribution.standard());
    Tensor sequence = RandomSample.of(rsi, len);
    for (BarycentricCoordinate barycentricCoordinate : list()) {
      int index = random.nextInt(sequence.length());
      Tensor point = sequence.get(index);
      Tensor weights = barycentricCoordinate.weights(sequence, point);
      AffineQ.INSTANCE.requireMember(weights); // , Chop._08);
      Tolerance.CHOP.requireClose(weights, UnitVector.of(len, index));
      Tensor spd = SpdManifold.INSTANCE.biinvariantMean().mean(sequence, weights);
      Tolerance.CHOP.requireClose(spd, point);
    }
  }

  @Disabled
  @Test
  void testBiinvarianceSon() {
    RandomGenerator randomGenerator = new Random(4);
    int n = 2 + randomGenerator.nextInt(3);
    Map<Biinvariants, Biinvariant> map = Biinvariants.all(SpdManifold.INSTANCE);
    for (Entry<Biinvariants, Biinvariant> entry : map.entrySet())
      if (!entry.getKey().equals(Biinvariants.CUPOLA) || n < 4) {
        Biinvariant biinvariant = entry.getValue();
        int count = 1 + randomGenerator.nextInt(3);
        int len = n * (n + 1) / 2 + count;
        RandomSampleInterface rsi = new Spd0RandomSample(n, TriangularDistribution.with(0, 1));
        Tensor sequence = RandomSample.of(rsi, len);
        Tensor mL = RandomSample.of(rsi);
        Tensor weights1 = biinvariant.coordinate(InversePowerVariogram.of(2), sequence).sunder(mL);
        // ---
        Tensor g = RandomSample.of(new SoNGroup(n), randomGenerator);
        Tensor sR = Tensor.of(sequence.stream().map(t -> BasisTransform.ofForm(t, g)));
        Tensor mR = BasisTransform.ofForm(mL, g);
        Tensor weights2 = biinvariant.coordinate(InversePowerVariogram.of(2), sR).sunder(mR);
        Chop._02.requireClose(weights1, weights2);
      }
  }

  @Disabled
  @Test
  void testBiinvarianceGln() {
    RandomGenerator randomGenerator = new Random(4);
    int n = 2 + randomGenerator.nextInt(2);
    Map<Biinvariants, Biinvariant> map = Biinvariants.magic4(SpdManifold.INSTANCE);
    for (Biinvariant biinvariant : map.values()) {
      int count = 1 + randomGenerator.nextInt(3);
      int len = n * (n + 1) / 2 + count;
      RandomSampleInterface rsi = new Spd0RandomSample(n, TriangularDistribution.with(0, 1));
      Tensor sequence = RandomSample.of(rsi, randomGenerator, len);
      Tensor mL = RandomSample.of(rsi, randomGenerator);
      Tensor weights1 = biinvariant.coordinate(InversePowerVariogram.of(2), sequence).sunder(mL);
      // ---
      Tensor g = RandomVariate.of(NormalDistribution.standard(), randomGenerator, n, n);
      Tensor sR = Tensor.of(sequence.stream().map(t -> BasisTransform.ofForm(t, g)));
      Tensor mR = BasisTransform.ofForm(mL, g);
      Tensor weights2 = biinvariant.coordinate(InversePowerVariogram.of(2), sR).sunder(mR);
      Chop._02.requireClose(weights1, weights2);
    }
  }
}
