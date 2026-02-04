// code by jph
package ch.alpine.sophis.itp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.itp.RidgeRegression.Form2;
import ch.alpine.sophus.hs.Manifold;
import ch.alpine.sophus.lie.LieGroup;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.PositiveDefiniteMatrixQ;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;

class RidgeRegressionTest {
  public static Collection<TensorUnaryOperator> of(LieGroup lieGroup, Tensor g) {
    return List.of(lieGroup.actionL(g), lieGroup.actionR(g), lieGroup.conjugation(g), lieGroup::invert);
  }

  // private static final LieGroupOps LIE_GROUP_OPS = new LieGroupOps();
  @Test
  void testSe2C() {
    Distribution distribution = UniformDistribution.of(-10, +10);
    Manifold manifold = Se2CoveringGroup.INSTANCE;
    RidgeRegression ridgeRegression = new RidgeRegression(manifold);
    for (int count = 4; count < 10; ++count) {
      Tensor sequence = RandomVariate.of(distribution, count, 3);
      for (Tensor point : sequence) {
        Form2 form2 = ridgeRegression.new Form2(sequence, point);
        Tensor sigma_inverse = form2.sigma_inverse();
        assertTrue(PositiveDefiniteMatrixQ.ofHermitian(sigma_inverse));
      }
      {
        Tensor point = RandomVariate.of(distribution, 3);
        ridgeRegression.new Form2(sequence, point).leverages();
        Tensor shift = RandomVariate.of(distribution, 3);
        for (TensorUnaryOperator tensorMapping : of(Se2CoveringGroup.INSTANCE, shift)) {
          Tensor all = Tensor.of(sequence.stream().map(tensorMapping));
          Tensor one = tensorMapping.apply(point);
          ridgeRegression.new Form2(all, one).leverages();
          // System.out.println(Chop._05.close(l1, l2));
        }
      }
    }
  }
}
