package md.harta.db.gis;

import md.harta.geometry.Bounds;
import md.harta.osm.Waterway;
import md.harta.projector.AbstractProjector;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

/**
 * Created by serg on 07-Aug-16.
 */
public class WaterwayGisDao extends GisDao<Waterway> {
  public WaterwayGisDao(Connection connection) {
    super(connection);
  }

  @Override
  public void save(Waterway entity) {

  }

  @Override
  public void saveAll(List<Waterway> entities) {

  }

  @Override
  public Waterway load(long id) {
    return null;
  }

  @Override
  public Collection<Waterway> load(int zoomLevel, Bounds box, AbstractProjector projector) {
    return null;
  }

  @Override
  public Collection<Waterway> loadAll(AbstractProjector projector) {
    return null;
  }

  @Override
  public Bounds getBounds() {
    return null;
  }
}