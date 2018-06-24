package md.onemap.harta.osm;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Primitive Landuse - OsmWay that has only "landuse" tag.
 *
 * Created by serg on 08-Aug-16.
 */
public class Landuse extends OsmWay{

  public static final String LANDUSE = "landuse";

  private String type;
  private String name;
  private String nameRu;

  public Landuse(long id, List<OsmNode> nodes, Element element) {
    super(id, nodes);

    NodeList tags = element.getElementsByTagName("tag");
    for (int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      switch(key){
        case LANDUSE:
          type = item.getAttribute("v");
          break;
        case NAME:
          name = item.getAttribute("v");
          break;
        case NAME_RU:
          nameRu = item.getAttribute("v");
          break;
        default:
          break;
      }
    }
  }

  public Landuse(long id, ArrayList<OsmNode> nodes, String type, String name, String nameRu)
  {
    super(id, nodes);
    this.type = type;
    this.name = name;
    this.nameRu = nameRu;
  }

  public String getType() {
    return type;
  }

  public String getName()
  {
    return name;
  }

  public String getNameRu()
  {
    return nameRu;
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    if (!super.equals(o))
    {
      return false;
    }
    Landuse landuse = (Landuse) o;
    return Objects.equals(type, landuse.type) &&
        Objects.equals(name, landuse.name) &&
        Objects.equals(nameRu, landuse.nameRu);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(super.hashCode(), type, name, nameRu);
  }
}
