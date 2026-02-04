// code by jph
package ch.alpine.sophis.fit;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import ch.alpine.sophis.dv.Sedarim;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Transpose;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.ArgMin;
import ch.alpine.tensor.ext.Int;
import ch.alpine.tensor.num.RandomPermutation;

/** TODO SOPHIS API does not enforce mandatory initialization of class, i.e setSeeds */
public class KMeans {
  private final Sedarim sedarim;
  private final TensorUnaryOperator mean;
  private final Tensor sequence;
  // ---
  private Tensor seeds = null;
  private Integer[] labels;

  /** @param sedarim typically a notion of distance between a given point to all points in given sequence
   * @param mean to establish geometric center of a subset of points in given sequence
   * @param sequence */
  public KMeans(Sedarim sedarim, TensorUnaryOperator mean, Tensor sequence) {
    this.sedarim = sedarim;
    this.mean = mean;
    this.sequence = sequence;
  }

  /** @param seeds */
  public void setSeeds(Tensor seeds) {
    this.seeds = seeds;
  }

  public void setSeeds(int k, RandomGenerator randomGenerator) {
    setSeeds(randomChoice(k, randomGenerator));
  }

  public void setSeeds(int k) {
    setSeeds(k, ThreadLocalRandom.current());
  }

  private Tensor randomChoice(int k, RandomGenerator randomGenerator) {
    Tensor unique = Tensor.of(sequence.stream().distinct());
    return Tensor.of(IntStream.of(RandomPermutation.of(unique.length())) //
        .limit(k) //
        .mapToObj(unique::get));
  }

  /** need to set seeds before calling this */
  public void iterate() {
    // Tensor prev = seeds.copy();
    // compute distance matrix: sequence X seeds
    Tensor tensor = Transpose.of(Tensor.of(seeds.stream().map(sedarim::sunder)));
    // match all points in sequence to the "closest" seed
    labels = tensor.stream().map(ArgMin::of).toArray(Integer[]::new);
    // create redundant data structure
    Tensor partition = partition();
    // update seeds as mean of points in subset
    seeds = Tensor.of(partition.stream().filter(subset -> 0 < subset.length()).map(mean));
    // fill up seeds to previous size
    randomChoice(partition.length() - seeds.length(), ThreadLocalRandom.current()).forEach(seeds::append);
    // Scalar scalar = FrobeniusNorm.of(prev.subtract(seeds));
    // IO.println(scalar);
  }

  public int complete() {
    int count = 0;
    if (Objects.isNull(labels)) {
      iterate();
      ++count;
    }
    Tensor prev;
    Tensor next = Tensors.vector(labels);
    do {
      prev = next;
      iterate();
      ++count;
      next = Tensors.vector(labels);
    } while (!prev.equals(next));
    return count;
  }

  public Tensor partition() {
    Tensor partition = Array.of(_ -> Tensors.empty(), seeds.length());
    Int i = new Int();
    sequence.forEach(point -> partition.set(entry -> entry.append(point), labels[i.getAndIncrement()]));
    return partition;
  }

  /** @return array of sequence.length() with values from {0, 1, ..., seeds.length-1} */
  public Integer[] labels() {
    return labels;
  }

  /** @return k-means */
  public Tensor seeds() {
    return seeds.unmodifiable();
  }
}
