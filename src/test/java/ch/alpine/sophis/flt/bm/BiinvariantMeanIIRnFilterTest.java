// code by jph
package ch.alpine.sophis.flt.bm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import ch.alpine.sophis.crv.MonomialExtrapolationMask;
import ch.alpine.sophis.flt.WindowSideExtrapolation;
import ch.alpine.sophis.flt.bm.BiinvariantMeanIIRnFilter;
import ch.alpine.sophus.bm.LinearBiinvariantMean;
import ch.alpine.sophus.lie.rn.RGroup;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Range;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.chq.ExactTensorQ;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.win.WindowFunctions;

class BiinvariantMeanIIRnFilterTest {
  @Test
  void testSimple() {
    for (int radius = 0; radius < 6; ++radius) {
      TensorUnaryOperator tensorUnaryOperator = BiinvariantMeanIIRnFilter.of( //
          LinearBiinvariantMean.INSTANCE, MonomialExtrapolationMask.INSTANCE, RGroup.INSTANCE, radius, RationalScalar.HALF);
      Tensor signal = Range.of(0, 10);
      Tensor tensor = tensorUnaryOperator.apply(signal);
      assertEquals(signal, tensor);
      ExactTensorQ.require(tensor);
    }
  }

  @Test
  void testKernel() {
    for (WindowFunctions smoothingKernel : WindowFunctions.values())
      for (int radius = 0; radius < 6; ++radius) {
        Function<Integer, Tensor> function = WindowSideExtrapolation.of(smoothingKernel.get());
        TensorUnaryOperator tensorUnaryOperator = BiinvariantMeanIIRnFilter.of( //
            LinearBiinvariantMean.INSTANCE, function, RGroup.INSTANCE, radius, RationalScalar.HALF);
        Tensor signal = Range.of(0, 10);
        Tensor tensor = tensorUnaryOperator.apply(signal);
        Chop._10.requireClose(tensor, signal);
      }
  }
}
