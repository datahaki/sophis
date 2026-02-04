// code by jph
package ch.alpine.sophis.dv;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.dv.Biinvariant;
import ch.alpine.sophis.dv.Biinvariants;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.var.InversePowerVariogram;

class BiinvariantsTest {
  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    Serialization.copy(Biinvariants.all(RGroup.INSTANCE));
  }

  @Test
  void testDistanceSequenceNullFail() {
    for (Biinvariant biinvariant : Biinvariants.all(RGroup.INSTANCE).values())
      assertThrows(Exception.class, () -> biinvariant.distances(null));
  }

  @Test
  void testVarDistVariogramNullFail() {
    for (Biinvariant biinvariant : Biinvariants.all(RGroup.INSTANCE).values())
      assertThrows(Exception.class, () -> biinvariant.var_dist(null, Tensors.empty()));
  }

  @Test
  void testWeightingVariogramNullFail() {
    for (Biinvariant biinvariant : Biinvariants.all(RGroup.INSTANCE).values())
      assertThrows(Exception.class, () -> biinvariant.weighting(null, Tensors.empty()));
  }

  @Test
  void testCoordinateVariogramNullFail() {
    for (Biinvariant biinvariant : Biinvariants.all(RGroup.INSTANCE).values())
      assertThrows(Exception.class, () -> biinvariant.coordinate(null, Tensors.empty()));
  }

  @Test
  void testCoordinateSequenceNullFail() {
    for (Biinvariant biinvariant : Biinvariants.all(RGroup.INSTANCE).values())
      assertThrows(Exception.class, () -> biinvariant.coordinate(InversePowerVariogram.of(2), null));
  }

  @Test
  void testSerializationFail() throws ClassNotFoundException, IOException {
    // in earlier versions, the instance used to be non-serializable
    for (Biinvariant biinvariant : Biinvariants.all(RGroup.INSTANCE).values())
      Serialization.copy(biinvariant.coordinate(InversePowerVariogram.of(2), Tensors.empty()));
  }
}
