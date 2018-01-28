package md.onemap.harta.db.gis;

import md.onemap.harta.db.dao.Dao;
import md.onemap.harta.osm.OsmNode;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by serg on 11/7/15.
 */
public abstract class GisDao<T> extends Dao<T>
{
  protected void toGisConnection(Connection connection)
  {
    try
    {
      /*
       * Add the geometry types to the connection. Note that you
       * must cast the connection to the pgsql-specific connection
       * implementation before calling the addDataType() method.
       */
      Class<? extends PGobject> geometryClass = (Class<? extends PGobject>) Class.forName("org.postgis.PGgeometry");
      ((org.postgresql.PGConnection)connection).addDataType("geometry", geometryClass);
    }
    catch (SQLException | ClassNotFoundException e)
    {
      e.printStackTrace();
    }
  }

  protected String createPolygon(List<OsmNode> nodes)
  {
    StringBuilder geometry = new StringBuilder("ST_GeomFromText('POLYGON((");
    for (OsmNode node : nodes)
    {
      geometry.append(node.getLon()).append(' ').append(node.getLat()).append(',');
    }
    geometry.append(nodes.get(0).getLon()).append(' ').append(nodes.get(0).getLat()).append("))')");

    return geometry.toString();
  }

  protected String createLineString(List<OsmNode> nodes)
  {
    StringBuilder geometry = new StringBuilder("ST_GeomFromText('LINESTRING(");
    for (OsmNode node : nodes)
    {
      geometry.append(node.getLon()).append(' ').append(node.getLat()).append(',');
    }
    geometry.delete(geometry.length() - 1, geometry.length());
    geometry.append(")')");

    return geometry.toString();
  }
}
