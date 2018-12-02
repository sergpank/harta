package md.onemap.harta.db.gis;

import md.onemap.harta.db.DbHelper;
import md.onemap.harta.db.gis.entity.Node;
import md.onemap.harta.db.gis.entity.Tag;
import md.onemap.harta.db.gis.entity.Unit;
import md.onemap.harta.db.gis.entity.Way;
import md.onemap.harta.geometry.BoundsLatLon;
import md.onemap.harta.osm.Highway;
import md.onemap.harta.osm.Waterway;

import org.postgis.Geometry;
import org.postgis.PGgeometry;
import org.postgis.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class WayGisDao extends GisDao<Way>
{
  private static final Logger LOG = LoggerFactory.getLogger(WayGisDao.class);

  public static final String WAY_TABLE_NAME = "gis.way";

  private static final String INSERT_WAY = "INSERT INTO " + WAY_TABLE_NAME + " (id, type, geometry) VALUES (?, ?, %s)";
  private static final String SELECT_WAY = "SELECT w.id, w.type, w.geometry, t.key, t.value, ST_Envelope(w.geometry) " +
      "FROM gis.way w " +
      "LEFT JOIN gis.tag t ON w.id = t.id " +
      "WHERE w.id = ";
  private static final String SELECT_TILE = "SELECT w.id, w.type, w.geometry, t.key, t.value, ST_Envelope(w.geometry) " +
      "FROM " + WAY_TABLE_NAME + " w " +
      "JOIN " + TagGisDao.TAG_TABLE_NAME + " t ON w.id = t.id " +
      "WHERE ST_Intersects( " +
      "ST_GeomFromText('Polygon(( " +
      "%f %f, " +
      "%f %f, " +
      "%f %f, " +
      "%f %f, " +
      "%f %f " +
      "))'), geometry)";


  @Override
  public void save(Way way)
  {
    Map<String, String> tags = way.getTags();
    Object geometry = createGeometry(way);

    if (geometry == null)
    {
      // That means that WAY contains insufficient points and can't be converted to GEOMETRY
      return;
    }

    String sql = String.format(INSERT_WAY, geometry);
    DbHelper.getJdbcTemplate().update(sql, way.getId(), Unit.defineType(tags));
    new TagGisDao().save(new Tag(way.getId(), tags));
  }

  @Override
  public void saveAll(Collection<Way> entities)
  {
    entities.forEach(this::save);
  }

  @Override
  public Way load(long id)
  {
    Collection<Way> ways = DbHelper.getJdbcTemplate().query(SELECT_WAY + id, this::extractData);
    if (ways == null || ways.isEmpty())
    {
//      LOG.error("Way not found, id: {}", id);
      return null;
    }
    else
    {
      return ways.iterator().next();
    }
  }

  @Override
  public Collection<Way> load(int zoomLevel, BoundsLatLon box)
  {
    double dLat = box.getMaxLat() - box.getMinLat();
    double dLon = box.getMaxLon() - box.getMinLon();
    String sql = String.format(SELECT_TILE,
        box.getMinLon() - dLon, box.getMinLat() - dLat,
        box.getMinLon() - dLon, box.getMaxLat() + dLat,
        box.getMaxLon() + dLon, box.getMaxLat() + dLat,
        box.getMaxLon() + dLon, box.getMinLat() - dLat,
        box.getMinLon() - dLon, box.getMinLat() - dLat
    );

//    LOG.info(sql);

    Collection<Way> ways = new HashSet<>();
    Collection<Way> query = DbHelper.getJdbcTemplate().query(sql, this::extractData);
    if (query != null)
    {
      ways.addAll(query);
    }
    return ways;
  }

  @Override
  public Collection<Way> loadAll()
  {
    return null;
  }

  private Object createGeometry(Way way)
  {
    if (way.getTags().containsKey(Highway.HIGHWAY) || way.getTags().containsKey(Waterway.WATERWAY))
    {
      if (way.getNodes().size() < 2)
      {
        LOG.error("Way {} has only {} points and can't be converted to LineString. Tags: {}", way.getId(), way.getNodes().size(), way.getTags());
        return null;
      }
      return createLineString(way.getNodes());
    }
    else
    {
      if (way.getNodes().size() < 3)
      {
        LOG.error("Way {} has only {} points and can't be converted to Polygon. Tags: {}", way.getId(), way.getNodes().size(), way.getTags());
        return null;
      }
      return createPolygon(way.getNodes());
    }
  }

  private Collection<Way> extractData(ResultSet rs) throws SQLException
  {
    Map<Long, Way> resultMap = new HashMap<>();

    while (rs.next())
    {
      Long id = rs.getLong("id");
      String key = rs.getString("key");
      String value = rs.getString("value");

      Way way = resultMap.get(id);
      if (way == null)
      {
        String type = rs.getString("type");
        List<Node> nodes = getNodes(rs, "geometry");
        Geometry envelope = ((PGgeometry) rs.getObject("st_envelope")).getGeometry();
        BoundsLatLon boundsLatLon = getBounds(envelope);

        way = new Way(id, type, nodes, new HashMap<>(), boundsLatLon);
        resultMap.put(id, way);
      }
      way.getTags().put(key, value);
    }

    return resultMap.values();
  }

  private BoundsLatLon getBounds(Geometry envelope)
  {
    Point minLonLat;
    Point maxLonLat;
    if (envelope.numPoints() == 2){
      // This is a LineString
      minLonLat = envelope.getPoint(0);
      maxLonLat = envelope.getPoint(1);
    }
    else
    {
      // This is a Polygon
      minLonLat = envelope.getPoint(0);
      maxLonLat = envelope.getPoint(2);
    }
    return new BoundsLatLon(minLonLat.y, minLonLat.x, maxLonLat.y, maxLonLat.x);
  }

  public static void main(String[] args)
  {
    Way way = new WayGisDao().load(206834772);

    System.out.println(way);
    System.out.println(way.getBoundsLatLon());
    way.getNodes().forEach(System.out::println);

    System.out.println();


    System.out.println("\n\nLoading tile:\n\n");
    BoundsLatLon bounds = new BoundsLatLon(47.022, 28.835, 47.023, 28.845);
    System.out.println("My bounds: " + bounds);

    Collection<Way> load = new WayGisDao().load(18, bounds);
    load.forEach(System.out::println);
  }
}