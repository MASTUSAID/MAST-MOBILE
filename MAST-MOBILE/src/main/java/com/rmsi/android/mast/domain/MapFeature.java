package com.rmsi.android.mast.domain;

import android.graphics.Color;
import android.graphics.Point;

import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GisUtility;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Light version of the feature, containing properties and methods to show on the map
 */
public class MapFeature {
    private Feature feature;
    private List<LatLng> points = new ArrayList<>();
    private List<android.graphics.Point> screenPoints;
    private PolygonOptions polygon;
    private CircleOptions point;
    private MarkerOptions label;
    private Marker mapLabel;
    private List<MarkerOptions> vertices;
    private List<Marker> mapVertices;
    private PolylineOptions line;
    private Polygon mapPolygon;
    private com.vividsolutions.jts.geom.Polygon wktPolygon;
    private com.vividsolutions.jts.geom.LineString wktLine;
    private com.vividsolutions.jts.geom.Point wktPoint;
    private Circle mapPoint;
    private Polyline mapLine;
    private TYPE featureType;

    public enum TYPE {POLYGON, LINE, POINT};

    public MapFeature(Feature feature){
        this.feature = feature;
        buildMapFeature();
    }

    public Feature getFeature() {
        return feature;
    }

    public void setFeature(Feature feature) {
        this.feature = feature;
        buildMapFeature();
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public List<Point> getScreenPoints() {
        if(screenPoints == null)
            screenPoints = new ArrayList<>();
        return screenPoints;
    }

    public PolygonOptions getPolygon() {
        if(polygon == null && points != null && points.size() > 0){
            polygon = new PolygonOptions();
            for (LatLng p : points) {
                polygon.add(p);
            }
            polygon.fillColor(Color.argb(0, 0, 0, 0)).strokeWidth(5).strokeColor(Color.argb(255, 255, 200, 0));
            polygon.zIndex(4);
        }
        return polygon;
    }

    public com.vividsolutions.jts.geom.Polygon getWktPolygon() {
        if(wktPolygon == null && points != null && points.size() > 0){
            try {
                wktPolygon = (com.vividsolutions.jts.geom.Polygon) CommonFunctions.getInstance().getWktReader()
                        .read("POLYGON ((" + feature.getCoordinates() + "))");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return wktPolygon;
    }

    public PolylineOptions getLine() {
        if(line == null && points != null && points.size() > 0){
            line = new PolylineOptions();
            if (feature.getStatus().equalsIgnoreCase("final")) {
                line.color(Color.rgb(204, 153, 255));
            } else if (feature.getStatus().equalsIgnoreCase("rejected")) {
                line.color(Color.rgb(255, 51, 51));
            } else if (feature.getStatus().equalsIgnoreCase("complete")) {
                line.color(Color.rgb(128, 255, 0));
            } else
                line.color(CommonFunctions.lineColor);

            for (LatLng p : points) {
                line.add(p);
            }
            line.zIndex(4);
        }
        return line;
    }

    public LineString getWktLine() {
        if(wktLine == null && points != null && points.size() > 0){
            try {
                wktLine = (com.vividsolutions.jts.geom.LineString) CommonFunctions.getInstance().getWktReader()
                        .read("LINESTRING (" + feature.getCoordinates() + ")");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return wktLine;
    }

    public Polygon getMapPolygon() {
        return mapPolygon;
    }

    public void setMapPolygon(Polygon mapPolygon) {
        this.mapPolygon = mapPolygon;
    }

    public CircleOptions getPoint() {
        if(point == null && points != null && points.size() > 0){
            point = new CircleOptions().center(points.get(0));
            point.radius(3); // In meters
            if (feature.getStatus().equalsIgnoreCase("final")) {
                point.fillColor(Color.argb(100, 204, 153, 255)).strokeWidth(5).strokeColor(Color.BLACK);
            } else if (feature.getStatus().equalsIgnoreCase("rejected")) {
                point.fillColor(Color.argb(100, 255, 51, 51)).strokeWidth(4).strokeColor(Color.BLACK);
            } else if (feature.getStatus().equalsIgnoreCase("complete")) {
                point.fillColor(Color.argb(150, 204, 255, 153)).strokeWidth(5).strokeColor(Color.BLACK);
            } else
                point.fillColor(CommonFunctions.pointColor).strokeWidth(4).strokeColor(Color.BLUE);
            point.zIndex(4);
        }
        return point;
    }

    public com.vividsolutions.jts.geom.Point getWktPoint() {
        if(wktPoint == null && points != null && points.size() > 0){
            try {
                wktPoint = (com.vividsolutions.jts.geom.Point) CommonFunctions.getInstance().getWktReader()
                        .read("POINT (" + feature.getCoordinates() + ")");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return wktPoint;
    }

    public Circle getMapPoint() {
        return mapPoint;
    }

    public void setMapPoint(Circle mapPoint) {
        this.mapPoint = mapPoint;
    }

    public Polyline getMapLine() {
        return mapLine;
    }

    public void setMapLine(Polyline mapLine) {
        this.mapLine = mapLine;
    }

    public TYPE getFeatureType() {
        return featureType;
    }

    public MarkerOptions getLabel() {
        if(label == null){
            label = GisUtility.makeLabel(feature);
        }
        return label;
    }

    public List<MarkerOptions> getVertices() {
        if(vertices == null)
            vertices = new ArrayList<>();
        return vertices;
    }

    public Marker getMapLabel() {
        return mapLabel;
    }

    public void setMapLabel(Marker mapLabel) {
        this.mapLabel = mapLabel;
    }

    public List<Marker> getMapVertices() {
        if(mapVertices == null)
            mapVertices = new ArrayList<>();
        return mapVertices;
    }

    private void buildMapFeature(){
        points = new ArrayList<>();
        screenPoints = new ArrayList<>();
        polygon = null;
        wktPolygon = null;
        point = null;
        wktPoint = null;
        label = null;
        line = null;
        wktLine = null;
        vertices = null;

        removeFromMap();

        if(feature != null){
            String coordinates = feature.getCoordinates().replaceAll(", ", ",");
            String[] wktPoints = null;

            if(feature.getGeomType().equalsIgnoreCase(Feature.GEOM_POLYGON)) {
                featureType = TYPE.POLYGON;
                wktPoints = coordinates.split(",");
            } else if(feature.getGeomType().equalsIgnoreCase(Feature.GEOM_LINE)) {
                featureType = TYPE.LINE;
                wktPoints = coordinates.split(",");
            } else {
                featureType = TYPE.POINT;
                wktPoints = coordinates.split(" ");
            }

            if(featureType == TYPE.POLYGON || featureType == TYPE.LINE) {
                for (String wktPoint : wktPoints) {
                    String[] tmpPoint = wktPoint.split(" ");
                    points.add(new LatLng(Double.parseDouble(tmpPoint[1]), Double.parseDouble(tmpPoint[0])));
                }
            } else {
                String[] tmpPoint = coordinates.split(" ");
                points.add(new LatLng(Double.parseDouble(tmpPoint[1]), Double.parseDouble(tmpPoint[0])));
            }
        }
    }

    public void removeFromMap(){
        if(mapPolygon != null){
            mapPolygon.remove();
            mapPolygon = null;
        }
        if(mapLine != null){
            mapLine.remove();
            mapLine = null;
        }
        if(mapPoint != null){
            mapPoint.remove();
            mapPoint = null;
        }
        removeMapLabel();
        removeMapVertices();
    }

    public void removeMapLabel(){
        if(mapLabel != null){
            mapLabel.remove();
            mapLabel = null;
        }
    }

    public void removeMapVertices(){
        if(getMapVertices() != null){
            for(Marker vertex : mapVertices){
                vertex.remove();
            }
            mapVertices.clear();
        }
    }

    /** Returns true if any point of the feature falls into provided bounds */
    public boolean containsInBoundary(LatLngBounds bounds){
        for(LatLng p : points){
            if(bounds.contains(p))
                return true;
        }
        return false;
    }

    public void calculateScreenPoints(Projection projection) {
        getScreenPoints().clear();
        for(LatLng p : getPoints()){
            screenPoints.add(projection.toScreenLocation(p));
        }
    }

    public LatLng getPointToSnap(LatLngBounds bounds, android.graphics.Point pointToTest, int snappingTolerance){
        for (int i = 0; i < getScreenPoints().size(); i++){
            if (GisUtility.getDistanceBetweenPoints(screenPoints.get(i), pointToTest) <= snappingTolerance) {
                // Test point to be in the visible bound
                if(bounds.contains(points.get(i)))
                    return points.get(i);
            }
        }
        return null;
    }

    public LatLng getSegmentPointToSnap(LatLngBounds bounds, LatLng pointToTest, android.graphics.Point screenPointToTest, int snappingTolerance){
        for (int i = 0; i < getScreenPoints().size(); i++){
            if(i + 1 < getScreenPoints().size()){
                if (GisUtility.getDistanceToSegment(screenPointToTest, screenPoints.get(i), screenPoints.get(i + 1)) <= snappingTolerance) {
                    LatLng pointToSnap = GisUtility.getClosestPointOnSegment(pointToTest, points.get(i), points.get(i + 1));
                    if(bounds.contains(pointToSnap))
                        return pointToSnap;
                }
            }
        }
        return null;
    }

    public boolean containsPoint(com.vividsolutions.jts.geom.Point p){
        if (getFeatureType() == TYPE.POLYGON) {
            if(getWktPolygon() != null)
                return GisUtility.IsPointInPolygon(p, wktPolygon);
        } else if (getFeatureType() == TYPE.LINE) {
            if(getWktLine() != null)
                return GisUtility.IsPointIntersectsLine(p, wktLine);
        } else if (getFeatureType() == TYPE.POINT) {
            if(getWktPoint() != null)
                return GisUtility.IsPointIntersectsPoint(p, wktPoint);
        }
        return false;
    }
}
