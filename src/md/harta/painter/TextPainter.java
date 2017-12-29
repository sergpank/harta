package md.harta.painter;

import md.harta.drawer.AbstractDrawer;
import md.harta.geometry.*;
import md.harta.geometry.Label;
import md.harta.osm.Highway;
import md.harta.osm.OsmNode;
import md.harta.projector.AbstractProjector;
import md.harta.tile.TilePalette;
import md.harta.util.TextUtil;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.util.List;

/**
 * Created by sergpank on 07.07.15.
 */
public class TextPainter extends AbstractPainter
{
  public TextPainter(AbstractProjector projector, BoundsXY bounds)
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
    Font font = new Font(TilePalette.HIGHWAY_FONT_NAME, Font.PLAIN, TilePalette.HIGHWAY_FONT_SIZE);

    int charHeight = (int) TextUtil.getStringHeight(TilePalette.HIGHWAY_FONT_NAME, TilePalette.HIGHWAY_FONT_SIZE);

    // XYPoint center = getIntersectionPoint(label.getHighway(), label.getCenter());
    XYPoint roadStartPoint = getRoadStartPoint(label);
    if (roadStartPoint == null)
    {
      return;
    }
    int xShift = (int) roadStartPoint.getX();
    int yShift = (int) roadStartPoint.getY() + charHeight / 2;
    double roadLength = GeometryUtil.getHighwayLength(label.getHighway(), projector);

//    System.out.printf("%s - %d : %d\n", text, xShift, yShift);
//    System.out.printf("Start at: %f : %f\n", roadStartPoint.getX(), roadStartPoint.getY());
//    System.out.printf("Width = %d; Height = %d\n", labelWidth, charHeight);

    BoundsLatLon highwayBounds = label.getHighway().getBounds();
    XYPoint minXY = shiftPoint(projector.getXY(highwayBounds.getMinLat(), highwayBounds.getMinLon()));
    XYPoint maxXY = shiftPoint(projector.getXY(highwayBounds.getMaxLat(), highwayBounds.getMaxLon()));
//    System.out.printf("Road bounding box = {(%s); (%s)}\n", minXY, maxXY);
//
//    System.out.println("Road length = " + roadLength + "\n");

    int labelWidth = calcLabelWidth(label);

    if (roadLength > labelWidth)
    {
      RoadLabelIntersector intersector = new RoadLabelIntersector(bounds, labelWidth, charHeight);
      List<Intersection> intersections = intersector.getIntersections(label.getHighway(), label, projector);
      for (int i = 0; i < intersections.size(); i++)
      {
        String character = label.getText().charAt(i % label.getText().length()) + "";
        GlyphVector glyphVector = font.createGlyphVector(drawer.getFontRenderContext(), character);
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

  private int calcLabelWidth(Label label)
  {
    String text = label.getText();
    int labelWidth = text.length(); // interval between letters = 1 pixel

    for (char character : text.toCharArray())
    {
      int charWidth = (int) TextUtil.getStringWidth(Character.toString(character), TilePalette.HIGHWAY_FONT_NAME, TilePalette.HIGHWAY_FONT_SIZE);
      labelWidth += charWidth;
    }
    return labelWidth;
  }

  /**
   * Road start point is upper left of lower left point
   * Because road name is drawn from left to right
   */
  private XYPoint getRoadStartPoint(Label label)
  {
    List<OsmNode> nodes = label.getHighway().getNodes();
    if (nodes.isEmpty())
    {
      return null;
    }
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
