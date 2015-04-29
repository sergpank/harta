package md.harta.osm;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.List;

/**
 * Created by sergpank on 28.02.2015.
 */
public class Building extends OsmWay{
  private String houseNumber;
  private String street;
  private String height;
  private String design;
  private int levels;

  public Building(long id, List<OsmNode> nodes, Element element) {
    super(id, nodes);

    NodeList tags = element.getElementsByTagName("tag");
    for (int i = 0; i < tags.getLength(); i++) {
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      switch (key) {
        case "addr:housenumber":
          houseNumber = item.getAttribute("v");
          break;
        case "addr:street":
          street = item.getAttribute("v");
          break;
        case "building:height":
          height = item.getAttribute("v");
          break;
        case "building:levels":
          levels = Integer.valueOf(item.getAttribute("v"));
          break;
        case "design":
          design = item.getAttribute("v");
      }
    }
  }

  public String getHouseNumber() {
    return houseNumber;
  }

  public String getStreet() {
    return street;
  }

  public String getHeight() {
    return height;
  }

  public int getLevels() {
    return levels;
  }

  public String getDesign()
  {
    return design;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Building)) return false;
    if (!super.equals(o)) return false;

    Building building = (Building) o;

    if (levels != building.levels) return false;
    if (houseNumber != null ? !houseNumber.equals(building.houseNumber) : building.houseNumber != null) return false;
    if (street != null ? !street.equals(building.street) : building.street != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (houseNumber != null ? houseNumber.hashCode() : 0);
    result = 31 * result + (street != null ? street.hashCode() : 0);
    result = 31 * result + levels;
    return result;
  }
}
