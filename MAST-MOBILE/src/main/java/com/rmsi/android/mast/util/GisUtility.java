package com.rmsi.android.mast.util;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.domain.Feature;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import java.util.List;

/**
 * Contains various GIS functions
 */

public class GisUtility {
    private static WKTReader wktReader;
    private static IconGenerator iconFactory;

    /**
     * Returns distance between two points represented by Point coordinates
     */
    public static double getDistanceBetweenPoints(android.graphics.Point point1, android.graphics.Point point2){
        return Math.sqrt(Math.pow(point2.x-point1.x, 2) + Math.pow(point2.y - point1.y, 2));
    }

    /**
     * Returns distance between two points represented by LatLng coordinates
     */
    public static double getDistanceBetweenPoints(LatLng point1, LatLng point2){
        return Math.sqrt(Math.pow(point2.longitude-point1.longitude, 2) + Math.pow(point2.latitude - point1.latitude, 2));
    }

    /**
     * Returns distance between two points represented by x, y coordinates
     */
    public static double getDistanceBetweenPoints(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
    }

    /**
     * Returns distance from the point represented by Point to the segment with Point coordinates
     * @param point The point from where to search the distance
     * @param segmentPoint1 First point of the segment
     * @param segmentPoint2 Second point of the segment
     * @return
     */
    public static double getDistanceToSegment(android.graphics.Point point, android.graphics.Point segmentPoint1, android.graphics.Point segmentPoint2) {
        return getDistanceToSegment(point.x, point.y, segmentPoint1.x, segmentPoint1.y, segmentPoint2.x, segmentPoint2.y);
    }

    /**
     * Returns distance from the point represented by LatLng to the segment with LatLng coordinates
     * @param point The point from where to search the distance
     * @param segmentPoint1 First point of the segment
     * @param segmentPoint2 Second point of the segment
     * @return
     */
    public static double getDistanceToSegment(LatLng point, LatLng segmentPoint1, LatLng segmentPoint2) {
        return getDistanceToSegment(point.longitude, point.latitude, segmentPoint1.longitude,
                segmentPoint1.latitude, segmentPoint2.longitude, segmentPoint2.latitude);
    }

