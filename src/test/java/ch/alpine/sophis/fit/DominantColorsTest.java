// code by jph
package ch.alpine.sophis.fit;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.ResourceData;
import ch.alpine.tensor.img.ImageResize;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.io.Pretty;

class DominantColorsTest {
  @Test
  void test() {
    BufferedImage bufferedImage = ResourceData.bufferedImage("ch/alpine/sophis/fit/gemini_nature.png");
    bufferedImage = ImageResize.DEGREE_0.of(bufferedImage, 10, 10);
    Tensor tensor = ImageFormat.from(bufferedImage);
    Tensor seeds = DominantColors.of(tensor, 5);
    IO.println(Pretty.of(seeds));
  }
}
