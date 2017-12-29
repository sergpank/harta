package md.harta.loader;

import md.harta.db.DbHelper;
import md.harta.db.gis.BuildingGisDao;
import md.harta.db.gis.HighwayGisDao;
import md.harta.geometry.BoundsLatLon;
import md.harta.osm.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Map;

/**
 * Created by serg on 11/8/15.
 */
public class PostgisLoader extends AbstractLoader
{
  private Connection connection;

  public PostgisLoader(String dbName)
  {
    this.connection = DbHelper.getConnection(dbName);
    ;
  }

  @Override
  public Map<Long, OsmNode> getNodes()
  {
    return null;
  }

  @Override
  public void load(String dataSource)
  {

  }

  @Override
  public Map<Long, Highway> getHighways()
  {
    return null;
  }

  @Override
  public Map<Long, Building> getBuildings()
  {
    return null;
  }

  @Override
  public Map<Long, Leisure> getLeisure()
  {
    return null;
  }

  @Override
  public Map<Long, Natural> getNature()
  {
    return null;
  }

  @Override
  public Map<Long, Waterway> getWaterways()
  {
    return null;
  }

  @Override
  public Map<Long, Landuse> getLanduse()
  {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Border> getBorders(int level, BoundsLatLon tileBounds)
  {
    return null;
  }

  @Override
  public Collection<Highway> getHighways(int level, BoundsLatLon tileBounds)
  {
    return new HighwayGisDao(connection).load(level, tileBounds);
  }

  @Override
  public Collection<Building> getBuildings(int level, BoundsLatLon tileBounds)
  {
    return new BuildingGisDao(connection).load(level, tileBounds);
  }

  @Override
  public Collection<Leisure> getLeisure(int level, BoundsLatLon tileBounds)
  {
    return null;
  }

  @Override
  public Collection<Natural> getNature(int level, BoundsLatLon tileBounds)
  {
    return null;
  }

  @Override
  public Collection<Waterway> getWaterways(int level, BoundsLatLon tileBounds)
  {
    return null;
  }

  @Override
  public Collection<Landuse> getLanduse(int level, BoundsLatLon tileBounds)
  {
    return null;
  }

  @Override
  public BoundsLatLon getBounds()
  {
    double minLonBuilding = 0;
    double minLatBuilding = 0;
    double maxLonBuilding = 0;
    double maxLatBuilding = 0;

    double minLonHighway = 0;
    double minLatHighway = 0;
    double maxLonHighway = 0;
    double maxLatHighway = 0;

    try (Statement statement = connection.createStatement();)
    {
      try (ResultSet rs = statement.executeQuery("select min(ST_XMin(building_geometry)), min(ST_YMin(building_geometry)), max(ST_XMax(building_geometry)), max(ST_YMax(building_geometry))from buildings_gis"))
      {
        rs.next();
        minLonBuilding = rs.getDouble(1);
        minLatBuilding = rs.getDouble(2);
        maxLonBuilding = rs.getDouble(3);
        maxLatBuilding = rs.getDouble(4);
      }
      try (ResultSet rs = statement.executeQuery("select min(ST_XMin(highway_geometry)), min(ST_YMin(highway_geometry)), max(ST_XMax(highway_geometry)), max(ST_YMax(highway_geometry))from highways_gis"))
      {
        rs.next();
        minLonHighway = rs.getDouble(1);
        minLatHighway = rs.getDouble(2);
        maxLonHighway = rs.getDouble(3);
        maxLatHighway = rs.getDouble(4);
      }
    }
    catch (SQLException e)
    {
      e.printStackTrace();
    }

    double minLat = Math.min(minLatBuilding, minLatHighway);
    double minLon = Math.min(minLonBuilding, minLonHighway);
    double maxLat = Math.max(maxLatBuilding, maxLatHighway);
    double maxLon = Math.max(maxLonBuilding, maxLonHighway);

    return new BoundsLatLon(minLat, minLon, maxLat, maxLon);
  }
}
