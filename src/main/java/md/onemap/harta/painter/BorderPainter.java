package md.onemap.harta.painter;

import md.onemap.harta.drawer.AbstractDrawer;
import md.onemap.harta.geometry.BoundsXY;
import md.onemap.harta.geometry.CanvasPolygon;
import md.onemap.harta.geometry.Label;
import md.onemap.harta.geometry.XYPoint;
import md.onemap.harta.osm.Boundary;
import md.onemap.harta.projector.AbstractProjector;
import md.onemap.harta.tile.Palette;
import md.onemap.harta.tile.TileCutter;
import md.onemap.harta.util.BoxIntersector;
import md.onemap.harta.util.TextUtil;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sergpank on 05.05.15.
 */
public class BorderPainter extends AbstractPainter
{
  public static final String FONT_NAME = "Tahoma";
  public static final int FONT_SIZE = 14;

  public BorderPainter(AbstractProjector projector, BoundsXY bounds)
  {
    super(projector, bounds);
  }

  public void drawBorders(AbstractDrawer drawer, Collection<Boundary> boundaries, int x, int y, TileCutter tileCutter)
  {
    List<Label> labels = new ArrayList<>();

    drawer.setStrokeColor(Palette.BORDER_COLOR);
    for (Boundary boundary : boundaries)
    {
      CanvasPolygon polygon = createPolygon(boundary.getNodes());
      BoundsXY bounds = boundary.getBounds().toXY(projector);
      double width = bounds.getXmax() - bounds.getXmin();

      shiftPoints(this.bounds.getXmin(), polygon.getxPoints());
      shiftPoints(this.bounds.getYmin(), polygon.getyPoints());
      drawer.drawPolyLine(polygon, 1, false);
      labels.add(createRegionLabel(polygon, boundary.getName(), drawer.getGraphics()));
    }

    paintLabels(drawer, labels, x, y, tileCutter);
  }

  private void paintLabels(AbstractDrawer drawer, List<Label> labels, int x, int y, TileCutter tileCutter)
  {
    drawer.setFillColor(Palette.FONT_COLOR);
    for (int i = 0; i < labels.size() - 1; i++)
    {
      Label label = labels.get(i);
      for (int j = i + 1; j < labels.size();)
      {
        Label lbl = labels.get(j);
        if (BoxIntersector.intersectXY(label.getBounds(), lbl.getBounds()))
        {
          labels.remove(j);
        }
        else
        {
          j++;
        }
      }
    }
    for (Label label : labels)
    {
//      drawer.setFont(label.getFont().getName(), label.getFont().getSize());
//      drawer.fillText(label.getText(), label.getLabelCenter().getX(), label.getLabelCenter().getY());
      drawer.paintLabel(label, x, y, tileCutter);
    }
  }

  private Label createRegionLabel(CanvasPolygon polygon, String label, Graphics2D graphics)
  {
    XYPoint center = null;
    float stringWidth = 0;
    float stringHeight = 0;
    if (label != null)
    {
      stringWidth = TextUtil.getStringWidth(label, FONT_NAME, FONT_SIZE, graphics);
      stringHeight = TextUtil.getStringHeight(FONT_NAME, FONT_SIZE, graphics);
      center = getLabelCenter(polygon, label, stringWidth, stringHeight);
    }
    //System.out.printf("%s - %d\n", label, (int) font.getSize());
    return new Label(label, center, stringHeight, stringWidth, null);
  }
}
