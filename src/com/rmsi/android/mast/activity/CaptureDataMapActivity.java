package com.rmsi.android.mast.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.rmsi.android.mast.adapter.AddFeaturesOptionsAdapter;
import com.rmsi.android.mast.db.DBController;
import com.rmsi.android.mast.domain.Bookmark;
import com.rmsi.android.mast.domain.Feature;
import com.rmsi.android.mast.domain.ProjectSpatialDataDto;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GeomFunctions;
import com.rmsi.android.mast.util.OfflineTileProvider;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @author Prashant.Nigam
 */
public class CaptureDataMapActivity extends ActionBarActivity {
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (mDrawerList.isItemChecked(position))
				mDrawerList.setItemChecked(position, true);
			else
				mDrawerList.setItemChecked(position, false);
		}
	}

	// ########### MAP CLICK LISTENER STARTS ######
	// ####################################
	private class mapClickListener implements OnMapClickListener {
		@Override
		public void onMapClick(LatLng pointFromMap) {
			// Condition for DRAW MODE
			if (MAP_MODE == FEATURE_DRAW_MAP_MODE) {
				if (!contextualMenuShown)
					toolbar.startActionMode(myActionModeCallback);

				// for point
				points.add(pointFromMap);
				MarkerOptions marker = new MarkerOptions()
						.position(pointFromMap);
				marker.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
				marker.draggable(true);
				marker.snippet(pointFromMap.latitude + ","
						+ pointFromMap.longitude);
				Marker mark = googleMap.addMarker(marker);
				currMarkers.add(mark);

				if (dataCaptureMode.equalsIgnoreCase("Draw Point")) {
					if (points.size() > 1) {
						points.clear();
						currMarkers.clear();

						points.add(pointFromMap);
						currMarkers.add(mark);
						Marker tmp = (Marker) drawnFeature;
						tmp.remove();
					}
					drawnFeature = mark;
				} else if (dataCaptureMode.equalsIgnoreCase("Draw Line")) {
					if (points.size() > 1) {
						if (points.size() > 2) {
							Polyline polyline = (Polyline) drawnFeature;
							polyline.setPoints(points);
						} else {
							// for polyline
							PolylineOptions rectOptions = new PolylineOptions();

							for (LatLng p : points) {
								rectOptions.add(p);
							}
							rectOptions.zIndex(4);
							Polyline polyline = googleMap
									.addPolyline(rectOptions);
							drawnFeature = polyline;
						}
					}
				} else if (dataCaptureMode.equalsIgnoreCase("Draw Polygon")) {
					// for polygon
					if (points.size() > 2) {
						if (points.size() > 3) {
							Polygon polygon = (Polygon) drawnFeature;
							polygon.setPoints(points);
						} else {
							PolygonOptions rectOptions = new PolygonOptions();
							for (LatLng p : points) {
								rectOptions.add(p);
							}
							rectOptions.fillColor(Color.argb(80, 0, 255, 0))
									.strokeWidth(5);
							rectOptions.zIndex(4);
							Polygon polygon = googleMap.addPolygon(rectOptions);
							drawnFeature = polygon;
						}
					}
				}
			}// Condition for MAP_MEASURE_LINE_MODE MODE
			else if (MAP_MODE == MAP_MEASURE_LINE_MODE) {
				if (!contextualMenuShown)
					toolbar.startActionMode(myActionModeCallback);

				// for point
				Coordinate jtspoint = new Coordinate(pointFromMap.longitude,
						pointFromMap.latitude);
				jtspoints.add(jtspoint);
				points.add(pointFromMap);
				MarkerOptions marker = new MarkerOptions()
						.position(pointFromMap);
				marker.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
				marker.snippet(pointFromMap.latitude + ","
						+ pointFromMap.longitude);
				Marker mark = googleMap.addMarker(marker);
				currMarkers.add(mark);

				if (points.size() > 1) {
					Polyline polyline;
					if (points.size() > 2) {
						polyline = (Polyline) drawnFeature;
						polyline.setPoints(points);
					} else {
						// for polyline
						PolylineOptions rectOptions = new PolylineOptions();

						for (LatLng p : points) {
							rectOptions.add(p);
						}
						rectOptions.zIndex(4);
						polyline = googleMap.addPolyline(rectOptions);
						drawnFeature = polyline;
					}

					// calculating Length
					Coordinate[] coordinates = jtspoints
							.toArray(new Coordinate[jtspoints.size()]);
					GeometryFactory geometryFactory = new GeometryFactory(
							new PrecisionModel(), 4326);
					LineString lineString = geometryFactory
							.createLineString(coordinates);
					double lineLength = lineString.getLength();
					double lengthMeter = lineLength * (111319.9)
							* Math.cos(Math.toRadians(jtspoints.get(0).y));
					String lineLengthStr = df.format(lengthMeter);

					if (actionMode != null) {
						actionMode.setTitle(lineLengthStr + " m");
					}
				}
			}// Condition for MAP_MEASURE_POLYGON_MODE MODE
			else if (MAP_MODE == MAP_MEASURE_POLYGON_MODE) {
				if (!contextualMenuShown)
					toolbar.startActionMode(myActionModeCallback);

				// for point
				Coordinate jtspoint = new Coordinate(pointFromMap.longitude,
						pointFromMap.latitude);
				jtspoints.add(jtspoint);
				points.add(pointFromMap);
				MarkerOptions marker = new MarkerOptions()
						.position(pointFromMap);
				marker.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
				marker.snippet(pointFromMap.latitude + ","
						+ pointFromMap.longitude);
				Marker mark = googleMap.addMarker(marker);
				currMarkers.add(mark);

				// for polygon
				if (points.size() > 2) {
					if (points.size() > 3) {
						Polygon polygon = (Polygon) drawnFeature;
						polygon.setPoints(points);
					} else {
						PolygonOptions rectOptions = new PolygonOptions();
						for (LatLng p : points) {
							rectOptions.add(p);
						}
						rectOptions.fillColor(Color.argb(90, 255, 255, 0));
						rectOptions.zIndex(4);
						Polygon polygon = googleMap.addPolygon(rectOptions);
						drawnFeature = polygon;
					}

					// calculating AREA
					List<Coordinate> tmpJtsPoints = new ArrayList<Coordinate>(
							jtspoints);
					tmpJtsPoints.add(jtspoints.get(0)); // adding first point at
														// last to create Ring
					Coordinate[] coordinates = tmpJtsPoints
							.toArray(new Coordinate[tmpJtsPoints.size()]);
					GeometryFactory geometryFactory = new GeometryFactory(
							new PrecisionModel(), 4326);
					LinearRing lr = geometryFactory
							.createLinearRing(coordinates);
					com.vividsolutions.jts.geom.Polygon polygonJts = geometryFactory
							.createPolygon(lr, null);
					double area = polygonJts.getArea();
					double areaMeter = area * (Math.pow(111319.9, 2))
							* Math.cos(Math.toRadians(tmpJtsPoints.get(0).y));
					String areaStr = df.format(areaMeter);
					if (actionMode != null) {
						actionMode.setTitle(areaStr + " sqm");
					}
				}
			} else if (MAP_MODE == FEATURE_INFO_MODE) {
				processFeaturesInfo(pointFromMap);
				MAP_MODE = CLEAR_MODE;
			} else if (MAP_MODE == FEATURE_DELETE_MODE) {
				processFeaturesInfo(pointFromMap);
				MAP_MODE = CLEAR_MODE;
			} else if (MAP_MODE == FEATURE_EDIT_MODE) {
				processFeaturesInfo(pointFromMap);
				// MAP_MODE = CLEAR_MODE;
			} else if (MAP_MODE == MEASURE_FEATURE_AREA_MODE) {
				processFeaturesForMeasurement(pointFromMap);
			} else if (MAP_MODE == MEASURE_FEATURE_LENGTH_MODE) {
				processFeaturesForMeasurement(pointFromMap);
			}
		}
	}

	// ########### MAP LOADED LISTENER STARTS ######
	// ####################################
	private class mapLoadedCallBackListener implements OnMapLoadedCallback {
		@Override
		public void onMapLoaded() {
			// loadUserSelectedLayers();
			/*
			 * if(featureId!=0) { loadFeatureforEdit(); }
			 */
		}
	}

	// ########### MARKER DRAG LISTENER ENDS
	// ####################################
	// ########### MARKER CLICK LISTENER STARTS
	// ###################################
	private class markerClickListener implements OnMarkerClickListener {
		@Override
		public boolean onMarkerClick(Marker marker) {
			if (MAP_MODE == FEATURE_EDIT_MODE
					|| MAP_MODE == FEATURE_DELETE_MODE) {
				LatLng position = marker.getPosition();
				processFeaturesInfo(position);
			}
			return false;
		}
	}

	// ########### MARKER DRAG CLICK LISTENER STARTS
	// ####################################
	private class markerDragListener implements OnMarkerDragListener {
		@Override
		public void onMarkerDrag(Marker marker) {
			boolean changed = false;

			for (int i = 0; i < currMarkers.size(); i++) {
				if (marker.getId().equalsIgnoreCase(currMarkers.get(i).getId())) {
					currMarkers.set(i, marker);
					changed = true;
				}
			}

			if (changed) {
				if (drawnFeature instanceof Polygon) {
					Polygon poly = (Polygon) drawnFeature;
					PolygonOptions rectOptions = new PolygonOptions();
					for (Marker m : currMarkers) {
						LatLng p = m.getPosition();
						rectOptions.add(p);
					}
					rectOptions.fillColor(Color.argb(80, 0, 255, 0))
							.strokeColor(Color.RED);// .strokeWidth(5);
					rectOptions.zIndex(4);
					Polygon newpoly = googleMap.addPolygon(rectOptions);
					poly.remove();
					drawnFeature = newpoly;
				} else if (drawnFeature instanceof Polyline) {
					Polyline line = (Polyline) drawnFeature;
					PolylineOptions rectOptions = new PolylineOptions();
					for (Marker m : currMarkers) {
						LatLng p = m.getPosition();
						rectOptions.add(p);
					}
					rectOptions.color(Color.RED);// .width(5);
					rectOptions.zIndex(4);
					Polyline newline = googleMap.addPolyline(rectOptions);
					line.remove();
					drawnFeature = newline;
				}
			}
		}

		@Override
		public void onMarkerDragEnd(Marker marker) {
			boolean changed = false;
			for (int i = 0; i < currMarkers.size(); i++) {
				if (marker.getId().equalsIgnoreCase(currMarkers.get(i).getId())) {
					currMarkers.set(i, marker);
					changed = true;
				}
			}

			if (changed) {
				if (drawnFeature instanceof Polygon) {
					Polygon poly = (Polygon) drawnFeature;
					PolygonOptions rectOptions = new PolygonOptions();
					for (Marker m : currMarkers) {
						LatLng p = m.getPosition();
						rectOptions.add(p);
					}
					rectOptions.fillColor(Color.argb(80, 0, 255, 0))
							.strokeColor(Color.RED);// .strokeWidth(5);
					rectOptions.zIndex(4);
					poly.remove();
					poly = googleMap.addPolygon(rectOptions);
					drawnFeature = poly;
				} else if (drawnFeature instanceof Polyline) {
					Polyline line = (Polyline) drawnFeature;
					PolylineOptions rectOptions = new PolylineOptions();
					for (Marker m : currMarkers) {
						LatLng p = m.getPosition();
						rectOptions.add(p);
					}
					rectOptions.color(Color.RED);// .width(5);
					rectOptions.zIndex(4);
					line.remove();
					line = googleMap.addPolyline(rectOptions);
					drawnFeature = line;
				}
			}
		}

		@Override
		public void onMarkerDragStart(Marker marker) {

		}

	}

	// ############## Location Listener Class #################
	private class MyLocationListener implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			if (MAP_MODE == FEATURE_DRAW_LINE_GPS_MODE
					|| MAP_MODE == FEATURE_DRAW_POLYGON_GPS_MODE
					|| MAP_MODE == FEATURE_DRAW_POINT_GPS_MODE) {
				int satellitesInFix = 0;
				for (GpsSatellite sat : locationManager.getGpsStatus(null)
						.getSatellites()) {
					if (sat.usedInFix()) {
						satellitesInFix++;
					}
				}
				if (actionMode != null) {
					int accuracy = (int) locationManager.getLastKnownLocation(
							LocationManager.GPS_PROVIDER).getAccuracy();
					actionMode.setTitle(accuracy
							+ getResources().getString(R.string.gps_accuracy));
					actionMode.setSubtitle(satellitesInFix + " "
							+ getResources().getString(R.string.sats_use_msg));
				}
			}
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	Context context = this;
	private GoogleMap googleMap;
	private List<Marker> currMarkers = new ArrayList<Marker>();
	Object drawnFeature = new Object();
	List<LatLng> points = new ArrayList<LatLng>();
	private List<String> parentItems = new ArrayList<String>();
	private List<Object> childItems = new ArrayList<Object>();
	Integer lastExpandedGroup = null;
	ExpandableListView expandableList = null;
	Toolbar toolbar;
	Dialog addFeaturesDialog;
	private List<String> mLayerTitles = new ArrayList<String>();
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private LinearLayout drawerlayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private String dataCaptureMode = "";
	ActionMode actionMode;
	List<Coordinate> jtspoints = new ArrayList<Coordinate>();
	private boolean contextualMenuShown = false;
	CommonFunctions cf = CommonFunctions.getInstance();
	LocationManager locationManager;
	MyLocationListener locationListener;
	List<ProjectSpatialDataDto> offlineSpatialData;
	DecimalFormat df = new DecimalFormat("#.######");
	Long featureId = 0L;
	public static int MAP_MODE = 0;
	private static int CLEAR_MODE = 0;
	private static int FEATURE_DRAW_MAP_MODE = 1;
	private static int FEATURE_INFO_MODE = 2;
	private static int FEATURE_EDIT_MODE = 3;
	private static int FEATURE_DELETE_MODE = 4;
	private static int MAP_MEASURE_LINE_MODE = 5;
	private static int MAP_MEASURE_POLYGON_MODE = 6;
	private static int FEATURE_DRAW_POINT_GPS_MODE = 7;
	private static int FEATURE_DRAW_LINE_GPS_MODE = 8;

	private static int FEATURE_DRAW_POLYGON_GPS_MODE = 9;

	// ########### END OF
	// OnCREATE--------------##########################################

	private static int MEASURE_FEATURE_AREA_MODE = 10;

	private static int MEASURE_FEATURE_LENGTH_MODE = 11;

	private static int GPS_TIME = 5000;// in ms

	// ########### MAP LOADED LISTENER ENDS ######
	// ####################################

	private static int GPS_DISTANCE = 7;// in meters

	// ########### MAP CLICK LISTENER ENDS ####################################

	boolean googlemapReinitialized = false;

	private ActionMode.Callback myActionModeCallback = new ActionMode.Callback() {
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			int id = item.getItemId();
			if (points.size() <= 1 && id == R.id.undo)
				id = R.id.clear_features;
			switch (id) {
			case R.id.clear_features:
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
				alertDialogBuilder.setMessage(R.string.clearFeatureDrawnMsg);
				alertDialogBuilder.setPositiveButton(R.string.btn_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								clearDrawnFeaturesFromMap();
								actionMode.setTitle("");
								actionMode.setSubtitle("");
							}
						});

				alertDialogBuilder.setNegativeButton(R.string.btn_cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				return true;
			case R.id.save_features:
				if (MAP_MODE == FEATURE_DRAW_MAP_MODE
						|| MAP_MODE == FEATURE_DRAW_LINE_GPS_MODE
						|| MAP_MODE == FEATURE_DRAW_POINT_GPS_MODE
						|| MAP_MODE == FEATURE_DRAW_POLYGON_GPS_MODE) {
					saveNewFeature();
					cf.saveGPSmode(CLEAR_MODE, null);
					googlemapReinitialized = false;
				} else if (MAP_MODE == FEATURE_EDIT_MODE) {
					updateFeature();
				}

				mode.finish();
				return true;
			case R.id.plot_by_gps:
				drawFeatureByGPS();
				return true;
			case R.id.undo:
				undoLastpointDraw();
				return true;
			default:
				mode.finish();
				return false;
			}
		}

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.map_create_features_contextual_menu, menu);
			contextualMenuShown = true;
			actionMode = mode;

			// removing clear option in case of feature edit
			if (MAP_MODE == FEATURE_EDIT_MODE)
				menu.removeItem(R.id.clear_features);

			// removing plot by gps in case of other modes
			if (MAP_MODE != FEATURE_DRAW_LINE_GPS_MODE
					&& MAP_MODE != FEATURE_DRAW_POLYGON_GPS_MODE
					&& MAP_MODE != FEATURE_DRAW_POINT_GPS_MODE) {
				menu.removeItem(R.id.plot_by_gps);
			}

			// removing save feature item in case of measure and UNDO
			if (MAP_MODE == MAP_MEASURE_LINE_MODE
					|| MAP_MODE == MAP_MEASURE_POLYGON_MODE) {
				menu.removeItem(R.id.save_features);
				menu.removeItem(R.id.undo);
			}
			// hiding bottom menu
			hideBottomMenu();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			actionMode.setTitle("");
			actionMode.setSubtitle("");
			resetMapMode();
			// showing bottom menu
			showBottomMenu();
			if (locationManager != null) {
				locationManager.removeGpsStatusListener(gpsListener);
			}
		}

		@Override
		public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
			return false;
		}
	};

	// ############MARKER CLICK LISTENER ENDS
	// ########################################

	// ########## GPS Engine status listener ################################
	private final GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
		@Override
		public void onGpsStatusChanged(int event) {
			// GpsStatus gpsStatus = locationManager.getGpsStatus(null);
			switch (event) {
			case GpsStatus.GPS_EVENT_STARTED:
				// displayGPSNotification();
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				try {
					showToast(
							getResources().getString(
									R.string.gps_fix_success_msg),
							Toast.LENGTH_LONG, Gravity.CENTER);
					int satellitesInFix = 0;
					for (GpsSatellite sat : locationManager.getGpsStatus(null)
							.getSatellites()) {
						if (sat.usedInFix()) {
							satellitesInFix++;
						}
					}
					if (actionMode != null) {
						int accuracy = (int) locationManager
								.getLastKnownLocation(
										LocationManager.GPS_PROVIDER)
								.getAccuracy();
						actionMode.setTitle(accuracy
								+ getResources().getString(
										R.string.gps_accuracy));
						actionMode.setSubtitle(satellitesInFix
								+ " "
								+ getResources().getString(
										R.string.sats_use_msg));
					}
				} catch (Exception e) {
					e.printStackTrace();
					cf.appLog("", e);
				}
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				// showToast("GPS_EVENT_STOPPED", Toast.LENGTH_SHORT, 0);
				break;
			}
		}
	};

	private void addNewBookmark() {
		final EditText input = new EditText(context);
		new AlertDialog.Builder(context)
				.setTitle("Enter Bookmark Name")
				// .setMessage(message)
				.setView(input)
				.setPositiveButton(R.string.save,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								String bookmarkName = input.getText()
										.toString();
								if (!TextUtils.isEmpty(bookmarkName)) {
									Bookmark bookmark = new Bookmark();
									bookmark.setName(bookmarkName);
									bookmark.setZoomlevel(googleMap
											.getCameraPosition().zoom);
									bookmark.setLatitude(googleMap
											.getCameraPosition().target.latitude);
									bookmark.setLongitude(googleMap
											.getCameraPosition().target.longitude);
									DBController db = new DBController(context);
									boolean result = db.saveBookmark(bookmark);
									db.close();
									if (result) {
										showToast(
												getResources()
														.getString(
																R.string.bookmark_saved_msg),
												Toast.LENGTH_SHORT, 0);
									} else {
										showToast(
												getResources()
														.getString(
																R.string.bookmark_save_error),
												Toast.LENGTH_SHORT, 0);
									}
									dialog.dismiss();
								} else {
									showToast(
											getResources()
													.getString(
															R.string.bookmark_enter_name),
											Toast.LENGTH_SHORT, 0);
								}
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.dismiss();
							}
						}).show();
	}

	private void clearDrawnFeaturesFromMap() {
		try {
			points.clear();
			jtspoints.clear();

			for (Marker mark : currMarkers) {
				mark.remove();
			}
			currMarkers.clear();

			if (drawnFeature instanceof Polyline) {
				Polyline tmp = (Polyline) drawnFeature;
				tmp.remove();
			} else if (drawnFeature instanceof Polygon) {
				Polygon tmp = (Polygon) drawnFeature;
				tmp.remove();
			} else if (drawnFeature instanceof Marker) {
				Marker tmp = (Marker) drawnFeature;
				tmp.remove();
			}

			drawnFeature = new Object();

		} catch (Exception e) {
			cf.appLog("", e);
			e.printStackTrace();
		}
	}

	private void deleteSpatialFeature(final Long featureId) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setMessage(R.string.deleteFeatureEntryMsg);
		alertDialogBuilder.setPositiveButton(R.string.btn_ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						DBController db = new DBController(context);
						boolean result = db.deleteFeature(featureId);
						db.close();
						if (result) {
							googleMap.clear();
							loadCapturedFeaturesFromDB();
						} else {
							Toast.makeText(context,
									R.string.errorDeleteFeatureMsg,
									Toast.LENGTH_SHORT).show();
						}
					}
				});

		alertDialogBuilder.setNegativeButton(R.string.btn_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	private void drawFeatureByGPS() {
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location != null) {
			LatLng pointFromGps = new LatLng(location.getLatitude(),
					location.getLongitude());
			float currzoom = googleMap.getCameraPosition().zoom;
			float zoom = 19;
			if (currzoom > zoom) {
				zoom = currzoom;
			}
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
					pointFromGps, zoom));

			points.add(pointFromGps);
			MarkerOptions marker = new MarkerOptions().position(pointFromGps);
			marker.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
			marker.draggable(true);
			marker.snippet(pointFromGps.latitude + "," + pointFromGps.longitude);
			Marker mark = googleMap.addMarker(marker);
			currMarkers.add(mark);

			if (MAP_MODE == FEATURE_DRAW_POINT_GPS_MODE) {
				if (points.size() > 1) {
					points.clear();
					currMarkers.clear();

					points.add(pointFromGps);
					currMarkers.add(mark);

					Marker tmp = (Marker) drawnFeature;
					tmp.remove();
				}
				drawnFeature = mark;
			} else if (MAP_MODE == FEATURE_DRAW_LINE_GPS_MODE) {
				if (points.size() > 1) {
					if (points.size() > 2) {
						Polyline polyline = (Polyline) drawnFeature;
						polyline.setPoints(points);
					} else {
						// for polyline
						PolylineOptions rectOptions = new PolylineOptions();

						for (LatLng p : points) {
							rectOptions.add(p);
						}
						rectOptions.zIndex(4);
						Polyline polyline = googleMap.addPolyline(rectOptions);
						drawnFeature = polyline;
					}
				}
			} else if (MAP_MODE == FEATURE_DRAW_POLYGON_GPS_MODE) {
				// for polygon
				if (points.size() > 2) {
					if (points.size() > 3) {
						Polygon polygon = (Polygon) drawnFeature;
						polygon.setPoints(points);

						/*
						 * //TESTING if(points.size()==5) {
						 * Toast.makeText(context,"RECREATING >>> GPS",
						 * Toast.LENGTH_SHORT).show(); recreate(); }
						 */
					} else {
						PolygonOptions rectOptions = new PolygonOptions();
						for (LatLng p : points) {
							rectOptions.add(p);
						}
						rectOptions.fillColor(Color.argb(100, 0, 0, 255));
						rectOptions.zIndex(4);
						Polygon polygon = googleMap.addPolygon(rectOptions);
						drawnFeature = polygon;
					}
				}
			}
		} else {
			Toast.makeText(context, R.string.noLocationFound,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void hideBottomMenu() {
		View bottomMenu = findViewById(R.id.bottom_menu);
		// Hide the Panel
		Animation bottomDown = AnimationUtils.loadAnimation(this,
				R.anim.bottom_down);

		bottomMenu.startAnimation(bottomDown);
		bottomMenu.setVisibility(View.GONE);
	}

	/**
	 * function to load map If map is not created it will create it for you
	 * */
	private void initializeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();

			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			} else {
				// googleMap.setOnMapClickListener(new mapClickListener());
				googleMap
						.setOnMapLoadedCallback(new mapLoadedCallBackListener());
				googleMap.setOnMarkerDragListener(new markerDragListener());
				// googleMap.setOnMarkerClickListener(new
				// markerClickListener());
				// Changing map type
				googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				// googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
				// googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				// googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
				// googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);

				// Showing / hiding your current location
				// googleMap.setMyLocationEnabled(true);

				// Enable / Disable zooming controls
				googleMap.getUiSettings().setZoomControlsEnabled(false);
				// Enable / Disable my location button
				googleMap.getUiSettings().setMyLocationButtonEnabled(false);
				// Enable / Disable Compass icon
				googleMap.getUiSettings().setCompassEnabled(false);
				// Enable / Disable Rotate gesture
				googleMap.getUiSettings().setRotateGesturesEnabled(false);
				// Enable / Disable zooming functionality
				googleMap.getUiSettings().setZoomGesturesEnabled(true);

				
				double latitude = CommonFunctions.latitude;
				double longitude = CommonFunctions.longitude;
				
				if(latitude !=0 & longitude!=0){
					LatLng myLocation = new LatLng(latitude, longitude);
					googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
				}

				loadUserSelectedLayers();

				if (featureId != 0) {
					loadFeatureforEdit();
				}
				if (cf.getMAP_MODE() > 0)
					googlemapReinitialized = true;
			}
		}
	}

	private void loadCapturedFeaturesFromDB() {
		try {
			DBController dbObj = new DBController(context);
			List<Feature> features = dbObj.fetchFeatures();
			dbObj.close();
			int pointcolor = cf.getFeatureColor(cf.getPointColor(), "point");
			int linecolor = cf.getFeatureColor(cf.getLineColor(), "line");
			int polygoncolor = cf.getFeatureColor(cf.getPolygonColor(),
					"polygon");

			if (features.size() == 0) {
				Toast.makeText(context, R.string.noFeaturesToLoad,
						Toast.LENGTH_SHORT).show();
			}
			for (Feature feature : features) {
				if (feature.getGeomtype().equalsIgnoreCase(
						CommonFunctions.GEOM_POLYGON)) {
					String coordinates = feature.getCoordinates();

					String[] wktpoints = coordinates.split(",");

					PolygonOptions rectOptions = new PolygonOptions();

					for (String point : wktpoints) {
						String[] tmpPoint = point.replaceAll(", ", ",").split(
								" ");
						LatLng mapPoint = new LatLng(
								Double.parseDouble(tmpPoint[1]),
								Double.parseDouble(tmpPoint[0]));
						rectOptions.add(mapPoint);
					}
					if (feature.getStatus().equalsIgnoreCase("final")) {
						rectOptions.fillColor(Color.argb(100, 204, 153, 255))
								.strokeWidth(5).strokeColor(Color.BLACK);
					} else if (feature.getStatus().equalsIgnoreCase("rejected")) {
						rectOptions.fillColor(Color.argb(100, 255, 51, 51))
								.strokeWidth(5).strokeColor(Color.BLACK);
					} else if (feature.getStatus().equalsIgnoreCase("complete")) {
						rectOptions.fillColor(Color.argb(150, 204, 255, 153))
								.strokeWidth(5).strokeColor(Color.BLACK);
					} else
						rectOptions.fillColor(polygoncolor).strokeWidth(5)
								.strokeColor(Color.BLUE);
					rectOptions.zIndex(4);
					googleMap.addPolygon(rectOptions);
				} else if (feature.getGeomtype().equalsIgnoreCase(
						CommonFunctions.GEOM_LINE)) {
					String coordinates = feature.getCoordinates();

					String[] wktpoints = coordinates.replaceAll(", ", ",")
							.split(",");

					PolylineOptions rectOptions = new PolylineOptions();
					if (feature.getStatus().equalsIgnoreCase("final")) {
						rectOptions.color(Color.rgb(204, 153, 255));
					} else if (feature.getStatus().equalsIgnoreCase("rejected")) {
						rectOptions.color(Color.rgb(255, 51, 51));
					} else if (feature.getStatus().equalsIgnoreCase("complete")) {
						rectOptions.color(Color.rgb(128, 255, 0));
					} else
						rectOptions.color(linecolor);

					for (String point : wktpoints) {
						String[] tmpPoint = point.split(" ");
						LatLng mapPoint = new LatLng(
								Double.parseDouble(tmpPoint[1]),
								Double.parseDouble(tmpPoint[0]));
						rectOptions.add(mapPoint);
					}
					rectOptions.zIndex(4);
					googleMap.addPolyline(rectOptions);
				} else if (feature.getGeomtype().equalsIgnoreCase(
						CommonFunctions.GEOM_POINT)) {
					String coordinates = feature.getCoordinates();

					String[] tmpPoint = coordinates.split(" ");
					LatLng mapPoint = new LatLng(
							Double.parseDouble(tmpPoint[1]),
							Double.parseDouble(tmpPoint[0]));
					/*
					 * MarkerOptions marker = new
					 * MarkerOptions().position(mapPoint);
					 * marker.icon(BitmapDescriptorFactory
					 * .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
					 * marker.draggable(false);
					 * marker.snippet(mapPoint.latitude+","+mapPoint.longitude);
					 * googleMap.addMarker(marker);
					 */
					CircleOptions circleOptions = new CircleOptions()
							.center(mapPoint);
					circleOptions.radius(3); // In meters
					if (feature.getStatus().equalsIgnoreCase("final")) {
						circleOptions.fillColor(Color.argb(100, 204, 153, 255))
								.strokeWidth(5).strokeColor(Color.BLACK);
					} else if (feature.getStatus().equalsIgnoreCase("rejected")) {
						circleOptions.fillColor(Color.argb(100, 255, 51, 51))
								.strokeWidth(4).strokeColor(Color.BLACK);
					} else if (feature.getStatus().equalsIgnoreCase("complete")) {
						circleOptions.fillColor(Color.argb(150, 204, 255, 153))
								.strokeWidth(5).strokeColor(Color.BLACK);
					} else
						circleOptions.fillColor(pointcolor).strokeWidth(4)
								.strokeColor(Color.BLUE);
					circleOptions.zIndex(4);
					googleMap.addCircle(circleOptions);
				}
			}
		} catch (Exception e) {
			cf.appLog("", e);
			Toast.makeText(context, "unable to load features from db",
					Toast.LENGTH_SHORT).show();
		}
	}

	// ############## Location Listener Class ENDS#################
	private void loadFeatureforEdit() {
		if (featureId != 0) {
			Toast toast = Toast.makeText(context, R.string.featureEditTipMsg,
					Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			DBController db = new DBController(context);
			Feature feature = db.fetchFeaturebyID(featureId);

			if (feature != null) {
				// googleMap.clear();
				MAP_MODE = FEATURE_EDIT_MODE;
				if (!contextualMenuShown)
					toolbar.startActionMode(myActionModeCallback);

				if (feature.getGeomtype().equalsIgnoreCase(
						CommonFunctions.GEOM_POLYGON)) {
					String coordinates = feature.getCoordinates();

					String[] wktpoints = coordinates.split(",");

					PolygonOptions rectOptions = new PolygonOptions();
					LatLng mapPoint = null;
					for (int i = 0; i < wktpoints.length - 1; i++) {
						// wktpoints.length-1 to remove same first and last
						// points in WKT
						String point = wktpoints[i];

						String[] tmpPoint = point.split(" ");
						mapPoint = new LatLng(Double.parseDouble(tmpPoint[1]),
								Double.parseDouble(tmpPoint[0]));
						rectOptions.add(mapPoint);

						// Adding marker for edit
						MarkerOptions marker = new MarkerOptions()
								.position(mapPoint);
						marker.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
						marker.draggable(true);
						marker.snippet(mapPoint.latitude + ","
								+ mapPoint.longitude);
						Marker mark = googleMap.addMarker(marker);
						currMarkers.add(mark);
					}
					rectOptions.zIndex(4);
					Polygon polygon = googleMap.addPolygon(rectOptions);
					drawnFeature = polygon;
					if (mapPoint != null) {
						googleMap.animateCamera(CameraUpdateFactory
								.newLatLngZoom(mapPoint, 19));
					}
				} else if (feature.getGeomtype().equalsIgnoreCase(
						CommonFunctions.GEOM_LINE)) {
					String coordinates = feature.getCoordinates();

					String[] wktpoints = coordinates.split(",");

					PolylineOptions rectOptions = new PolylineOptions();
					LatLng mapPoint = null;
					for (String point : wktpoints) {
						String[] tmpPoint = point.split(" ");
						mapPoint = new LatLng(Double.parseDouble(tmpPoint[1]),
								Double.parseDouble(tmpPoint[0]));
						rectOptions.add(mapPoint);

						// Adding marker for edit
						MarkerOptions marker = new MarkerOptions()
								.position(mapPoint);
						marker.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
						marker.draggable(true);
						marker.snippet(mapPoint.latitude + ","
								+ mapPoint.longitude);
						Marker mark = googleMap.addMarker(marker);
						currMarkers.add(mark);
					}
					rectOptions.zIndex(4);
					Polyline polyline = googleMap.addPolyline(rectOptions);
					drawnFeature = polyline;
					if (mapPoint != null) {
						googleMap.animateCamera(CameraUpdateFactory
								.newLatLng(mapPoint));
					}
				} else if (feature.getGeomtype().equalsIgnoreCase(
						CommonFunctions.GEOM_POINT)) {
					String coordinates = feature.getCoordinates();
					// googleMap.clear();
					String[] tmpPoint = coordinates.split(" ");
					LatLng mapPoint = new LatLng(
							Double.parseDouble(tmpPoint[1]),
							Double.parseDouble(tmpPoint[0]));
					MarkerOptions marker = new MarkerOptions()
							.position(mapPoint);
					marker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_RED));
					marker.draggable(true);
					marker.snippet(mapPoint.latitude + "," + mapPoint.longitude);
					Marker mark = googleMap.addMarker(marker);
					currMarkers.add(mark);
					googleMap.animateCamera(CameraUpdateFactory
							.newLatLng(mapPoint));
					drawnFeature = mark;
				}
			}
		}
	}

	private void loadOfflineData(int pos) {
		try {
			String extPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath();
			String filepath = extPath + "/" + CommonFunctions.parentFolderName
					+ "/" + CommonFunctions.dataFolderName + "/"
					+ offlineSpatialData.get(pos).getFile_Name();
			OfflineTileProvider provider = offlineSpatialData.get(pos)
					.getProvider();
			File mbtileFile = new File(filepath);

			TileOverlayOptions opts = new TileOverlayOptions();
			if (provider == null) {
				// Create an instance of OfflineTileProvider.
				provider = new OfflineTileProvider(mbtileFile);
				offlineSpatialData.get(pos).setProvider(provider);
			}
			// Set the tile provider on the TileOverlayOptions.
			opts.tileProvider(provider);
			// Add the tile overlay to the map.
			TileOverlay overlay = googleMap.addTileOverlay(opts);
			offlineSpatialData.get(pos).setOverlay(overlay);

			// Sometime later when the map view is destroyed, close the
			// provider.
			// This is important to prevent a leak of the backing
			// SQLiteDatabase.
			provider.close();
		} catch (Exception e) {
			cf.appLog("", e);
			String msg = getResources().getString(
					R.string.unableToLoadOfflineData);
			Toast.makeText(context,
					msg + ": " + offlineSpatialData.get(pos).getAlias(),
					Toast.LENGTH_SHORT).show();
		}
	}

	/*
	 * private LatLng getLineWithBearingandDistance(LatLng startPoint,double
	 * angle) { double angleInRadians = Math.toRadians(angle); double startX =
	 * startPoint.longitude; double startY = startPoint.latitude; int distance =
	 * 10;
	 * 
	 * 
	 * double endX = startX + distance * Math.sin(angle); double endY = startY +
	 * distance * Math.cos(angle);
	 * 
	 * double endXDegree = endX;//Math.toDegrees(endX); double endYDegree =
	 * endY;//Math.toDegrees(endY);
	 * 
	 * LatLng endPoint = new LatLng(endYDegree, endXDegree);
	 * 
	 * return endPoint; }
	 */

	private void loadUserSelectedLayers() {
		googleMap.clear();
		String visibleLayers = cf.getVisibleLayers();

		if (!visibleLayers.isEmpty()) {
			// Toast.makeText(context, "Loading user selected Layers...",
			// Toast.LENGTH_SHORT).show();
			if (visibleLayers.contains("0")) {
				googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				mDrawerList.setItemChecked(0, true);
			}
			if (visibleLayers.contains("1")) {
				loadCapturedFeaturesFromDB();
				mDrawerList.setItemChecked(1, true);
			}

			for (int i = 0; i < offlineSpatialData.size(); i++) {
				if (visibleLayers.contains((i + 2) + "")) {
					loadOfflineData(i);
					// loadOfflineData(offlineSpatialData.size()-i-1);
					mDrawerList.setItemChecked(((i + 2)), true);
				}
			}
		}
	}

	private void onBackButtonPressedinGPSmode() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setMessage(R.string.clearFeatureDrawnMsg);
		alertDialogBuilder.setPositiveButton(R.string.btn_ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						actionMode.finish();
						googlemapReinitialized = false;
						cf.saveGPSmode(CLEAR_MODE, null);
					}
				});

		alertDialogBuilder.setNegativeButton(R.string.btn_cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	@Override
	public void onBackPressed() {
		if (!contextualMenuShown) {
			Intent i = new Intent(context, LandingPageActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
		} else {
			if ((MAP_MODE == FEATURE_DRAW_POLYGON_GPS_MODE
					|| MAP_MODE == FEATURE_DRAW_POINT_GPS_MODE || MAP_MODE == FEATURE_DRAW_LINE_GPS_MODE)
					&& points.size() > 0) {
				onBackButtonPressedinGPSmode();
			} else
				actionMode.finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("CAPTUREDATA", "ON_CREATE");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// Initializing context in common functions in case of a crash
		try {
			CommonFunctions.getInstance().Initialize(getApplicationContext());
		} catch (Exception e) {
		}
		cf.loadLocale(getApplicationContext());

		setContentView(R.layout.activity_capture_data_map);

		Bundle extras = getIntent().getExtras();
		if (extras != null) // this feature ID is send when spatial feature is
							// to be edited
		{
			featureId = extras.getLong("featureid");
		}

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		toolbar.setTitle(R.string.title_activity_capture_spatial_data);
		if (toolbar != null)
			setSupportActionBar(toolbar);
		CommonFunctions.getInstance().Initialize(getApplicationContext());
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// ########### LOCATION MANAGER
		// ##############################################################
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationListener = new MyLocationListener();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				30000, 10, locationListener);
		boolean isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!isGPSEnabled) {
			cf.showGPSSettingsAlert(context);
		}
		// ############################################################################################
		// ########## NAVIGATION DRAWER
		// #####################################################################
		offlineSpatialData = new DBController(context).getProjectSpatialData();
		mLayerTitles.add("Satellite Map");
		mLayerTitles.add("Captured features");
		// mLayerTitles.add("Offline data");

		for (int i = 0; i < offlineSpatialData.size(); i++) {
			mLayerTitles.add(offlineSpatialData.get(i).getAlias());
		}
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_layer_manager);
		drawerlayout = (LinearLayout) findViewById(R.id.left_drawer);

		// calculating drawer width
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int width = displaymetrics.widthPixels;
		int newWidth = (width / 3) * 2;

		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.item_list_multiple_choice, mLayerTitles));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		LayoutParams params = drawerlayout.getLayoutParams();
		params.width = newWidth;
		drawerlayout.setLayoutParams(params);

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		) {
			/** Called when a drawer has settled in a completely closed state. */
			@Override
			public void onDrawerClosed(View view) {
				toggleLayers();
			}

			/** Called when a drawer has settled in a completely open state. */
			@Override
			public void onDrawerOpened(View drawerView) {
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerToggle.syncState(); // for button animation

		/*
		 * for (int i = 0; i < mDrawerList.getCount(); i++) {
		 * mDrawerList.setItemChecked(i, true); }
		 */
		// ########## NAVIGATION DRAWER ENDS
		// ###################################################################

		// ######################################## ADD Feature Dialog
		// ##########################################
		ImageView imgFavorite = (ImageView) findViewById(R.id.add_feature);
		View viewForAddfeatureToolTip = imgFavorite;
		String add_feature = getResources().getString(R.string.add_feature);
		cf.setup(viewForAddfeatureToolTip, add_feature);
		imgFavorite.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addFeaturesDialog = new Dialog(context, R.style.DialogTheme);
				addFeaturesDialog.setContentView(R.layout.dialog_add_features);
				addFeaturesDialog.setTitle(getResources().getString(
						R.string.capture_new_data));
				addFeaturesDialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
				// dialog.setCancelable(false);

				expandableList = (ExpandableListView) addFeaturesDialog
						.findViewById(R.id.addfeaturesOptionList);

				expandableList.setDividerHeight(2);
				expandableList.setGroupIndicator(null);
				expandableList.setBackgroundColor(getResources().getColor(
						R.color.white));
				expandableList.setClickable(true);

				setGroupParents();
				setChildData();

				AddFeaturesOptionsAdapter adapter = new AddFeaturesOptionsAdapter(
						parentItems, childItems, context);

				expandableList.setAdapter(adapter);
				expandableList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

				if (parentItems.size() == 1) {
					expandableList.expandGroup(0);
				}
				expandableList
						.setOnGroupExpandListener(new OnGroupExpandListener() {
							@Override
							public void onGroupExpand(int groupPosition) {
								if (lastExpandedGroup != null
										&& groupPosition != lastExpandedGroup) {
									if (lastExpandedGroup != null) {
										expandableList
												.collapseGroup(lastExpandedGroup);
										lastExpandedGroup = groupPosition;
									}
								} else {
									lastExpandedGroup = groupPosition;
								}
							}
						});

				expandableList
						.setOnChildClickListener(new OnChildClickListener() {
							@SuppressWarnings("unchecked")
							@Override
							public boolean onChildClick(
									ExpandableListView parent, View v,
									int groupPosition, int childPosition,
									long id) {
								// generating key dynamically to get selected
								// option in English
								List<String> child = (List<String>) childItems
										.get(groupPosition);
								String selectedItem = child.get(childPosition);
								String key = "option_"
										+ selectedItem.substring(0, 2);
								int key_id = getResources().getIdentifier(key,
										"string", getPackageName());
								selectedItem = getResources().getString(key_id);
								// Toast.makeText(context, selectedItem,
								// Toast.LENGTH_SHORT).show();
								setCaptureSpatialDataMode(selectedItem);
								return true;
							}
						});

				addFeaturesDialog.show();
			}
		});
		// ############### ADD Features Dialog ENDS
		// #######################################################
		// ################## MEASURE
		// #####################################################################
		ImageView imgMeasure = (ImageView) findViewById(R.id.measure);
		View viewForMeasureToolTip = imgMeasure;
		String measure = getResources().getString(R.string.menu_measure);
		cf.setup(viewForMeasureToolTip, measure);
		imgMeasure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showMeasureDialog();
			}
		});
		// ################# MEASURE ENDS
		// #################################################################
		// ################## DELETE
		// ######################################################################
		ImageView imgDelete = (ImageView) findViewById(R.id.delete_feature);
		View viewForDeleteTooltip = imgDelete;
		String delete = getResources().getString(R.string.delete_feature);
		cf.setup(viewForDeleteTooltip, delete);
		imgDelete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MAP_MODE = FEATURE_DELETE_MODE;
				googleMap.setOnMapClickListener(new mapClickListener());
				googleMap.setOnMarkerClickListener(new markerClickListener());
			}
		});
		// ################# DELETE ENDS
		// #################################################################
		// ################## INFO
		// ######################################################################
		ImageView imgInfo = (ImageView) findViewById(R.id.map_info);
		View viewForMapInfoTooltip = imgInfo;
		String MapInfo = getResources().getString(R.string.menu_map_info);
		cf.setup(viewForMapInfoTooltip, MapInfo);
		imgInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MAP_MODE = FEATURE_INFO_MODE;
				googleMap.setOnMapClickListener(new mapClickListener());
				googleMap.setOnMarkerClickListener(new markerClickListener());
			}
		});
		// ################# INDO ENDS
		// #################################################################
		// ################## FEATURE EDIT
		// ######################################################################
		ImageView imgEdit = (ImageView) findViewById(R.id.edit_feature);
		View viewForEditFeatureTooltip = imgEdit;
		String editFeature = getResources().getString(R.string.edit_spatial);
		cf.setup(viewForEditFeatureTooltip, editFeature);
		imgEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MAP_MODE = FEATURE_EDIT_MODE;
				googleMap.setOnMapClickListener(new mapClickListener());
				googleMap.setOnMarkerClickListener(new markerClickListener());
			}
		});
		// ################# FEATURE EDIT ENDS
		// #################################################################
		// ############### INITIALIZING MAP
		// #################################################################
		try {
			// Loading map
			initializeMap();
		} catch (Exception e) {
			cf.appLog("", e);
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map_menu, menu);
		return true;
	}

	// Always keep this method at the last
	@Override
	protected void onDestroy() {
		super.onDestroy();

		for (int i = 0; i < offlineSpatialData.size(); i++) {
			OfflineTileProvider provider = offlineSpatialData.get(i)
					.getProvider();
			if (provider != null)
				provider.close();
		}
		if (locationManager != null) {
			locationManager.removeUpdates(locationListener);
			locationManager.removeGpsStatusListener(gpsListener);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_GPS) {
			// Location location = googleMap.getMyLocation();
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				LatLng myLocation = new LatLng(location.getLatitude(),
						location.getLongitude());
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						myLocation, 19));
			} else {
				Toast.makeText(context, R.string.no_location,
						Toast.LENGTH_SHORT).show();
			}

		} else if (id == R.id.action_show_bookmark) {
			showBookmarks();
		} else if (id == R.id.action_add_bookmark) {
			addNewBookmark();
		} else if (id == android.R.id.home) {
			// finish();
			return mDrawerToggle.onOptionsItemSelected(item)
					|| super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		if ((MAP_MODE == FEATURE_DRAW_POLYGON_GPS_MODE
				|| MAP_MODE == FEATURE_DRAW_POINT_GPS_MODE || MAP_MODE == FEATURE_DRAW_LINE_GPS_MODE)
				&& points.size() > 0) {
			cf.saveGPSmode(MAP_MODE, points);
		}
		super.onPause();
	}

	// Always keep this method at the last
	@Override
	protected void onResume() {
		super.onResume();
		try {
			Log.i("CAPTUREDATA", "ON_RESUME");
			MAP_MODE = cf.getMAP_MODE();
			initializeMap();
			if (MAP_MODE != 0 && cf.getPoints() != null
					&& googlemapReinitialized) {
				googlemapReinitialized = false;
				if ((MAP_MODE == FEATURE_DRAW_POLYGON_GPS_MODE
						|| MAP_MODE == FEATURE_DRAW_POINT_GPS_MODE || MAP_MODE == FEATURE_DRAW_LINE_GPS_MODE)
						&& cf.getPoints().size() > 0) {
					points = cf.getPoints();
					toolbar.startActionMode(myActionModeCallback);
					startFetchingLocationfromGPS();
					reDrawPointsforGPSmode();
				}
			}
		} catch (Exception e) {
			cf.appLog("", e);
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	private void processFeaturesForMeasurement(LatLng pointFromMap) {
		try {
			boolean result = false;
			String geomtype = "";

			if (MAP_MODE == MEASURE_FEATURE_AREA_MODE) {
				geomtype = CommonFunctions.GEOM_POLYGON;
			} else if (MAP_MODE == MEASURE_FEATURE_LENGTH_MODE) {
				geomtype = CommonFunctions.GEOM_LINE;
			}

			DBController dbObj = new DBController(context);
			List<Feature> features = dbObj.fetchFeaturesByGeomtype(geomtype);
			dbObj.close();
			String coordinates = null;

			for (Feature feature : features) {
				if (feature.getGeomtype().equalsIgnoreCase(
						CommonFunctions.GEOM_POLYGON)) {
					com.vividsolutions.jts.geom.Point ptClicked = new GeometryFactory()
							.createPoint(new Coordinate(pointFromMap.longitude,
									pointFromMap.latitude));
					Geometry geom = new WKTReader().read("POLYGON (("
							+ feature.getCoordinates() + "))");

					com.vividsolutions.jts.geom.Polygon poly = (com.vividsolutions.jts.geom.Polygon) geom;

					result = GeomFunctions.IsPointInPolygon(ptClicked, poly);

					if (result) {
						coordinates = feature.getCoordinates();
					}
				} else if (feature.getGeomtype().equalsIgnoreCase(
						CommonFunctions.GEOM_LINE)) {
					com.vividsolutions.jts.geom.Point ptClicked = new GeometryFactory()
							.createPoint(new Coordinate(pointFromMap.longitude,
									pointFromMap.latitude));

					Geometry geom = new WKTReader().read("LINESTRING ("
							+ feature.getCoordinates() + ")");

					com.vividsolutions.jts.geom.LineString line = (com.vividsolutions.jts.geom.LineString) geom;

					result = GeomFunctions.IsPointIntersectsLine(ptClicked,
							line);

					if (result) {
						coordinates = feature.getCoordinates();
					}
				}

				if (result)
					break;
			}

			if (result) {
				googleMap.setOnMarkerClickListener(null);
				if (MAP_MODE == MEASURE_FEATURE_AREA_MODE
						&& !TextUtils.isEmpty(coordinates)) {
					Geometry geom = new WKTReader().read("POLYGON (("
							+ coordinates + "))");
					com.vividsolutions.jts.geom.Polygon polygonJts = (com.vividsolutions.jts.geom.Polygon) geom;
					double area = polygonJts.getArea();
					double areaMeter = area
							* (Math.pow(111319.9, 2))
							* Math.cos(Math.toRadians(polygonJts
									.getCoordinates()[0].y));
					String areaStr = df.format(areaMeter);
					showToast(getResources().getString(R.string.areaTxt)
							+ " : " + areaStr + " Sqm", Toast.LENGTH_LONG,
							Gravity.CENTER);
				} else if (MAP_MODE == MEASURE_FEATURE_LENGTH_MODE
						&& !TextUtils.isEmpty(coordinates)) {
					Geometry geom = new WKTReader().read("LINESTRING ("
							+ coordinates + ")");
					LineString lineString = (LineString) geom;
					double lineLength = lineString.getLength();
					double lengthMeter = lineLength
							* (111319.9)
							* Math.cos(Math.toRadians(lineString
									.getCoordinates()[0].y));
					String lineLengthStr = df.format(lengthMeter);
					showToast(getResources().getString(R.string.lengthTxt)
							+ " : " + lineLengthStr + " meters",
							Toast.LENGTH_LONG, Gravity.CENTER);
				}
				resetMapMode();
			} else {
				Toast.makeText(context, R.string.noFeatureFoundMsg,
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			cf.appLog("", e);
			e.printStackTrace();
		}
	}

	private void processFeaturesInfo(LatLng pointFromMap) {
		try {
			boolean result = false;
			Long featureid = 0L;
			String featurestatus = "";
			DBController dbObj = new DBController(context);
			List<Feature> features = dbObj.fetchFeatures();
			dbObj.close();

			for (Feature feature : features) {
				if (feature.getGeomtype().equalsIgnoreCase(
						CommonFunctions.GEOM_POLYGON)) {
					com.vividsolutions.jts.geom.Point ptClicked = new GeometryFactory()
							.createPoint(new Coordinate(pointFromMap.longitude,
									pointFromMap.latitude));
					Geometry geom = new WKTReader().read("POLYGON (("
							+ feature.getCoordinates() + "))");

					com.vividsolutions.jts.geom.Polygon poly = (com.vividsolutions.jts.geom.Polygon) geom;

					result = GeomFunctions.IsPointInPolygon(ptClicked, poly);

					if (result) {
						featureid = feature.getFeatureid();
						featurestatus = feature.getStatus();
					}
				} else if (feature.getGeomtype().equalsIgnoreCase(
						CommonFunctions.GEOM_LINE)) {
					com.vividsolutions.jts.geom.Point ptClicked = new GeometryFactory()
							.createPoint(new Coordinate(pointFromMap.longitude,
									pointFromMap.latitude));

					Geometry geom = new WKTReader().read("LINESTRING ("
							+ feature.getCoordinates() + ")");

					com.vividsolutions.jts.geom.LineString line = (com.vividsolutions.jts.geom.LineString) geom;

					result = GeomFunctions.IsPointIntersectsLine(ptClicked,
							line);

					if (result) {
						featureid = feature.getFeatureid();
						featurestatus = feature.getStatus();
					}
				} else if (feature.getGeomtype().equalsIgnoreCase(
						CommonFunctions.GEOM_POINT)) {
					com.vividsolutions.jts.geom.Point ptClicked = new GeometryFactory()
							.createPoint(new Coordinate(pointFromMap.longitude,
									pointFromMap.latitude));

					Geometry geom = new WKTReader().read("POINT ("
							+ feature.getCoordinates() + ")");

					com.vividsolutions.jts.geom.Point point = (com.vividsolutions.jts.geom.Point) geom;

					result = GeomFunctions.IsPointIntersectsPoint(ptClicked,
							point);

					if (result) {
						featureid = feature.getFeatureid();
						featurestatus = feature.getStatus();
					}
				}
				if (result)
					break;
			}

			if (result) {
				Log.i("CaptureDataMapActivity",
						"Feature found with feature ID:" + featureid);
				googleMap.setOnMarkerClickListener(null);
				if (MAP_MODE == FEATURE_INFO_MODE) {
					if (featurestatus.equalsIgnoreCase("draft")) {
						// Open attributes form to view --------------
						Intent myIntent = new Intent(context,
								CaptureAttributesActivity.class);
						myIntent.putExtra("featureid", featureid);
						startActivity(myIntent);
					} else {
						Toast.makeText(context, R.string.featurenotdraft,
								Toast.LENGTH_LONG).show();
					}
				} else if (MAP_MODE == FEATURE_DELETE_MODE) {
					if (featurestatus.equalsIgnoreCase("draft")) {
						deleteSpatialFeature(featureid);
					} else {
						Toast.makeText(context, R.string.featurenotdraft,
								Toast.LENGTH_LONG).show();
					}
				} else if (MAP_MODE == FEATURE_EDIT_MODE) {
					if (featurestatus.equalsIgnoreCase("draft")) {
						this.featureId = featureid;
						loadFeatureforEdit();
					} else {
						Toast.makeText(context, R.string.featurenotdraft,
								Toast.LENGTH_LONG).show();
					}
				}
			} else {
				Toast.makeText(context, R.string.noFeatureFoundMsg,
						Toast.LENGTH_SHORT).show();
			}
			// resetMapMode();
		} catch (Exception e) {
			cf.appLog("", e);
			e.printStackTrace();
		}
	}

	private void reDrawPointsforGPSmode() {
		try {
			Log.i("CAPTUREDATA", "ON_DRAW_POINTS");
			int i = 0;
			currMarkers.clear();
			for (i = 0; i < points.size(); i++) {
				LatLng pointFromGps = points.get(i);
				MarkerOptions marker = new MarkerOptions()
						.position(pointFromGps);
				marker.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
				marker.draggable(true);
				marker.snippet(pointFromGps.latitude + ","
						+ pointFromGps.longitude);
				Marker mark = googleMap.addMarker(marker);
				currMarkers.add(mark);

				if (MAP_MODE == FEATURE_DRAW_POINT_GPS_MODE) {
					drawnFeature = mark;
				}
			}

			if (MAP_MODE == FEATURE_DRAW_LINE_GPS_MODE) {
				if (points.size() > 1) {
					// for polyline
					PolylineOptions rectOptions = new PolylineOptions();
					for (LatLng p : points) {
						rectOptions.add(p);
					}
					rectOptions.zIndex(4);
					Polyline polyline = googleMap.addPolyline(rectOptions);
					drawnFeature = polyline;
				}
			} else if (MAP_MODE == FEATURE_DRAW_POLYGON_GPS_MODE) {
				// for polygon
				if (points.size() > 2) {
					PolygonOptions rectOptions = new PolygonOptions();
					for (LatLng p : points) {
						rectOptions.add(p);
					}
					rectOptions.fillColor(Color.argb(100, 0, 0, 255));
					rectOptions.zIndex(4);
					Polygon polygon = googleMap.addPolygon(rectOptions);
					drawnFeature = polygon;
				}
			}
			if (points.size() > 0) {
				float currzoom = googleMap.getCameraPosition().zoom;
				float zoom = 19;
				if (currzoom > zoom) {
					zoom = currzoom;
				}
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
						points.get(points.size() - 1), zoom));
			}
		} catch (Exception e) {
			e.printStackTrace();
			cf.appLog("", e);
		}
	}

	private void refreshCapturedFeaturesFromDB() {
		loadUserSelectedLayers();
	}

	private void resetMapMode() {
		googleMap.setOnMapClickListener(null);
		contextualMenuShown = false;
		clearDrawnFeaturesFromMap();
		MAP_MODE = CLEAR_MODE;
		dataCaptureMode = "";
		featureId = 0L;
	}

	private void saveNewFeature() {
		String geomtype = "";
		List<LatLng> pointslist = new ArrayList<LatLng>();
		try {
			if (drawnFeature instanceof Polyline) {
				Polyline tmp = (Polyline) drawnFeature;

				pointslist = tmp.getPoints();
				geomtype = CommonFunctions.GEOM_LINE;
			} else if (drawnFeature instanceof Marker) {
				Marker tmp = (Marker) drawnFeature;
				pointslist.add(tmp.getPosition());
				geomtype = CommonFunctions.GEOM_POINT;
			} else if (drawnFeature instanceof Polygon) {
				Polygon tmp = (Polygon) drawnFeature;
				pointslist = tmp.getPoints();
				geomtype = CommonFunctions.GEOM_POLYGON;
			}

			if (pointslist.size() > 0) {
				String WKTStr = GeomFunctions.getWKTfromPoints(geomtype,
						pointslist);
				String imei = cf.getIMEI();
				DBController db = new DBController(context);
				Long featureId = db.saveNewFeature(geomtype, WKTStr, imei);
				db.close();
				if (featureId != 0) {
					Toast.makeText(context, R.string.successFeatureSave,
							Toast.LENGTH_SHORT).show();

					if (geomtype.equalsIgnoreCase(CommonFunctions.GEOM_LINE)) {
						cf.updateLineCount();
					} else if (geomtype
							.equalsIgnoreCase(CommonFunctions.GEOM_POINT)) {
						cf.updatePointCount();
					} else if (geomtype
							.equalsIgnoreCase(CommonFunctions.GEOM_POLYGON)) {
						cf.updatePolygonCount();
					}

					// refresh features from db
					refreshCapturedFeaturesFromDB();

					// Open attributes form to fill --------------
					Intent myIntent = new Intent(context,
							CaptureAttributesActivity.class);
					myIntent.putExtra("featureid", featureId);
					startActivity(myIntent);
				} else {
					Toast.makeText(context, R.string.unableSaveFeatureMsg,
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			Toast.makeText(context, R.string.unableSaveFeatureMsg,
					Toast.LENGTH_SHORT).show();
			cf.appLog("", e);
			e.printStackTrace();
		}
	}

	public void setCaptureSpatialDataMode(String captureMode) {
		addFeaturesDialog.dismiss();
		if (captureMode.equalsIgnoreCase("Capture line by GPS")) {
			MAP_MODE = FEATURE_DRAW_LINE_GPS_MODE;
			if (!contextualMenuShown)
				toolbar.startActionMode(myActionModeCallback);
			startFetchingLocationfromGPS();
		} else if (captureMode.equalsIgnoreCase("Capture polygon by GPS")) {
			MAP_MODE = FEATURE_DRAW_POLYGON_GPS_MODE;
			if (!contextualMenuShown)
				toolbar.startActionMode(myActionModeCallback);
			startFetchingLocationfromGPS();
		} else if (captureMode.equalsIgnoreCase("Capture point by GPS")) {
			MAP_MODE = FEATURE_DRAW_POINT_GPS_MODE;
			if (!contextualMenuShown)
				toolbar.startActionMode(myActionModeCallback);
			startFetchingLocationfromGPS();
		} else if (captureMode.equalsIgnoreCase("Enter location")) {
			showXYDialog();
		} else {
			MAP_MODE = FEATURE_DRAW_MAP_MODE;
			dataCaptureMode = captureMode;
			googleMap.setOnMapClickListener(new mapClickListener());
		}
	}

	// Set child items
	public void setChildData() {
		String dct = cf.getDataCollectionTools();
		if (TextUtils.isEmpty(dct)) {
			dct = "0,1,2";
		}
		List<String> child = new ArrayList<String>();
		childItems.clear();
		if (dct.contains("0")) {
			// POINT
			String[] c1Items = getResources().getStringArray(
					R.array.add_point_options_arrays);
			child = Arrays.asList(c1Items);
			childItems.add(child);
		}
		if (dct.contains("1")) {
			// LINE
			child = new ArrayList<String>();
			String[] c2Items = getResources().getStringArray(
					R.array.add_line_options_arrays);
			child = Arrays.asList(c2Items);
			childItems.add(child);
		}
		if (dct.contains("2")) {
			// POLYGON
			child = new ArrayList<String>();
			String[] c3Items = getResources().getStringArray(
					R.array.add_poly_options_arrays);
			child = Arrays.asList(c3Items);
			childItems.add(child);
		}
	}

	// Set parent items
	public void setGroupParents() {
		try {
			parentItems.clear();
			String[] pItems = getResources().getStringArray(
					R.array.add_options_arrays);
			// parentItems = Arrays.asList(pItems);
			String dct = cf.getDataCollectionTools();
			if (TextUtils.isEmpty(dct)) {
				dct = "0,1,2";
			}

			if (dct.contains("0")) {
				parentItems.add(pItems[0]);
			}
			if (dct.contains("1")) {
				parentItems.add(pItems[1]);
			}
			if (dct.contains("2")) {
				parentItems.add(pItems[2]);
			}
		} catch (Exception e) {
			cf.appLog("", e);
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void showBookmarks() {
		try {
			DBController db = new DBController(context);
			Object[] obj = db.fetchAllBookmarks();

			final List<Bookmark> bookmarks = (List<Bookmark>) obj[0];
			List<String> bookmarksStr = (List<String>) obj[1];

			if (bookmarks.size() > 0) {
				final Dialog dialog = new Dialog(context, R.style.DialogTheme);
				dialog.setContentView(R.layout.dialog_show_list);
				dialog.setTitle(R.string.bookmarkDialogTitle);
				dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
				ListView listViewForMeasureoptions = (ListView) dialog
						.findViewById(R.id.commonlistview);

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						context, R.layout.item_list_single_choice, bookmarksStr);

				Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
				btn_ok.setVisibility(View.GONE);

				listViewForMeasureoptions.setAdapter(adapter);

				listViewForMeasureoptions
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								dialog.dismiss();
								Bookmark bookmrk = bookmarks.get(position);

								LatLng myLocation = new LatLng(bookmrk
										.getLatitude(), bookmrk.getLongitude());
								googleMap.animateCamera(CameraUpdateFactory
										.newLatLngZoom(myLocation,
												bookmrk.getZoomlevel()));
							}
						});
				dialog.show();
			} else {
				Toast.makeText(context, R.string.no_bookmark_msg,
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			cf.appLog("", e);
			e.printStackTrace();
		}
	}

	private void showBottomMenu() {
		View bottomMenu = findViewById(R.id.bottom_menu);
		// Show the panel
		Animation bottomUp = AnimationUtils.loadAnimation(this,
				R.anim.bottom_up);

		bottomMenu.startAnimation(bottomUp);
		bottomMenu.setVisibility(View.VISIBLE);
	}

	// ########## GPS Engine status listener ENDS
	// ################################

	private void showMeasureDialog() {
		try {
			String[] measure_options = getResources().getStringArray(
					R.array.measure_options_arrays);

			final Dialog dialog = new Dialog(context, R.style.DialogTheme);
			dialog.setContentView(R.layout.dialog_show_list);
			dialog.setTitle(getResources().getString(
					R.string.measureDialogTitle));
			dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
			ListView listViewForMeasureoptions = (ListView) dialog
					.findViewById(R.id.commonlistview);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
					R.layout.item_list_single_choice, measure_options);

			Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
			btn_ok.setVisibility(View.GONE);

			listViewForMeasureoptions.setAdapter(adapter);

			listViewForMeasureoptions
					.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							dialog.dismiss();
							int itemPosition = position;
							if (itemPosition == 0) // for Polyline
							{
								MAP_MODE = MAP_MEASURE_LINE_MODE;
								googleMap
										.setOnMapClickListener(new mapClickListener());
							} else if (itemPosition == 1) // for polygon
							{
								MAP_MODE = MAP_MEASURE_POLYGON_MODE;
								googleMap
										.setOnMapClickListener(new mapClickListener());
							} else if (itemPosition == 2) // for polygon
							{
								MAP_MODE = MEASURE_FEATURE_AREA_MODE;
								googleMap
										.setOnMapClickListener(new mapClickListener());
							} else if (itemPosition == 3) // for polyline
							{
								MAP_MODE = MEASURE_FEATURE_LENGTH_MODE;
								googleMap
										.setOnMapClickListener(new mapClickListener());
							}
						}
					});

			dialog.show();
		} catch (Exception e) {
			Toast.makeText(context, R.string.errorOccured, Toast.LENGTH_SHORT)
					.show();
			cf.appLog("", e);
			e.printStackTrace();
		}
	}

	private void showToast(final String message, final int length,
			final int position) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(CaptureDataMapActivity.this,
						message, length);
				if (position != 0)
					toast.setGravity(position, 0, 0);
				toast.show();
			}
		});
	}

	private void showXYDialog() {
		final Dialog xyInputDialog = new Dialog(context, R.style.DialogTheme);
		xyInputDialog.setContentView(R.layout.dialog_x_y_input);
		xyInputDialog.setTitle(getResources().getString(
				R.string.xydialogTitle_captureData));
		xyInputDialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;

		Button btn_ok = (Button) xyInputDialog.findViewById(R.id.btn_ok);

		btn_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText et_value_x = (EditText) xyInputDialog
						.findViewById(R.id.value_x);
				EditText et_value_y = (EditText) xyInputDialog
						.findViewById(R.id.value_y);

				String x_value = et_value_x.getText().toString();
				String y_value = et_value_y.getText().toString();

				if (x_value.isEmpty() || y_value.isEmpty()) {
					Toast.makeText(context, R.string.correctValuesWarning,
							Toast.LENGTH_LONG).show();
				} else {
					xyInputDialog.dismiss();

					if (!contextualMenuShown)
						toolbar.startActionMode(myActionModeCallback);

					MAP_MODE = FEATURE_DRAW_MAP_MODE;
					double lat = Double.parseDouble(y_value);
					double lon = Double.parseDouble(x_value);
					LatLng pointxy = new LatLng(lat, lon);

					googleMap.animateCamera(CameraUpdateFactory
							.newLatLng(pointxy));

					points.add(pointxy);
					MarkerOptions marker = new MarkerOptions()
							.position(pointxy);
					marker.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
					marker.draggable(true);
					marker.snippet(pointxy.latitude + "," + pointxy.longitude);
					Marker mark = googleMap.addMarker(marker);
					currMarkers.add(mark);

					if (points.size() > 1) {
						points.clear();
						currMarkers.clear();

						points.add(pointxy);
						currMarkers.add(mark);

						Marker tmp = (Marker) drawnFeature;
						tmp.remove();
					}
					drawnFeature = mark;
				}
			}
		});

		xyInputDialog.show();
	}

	private void startFetchingLocationfromGPS() {
		// restarting service to show suitable notifications
		locationManager.removeUpdates(locationListener);
		locationListener = new MyLocationListener();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				GPS_TIME, GPS_DISTANCE, locationListener);
		boolean isGPSEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (!isGPSEnabled) {
			cf.showGPSSettingsAlert(context);
		}
		if (actionMode != null) {
			actionMode.setTitle(R.string.no_fix_msg);
			actionMode.setSubtitle("");
		}
		locationManager.addGpsStatusListener(gpsListener);
	}

	private void toggleLayers() {
		String currVisibleLayers = cf.getVisibleLayers();
		SparseBooleanArray checkedItems = mDrawerList.getCheckedItemPositions();
		StringBuffer visibleLayers = new StringBuffer();
		if (checkedItems.indexOfKey(0) > -1) // satellite
		{
			if (checkedItems.get(0)) {
				if (!currVisibleLayers.contains("0"))
					googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
				visibleLayers.append("0");
			} else {
				googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}
		}

		if (checkedItems.indexOfKey(1) > -1) // captured data
		{
			if (checkedItems.get(1)) {
				if (visibleLayers.length() != 0)
					visibleLayers.append(",");

				visibleLayers.append("1");

				if (!currVisibleLayers.contains("1"))
					loadCapturedFeaturesFromDB();
			} else {
				googleMap.clear();
			}
		}

		for (int i = 0; i < offlineSpatialData.size(); i++) {
			if (offlineSpatialData.get(i).getOverlay() != null) {
				offlineSpatialData.get(i).getOverlay().remove();
				offlineSpatialData.get(i).setOverlay(null);
			}
			if (checkedItems.indexOfKey(i + 2) > -1) // load offline data
			{
				if (checkedItems.get(i + 2)) {
					if (visibleLayers.length() != 0)
						visibleLayers.append(",");
					visibleLayers.append(i + 2);
					// if(!currVisibleLayers.contains((i+2)+""))
					loadOfflineData(i);
				}
				/*
				 * else { if(offlineSpatialData.get(i).getOverlay()!=null) {
				 * offlineSpatialData.get(i).getOverlay().remove();
				 * offlineSpatialData.get(i).setOverlay(null);} }
				 */
			}
		}
		cf.saveVisibleLayers(visibleLayers.toString());
	}

	private void undoLastpointDraw() {
		try {
			if (drawnFeature instanceof Polygon) {
				Polygon poly = (Polygon) drawnFeature;
				List<LatLng> allPoints = poly.getPoints();
				if (allPoints.size() > 2 && points.size() > 0) {
					allPoints.remove(allPoints.size() - 2);// for removing last
															// point as first
															// and last point in
															// list are same
					points.remove(points.size() - 1);
					Marker marker = currMarkers.get(currMarkers.size() - 1);
					marker.remove();
					currMarkers.remove(currMarkers.size() - 1);
					poly.setPoints(allPoints);
					drawnFeature = poly;
				}
			} else if (drawnFeature instanceof Polyline) {
				Polyline line = (Polyline) drawnFeature;
				List<LatLng> allPoints = line.getPoints();
				if (allPoints.size() > 1 && points.size() > 0) {
					allPoints.remove(allPoints.size() - 1);
					points.remove(points.size() - 1);
					Marker marker = currMarkers.get(currMarkers.size() - 1);
					marker.remove();
					currMarkers.remove(currMarkers.size() - 1);
					line.setPoints(allPoints);
					drawnFeature = line;
				}
			}
		} catch (Exception e) {
			cf.appLog("", e);
			e.printStackTrace();
			Toast.makeText(context, "Error undo", Toast.LENGTH_SHORT).show();
		}
	}

	private void updateFeature() {
		String geomtype = "";
		List<LatLng> pointslist = new ArrayList<LatLng>();
		try {
			if (drawnFeature instanceof Polyline) {
				Polyline tmp = (Polyline) drawnFeature;

				pointslist = tmp.getPoints();
				geomtype = CommonFunctions.GEOM_LINE;
			} else if (drawnFeature instanceof Marker) {
				Marker tmp = (Marker) drawnFeature;
				pointslist.add(tmp.getPosition());
				geomtype = CommonFunctions.GEOM_POINT;
			} else if (drawnFeature instanceof Polygon) {
				Polygon tmp = (Polygon) drawnFeature;
				pointslist = tmp.getPoints();
				geomtype = CommonFunctions.GEOM_POLYGON;
			}

			if (pointslist.size() > 0) {
				String WKTStr = GeomFunctions.getWKTfromPoints(geomtype,
						pointslist);
				DBController db = new DBController(context);
				boolean result = db.updateFeature(WKTStr, featureId);
				db.close();

				if (result) {
					// refresh features from db
					refreshCapturedFeaturesFromDB();
					featureId = 0L;
					Toast.makeText(context, R.string.updateFeatureMsg,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(context, R.string.unableUpdatefeatureMsg,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(context, R.string.unableUpdatefeatureMsg,
						Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(context, R.string.unableUpdatefeatureMsg,
					Toast.LENGTH_SHORT).show();
			cf.appLog("", e);
			e.printStackTrace();
		}
	}
}
