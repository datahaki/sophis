// code by jph
package ch.alpine.sophis.noise;

import java.util.random.RandomGenerator;
import java.util.stream.Stream;

import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.RandomSampleInterface;

/** @param randomSampleInterface
 * @param threshold */
public record NoiseFilteredSample(RandomSampleInterface randomSampleInterface, Scalar threshold) implements RandomSampleInterface {
  private static final int MAX_ITERATIONS = 100;

  @Override
  public Tensor randomSample(RandomGenerator randomGenerator) {
    return Stream.generate(() -> randomSampleInterface.randomSample(randomGenerator)) //
        .limit(MAX_ITERATIONS) //
        .filter(x -> Scalars.lessThan(SimplexContinuousNoise.FUNCTION.apply(x), threshold)) //
        .findFirst() //
        .orElseThrow();
  }
}
