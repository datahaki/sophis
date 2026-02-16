// code by jph
package ch.alpine.sophis.dv;

import ch.alpine.sophis.math.Genesis;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.math.api.Manifold;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.qty.Timing;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

/* package */ enum LeveragesCoordinateDemo {
  ;
  static void main() {
    {
      Manifold manifold = RGroup.INSTANCE;
      ScalarUnaryOperator variogram = InversePowerVariogram.of(2);
      BarycentricCoordinate c1 = LeveragesCoordinate.of(manifold, variogram);
      Timing t1 = Timing.stopped();
      Timing t2 = Timing.stopped();
      for (int count = 0; count < 1000; ++count) {
        Tensor sequence = RandomVariate.of(NormalDistribution.standard(), 100, 3);
        Tensor point = RandomVariate.of(NormalDistribution.standard(), 3);
        t1.start();
        c1.weights(sequence, point);
        t1.stop();
      }
      System.out.println(t1.seconds());
      System.out.println(t2.seconds());
    }
    {
      Manifold manifold = RGroup.INSTANCE;
      Genesis genesis = new LeveragesBiinvariant(manifold);
      BarycentricCoordinate w1 = new HsCoordinates(manifold, genesis);
      Timing t1 = Timing.stopped();
      Timing t2 = Timing.stopped();
      for (int count = 0; count < 1000; ++count) {
        Tensor sequence = RandomVariate.of(NormalDistribution.standard(), 100, 3);
        Tensor point = RandomVariate.of(NormalDistribution.standard(), 3);
        t1.start();
        w1.weights(sequence, point);
        t1.stop();
      }
      System.out.println(t1.seconds());
      System.out.println(t2.seconds());
    }
  }
}
