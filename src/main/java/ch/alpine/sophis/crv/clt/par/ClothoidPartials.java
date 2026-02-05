package ch.alpine.sophis.crv.clt.par;

import java.io.Serializable;

import ch.alpine.tensor.ComplexScalar;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.erf.Erfi;
import ch.alpine.tensor.sca.exp.Exp;
import ch.alpine.tensor.sca.pow.Sqrt;

enum ClothoidPartials {
  INSTANCE;

  /* package */ class Degree0 implements ClothoidPartial, Serializable {
    private final Scalar factor;

    public Degree0(Scalar c0) {
      factor = Exp.FUNCTION.apply(ComplexScalar.I.multiply(c0));
    }

    @Override // from Partial
    public Scalar il(Scalar t) {
      return t.multiply(factor);
    }
  }

  /* package */ class Degree1 implements ClothoidPartial, Serializable {
    private final Scalar c0;
    private final Scalar c1;
    private final Scalar factor;
    private final Scalar ofs;

    public Degree1(Scalar c0, Scalar c1) {
      this.c0 = c0;
      this.c1 = c1;
      factor = ComplexScalar.I.divide(c1);
      ofs = Exp.FUNCTION.apply(ComplexScalar.I.multiply(c0));
    }

    @Override // from Partial
    public Scalar il(Scalar t) {
      Scalar ofs2 = Exp.FUNCTION.apply(ComplexScalar.I.multiply(c1.multiply(t).add(c0)));
      return ofs.subtract(ofs2).multiply(factor);
    }
  }

  /* package */ class Degree2 implements ClothoidPartial, Serializable {
    // TODO SOPHIS this seems redundant to DIAG = {sqrt(1/2),sqrt(1/2)}
    private static final Scalar _N1_1_4 = ComplexScalar.of(+0.7071067811865476, 0.7071067811865475);
    private static final Scalar _N1_3_4 = ComplexScalar.of(-0.7071067811865475, 0.7071067811865476);
    private static final Scalar _1_4 = RationalScalar.of(1, 4);
    // ---
    private final Scalar c1;
    private final Scalar c2;
    private final Scalar f4;
    private final Scalar factor;
    private final Scalar ofs;

    public Degree2(Scalar c0, Scalar c1, Scalar c2) {
      this.c1 = c1;
      this.c2 = c2;
      Scalar f1 = _N1_3_4;
      Scalar f2 = Exp.FUNCTION.apply(c0.subtract(_1_4.multiply(c1).multiply(c1).divide(c2)).multiply(ComplexScalar.I));
      Scalar f3 = Sqrt.FUNCTION.apply(Pi.VALUE);
      f4 = RationalScalar.HALF.divide(Sqrt.FUNCTION.apply(c2));
      factor = Times.of(f1, f2, f3, f4);
      ofs = Erfi.FUNCTION.apply(_N1_1_4.multiply(c1).multiply(f4));
    }

    @Override // from ClothoidIntegral
    public Scalar il(Scalar t) {
      Scalar c2t = c2.multiply(t);
      Scalar ofs2 = Erfi.FUNCTION.apply(_N1_1_4.multiply(c1.add(c2t).add(c2t)).multiply(f4));
      return ofs.subtract(ofs2).multiply(factor);
    }
  }
}
