// code by jph
package ch.alpine.sophis.flow;

import ch.alpine.tensor.Rational;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** 2nd order RungeKutta
 * integrator requires 2 flow evaluations
 * 
 * Numerical Recipes 3rd Edition (17.1.2)
 * 
 * "Numerical Time-Integration Methods" */
public enum TimeIntegrators implements TimeIntegrator {
  EULER {
    @Override
    public Tensor step(StateSpaceModel stateSpaceModel, Tensor x, Tensor u, Scalar h) {
      return x.add(stateSpaceModel.f(x, u).multiply(h));
    }
  },
  MIDPOINT {
    @Override
    public Tensor step(StateSpaceModel stateSpaceModel, Tensor x0, Tensor u, Scalar _2h) {
      Scalar h = _2h.multiply(Rational.HALF);
      Tensor xm = x0.add(stateSpaceModel.f(x0, u).multiply(h)); // h
      return /**/ x0.add(stateSpaceModel.f(xm, u).multiply(_2h)); // 2h
    }
  },
  /** fourth-order Runge-Kutta formula
   * integrator requires 4 flow evaluations
   * 
   * Numerical Recipes 3rd Edition (17.1.3) */
  RK4 {
    private static final Scalar HALF = Rational.HALF;
    private static final Scalar THIRD = Rational.THIRD;
    private static final Scalar SIXTH = Rational.of(1, 6);

    @Override
    public Tensor step(StateSpaceModel stateSpaceModel, Tensor x, Tensor u, Scalar h) {
      Tensor k1 = stateSpaceModel.f(x, u).multiply(h); // euler increment
      Tensor k2 = stateSpaceModel.f(x.add(k1.multiply(HALF)), u).multiply(h);
      Tensor k3 = stateSpaceModel.f(x.add(k2.multiply(HALF)), u).multiply(h);
      Tensor k4 = stateSpaceModel.f(x.add(k3), u).multiply(h);
      return x.add(k1.add(k4).multiply(SIXTH).add(k2.add(k3).multiply(THIRD)));
    }
  },
  /** fifth-order Runge-Kutta formula based on RK4
   * implementation requires 11 flow evaluations
   * 
   * Numerical Recipes 3rd Edition (17.2.3) */
  RK45 {
    private static final Scalar HALF = Rational.HALF;
    private static final Scalar THIRD = Rational.THIRD;
    private static final Scalar SIXTH = Rational.of(1, 6);
    // ---
    private static final Scalar W1 = Rational.of(-1, 15);
    private static final Scalar W2 = Rational.of(16, 15);

    @Override
    public Tensor step(StateSpaceModel stateSpaceModel, Tensor x, Tensor u, Scalar h) {
      Tensor y1;
      final Tensor flow_at_x = stateSpaceModel.f(x, u); // used twice
      {
        Tensor k1 = flow_at_x.multiply(h); // euler increment
        Tensor k2 = stateSpaceModel.f(x.add(k1.multiply(HALF)), u).multiply(h);
        Tensor k3 = stateSpaceModel.f(x.add(k2.multiply(HALF)), u).multiply(h);
        Tensor k4 = stateSpaceModel.f(x.add(k3), u).multiply(h);
        y1 = k1.add(k4).multiply(SIXTH).add(k2.add(k3).multiply(THIRD));
      }
      Scalar h2 = h.multiply(HALF);
      Tensor xm;
      {
        Tensor k1 = flow_at_x.multiply(h2); // euler increment
        Tensor k2 = stateSpaceModel.f(x.add(k1.multiply(HALF)), u).multiply(h2);
        Tensor k3 = stateSpaceModel.f(x.add(k2.multiply(HALF)), u).multiply(h2);
        Tensor k4 = stateSpaceModel.f(x.add(k3), u).multiply(h2);
        Tensor incr = k1.add(k4).multiply(SIXTH).add(k2.add(k3).multiply(THIRD));
        xm = x.add(incr);
      }
      Tensor y2 = RK4.step(stateSpaceModel, xm, u, h2).subtract(x);
      Tensor ya = y1.multiply(W1).add(y2.multiply(W2));
      return x.add(ya);
    }
  }
}
