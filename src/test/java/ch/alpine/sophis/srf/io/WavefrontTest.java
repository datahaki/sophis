// code by jph
package ch.alpine.sophis.srf.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.ext.HomeDirectory;
import ch.alpine.tensor.ext.ReadLine;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.red.Min;
import ch.alpine.tensor.sca.Sign;

class WavefrontTest {
  private static void check3d(Path file) throws IOException {
    try (InputStream inputStream = Files.newInputStream(file)) {
      Wavefront wavefront = WavefrontFormat.parse(ReadLine.of(inputStream));
      Tensor normals = wavefront.normals();
      assertEquals(Dimensions.of(normals).get(1), Integer.valueOf(3));
      Tensor vertices = wavefront.vertices();
      assertEquals(Dimensions.of(vertices).get(1), Integer.valueOf(3));
      List<WavefrontObject> objects = wavefront.objects();
      for (WavefrontObject wavefrontObject : objects) {
        Tensor faces = wavefrontObject.faces();
        if (0 < faces.length()) {
          CoordinateBoundingBox minMax = CoordinateBounds.of(faces);
          assertEquals(minMax.min().maps(Sign::requirePositiveOrZero), minMax.min());
          ScalarUnaryOperator hi_bound = Min.function(RealScalar.of(vertices.length() - 1));
          assertEquals(minMax.max().maps(hi_bound), minMax.max());
        }
        Tensor nrmls = wavefrontObject.normals();
        if (0 < nrmls.length()) {
          CoordinateBoundingBox minMax = CoordinateBounds.of(nrmls);
          assertEquals(minMax.min().maps(Sign::requirePositiveOrZero), minMax.min());
          ScalarUnaryOperator hi_bound = Min.function(RealScalar.of(normals.length() - 1));
          assertEquals(minMax.max().maps(hi_bound), minMax.max());
        }
      }
    }
  }

  @Test
  void testLoad() throws IOException {
    Path directory = HomeDirectory.Projects.resolve("gym-duckietown", "gym_duckietown", "meshes");
    if (Files.isDirectory(directory))
      for (Path file : Files.list(directory).toList())
        if (file.endsWith(".obj"))
          check3d(file);
  }
}
