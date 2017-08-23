package com.rmsi.android.mast.util;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;



public class GeomFunctions 
{
	public static boolean IsPointInPolygon(Point ptClicked, Polygon polygon )
	{
		if (polygon.contains(ptClicked)) 
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
		if(geomtype.equalsIgnoreCase(CommonFunctions.GEOM_POINT))
		{
			LatLng point = pointslist.get(0);

			//WKTString = "POINT ("+point.longitude+" "+point.latitude+")";
			WKTString = point.longitude+" "+point.latitude;
		}
		else if(geomtype.equalsIgnoreCase(CommonFunctions.GEOM_LINE))
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
		else if(geomtype.equalsIgnoreCase(CommonFunctions.GEOM_POLYGON))
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
}