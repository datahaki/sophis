// code by jph
package ch.alpine.sophis.ref.d2;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.RandomGenerator;

import ch.alpine.sophus.hs.r3s2.R3S2Geodesic;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.Throw;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.io.Put;
import ch.alpine.tensor.nrm.Normalize;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.red.Nest;
import ch.alpine.tensor.sca.pow.Sqrt;

enum CatmullClarkSubdivisionExport {
  ;
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Vector2Norm::of);

  private static Tensor univariate() {
    Distribution distribution = NormalDistribution.standard();
    GeodesicCatmullClarkSubdivision catmullClarkSubdivision = new GeodesicCatmullClarkSubdivision(RGroup.INSTANCE);
    Tensor tensor = RandomVariate.of(distribution, 4, 5);
    return Nest.of(catmullClarkSubdivision::refine, tensor, 3);
  }

  private static Tensor se2() {
    GeodesicCatmullClarkSubdivision catmullClarkSubdivision = new GeodesicCatmullClarkSubdivision(Se2Group.INSTANCE);
    RandomGenerator random = ThreadLocalRandom.current();
    Tensor tensor = Tensors.matrix((i, j) -> Tensors.vector( //
        i + random.nextGaussian() * .2, //
        j + random.nextGaussian() * .2, //
        0 + random.nextGaussian() * .4 //
    ), 5, 6);
    return Nest.of(catmullClarkSubdivision::refine, tensor, 4);
  }

  private static Tensor r3s2() {
    GeodesicCatmullClarkSubdivision catmullClarkSubdivision = new GeodesicCatmullClarkSubdivision(R3S2Geodesic.INSTANCE);
    RandomGenerator random = new Random();
    Tensor tensor = Tensors.matrix((i, j) -> Tensors.of( //
        Tensors.vector( //
            i + random.nextGaussian() * .4, //
            j + random.nextGaussian() * .4, //
            0 + random.nextGaussian() * .4 //
        ), //
        NORMALIZE.apply(Tensors.vector( //
            random.nextDouble() * 0.5, //
            random.nextDouble() * 0.5, //
            random.nextDouble() + .5//
        )) //
    ), 4, 5);
    return Nest.of(catmullClarkSubdivision::refine, tensor, 3);
  }

  private static Tensor r3s2_sp(Number x, Number y) {
    return r3s2_sp(RealScalar.of(x), RealScalar.of(y));
  }

  private static Tensor r3s2_sp(Scalar x, Scalar y) {
    Scalar z = Sqrt.FUNCTION.apply(RealScalar.ONE.subtract(x.multiply(x)).subtract(y.multiply(y)));
    if (z instanceof RealScalar)
      return Tensors.of(x, y, z);
    throw new Throw(z);
  }

  private static Tensor r3s2_sphere() {
    GeodesicCatmullClarkSubdivision catmullClarkSubdivision = new GeodesicCatmullClarkSubdivision(R3S2Geodesic.INSTANCE);
    Tensor tensor = Tensors.matrix((i, j) -> Tensors.of( //
        r3s2_sp( //
            (i - 1) * .7, //
            (j - 1) * .7), //
        r3s2_sp( //
            (i - 1) * .7, //
            (j - 1) * .7)) //
        , 3, 3);
    return Nest.of(catmullClarkSubdivision::refine, tensor, 3);
  }

  static void main() throws IOException {
    Path path = HomeDirectory.Ephemeral.mk_dirs(CatmullClarkSubdivisionExport.class.getSimpleName());
    Put.of(path.resolve("grid.mathematica"), univariate());
    Put.of(path.resolve("se2.mathematica"), se2());
    Put.of(path.resolve("r3s2.mathematica"), r3s2());
    Put.of(path.resolve("sphere.mathematica"), r3s2_sphere());
  }
}
