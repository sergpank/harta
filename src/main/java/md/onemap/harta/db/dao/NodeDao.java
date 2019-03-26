package md.onemap.harta.db.dao;

import md.onemap.exception.NotImplementedException;
import md.onemap.harta.db.DbHelper;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.OsmNode;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sergpank on 21.04.15.
 */
public class NodeDao extends Dao<OsmNode>
{
  public static final String INSERT_SQL = "INSERT INTO nodes (node_id, lat, lon) VALUES (?, ?, ?)";
  public static final String SELECT_ALL = "SELECT node_id, lat, lon FROM nodes";
  public static final String SELECT_BOUNDS = "SELECT min(lat) AS min_lat, min(lon) AS min_lon, max(lat) AS max_lat, max(lon) AS max_lon FROM nodes;";
  public static final String SELECT_NODE = "SELECT lat, lon FROM nodes WHERE node_id = ?";
  public static final String SELECT_NODES = "SELECT node_id, lat, lon FROM nodes WHERE node_id in (%s)";

  public void saveAll(Collection<OsmNode> nodes)
  {
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement insertStmt = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
      int count = 0;
      for (OsmNode node : nodes)
      {
        int pos = 1;
        insertStmt.setLong(pos++, node.getId());
        insertStmt.setDouble(pos++, node.getLat());
        insertStmt.setDouble(pos++, node.getLon());
        insertStmt.addBatch();
        if (count++ > 999)
        {
          count = 0;
          insertStmt.executeBatch();
        }
      }
      if (count > 0)
      {
        insertStmt.executeBatch();
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public void save(OsmNode node)
  {
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement insertStmt = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);

      int pos = 1;
      insertStmt.setLong(pos++, node.getId());
      insertStmt.setDouble(pos++, node.getLat());
      insertStmt.setDouble(pos++, node.getLon());
      insertStmt.executeUpdate();
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }
  }

  @Override
  public OsmNode load(long id)
  {
    OsmNode node = null;
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement pStmt = connection.prepareStatement(SELECT_NODE);
      pStmt.setLong(1, id);
      try (ResultSet rs = pStmt.executeQuery())
      {
        if (rs.next())
        {
          double lat = rs.getDouble("lat");
          double lon = rs.getDouble("lon");
          node = new OsmNode(id, lat, lon);
        }
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return node;
  }

  @Override
  public Collection<OsmNode> load(int zoomLevel, BoundsLatLon box)
  {
    throw new NotImplementedException();
  }

  @Override
  public Collection<OsmNode> loadAll()
  {
    List<OsmNode> nodes = new ArrayList<>();
    try (Connection connection = DbHelper.getConnection())
    {
      PreparedStatement pStmt = connection.prepareStatement(SELECT_ALL);
      ResultSet rs = pStmt.executeQuery();
      while (rs.next())
      {
        long id = rs.getLong("node_id");
        double lat = rs.getDouble("lat");
        double lon = rs.getDouble("lon");
        nodes.add(new OsmNode(id, lat, lon));
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return nodes;
  }

  public List<OsmNode> loadNodes(List<Long> nodeIds)
  {
    List<OsmNode> nodes = new ArrayList<>();
    String collectIds = nodeIds.stream().map(Object::toString).collect(Collectors.joining(","));
    String sql = String.format(SELECT_NODES, collectIds);
    try (Connection connection = DbHelper.getConnection())
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next())
      {
        long id = rs.getLong("node_id");
        double lat = rs.getDouble("lat");
        double lon = rs.getDouble("lon");
        nodes.add(new OsmNode(id, lat, lon));
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
    return nodes;
  }

  public List<OsmNode> loadNodes(Array nodeIdsArray) throws SQLException
  {
    List<Long> nodeIds = new ArrayList<>();
    ResultSet nodeSet = nodeIdsArray.getResultSet();
    while (nodeSet.next())
    {
      long nodeId = nodeSet.getLong(2);
      nodeIds.add(nodeId);
    }

    return loadNodes(nodeIds);
  }
}