package md.harta.geometry;

import md.harta.projector.AbstractProjector;
import md.harta.projector.MercatorProjector;
import org.junit.Assert;
import org.junit.Test;

public class GeometryUtilTest
{

  @Test
  public void testGetLine1() throws Exception {
    Line line = GeometryUtil.getLine(new XYPoint(1, 1), new XYPoint(2, 2));
    Assert.assertEquals(new Line(-1, 1, 0), line);
  }

  @Test
  public void testGetLine2() throws Exception {
    Line line = GeometryUtil.getLine(new XYPoint(0, 2), new XYPoint(2, 0));
    Assert.assertEquals(new Line(2, 2, -4), line);
  }

  @Test
  public void testPerpendicular45() throws Exception {
    Line line = GeometryUtil.getLine(new XYPoint(1, 1), new XYPoint(2, 2));
    Line perpendicular = GeometryUtil.getPerpendicular(line, new XYPoint(1, 1));
    Assert.assertEquals(perpendicular, new Line(-1, -1, 2));
  }

  @Test
  public void testPerpendicular30() throws Exception {
    Line line = GeometryUtil.getLine(new XYPoint(1, 1), new XYPoint(3, 2));
    Line perpendicular = GeometryUtil.getPerpendicular(line, new XYPoint(1, 1));
    Assert.assertEquals(perpendicular, new Line(-2, -1, 3));
  }

  @Test
  public void testPerpendicularToVertical() throws Exception {
    Line line = GeometryUtil.getLine(new XYPoint(1, 1), new XYPoint(1, 2));
    Line perpendicular = GeometryUtil.getPerpendicular(line, new XYPoint(1, 2));
    Assert.assertEquals(perpendicular, new Line(0, -1, 2));
  }

  @Test
  public void testPerpendicularToHorizontal() throws Exception {
    Line line = GeometryUtil.getLine(new XYPoint(1, 1), new XYPoint(2, 1));
    Line perpendicular = GeometryUtil.getPerpendicular(line, new XYPoint(2, 1));
    Assert.assertEquals(perpendicular, new Line(-1, 0, 2));
  }

//  @Test
//  public void getPerpendicularPoints() throws Exception {
//    Line line = GeometryUtil.getLine(new XYPoint(1, 1), new XYPoint(2, 2));
//    XYPoint[] perpendicularPoints = GeometryUtil.getPerpendicularPoints(line, new XYPoint(3, 3), Math.sqrt(2));
//    Assert.assertEquals(new XYPoint(2, 4), perpendicularPoints[0]);
//    Assert.assertEquals(new XYPoint(4, 2), perpendicularPoints[1]);
//  }
//
//  @Test
//  public void getPerpendicularPointsHorizontal() throws Exception {
//    Line line = GeometryUtil.getLine(new XYPoint(1, 1), new XYPoint(2, 1));
//    XYPoint[] perpendicularPoints = GeometryUtil.getPerpendicularPoints(line, new XYPoint(3, 1), 1);
//    Assert.assertEquals(new XYPoint(3, 0), perpendicularPoints[0]);
//    Assert.assertEquals(new XYPoint(3, 2), perpendicularPoints[1]);
//  }
//
//  @Test
//  public void getPerpendicularPointsVertical() throws Exception {
//    Line line = GeometryUtil.getLine(new XYPoint(1, 1), new XYPoint(1, 2));
//    XYPoint[] perpendicularPoints = GeometryUtil.getPerpendicularPoints(line, new XYPoint(1, 3), 1);
//    Assert.assertEquals(new XYPoint(0, 3), perpendicularPoints[0]);
//    Assert.assertEquals(new XYPoint(2, 3), perpendicularPoints[1]);
//  }

  @Test
  public void getDistanceOrtoEquatorTest1degree(){
    LatLonPoint pointA = new LatLonPoint(0, 0);
    LatLonPoint pointB = new LatLonPoint(0, 1);
    double distance = GeometryUtil.getDistanceOrtodroma(pointA, pointB);
    Assert.assertEquals(111111, distance, 1000);
  }

  @Test
  public void getDistanceOrtoEquatorTest01degree(){
    LatLonPoint pointA = new LatLonPoint(0, 0);
    LatLonPoint pointB = new LatLonPoint(0, 0.1);
    double distance = GeometryUtil.getDistanceOrtodroma(pointA, pointB);
    Assert.assertEquals(11111, distance, 100);
  }