    /**
     * Returns distance from point with x, y coordinates to the segment with points x1, y1 and x2, y2
     * @param px X coordinate of the point
     * @param py Y coordinate of the point
     * @param sx1 X coordinate of the first segment point
     * @param sy1 Y coordinate of the first segment point
     * @param sx2 X coordinate of the second segment point
     * @param sy2 Y coordinate of the second segment point
     * @return
     */
    public static double getDistanceToSegment(double px, double py, double sx1, double sy1, double sx2, double sy2) {
        double A = px - sx1;
        double B = py - sy1;
        double C = sx2 - sx1;
        double D = sy2 - sy1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = -1;
        if (len_sq != 0) //in case of 0 length line
            param = dot / len_sq;

        double xx, yy;

        if (param < 0) {
            xx = sx1;
            yy = sy1;
        }
        else if (param > 1) {
            xx = sx2;
            yy = sy2;
        }
        else {
            xx = sx1 + param * C;
            yy = sy1 + param * D;
        }

        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Returns the closest point on the segment from provided point
     * @param point The point from where to search
     * @param segmentPoint1 First segment point
     * @param segmentPoint2 Second segment point
     * @return
     */
    public static LatLng getClosestPointOnSegment(LatLng point, LatLng segmentPoint1, LatLng segmentPoint2)
    {
        return getClosestPointOnSegment(point.longitude, point.latitude,
                segmentPoint1.longitude, segmentPoint1.latitude,
                segmentPoint2.longitude, segmentPoint2.latitude);
    }

    /**
     * Returns the closest point on the segment from provided point
     * @param px X coordinate of the point
     * @param py Y coordinate of the point
     * @param sx1 X coordinate of the first segment point
     * @param sy1 Y coordinate of the first segment point
     * @param sx2 X coordinate of the second segment point
     * @param sy2 Y coordinate of the second segment point
     * @return
     */
    public static LatLng getClosestPointOnSegment(double px, double py, double sx1, double sy1, double sx2, double sy2)
    {
        double xDelta = sx2 - sx1;
        double yDelta = sy2 - sy1;

        if ((xDelta == 0) && (yDelta == 0))
        {
            return new LatLng(sy1, sx1);
        }

        double u = ((px - sx1) * xDelta + (py - sy1) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

        final LatLng closestPoint;
        if (u < 0)
        {
            closestPoint = new LatLng(sy1, sx1);
        }
        else if (u > 1)
        {
            closestPoint = new LatLng(sy2, sx2);
        }
        else
        {
            closestPoint = new LatLng(sy1 + u * yDelta, sx1 + u * xDelta);
        }

        return closestPoint;
    }

    public static boolean IsPointInPolygon(Point ptClicked, Polygon polygon )
    {
        if (polygon.contains(ptClicked) || polygon.touches(ptClicked))
        {
            return true;
        }else
        {
            return false;
        }
    }

    public static boolean IsPointIntersectsLine(Point ptClicked, LineString polyline)
    {
        //0.00005 = Approx 5 meter
        if (polyline.intersects(ptClicked.buffer(0.00015)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static boolean IsPointIntersectsPoint(Point ptClicked, Point featurePoint)
    {
        double distance = featurePoint.distance(ptClicked);

        if (distance < 0.00010)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static String getWKTfromPoints(String geomtype, List<LatLng> pointslist)
    {
        String WKTString="";
        if(geomtype.equalsIgnoreCase(Feature.GEOM_POINT))
        {
            LatLng point = pointslist.get(0);

            //WKTString = "POINT ("+point.longitude+" "+point.latitude+")";
            WKTString = point.longitude+" "+point.latitude;
        }
        else if(geomtype.equalsIgnoreCase(Feature.GEOM_LINE))
        {
            String WKTSubStr="";

            for (int i = 0; i < pointslist.size(); i++)
            {
                LatLng latLng = pointslist.get(i);

                if(i > 0)
                {
                    WKTSubStr = WKTSubStr +",";
                }

                WKTSubStr = WKTSubStr + latLng.longitude+" "+latLng.latitude;
            }
            //WKTString = "LINESTRING ("+WKTSubStr+")";
            WKTString = WKTSubStr;
        }
        else if(geomtype.equalsIgnoreCase(Feature.GEOM_POLYGON))
        {
            String WKTSubStr="";

            for (int i = 0; i < pointslist.size(); i++)
            {
                LatLng latLng = pointslist.get(i);

                if(i > 0)
                {
                    WKTSubStr = WKTSubStr +",";
                }

                WKTSubStr = WKTSubStr + latLng.longitude+" "+latLng.latitude;
            }
            //WKTString = "POLYGON (("+WKTSubStr+"))";
            WKTString = WKTSubStr;
        }
        return WKTString;
    }

    /** Vertex type enumeration. */
    public static enum VERTEX_TYPE {NORMAL, SNAPPED, MOVING};

    /**
     * Processes string representing bounding box with 2 coordinate and returns LatLngBounds
     * @param strBounds Bounding box string
     * @return
     */
    public static LatLngBounds getBoundsFromString(String strBounds){
        if(strBounds == null || strBounds.equals("")){
            return null;
        }
        String[] xy = strBounds.replace(" ", "").split(",");
        if(xy == null || xy.length !=4){
            return null;
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(Double.parseDouble(xy[1]), Double.parseDouble(xy[0])));
        builder.include(new LatLng(Double.parseDouble(xy[3]), Double.parseDouble(xy[2])));
        return builder.build();
    }

    /**
     * Returns map extent
     */
    public static LatLngBounds getMapExtent(){
        return getBoundsFromString(CommonFunctions.getInstance().getMapExtent());
    }

    /**
     * Zooms to the default map extent
     * @param googleMap Google map component to zoom on
     */
    public static void zoomToMapExtent(GoogleMap googleMap){
        LatLngBounds mapExtent = GisUtility.getMapExtent();
        if(mapExtent != null){
            int padding = 0;
            com.google.android.gms.maps.CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(mapExtent, padding);
            googleMap.moveCamera(cu);
            googleMap.animateCamera(cu);
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(CommonFunctions.latitude, CommonFunctions.longitude), 15)
            );
        }
    }

    /**
     * Zooms to the polyline
     * @param googleMap Google map component to zoom on
     * @param line Polyline to zoom to
     */
    public static void zoomToPolyline(GoogleMap googleMap, PolylineOptions line){
        if(line != null)
            zoomToPoints(googleMap, line.getPoints());
    }

    /**
     * Zooms to the polygon
     * @param googleMap Google map component to zoom on
     * @param polygon Polygon to zoom to
     */
    public static void zoomToPolygon(GoogleMap googleMap, PolygonOptions polygon){
        if(polygon != null)
            zoomToPoints(googleMap, polygon.getPoints());
    }

    /**
     * Zooms to list of points representing polygon or polyline. Muat be at least 2 points
     * @param googleMap Google map component to zoom on
     * @param points Points representing polygon or polyline where to zoom to
     */
    public static void zoomToPoints(GoogleMap googleMap, List<LatLng> points){
        if(googleMap == null || points == null || points.size() < 2){
            return;
        }

        double maxX = 0;
        double minX = 0;
        double maxY = 0;
        double minY = 0;
        int i = 0;

        for(LatLng coord : points){
            if(i == 0){
                minX = coord.longitude;
                maxX = coord.longitude;
                minY = coord.latitude;
                maxY = coord.latitude;
            } else {
                if(coord.longitude > maxX)
                    maxX = coord.longitude;
                if(coord.longitude < minX)
                    minX = coord.longitude;
                if(coord.latitude > maxY)
                    maxY = coord.latitude;
                if(coord.latitude < minY)
                    minY = coord.latitude;
            }
            i+=1;
        }

        LatLngBounds extent = new LatLngBounds(new LatLng(minY, minX), new LatLng(maxY, maxX));
        if(extent != null) {
            com.google.android.gms.maps.CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(extent, 30);
            googleMap.moveCamera(cu);
            googleMap.animateCamera(cu);
        }
    }

    /**
     * Creates vertex marker from provided point
     * @param point Point to be used as vertex location
     * @param vertexType Type of the vertex
     */
    public static MarkerOptions makeVertex(LatLng point, VERTEX_TYPE vertexType){
        MarkerOptions m = new MarkerOptions();
        m.position(point);
        m.draggable(false);
        m.anchor(0.5f, 0.5f);

        switch (vertexType){
            case NORMAL:
                m.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_circle_vertex));
            default:
                m.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_circle_vertex));
        }
        return m;
    }

