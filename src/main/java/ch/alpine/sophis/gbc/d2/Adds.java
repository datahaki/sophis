// code by jph
package ch.alpine.sophis.gbc.d2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Unprotect;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.ext.Cache;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.mat.IdentityMatrix;

/** performs matrix [1 1 0 0 0; 0 1 1 0 0; ... ] multiplication without
 * the need to build matrix */
enum Adds {
  ;
  /** @param tensor non-empty
   * @return */
  public static Tensor forward(Tensor tensor) {
    // TODO scale matrix to have |EV| lEq 1 !?
    List<Tensor> list = new ArrayList<>(tensor.length());
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor prev = iterator.next();
    Tensor _1st = prev;
    while (iterator.hasNext())
      list.add(prev.add(prev = iterator.next()).multiply(Rational.HALF));
    list.add(prev.add(_1st).multiply(Rational.HALF));
    Integers.requireEquals(tensor.length(), list.size());
    return Unprotect.using(list);
  }

  /** @param tensor non-empty
   * @return */
  public static Tensor reverse(Tensor tensor) {
    List<Tensor> list = new ArrayList<>(tensor.length());
    Iterator<Tensor> iterator = tensor.iterator();
    for (Tensor prev = Last.of(tensor); iterator.hasNext();)
      list.add(prev.add(prev = iterator.next()).multiply(Rational.HALF));
    Integers.requireEquals(tensor.length(), list.size());
    return Unprotect.using(list);
  }

  // ---
  private static final int CACHE_SIZE = 32;
  private static final Function<Integer, Tensor> FORWARD = Cache.of(Adds::build_forward, CACHE_SIZE);
  private static final Function<Integer, Tensor> REVERSE = Cache.of(Adds::build_reverse, CACHE_SIZE);

  /** @param n strictly positive
   * @return */
  public static Tensor matrix_forward(int n) {
    return FORWARD.apply(Integers.requirePositive(n));
  }

  /** @param n strictly positive
   * @return */
  public static Tensor matrix_reverse(int n) {
    return REVERSE.apply(Integers.requirePositive(n));
  }

  private static Tensor build_forward(int n) {
    return Tensor.of(IdentityMatrix.of(n).stream().map(Adds::forward));
  }

  private static Tensor build_reverse(int n) {
    return Tensor.of(IdentityMatrix.of(n).stream().map(Adds::reverse));
  }
}
