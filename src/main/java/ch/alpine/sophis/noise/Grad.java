// code by stegu
package ch.alpine.sophis.noise;

/** class extracted from {@link SimplexContinuousNoise} */
/* package */ record Grad(double x, double y, double z, double w) {
  public static Grad of(double x, double y, double z, double w) {
    return new Grad(x, y, z, w);
  }

  public static Grad of(double x, double y, double z) {
    return new Grad(x, y, z, 0.0);
  }

  public double dot(double x, double y) {
    return this.x * x + this.y * y;
  }

  public double dot(double x, double y, double z) {
    return this.x * x + this.y * y + this.z * z;
  }

  public double dot(double x, double y, double z, double w) {
    return this.x * x + this.y * y + this.z * z + this.w * w;
  }
}
