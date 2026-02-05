// code by jph
package ch.alpine.sophis.crv.d2.alg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.dv.AffineCoordinate;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Reverse;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.lie.rot.CirclePoints;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.nrm.NormalizeTotal;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.exp.Exp;

class OriginEnclosureQTest {
  @Test
  void testInsidePlain() {
    Tensor polygon = Tensors.matrix(new Number[][] { //
        { 0.1, 0.1 }, //
        { 1, 0 }, //
        { 1, 1 }, //
        { 0, 1 } //
    });
    assertFalse(OriginEnclosureQ.INSTANCE.isMember(polygon));
    for (int n = 3; n < 10; ++n) {
      assertTrue(OriginEnclosureQ.INSTANCE.isMember(CirclePoints.of(n)));
      assertTrue(OriginEnclosureQ.INSTANCE.isMember(Reverse.of(CirclePoints.of(n))));
    }
  }

  /** @param unit non-null
   * @return operator that maps a scalar to the quantity with value
   * of given scalar and given unit
   * @throws Exception if given unit is null */
  public static ScalarUnaryOperator attach(Unit unit) {
    Objects.requireNonNull(unit);
    return scalar -> Quantity.of(scalar, unit);
  }

  @Test
  void testInsidePlainQuantity() {
    ScalarUnaryOperator suo = attach(Unit.of("km"));
    Tensor polygon = Tensors.matrix(new Number[][] { //
        { 0.1, 0.1 }, //
        { 1, 0 }, //
        { 1, 1 }, //
        { 0, 1 } //
    });
    assertFalse(OriginEnclosureQ.INSTANCE.isMember(polygon.map(suo)));
    for (int n = 3; n < 10; ++n) {
      assertTrue(OriginEnclosureQ.INSTANCE.isMember(CirclePoints.of(n).map(suo)));
      assertTrue(OriginEnclosureQ.INSTANCE.isMember(Reverse.of(CirclePoints.of(n)).map(suo)));
    }
  }

  @Test
  void testSome() {
    Tensor asd = Tensors.vector(2, 3, 4, 5);
    asd.set(RealScalar.of(8), 1);
    assertEquals(asd.Get(1), RealScalar.of(8));
    List<Integer> list = new ArrayList<>();
    list.add(6);
    list.add(2);
    list.add(3);
    list.add(9);
    list.get(1).longValue();
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> OriginEnclosureQ.INSTANCE.isMember(IdentityMatrix.of(4)));
  }

  @Test
  void testCPointers() {
    {
      String wer = "asdf";
      String wer2 = wer;
      wer = "987345"; // does not change wer2
      assertNotEquals(wer2, wer);
    }
    {
      Tensor wo = Tensors.vector(2, 3, 4, 5);
      Tensor wo2 = wo;
      wo = Tensors.vector(9, 9); // does not change wo2
      assertNotEquals(wo, wo2);
    }
  }

  @Test
  void testIterateR2() {
    Distribution distribution = UniformDistribution.of(-1, 5);
    RandomGenerator randomGenerator = new Random(3);
    int d = 2;
    for (int n = 4; n < 10; ++n)
      for (int count = 0; count < 10; ++count) {
        Tensor levers = RandomVariate.of(distribution, randomGenerator, n, d);
        if (OriginEnclosureQ.INSTANCE.isMember(levers)) {
          for (int i = 0; i < 3; ++i) {
            Tensor weights = AffineCoordinate.INSTANCE.origin(levers);
            weights = NormalizeTotal.FUNCTION.apply(weights.map(Exp.FUNCTION));
            levers = Times.of(weights, levers);
          }
        }
      }
  }

  @Test
  void testScalarFail() {
    assertThrows(Exception.class, () -> FranklinPnpoly.isInside(RealScalar.of(2), Tensors.vector(0.5, .5)));
  }
}