  @Test
  public void getDistanceOrtoEquatorTest001degree(){
    LatLonPoint pointA = new LatLonPoint(0, 0);
    LatLonPoint pointB = new LatLonPoint(0, 0.01);
    double distance = GeometryUtil.getDistanceOrtodroma(pointA, pointB);
    Assert.assertEquals(1111, distance, 10);
  }

  @Test
  public void getDistanceOrtoEquatorTest0001degree(){
    LatLonPoint pointA = new LatLonPoint(0, 0);
    LatLonPoint pointB = new LatLonPoint(0, 0.001);
    double distance = GeometryUtil.getDistanceOrtodroma(pointA, pointB);
    Assert.assertEquals(111, distance, 1);
  }

  @Test
  public void getDistanceOrtoEquatorTest00001degree(){
    LatLonPoint pointA = new LatLonPoint(0, 0);
    LatLonPoint pointB = new LatLonPoint(0, 0.0001);
    double distance = GeometryUtil.getDistanceOrtodroma(pointA, pointB);
    Assert.assertEquals(11, distance, 0.2);
  }

  @Test
  public void getDistanceOrtoEquatorTest000001degree(){
    LatLonPoint pointA = new LatLonPoint(0, 0);
    LatLonPoint pointB = new LatLonPoint(0, 0.00001);
    double distance = GeometryUtil.getDistanceOrtodroma(pointA, pointB);
    Assert.assertEquals(1, distance, 0.2);
  }

  @Test
  public void getDistanceOrtoEquatorTest0000001degree(){
    LatLonPoint pointA = new LatLonPoint(0, 0);
    LatLonPoint pointB = new LatLonPoint(0, 0.000001);
    double distance = GeometryUtil.getDistanceOrtodroma(pointA, pointB);
    Assert.assertEquals(0.1, distance, 0.02);
  }

  @Test
  public void getDistanceOrtoEquatorTest00000001degree(){
    LatLonPoint pointA = new LatLonPoint(0, 0);
    LatLonPoint pointB = new LatLonPoint(0, 0.0000001);
    double distance = GeometryUtil.getDistanceOrtodroma(pointA, pointB);
    Assert.assertEquals(0.01, distance, 0.002);
  }

  @Test
  public void getRealDistanceTest(){
    LatLonPoint pointA = new LatLonPoint(46.9980284, 28.8710941);
    LatLonPoint pointB = new LatLonPoint(46.9980726, 28.8711904);
    MercatorProjector projector = new MercatorProjector(1, 1);
    System.out.println(GeometryUtil.getDistanceOrtodroma(pointA, pointB) * projector.getScale(pointA));
  }

