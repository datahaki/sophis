// code by jph
package ch.alpine.sophis.ref.d1h;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.mat.Tolerance;

class Hermite3SubdivisionsTest {
  @Test
  void testStandardCompare() {
    new HermiteSubdivisionQ(RnHermite3Subdivisions.standard()).check( //
        Hermite3Subdivisions.of(RGroup.INSTANCE, Tolerance.CHOP));
  }

  @Test
  void testA1Compare() {
    new HermiteSubdivisionQ(RnHermite3Subdivisions.a1()).check(Hermite3Subdivisions.a1(RGroup.INSTANCE, Tolerance.CHOP));
  }

  @Test
  void testA2Compare() {
    new HermiteSubdivisionQ(RnHermite3Subdivisions.a2()).check(Hermite3Subdivisions.a2(RGroup.INSTANCE, Tolerance.CHOP));
  }

  @Test
  void testTension() {
    HermiteHiConfig hermiteHiParam = new HermiteHiConfig(Rational.of(2, 157), Rational.of(1, 9));
    HermiteSubdivision hermiteSubdivision = Hermite3Subdivisions.of(RGroup.INSTANCE, Tolerance.CHOP, hermiteHiParam);
    new HermiteSubdivisionQ(RnHermite3Subdivisions.of(hermiteHiParam)).check(hermiteSubdivision);
    new HermiteSubdivisionQ(hermiteSubdivision).checkP(3);
  }

  @Test
  void testH1() {
    Scalar theta = RealScalar.ZERO;
    Scalar omega = RealScalar.ZERO;
    new HermiteSubdivisionQ(Hermite1Subdivisions.standard(RGroup.INSTANCE)).check( //
        Hermite3Subdivisions.of(RGroup.INSTANCE, Tolerance.CHOP, new HermiteHiConfig(theta, omega)));
    new HermiteSubdivisionQ(Hermite1Subdivisions.standard(RGroup.INSTANCE)).check( //
        Hermite3Subdivisions.of(RGroup.INSTANCE, Tolerance.CHOP, new HermiteHiConfig(theta, omega)));
  }

  @Test
  void testP1() {
    new HermiteSubdivisionQ(Hermite3Subdivisions.a1(RGroup.INSTANCE, Tolerance.CHOP)).checkP(1);
    new HermiteSubdivisionQ(Hermite3Subdivisions.a2(RGroup.INSTANCE, Tolerance.CHOP)).checkP(1);
  }
}
