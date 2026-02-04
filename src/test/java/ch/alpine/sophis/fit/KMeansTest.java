// code by jph
package ch.alpine.sophis.fit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.alpine.sophis.dv.Biinvariant;
import ch.alpine.sophis.dv.Biinvariants;
import ch.alpine.sophis.fit.KMeans;
import ch.alpine.sophus.bm.CenterMean;
import ch.alpine.sophus.hs.s.HemisphereRandomSample;
import ch.alpine.sophus.hs.s.SnManifold;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.red.Mean;

class KMeansTest {
  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4 })
  void testR2Single(int n) {
    Biinvariant biinvariant = Biinvariants.METRIC.ofSafe(RGroup.INSTANCE);
    Tensor sequence = RandomVariate.of(NormalDistribution.standard(), 20, 2);
    KMeans kMeans = new KMeans( //
        biinvariant.distances(sequence), //
        Mean::of, //
        sequence);
    kMeans.setSeeds(RandomVariate.of(NormalDistribution.standard(), n, 2));
    for (int i = 0; i < 10; ++i)
      kMeans.iterate();
    Tensor partition = kMeans.partition();
    int sum = partition.stream().mapToInt(Tensor::length).sum();
    assertEquals(sum, sequence.length());
    assertTrue(0 < kMeans.seeds().length());
    assertEquals(kMeans.seeds().length(), n);
  }

  @Test
  void testR2Standard() {
    Biinvariant biinvariant = Biinvariants.METRIC.ofSafe(RGroup.INSTANCE);
    Tensor sequence = RandomVariate.of(NormalDistribution.standard(), 20, 2);
    KMeans kMeans = new KMeans( //
        biinvariant.distances(sequence), //
        Mean::of, //
        sequence);
    kMeans.setSeeds(5);
    for (int i = 0; i < 5; ++i)
      kMeans.iterate();
    Tensor partition = kMeans.partition();
    int sum = partition.stream().mapToInt(Tensor::length).sum();
    assertEquals(sum, sequence.length());
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4 })
  void testS2Single(int n) {
    Biinvariant biinvariant = Biinvariants.METRIC.ofSafe(SnManifold.INSTANCE);
    RandomSampleInterface randomSampleInterface = HemisphereRandomSample.of(2);
    Tensor sequence = RandomSample.of(randomSampleInterface, 40);
    KMeans kMeans = new KMeans( //
        biinvariant.distances(sequence), //
        new CenterMean(SnManifold.INSTANCE.biinvariantMean()), //
        sequence);
    kMeans.setSeeds(RandomSample.of(randomSampleInterface, n));
    for (int i = 0; i < 5; ++i)
      kMeans.iterate();
    int complete = kMeans.complete();
    assertTrue(complete < 20);
    Tensor partition = kMeans.partition();
    int sum = partition.stream().mapToInt(Tensor::length).sum();
    assertEquals(sum, sequence.length());
    assertTrue(0 < kMeans.seeds().length());
  }

  @ParameterizedTest
  @ValueSource(ints = { 1, 2, 3, 4 })
  void testSnSingle(int d) {
    Biinvariant biinvariant = Biinvariants.METRIC.ofSafe(SnManifold.INSTANCE);
    RandomSampleInterface randomSampleInterface = HemisphereRandomSample.of(d);
    Tensor sequence = RandomSample.of(randomSampleInterface, 40);
    KMeans kMeans = new KMeans( //
        biinvariant.distances(sequence), //
        new CenterMean(SnManifold.INSTANCE.biinvariantMean()), //
        sequence);
    kMeans.setSeeds(RandomSample.of(randomSampleInterface, 3));
    for (int i = 0; i < 5; ++i)
      kMeans.iterate();
    int complete = kMeans.complete();
    assertTrue(complete < 20);
    Tensor partition = kMeans.partition();
    int sum = partition.stream().mapToInt(Tensor::length).sum();
    assertEquals(sum, sequence.length());
    assertTrue(0 < kMeans.seeds().length());
  }

  @Test
  void testSe2Standard() {
    LieGroup lieGroup = Se2CoveringGroup.INSTANCE;
    Biinvariant biinvariant = Biinvariants.LEVERAGES.ofSafe(lieGroup);
    Tensor sequence = RandomVariate.of(NormalDistribution.standard(), 40, 3);
    KMeans kMeans = new KMeans( //
        biinvariant.distances(sequence), //
        new CenterMean(lieGroup.biinvariantMean()), //
        sequence);
    kMeans.setSeeds(2);
    kMeans.complete();
    Tensor partition = kMeans.partition();
    int sum = partition.stream().mapToInt(Tensor::length).sum();
    assertEquals(sum, sequence.length());
  }
}