  @Test
  public void getPerpendicularPointsTest1000Meters(){
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M, 85);
    LatLonPoint pointA = new LatLonPoint(46.9980284, 28.8710941);
     LatLonPoint pointB = new LatLonPoint(46.9980726, 28.8711904);
    Line line = GeometryUtil.getLine(projector.getXY(pointA), projector.getXY(pointB));
    XYPoint[] perpendicularPoints = GeometryUtil.getPerpendicularPoints(line, pointA, 1000, projector);
    LatLonPoint pointA1 = projector.getLatLon(perpendicularPoints[0]);
    LatLonPoint pointA2 = projector.getLatLon(perpendicularPoints[1]);
    double distanceOrtodroma = GeometryUtil.getDistanceOrtodroma(pointA1, pointA2);
    Assert.assertEquals(2000, distanceOrtodroma, 0.1);
  }

  @Test
  public void getPerpendicularPointsTest1Meter(){
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M, 85);
    LatLonPoint pointA = new LatLonPoint(46.9980284, 28.8710941);
    LatLonPoint pointB = new LatLonPoint(46.9980726, 28.8711904);
    Line line = GeometryUtil.getLine(projector.getXY(pointA), projector.getXY(pointB));
    XYPoint[] perpendicularPoints = GeometryUtil.getPerpendicularPoints(line, pointA, 1, projector);
    LatLonPoint pointA1 = projector.getLatLon(perpendicularPoints[0]);
    LatLonPoint pointA2 = projector.getLatLon(perpendicularPoints[1]);
    double distanceOrtodroma = GeometryUtil.getDistanceOrtodroma(pointA1, pointA2);
    System.out.println(distanceOrtodroma);
    Assert.assertEquals(2, distanceOrtodroma, 0.01);
  }

  @Test
  public void getPerpendicularPointsTest05Meter(){
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M, 85);
    LatLonPoint pointA = new LatLonPoint(46.9980284, 28.8710941);
    LatLonPoint pointB = new LatLonPoint(46.9980726, 28.8711904);
    Line line = GeometryUtil.getLine(projector.getXY(pointA), projector.getXY(pointB));
    XYPoint[] perpendicularPoints = GeometryUtil.getPerpendicularPoints(line, pointA, 0.5, projector);
    LatLonPoint pointA1 = projector.getLatLon(perpendicularPoints[0]);
    LatLonPoint pointA2 = projector.getLatLon(perpendicularPoints[1]);
    double distanceOrtodroma = GeometryUtil.getDistanceOrtodroma(pointA1, pointA2);
    System.out.println(distanceOrtodroma);
    Assert.assertEquals(1, distanceOrtodroma, 0.01);
  }

  @Test
  public void getPerpendicularPointsTest01Meter(){
    MercatorProjector projector = new MercatorProjector(AbstractProjector.EARTH_RADIUS_M, 85);
    LatLonPoint pointA = new LatLonPoint(46.9980284, 28.8710941);
    LatLonPoint pointB = new LatLonPoint(46.9980726, 28.8711904);
    Line line = GeometryUtil.getLine(projector.getXY(pointA), projector.getXY(pointB));
    XYPoint[] perpendicularPoints = GeometryUtil.getPerpendicularPoints(line, pointA, 0.1, projector);
    LatLonPoint pointA1 = projector.getLatLon(perpendicularPoints[0]);
    LatLonPoint pointA2 = projector.getLatLon(perpendicularPoints[1]);
    double distanceOrtodroma = GeometryUtil.getDistanceOrtodroma(pointA1, pointA2);
    System.out.println(distanceOrtodroma);
    Assert.assertEquals(0.2, distanceOrtodroma, 0.02);
  }

  @Test
  public void testLineInterSectionTestDiagonal()
  {
    XYPoint startPoint = new XYPoint(10, 10);
    XYPoint endPoint = new XYPoint(20, 20);
    Line lineA = GeometryUtil.getLine(startPoint, endPoint);
    Line lineB = GeometryUtil.getLine(new XYPoint(10, 20), new XYPoint(20, 10));

    XYPoint intersection = GeometryUtil.getLineIntersection(lineA, lineB, startPoint, endPoint, true);

    Assert.assertEquals(15.0, intersection.getX(), 0.001);
    Assert.assertEquals(15.0, intersection.getY(), 0.001);
  }

  @Test
  public void testLineInterSectionTestOrthogonal()
  {
    XYPoint startPoint = new XYPoint(15, 10);
    XYPoint endPoint = new XYPoint(15, 20);
    Line lineA = GeometryUtil.getLine(startPoint, endPoint);
    Line lineB = GeometryUtil.getLine(new XYPoint(10, 15), new XYPoint(20, 15));

    XYPoint intersection = GeometryUtil.getLineIntersection(lineA, lineB, startPoint, endPoint, true);

    Assert.assertEquals(15.0, intersection.getX(), 0.001);
    Assert.assertEquals(15.0, intersection.getY(), 0.001);
  }

  @Test
  public void testNoIntersectionParallels()
  {
    XYPoint startPoint = new XYPoint(10, 10);
    XYPoint endPoint = new XYPoint(20, 20);
    Line lineA = GeometryUtil.getLine(startPoint, endPoint);
    Line lineB = GeometryUtil.getLine(new XYPoint(20, 20), new XYPoint(30, 30));

    XYPoint intersection = GeometryUtil.getLineIntersection(lineA, lineB, startPoint, endPoint, true);

    Assert.assertNull(intersection);
  }

  @Test
  public void testNoIntersectionLimited()
  {
    XYPoint startPoint = new XYPoint(10, 10);
    XYPoint endPoint = new XYPoint(20, 20);
    Line lineA = GeometryUtil.getLine(startPoint, endPoint);
    Line lineB = GeometryUtil.getLine(new XYPoint(40, 10), new XYPoint(30, 20));

    XYPoint intersection = GeometryUtil.getLineIntersection(lineA, lineB, startPoint, endPoint, true);

    Assert.assertNull(intersection);
  }

  @Test
  public void testNoIntersectionLimitedNoCheck()
  {
    XYPoint startPoint = new XYPoint(10, 10);
    XYPoint endPoint = new XYPoint(20, 20);
    Line lineA = GeometryUtil.getLine(startPoint, endPoint);
    Line lineB = GeometryUtil.getLine(new XYPoint(40, 10), new XYPoint(30, 20));

    XYPoint intersection = GeometryUtil.getLineIntersection(lineA, lineB, startPoint, endPoint, false);

    Assert.assertEquals(25, intersection.getX(), 0.00001);
    Assert.assertEquals(25, intersection.getY(), 0.00001);
  }

  @Test
  public void verticalRoadLengthTest()
  {
    XYPoint pointA = new XYPoint(10, 10);
    XYPoint pointB = new XYPoint(10, 20);

    double dx = Math.abs(pointA.getX() - pointB.getX());
    double dy = Math.abs(pointA.getY() - pointB.getY());

    double length = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

    Assert.assertEquals(10, length, 0.000001);
  }

  @Test
  public void horizontalRoadLengthTest()
  {
    XYPoint pointA = new XYPoint(10, 10);
    XYPoint pointB = new XYPoint(20, 10);

    double dx = Math.abs(pointA.getX() - pointB.getX());
    double dy = Math.abs(pointA.getY() - pointB.getY());

    double length = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

    Assert.assertEquals(10, length, 0.000001);
  }

  @Test
  public void diagonalRoadLengthTest()
  {
    XYPoint pointA = new XYPoint(10, 10);
    XYPoint pointB = new XYPoint(20, 20);

    double dx = Math.abs(pointA.getX() - pointB.getX());
    double dy = Math.abs(pointA.getY() - pointB.getY());

    double length = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

    Assert.assertEquals(10 * Math.sqrt(2), length, 0.000001);
  }

  @Test
  public void testHorizontalLineCircleIntersection1Point()
  {
    XYPoint leftPoint = new XYPoint(10, 20);
    XYPoint rightPoint = new XYPoint(20, 20);
    Line line = GeometryUtil.getLine(leftPoint, rightPoint);
    Circle circle = new Circle(new XYPoint(15, 15), 5);

    XYPoint[] intersection = GeometryUtil.getLineCircleIntersection(line, circle);
    Assert.assertEquals(new XYPoint(15, 20), intersection[0]);
  }

  @Test
  public void testHorizontalLineCircleIntersectionOtherPoint()
  {
    XYPoint leftPoint = new XYPoint(10, 20);
    XYPoint rightPoint = new XYPoint(40, 20);
    Line line = GeometryUtil.getLine(leftPoint, rightPoint);
    Circle circle = new Circle(new XYPoint(25, 18), 5);

    XYPoint[] intersection = GeometryUtil.getLineCircleIntersection(line, circle);
    Assert.assertEquals(new XYPoint(25, 20), intersection[0]);
  }

  @Test
  public void testVerticalLineCircleIntersection1Point()
  {
    XYPoint leftPoint = new XYPoint(10, 10);
    XYPoint rightPoint = new XYPoint(10, 20);
    Line line = GeometryUtil.getLine(leftPoint, rightPoint);
    Circle circle = new Circle(new XYPoint(10, 10), 5);

    XYPoint[] intersection = GeometryUtil.getLineCircleIntersection(line, circle);
    Assert.assertEquals(new XYPoint(10, 15), intersection[0]);
  }

  @Test
  public void testDiagonalLineCircleIntersection1Point()
  {
    XYPoint leftPoint = new XYPoint(10, 10);
    XYPoint rightPoint = new XYPoint(20, 20);
    Line line = GeometryUtil.getLine(leftPoint, rightPoint);
    Circle circle = new Circle(new XYPoint(10, 10), 5);

    XYPoint[] intersection = GeometryUtil.getLineCircleIntersection(line, circle);
    Assert.assertEquals(new XYPoint(10 + 5 / Math.sqrt(2), 10 + 5 / Math.sqrt(2)), intersection[0]);
  }
}