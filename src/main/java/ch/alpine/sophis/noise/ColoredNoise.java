// code adapted by ob
package ch.alpine.sophis.noise;

import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.io.MathematicaFormat;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

/* Adapted version of 'PinkNoise.java' to 'ColoredNoise.java'
 *
 * Copyright (c) 2008, Sampo Niskanen <sampo.niskanen@iki.fi>
 * All rights reserved.
 * Source: http://www.iki.fi/sampo.niskanen/PinkNoise/
 *
 * https://github.com/cr/PolynApp/blob/master/src/org/codelove/polynapp/PinkNoise.java */
/** A class that provides a source of pink noise with a power spectrum density
 * (PSD) proportional to 1/f^alpha. "Regular" pink noise has a PSD proportional
 * to 1/f, i.e. alpha=1. However, many natural systems may require a different
 * PSD proportionality. The value of alpha may be from 0 to 2, inclusive. The
 * special case alpha=0 results in white noise (directly generated random
 * numbers) and alpha=2 results in brown noise (integrated white noise).
 * <p>
 * The values are computed by applying an IIR filter to generated Gaussian
 * random numbers. The number of poles used in the filter may be specified. For
 * each number of poles there is a limiting frequency below which the PSD
 * becomes constant. Values as low as 1-3 poles produce relatively good results,
 * however these values will be concentrated near zero. Using a larger number of
 * poles will allow more low frequency components to be included, leading to
 * more variation from zero. However, the sequence is stationary, that is, it
 * will always return to zero even with a large number of poles.
 * <p>
 * The distribution of values is very close to Gaussian with mean zero, but the
 * variance depends on the number of poles used. The algorithm can be made
 * faster by changing the method call <code> rnd.nextGaussian() </code> to
 * <code> rnd.nextDouble()-0.5 </code> in the method {@link #nextValue()}. The
 * resulting distribution is almost Gaussian, but has a relatively larger amount
 * of large values.
 * <p>
 * The IIR filter used by this class is presented by N. Jeremy Kasdin,
 * Proceedings of the IEEE, Vol. 83, No. 5, May 1995, p. 822.
 * 
 * @author Sampo Niskanen <sampo.niskanen@iki.fi> */
public class ColoredNoise implements Distribution {
  /** Generate a specific colored noise using a five-pole IIR.
   * 
   * @param alpha: the exponent of the colored noise, 1/f^alpha.
   * @param alpha = -2: Violet Noise
   * @param alpha = -1: Blue Noise
   * @param alpha = 0: White Noise
   * @param alpha = 1: Pink Noise
   * @param alpha = 2: Brownian Noise
   * @throws IllegalArgumentException: if <code>alpha < 0</code> or <code>alpha > 2</code>. */
  public static Distribution of(Scalar alpha, RandomGenerator randomGenerator) {
    return new ColoredNoise(alpha, 5, ThreadLocalRandom.current());
  }

  public static Distribution of(Scalar alpha) {
    return of(alpha, ThreadLocalRandom.current());
  }

  private final Scalar alpha;
  private final Tensor multi;
  private Tensor values;

  /** Generate colored noise specifying alpha and the number of poles. The larger
   * the number of poles, the lower are the lowest frequency components that
   * are amplified.
   * 
   * 
   * @param alpha: the exponent of the colored noise, 1/f^alpha.
   * @param alpha = -2: Violet Noise
   * @param alpha = -1: Blue Noise
   * @param alpha = 0: White Noise
   * @param alpha = 1: Pink Noise
   * @param alpha = 2: Brownian Noise
   * @param poles: the number of poles to use.
   * @throws IllegalArgumentException: if <code>alpha < 0</code> or <code>alpha > 2</code>. */
  private ColoredNoise(Scalar alpha, int poles, RandomGenerator randomGenerator) {
    this.alpha = alpha;
    multi = Tensors.empty();
    this.values = Array.zeros(poles);
    Scalar a = RealScalar.ONE;
    for (int i = 0; i < poles; ++i) {
      a = RealScalar.of(i).subtract(alpha.multiply(Rational.HALF)).multiply(a).divide(RealScalar.of(i + 1));
      multi.append(a);
    }
    // Fill the history with random values
    for (int i = 0; i < 5 * poles; ++i)
      randomVariate(randomGenerator);
  }

  /** @return the next pink noise sample. */
  @Override
  public Scalar randomVariate(RandomGenerator randomGenerator) {
    Scalar x = RandomVariate.of(NormalDistribution.standard(), randomGenerator).subtract(multi.dot(values));
    values = Join.of(Tensors.of(x), values.extract(0, values.length() - 1));
    return x;
  }

  @Override
  public String toString() {
    return MathematicaFormat.concise("ColoredNoise", alpha, multi);
  }
}
