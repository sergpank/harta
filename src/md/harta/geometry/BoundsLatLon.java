package md.harta.geometry;

import md.harta.projector.AbstractProjector;

/**
 * Created by sergpank on 03.03.2015.
 */
public class BoundsLatLon {
  private double minLat;
  private double maxLat;
  private double minLon;
  private double maxLon;

  public BoundsLatLon(double minLat, double minLon, double maxLat, double maxLon)
  {
    this.minLat = minLat;
    this.minLon = minLon;
    this.maxLat = maxLat;
    this.maxLon = maxLon;
  }

  public BoundsXY toXY(AbstractProjector projector) {
    XYPoint min = projector.getXY(minLat, minLon);
    XYPoint max = projector.getXY(maxLat, maxLon);

    return new BoundsXY(min.getX(), min.getY(), max.getX(), max.getY());
  }

  public double getMinLat()
  {
    return minLat;
  }

  public double getMaxLat()
  {
    return maxLat;
  }

  public double getMinLon()
  {
    return minLon;
  }

  public double getMaxLon()
  {
    return maxLon;
  }

  @Override
  public String toString()
  {
    return "BoundsLatLon{" +
        ", minLat=" + minLat +
        ", minLon=" + minLon +
        ", maxLat=" + maxLat +
        ", maxLon=" + maxLon +
        '}';
  }
}
