// code by jph
package showcase;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import ch.alpine.sophus.hs.HomogeneousSpace;
import ch.alpine.sophus.hs.gr.Grassmannian;
import ch.alpine.sophus.hs.h.Hyperboloid;
import ch.alpine.sophus.hs.s.Sphere;
import ch.alpine.sophus.hs.st.StiefelManifold;
import ch.alpine.sophus.lie.rn.RnGroup;
import ch.alpine.sophus.lie.se.SeNGroup;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.sophus.lie.so.SoNGroup;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;

class SampleManifolds {
  public static List<HomogeneousSpace> homogeneousSpaces() {
    return Arrays.asList( //
        new StiefelManifold(3, 1), //
        new Grassmannian(5, 2), //
        new RnGroup(3), //
        Se2Group.INSTANCE, //
        Se2CoveringGroup.INSTANCE, //
        new SeNGroup(3), //
        new SeNGroup(4), //
        new SoNGroup(2), //
        new SoNGroup(3), //
        new SoNGroup(4), //
        new Sphere(2), //
        new Sphere(3), //
        new Hyperboloid(2), //
        new Hyperboloid(3), //
        new Hyperboloid(4) //
    );
  }

  @ParameterizedTest
  @MethodSource("homogeneousSpaces")
  void testSimple(HomogeneousSpace homogeneousSpace) {
    RandomSampleInterface rsi = (RandomSampleInterface) homogeneousSpace;
    Tensor p = RandomSample.of(rsi);
    homogeneousSpace.exponential(p);
  }
}
