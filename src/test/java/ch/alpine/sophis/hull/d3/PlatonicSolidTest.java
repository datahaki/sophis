// code by jph
package ch.alpine.sophis.hull.d3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.red.Mean;

class PlatonicSolidTest {
  @ParameterizedTest
  @EnumSource
  void testStructure(PlatonicSolid platonicSolid) {
    List<int[]> faces = platonicSolid.faces();
    assertEquals(faces.size(), platonicSolid.faceSize());
    assertEquals(faces.stream().map(a -> a.length).distinct().toList(), List.of(platonicSolid.faceShape()));
  }

  @ParameterizedTest
  @EnumSource
  void testCenter(PlatonicSolid platonicSolid) {
    Tolerance.CHOP.requireAllZero(Mean.of(platonicSolid.vertices()));
  }
}
