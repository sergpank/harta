package md.harta.painter;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.util.List;
import md.harta.drawer.AbstractDrawer;
import md.harta.geometry.Bounds;
import md.harta.geometry.GeometryUtil;
import md.harta.geometry.Intersection;
import md.harta.geometry.Label;
import md.harta.geometry.RoadLabelIntersector;
import md.harta.geometry.XYPoint;
import md.harta.osm.Highway;
import md.harta.osm.OsmNode;
import md.harta.projector.AbstractProjector;
import md.harta.tile.TilePalette;

/**
 * Created by sergpank on 07.07.15.
 */
public class TextPainter extends AbstractPainter
{
  public TextPainter(AbstractProjector projector, Bounds bounds)
  {
    super(projector, bounds);
  }

  public void paintHighwayLabel(AbstractDrawer drawer, Label label)
  {
    Highway highway = label.getHighway();
    if (highway != null && highway.getName() != null)
    {
      drawer.setFillColor(TilePalette.FONT_COLOR);
      drawTiltString(drawer, label);
    }
  }

  private void drawTiltString(AbstractDrawer drawer, Label label)
  {
    Font font = new Font(TilePalette.FONT_NAME, Font.PLAIN, TilePalette.FONT_SIZE);
    FontMetrics fontMetrics = drawer.getFontMetrics(font);

    String text = label.getText();
    int labelLength = label.getText().length();

    int height = fontMetrics.getHeight();
    // There is one pixel distance between characters
    int charWidth = fontMetrics.charWidth('A');
    int width = charWidth * labelLength + labelLength;

    // XYPoint center = getIntersectionPoint(label.getHighway(), label.getCenter());
    XYPoint roadStartPoint = getRoadStartPoint(label);
    int xShift = (int) roadStartPoint.getX();
    int yShift = (int) roadStartPoint.getY() + height / 2;
    double roadLength = GeometryUtil.getHighwayLength(label.getHighway(), projector);

    System.out.printf("%s - %d : %d\n", text, xShift, yShift);
    System.out.printf("Start at: %f : %f\n", roadStartPoint.getX(), roadStartPoint.getY());
    System.out.printf("Width = %d; Height = %d\n", width, height);

    Bounds highwayBounds = label.getHighway().getBounds();
    XYPoint minXY = shiftPoint(projector.getXY(highwayBounds.getMinLat(), highwayBounds.getMinLon()));
    XYPoint maxXY = shiftPoint(projector.getXY(highwayBounds.getMaxLat(), highwayBounds.getMaxLon()));
    System.out.printf("Road bounding box = {(%s); (%s)}\n", minXY, maxXY);

    System.out.println("Road length = " + roadLength + "\n");

    List<Intersection> intersections = new RoadLabelIntersector(bounds, charWidth).getIntersections(label.getHighway(), label, projector);
    if (roadLength > labelLength)
    {
      for (int i = 0; i < label.getText().length(); i++)
      {
        GlyphVector glyphVector = font.createGlyphVector(drawer.getFontRenderContext(), label.getText().charAt(i) + "");
        Intersection intersection = intersections.get(i);

        drawer.translate((int)intersection.getPoint().getX(), (int)intersection.getPoint().getY());
        drawer.rotate(intersection.getAngle());

        Shape outline = glyphVector.getGlyphOutline(0);
        drawer.fill(outline);

        drawer.rotate(-(intersection.getAngle()));
        drawer.translate(-(int)intersection.getPoint().getX(), -(int)intersection.getPoint().getY());
      }
    }
  }

  /**
   * Road start point is upper left of lower left point
   * Because road name is drawn from left to right
   */
  private XYPoint getRoadStartPoint(Label label)
  {
    List<OsmNode> nodes = label.getHighway().getNodes();
    OsmNode firstNode = nodes.get(0);
    OsmNode lastNode = nodes.get(nodes.size() - 1);

    if (firstNode.getLon() < lastNode.getLon())
    {
      return shiftPoint(projector.getXY(firstNode.getLat(), firstNode.getLon()));
    }
    else
    {
      return shiftPoint(projector.getXY(lastNode.getLat(), lastNode.getLon()));
    }
  }
}
