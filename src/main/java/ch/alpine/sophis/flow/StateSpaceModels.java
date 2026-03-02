// code by bapaden and jph
package ch.alpine.sophis.flow;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Join;
import ch.alpine.tensor.ext.Integers;

/** the name "single" hints that if the state is (position) then control u acts as (velocity).
 * 
 * implementation for arbitrary dimensions
 * 
 * | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 |
 * | f(x_1, u) - f(x_2, u) | == | u - u | == 0
 * therefore Lipschitz L == 0
 * 
 * see also {@link DoubleIntegratorStateSpaceModel} */
public enum StateSpaceModels implements StateSpaceModel {
  SINGLE_INTEGRATOR {
    /** f(x, u) == u */
    @Override
    public Tensor f(Tensor x, Tensor u) {
      return u;
    }
  },
  /** the name "double" hints that if the state is (position, velocity) then
   * control u acts as (acceleration).
   * 
   * implementation for linear coordinate system R^n
   * 
   * see also {@link IntegratorStateSpaceModels}
   * 
   * Lipschitz L == 1 */
  DOUBLE_INTEGRATOR {
    /** f((p, v), u) == (v, u) */
    @Override
    public Tensor f(Tensor x, Tensor u) {
      int xlength = x.length();
      int ulength = u.length();
      Integers.requireEquals(xlength, ulength + ulength);
      Tensor v = x.extract(ulength, xlength);
      return Join.of(v, u);
    }
  }
}
