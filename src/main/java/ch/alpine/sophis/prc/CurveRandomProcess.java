// code by jph
package ch.alpine.sophis.prc;

import java.io.Serializable;
import java.util.stream.Stream;

import ch.alpine.sophus.api.Manifold;
import ch.alpine.sophus.api.TangentSpace;
import ch.alpine.sophus.math.FrobeniusForm;
import ch.alpine.sophus.rsm.LocalRandomSample;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.pi.LinearSubspace;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;

public class CurveRandomProcess implements Serializable {
  public static Stream<Tensor> stream(Manifold manifold, Scalar sigma, Tensor p) {
    return Stream.generate(new CurveRandomProcess(manifold, sigma, p)::next);
  }

  // ---
  private final Manifold manifold;
  private final Scalar sigma;
  private TangentSpace tangentSpace;
  private Tensor v;

  public CurveRandomProcess(Manifold manifold, Scalar sigma, Tensor p) {
    this.manifold = manifold;
    this.sigma = sigma;
    tangentSpace = manifold.tangentSpace(p);
    RandomSampleInterface rsi = LocalRandomSample.of(tangentSpace, sigma);
    Tensor q = RandomSample.of(rsi);
    Tensor pq = tangentSpace.log(q);
    // TODO this is a temporary hack
    Scalar norm = FrobeniusForm.INSTANCE.norm(pq);
    v = pq.multiply(sigma.divide(norm));
  }

  public Tensor next() {
    Tensor p = tangentSpace.exp(v);
    tangentSpace = manifold.tangentSpace(p);
    TensorUnaryOperator tuo = tangentSpace.isTangentQ()::defect;
    LinearSubspace linearSubspace = LinearSubspace.of(tuo, Dimensions.of(v));
    Distribution d = NormalDistribution.of(sigma.zero(), sigma.divide(RealScalar.of(linearSubspace.dimensions())));
    Tensor dv = RandomVariate.of(d, Dimensions.of(v));
    v = linearSubspace.projection(v.add(dv));
    Scalar norm = FrobeniusForm.INSTANCE.norm(v);
    v = v.multiply(sigma.divide(norm));
    return p;
  }
}
