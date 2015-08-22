package md.harta.loader;

import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import md.harta.db.BorderDao;
import md.harta.db.BuildingDao;
import md.harta.db.DbHelper;
import md.harta.db.HighwayDao;
import md.harta.db.NodeDao;
import md.harta.geometry.Bounds;
import md.harta.osm.Border;
import md.harta.osm.Building;
import md.harta.osm.Highway;
import md.harta.osm.OsmNode;
import md.harta.projector.AbstractProjector;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by sergpank on 15.05.15.
 */
public class DbLoader extends AbstractLoader
{
  private Map<Long, OsmNode> nodes;
  private Map<Long, Border> borders;
  private Connection connection;

  public DbLoader(String dbName)
  {
    this.connection = DbHelper.getConnection(dbName);
  }

  @Override
  public void load(String dbName, AbstractProjector projector)
  {
    minLon = Double.MAX_VALUE;
    minLat = Double.MAX_VALUE;
    maxLon = Double.MIN_VALUE;
    maxLat = Double.MIN_VALUE;
    connection = DbHelper.getConnection(dbName);
    loadNodes(connection);
    loadBorders(connection, projector);
  }

  private void loadNodes(Connection connection)
  {
    nodes = new HashMap<>();
    NodeDao nodeDao = new NodeDao(connection);
    Collection<OsmNode> osmNodes = nodeDao.loadAll(null);
    for (OsmNode osmNode : osmNodes)
    {
      nodes.put(osmNode.getId(), osmNode);
      registerMinMax(osmNode.getLat(), osmNode.getLon());
    }
  }

  private void loadBorders(Connection connection, AbstractProjector projector)
  {
    borders = new HashMap<>();
    BorderDao borderDao = new BorderDao(connection);
    Collection<Border> osmBorders = borderDao.loadAll(projector);
    for (Border border : osmBorders)
    {
      borders.put(border.getId(), border);
    }
  }

  @Override
  public Map<Long, OsmNode> getNodes()
  {
    return nodes;
  }

  @Override
  public Map<Long, Highway> getHighways(AbstractProjector projector)
  {
    Collection<Highway> highways = new HighwayDao(connection).loadAll(projector);
    Map<Long, Highway> map = new HashMap<>(highways.size());
    for (Highway highway : highways)
    {
      map.put(highway.getId(), highway);
    }
    return map;
  }

  @Override
  public Map<Long, Building> getBuildings(AbstractProjector projector)
  {
    return new HashMap<>();
  }

  @Override
  public Collection<Border> getBorders(int level, Bounds tileBounds, Map<Long, OsmNode> nodes, AbstractProjector projector)
  {
    return new BorderDao(connection).load(level, tileBounds, nodes, projector);
  }

  @Override
  public Collection<Highway> getHighways(int level, Bounds tileBounds, Map<Long, OsmNode> nodeMap, AbstractProjector projector)
  {
    return new HighwayDao(connection).load(level, tileBounds, nodeMap, projector);
  }

  @Override
  public Collection<Building> getBuildings(int level, Bounds tileBounds, Map<Long, OsmNode> nodeMap, AbstractProjector projector)
  {
    return new BuildingDao(connection).load(level, tileBounds, nodeMap, projector);
  }

  @Override
  public Bounds getBounds()
  {
    throw new NotImplementedException();
  }
}
