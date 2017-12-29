package md.harta.loader;

import md.harta.geometry.BoundsLatLon;
import md.harta.osm.*;
import md.harta.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

/**
 * Created by sergpank on 20.01.2015.
 */
public class OsmLoader extends AbstractLoader{
  private Map<Long, OsmNode> nodeMap = new HashMap<>();
  private HashMap<Long, Building> buildingMap = new HashMap<>();
  private HashMap<Long, Highway>  highwayMap = new HashMap<>();
  private HashMap<Long, Leisure>  leisureMap = new HashMap<>();
  private HashMap<Long, Natural>  natureMap = new HashMap<>();
  private HashMap<Long, Border>   borderMap = new HashMap<>();
  private HashMap<Long, Waterway> waterwayMap = new HashMap<>();
  private HashMap<Long, Landuse>  landuseMap = new HashMap<>();
  private OsmBounds bounds;

  public OsmLoader() {
  }

  public Map<Long, OsmNode> getNodes() {
    return nodeMap;
  }

  public Map<Long, Highway> getHighways() {
    return highwayMap;
  }

  public Map<Long, Building> getBuildings() {
    return buildingMap;
  }

  @Override
  public Map<Long, Leisure> getLeisure()
  {
    return leisureMap;
  }

  @Override
  public Map<Long, Natural> getNature()
  {
    return natureMap;
  }

  @Override
  public Map<Long, Waterway> getWaterways() {
    return waterwayMap;
  }

  @Override
  public Map<Long, Landuse> getLanduse() {
    return landuseMap;
  }

  @Override
  public Collection<Border> getBorders(int level, BoundsLatLon tileBounds)
  {
    throw new NotImplementedException();
  }

  @Override
  public Collection<Highway> getHighways(int level, BoundsLatLon tileBounds)
  {
    return getHighways().values();
  }

  @Override
  public Collection<Building> getBuildings(int level, BoundsLatLon tileBounds)
  {
    return getBuildings().values();
  }

  @Override
  public Collection<Leisure> getLeisure(int level, BoundsLatLon tileBounds)
  {
    return leisureMap.values();
  }

  @Override
  public Collection<Natural> getNature(int level, BoundsLatLon tileBounds)
  {
    return natureMap.values();
  }

  @Override
  public Collection<Waterway> getWaterways(int level, BoundsLatLon tileBounds)
  {
    return waterwayMap.values();
  }

  @Override
  public Collection<Landuse> getLanduse(int level, BoundsLatLon tileBounds)
  {
    return landuseMap.values();
  }

  public Map<Long, Border> getBorders()
  {
    return borderMap;
  }

  public OsmBounds getOsmBounds() {
    return bounds;
  }

  public void load(String xmlFile) {
    minLon = Double.MAX_VALUE;
    minLat = Double.MAX_VALUE;
    maxLon = Double.MIN_VALUE;
    maxLat = Double.MIN_VALUE;

    Document doc = XmlUtil.parseDocument(xmlFile);

    nodeMap.clear();
    getNodes(doc);

    buildingMap.clear();
    highwayMap.clear();
    leisureMap.clear();
    natureMap.clear();
    waterwayMap.clear();
    borderMap.clear();
    landuseMap.clear();
    readOsm(doc);

    bounds = getBounds(doc);
  }

  private OsmBounds getBounds(Document doc) {
    NodeList nodeList = XmlUtil.getNodeList(doc, "/osm/bounds");
    Element item = (Element) nodeList.item(0);
    if (item != null) {
      double minlat = Double.parseDouble(item.getAttribute("minlat"));
      double minlon = Double.parseDouble(item.getAttribute("minlon"));
      double maxlat = Double.parseDouble(item.getAttribute("maxlat"));
      double maxlon = Double.parseDouble(item.getAttribute("maxlon"));
      return new OsmBounds(minlat, minlon, maxlat, maxlon);
    }
    return null;
  }

