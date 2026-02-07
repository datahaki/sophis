// code by jph
package ch.alpine.sophis.fit;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.io.Pretty;

class DominantColorsTest {
  @Disabled
  @Test
  void test() {
    BufferedImage bufferedImage = ResourceData.bufferedImage("/ch/alpine/sophus/fit/gemini_nature.png");
    Tensor tensor = ImageFormat.from(bufferedImage);
    IO.println(Dimensions.of(tensor));
    Tensor seeds = DominantColors.of(tensor, 5);
    IO.println(Pretty.of(seeds));
  }
}
