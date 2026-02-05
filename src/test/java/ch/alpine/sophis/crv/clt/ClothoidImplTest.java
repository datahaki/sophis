// code by jph
package ch.alpine.sophis.crv.clt;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.crv.clt.par.ClothoidIntegral;
import ch.alpine.sophis.crv.clt.par.ClothoidIntegration;
import ch.alpine.sophis.crv.clt.par.ClothoidIntegrations;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;

class ClothoidImplTest {
  @Test
  void testSimple() {
    LagrangeQuadratic lagrangeQuadratic = LagrangeQuadratic.interp(Pi.HALF, Pi.TWO, Pi.VALUE);
    for (ClothoidIntegration clothoidIntegration : ClothoidIntegrations.values()) {
      ClothoidIntegral clothoidIntegral = clothoidIntegration.clothoidIntegral(lagrangeQuadratic);
      assertThrows(Exception.class, () -> new ClothoidImpl(null, clothoidIntegral, Tensors.vector(1, 2)));
    }
  }

  @Test
  void testFinal() {
    assertTrue(Modifier.isFinal(ClothoidImpl.class.getModifiers()));
  }
}