  private void getNodes(Document doc) {
    NodeList nodeList = XmlUtil.getNodeList(doc, "/osm/node");
    for (int i = 0; i < nodeList.getLength(); i++) {
      Node item = nodeList.item(i);
      if (item.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) item;
        Long id = Long.parseLong(element.getAttribute("id"));
        Double lat = Double.parseDouble(element.getAttribute("lat"));
        Double lon = Double.parseDouble(element.getAttribute("lon"));
        nodeMap.put(id, new OsmNode(id, lat, lon));

        registerMinMax(lat, lon);
      }
    }
  }

  private void readOsm(Document doc) {
    NodeList nodeList = XmlUtil.getNodeList(doc, "/osm/way");
    for (int i = 0; i < nodeList.getLength(); i++)
    {
      Node item = nodeList.item(i);
      if (item.getNodeType() == Node.ELEMENT_NODE)
      {
        Element element = (Element) item;
        Long id = Long.parseLong(element.getAttribute("id"));
        NodeList wayNodeIds = element.getElementsByTagName("nd");
        List<OsmNode> wayNodes = new ArrayList<>(wayNodeIds.getLength());
        for (int j = 0; j < wayNodeIds.getLength(); j++)
        {
          long nodeId = Long.parseLong(((Element) wayNodeIds.item(j)).getAttribute("ref"));
          wayNodes.add(nodeMap.get(nodeId));
        }
        if (isBuilding(element))
        {
          Building building = new Building(id, wayNodes, element);
          buildingMap.put(id, building);
        }
        else if (isHighway(element))
        {
          Highway highway = new Highway(id, wayNodes, element);
          highwayMap.put(id, highway);
        }
        else if (isLeisure(element))
        {
          Leisure leisure = new Leisure(id, wayNodes, element);
          leisureMap.put(id, leisure);
        }
        else if (isNatural(element))
        {
          Natural natural = new Natural(id, wayNodes, element);
          natureMap.put(id, natural);
        }
        else if (isWaterway(element))
        {
          Waterway waterway = new Waterway(id, wayNodes, element);
          waterwayMap.put(id, waterway);
        }
        else if (isBorder(element))
        {
          Border border = new Border(id, wayNodes, element);
          borderMap.put(id, border);
        }
        else if (isItWhatWeNeed(element, Landuse.LANDUSE))
        {
          Landuse landuse = new Landuse(id, wayNodes, element);
          landuseMap.put(id, landuse);
        }
      }
    }
  }

  private boolean isItWhatWeNeed(Element element, String whatWeNeed)
  {
    NodeList tags = getTags(element);
    for (int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      if (key.equals(whatWeNeed)){
        return true;
      }
    }
    return false;
  }

  private boolean isNatural(Element element)
  {
    NodeList tags = getTags(element);
    for (int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      if (key.equals(Natural.NATURAL)){
        return true;
      }
    }
    return false;
  }

  private boolean isWaterway(Element element)
  {
    NodeList tags = getTags(element);
    for (int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      if (key.equals(Waterway.WATERWAY)){
        return true;
      }
    }
    return false;
  }

  private boolean isLeisure(Element element)
  {
    NodeList tags = getTags(element);
    for (int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      if (key.equals(Leisure.LEISURE)){
        return true;
      }
    }
    return false;
  }

  private boolean isHighway(Element element) {
    NodeList tags = getTags(element);
    for (int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      if (key.equals(Highway.HIGHWAY)){
        return true;
      }
    }
    return false;
  }

  private boolean isBuilding(Element element) {
    NodeList tags = getTags(element);
    for(int i = 0; i < tags.getLength(); i++){
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      if (key.equals(Building.BUILDING)){
//        String value = item.getAttribute("v");
//        return value.equals("yes") || value.equals("house");
        return true;
      }
    }
    return false;
  }

  private boolean isBorder(Element element)
  {
    NodeList tags = getTags(element);
    for (int i = 0; i < tags.getLength(); i++)
    {
      Element item = (Element) tags.item(i);
      String key = item.getAttribute("k");
      if (key.equals(Border.BORDER))
      {
        return true;
      }
    }
    return false;
  }

  private NodeList getTags(Element element)
  {
    return element.getElementsByTagName("tag");
  }

  @Override
  public BoundsLatLon getBounds()
  {
    return new BoundsLatLon(minLat, minLon, maxLat, maxLon);
  }
}