    /**
     * Makes feature label
     * @param feature Feature object to be used to create a label for
     */
    public static MarkerOptions makeLabel(Feature feature){
        if(wktReader == null){
            wktReader = new WKTReader();
        }

        if(iconFactory == null) {
            iconFactory = new IconGenerator(CommonFunctions.getApplicationContext());
            iconFactory.setBackground(null);
            iconFactory.setTextAppearance(R.style.parcelLabel);
        }

        try {
            Polygon poly = (Polygon) wktReader.read("POLYGON ((" + feature.getCoordinates() + "))");
            Point center = poly.getCentroid();

            String label = "";
            if(!StringUtility.isEmpty(feature.getPolygonNumber())){
                label = feature.getPolygonNumber();
            } else {
                label = "Polygon " + feature.getId();
            }

            return new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(label)))
                    .position(new LatLng(center.getY(), center.getX()))
                    .anchor(0.5f, 0.5f).draggable(false);
        } catch (ParseException e) {
            e.printStackTrace();
            CommonFunctions.getInstance().appLog("", e);
        }

        return null;
    }

    /**
     * Finds the biggest distance between min/max X or min/max Y. Which one is bigger will be returned in pixels.
     * @param  points List of points to test
     * @param projection Map projection to be used for calculating screen points
     */
    public static double getBiggestDistanceInPixels(List<LatLng> points, Projection projection){
        if(points == null || points.size() < 1)
            return 0;

        // Find min/max X and Y
        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;
        boolean first = true;

        for(LatLng p : points){
            if(first){
                first = false;
                minX = p.longitude;
                maxX = p.longitude;
                minY = p.latitude;
                maxY = p.latitude;
            } else {
                if(p.longitude < minX)
                    minX = p.longitude;
                else if(p.longitude > maxX)
                    maxX = p.longitude;

                if(p.latitude < minY)
                    minY = p.latitude;
                else if(p.latitude > maxY)
                    maxY = p.latitude;
            }
        }

        if(minX == 0 && maxX == 0 && minY == 0 && maxY == 0)
            return 0;

        double result = 0 ;
        LatLng point1 = new LatLng(maxY, maxX);
        LatLng point2 = new LatLng(minY, minX);
        android.graphics.Point p1 = projection.toScreenLocation(point1);
        android.graphics.Point p2 = projection.toScreenLocation(point2);

        if(Math.abs(maxX - minX) > Math.abs(maxY - minY)){
            // find distance by X
            result = Math.abs(p1.x - p2.x);
        } else {
            // find distance by Y
            result = Math.abs(p1.y - p2.y);
        }

        return result;
    }
}
