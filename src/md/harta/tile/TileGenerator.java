package md.harta.tile;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import javax.imageio.ImageIO;
import md.harta.drawer.AbstractDrawer;
import md.harta.drawer.TileDrawer;
import md.harta.geometry.Bounds;
import md.harta.loader.AbstractLoader;
import md.harta.loader.OsmLoader;
import md.harta.osm.Building;
import md.harta.osm.Highway;
import md.harta.painter.BuildingPainter;
import md.harta.painter.HighwayPainter;
import md.harta.projector.AbstractProjector;
import md.harta.projector.MercatorProjector;
import md.harta.util.ScaleCalculator;

/**
 * Created by sergpank on 23.05.15.
 */
public class TileGenerator
{
  public static final int TILE_SIZE = 256;
  public static final String TILE_DIR = "/home/sergpank/WORK/harta/tiles";

  public static void main(String[] args)
  {
//    double minLat = 45.4601058959962;//loader.getMinLat();
//    double minLon = 26.6213111877442;//loader.getMinLon();
//    double maxLat = 48.4901695251467;//loader.getMaxLat();
//    double maxLon = 30.1637401580812;//loader.getMaxLon();

    String database = "Hanul_Morii";
    File tilesFoder = new File(TILE_DIR, database);
    tilesFoder.mkdirs();

//    AbstractLoader loader = new DbLoader(database);
    AbstractLoader loader = new OsmLoader();
//    loader.load("osm/Hanul_Morii.osm", null);

//    NodeDao nodeDao = new NodeDao(DbHelper.getConnection(database));
//    Bounds bounds = nodeDao.getBounds();
//    Bounds bounds = loader.getBounds();
//    System.out.println(bounds);

//    Collection<OsmNode> nodes = nodeDao.loadAll(null);
//    Collection<OsmNode> nodes = loader.getNodes().values();
//    Map<Long, OsmNode> nodeMap = new HashMap<>(nodes.size());
//    for (OsmNode node : nodes)
//    {
//      nodeMap.put(node.getId(), node);
//    }

    for (int level = 10; level <= 20; level++)
    {
      new File(tilesFoder, Integer.toString(level)).mkdirs();

      AbstractProjector projector = new MercatorProjector(ScaleCalculator.getRadiusForLevel(level), 85);
      loader.load("osm/Hanul_Morii.osm", projector);
      Bounds bounds = loader.getBounds();

      TileCutter tileCutter = new TileCutter(projector, TILE_SIZE, level,
          bounds.getMinLat(), bounds.getMinLon(), bounds.getMaxLat(), bounds.getMaxLon());
      tileCutter.cut();

      Collection<Highway> highways = loader.getHighways(level, null, null, projector);
      Collection<Building> buildings = loader.getBuildings(level, null, null, projector);

      long numTiles = (tileCutter.getMaxTileXindex() - tileCutter.getMinTileXindex() + 1) * (tileCutter.getMaxTileYindex() - tileCutter.getMinTileYindex() + 1);
      long progressStep = 1;
      if (numTiles > 100)
      {
        progressStep = numTiles / 100;
      }

      long tileCnt = 0;
      for (int y = tileCutter.getMinTileYindex(); y <= tileCutter.getMaxTileYindex(); y++)
//      for (int y = 45885; y <= 45885; y++)
      {
        for (int x = tileCutter.getMinTileXindex(); x <= tileCutter.getMaxTileXindex(); x++)
//        for (int x = 76045; x <= 76045; x++)
        {
          if (((++tileCnt) % progressStep) == 0)
          {
            System.out.printf("%d ---> %.0f %% (%d of %d)\n", level, (double)tileCnt / numTiles * 100, tileCnt, numTiles);
          }
          BufferedImage bi = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
          Graphics2D graphics = bi.createGraphics();

          graphics.setPaint(TilePalette.BACKGROUND_COLOR);
          graphics.fillRect(0, 0, TILE_SIZE, TILE_SIZE);

          Bounds tileBounds = tileCutter.getTileBounds(x, y);
          AbstractDrawer drawer = new TileDrawer(graphics);
          drawer.setAAEnabled(true);

//          Collection<Highway> highways = loader.getHighways(level, tileBounds, nodeMap, projector);
//          Collection<Building> buildings = loader.getBuildings(level, tileBounds, nodeMap, projector);
          new HighwayPainter(projector, tileBounds).drawHighways(drawer, highways, level);
          new BuildingPainter(projector, tileBounds).drawBuildings(drawer, buildings, level);

          addTileNumberAndBorder(x, y, level, graphics);

          writeTile(bi, level, x, y, database);
        }
      }
    }
  }

  private static void addTileNumberAndBorder(int x, int y, int level, Graphics2D graphics)
  {
    String levelLabel = level + "";
//    String xyLabel = String.format("(%d; %d)", x, y);

    Font font = new Font("Calibri", Font.BOLD, 14);
    FontMetrics fontMetrics = graphics.getFontMetrics(font);
//    graphics.setFont(font);
//    graphics.setColor(Color.WHITE);

    int levelWidth = fontMetrics.stringWidth(levelLabel);
//    int xyWidth = fontMetrics.stringWidth(xyLabel);
    int h = fontMetrics.getHeight();

//    graphics.fillRect(128 - levelWidth / 2 - 4, 128 - h / 2 - 4, levelWidth + 8, h + 8);
//    graphics.fillRect(128 - xyWidth / 2 - 4, (128 + h / 2 + 4) + 4, xyWidth + 8, h + 8);

    graphics.setColor(Color.RED);
    graphics.drawString(levelLabel, 128 - levelWidth / 2, 128 + h / 2);
//    graphics.drawString(xyLabel, 128 - xyWidth / 2, 128 + h / 2 * 3 + 8);

//    graphics.drawLine(0, 0, 255, 0);
//    graphics.drawLine(256, 0, 255, 255);
//    graphics.drawLine(255, 255, 0, 255);
//    graphics.drawLine(0, 255, 0, 0);
  }

  private static void writeTile(BufferedImage bi, int level, int x, int y, String dbName)
  {
    try
    {
      String tileName = String.format("%s/%s/%s/tile_%d_%d_%d.png", TILE_DIR, dbName, level, level, y, x);
//      System.out.println(tileName);
      ImageIO.write(bi, "PNG", new File(tileName));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
