package com.rmsi.android.mast.activity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.rmsi.android.mast.activity.R.string;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Bookmark;
import com.rmsi.android.mast.domain.Feature;
import com.rmsi.android.mast.domain.MapFeature;
import com.rmsi.android.mast.domain.ProjectSpatialDataDto;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GisUtility;
import com.rmsi.android.mast.util.OfflineTileProvider;
import com.rmsi.android.mast.util.StringUtility;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTReader;

public class MapViewerActivity extends ActionBarActivity implements OnMapReadyCallback {
    // Google Map
    private GoogleMap googleMap;
    List<Marker> currMarkers = new ArrayList<Marker>();
    List<LatLng> points = new ArrayList<LatLng>();
    Context context = this;
    List<Coordinate> jtspoints = new ArrayList<Coordinate>();
    private List<String> mLayerTitles = new ArrayList<String>();
    private List<MapFeature> mapFeatures = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private LinearLayout drawerlayout;
    private ActionBarDrawerToggle mDrawerToggle;
    List<ProjectSpatialDataDto> offlineSpatialData;
    Object drawnFeature = new Object();
    private boolean contextualMenuShown = false;
    ActionMode actionMode;// assiging as global to set measured area/length in title
    Toolbar toolbar;
    TileOverlay overlay;
    private static int MAP_MODE = 0;
    private static int CLEAR_MODE = 0;
    private static int FEATURE_INFO_MODE = 1;
    private static int MAP_MEASURE_LINE_MODE = 2;
    private static int MAP_MEASURE_POLYGON_MODE = 3;
    private static int MEASURE_FEATURE_AREA_MODE = 4;
    private static int MEASURE_FEATURE_LENGTH_MODE = 5;
    private static int FETCH_XY_MODE = 6;
    private int colorTransparent = Color.argb(0, 255, 0, 0);
    DecimalFormat df = new DecimalFormat("#.######");
    CommonFunctions cf = CommonFunctions.getInstance();
    int role = 0;
    String capturFeatureStr, satelliteMapStr, rasterStr, offlineStr;
    private boolean enableLabeling = false;
    private boolean featuresAdded = false;
    private float lastZoom = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing context in common functions in case of a crash
        try {
            CommonFunctions.getInstance().Initialize(getApplicationContext());
        } catch (Exception e) {
        }
        cf.loadLocale(getApplicationContext());

        setContentView(R.layout.activity_map_viewer);

        capturFeatureStr = getResources().getString(R.string.capture_features);
        satelliteMapStr = getResources().getString(R.string.satellite_map);

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            role = extra.getInt("role");
        }

        if (role == 1)  // Hardcoded Id for Role (1=Trusted Intermediary, 2=Adjudicator)
        {
            findViewById(R.id.btn_verify_data).setVisibility(View.GONE);
            findViewById(R.id.btn_review_data).setVisibility(View.VISIBLE);
        } else if (role == 2) {
            findViewById(R.id.btn_verify_data).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_review_data).setVisibility(View.GONE);
        }

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        int newWidth = (width / 3) * 2;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.mapviewer);
        if (toolbar != null)
            setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //##########  NAVIGATION DRAWER #####################################################################
        offlineSpatialData = DbController.getInstance(context).getProjectSpatialData();

        mLayerTitles.add(satelliteMapStr);
        mLayerTitles.add(capturFeatureStr);
        //mLayerTitles.add("Offline data");

        for (int i = 0; i < offlineSpatialData.size(); i++) {
            mLayerTitles.add(offlineSpatialData.get(i).getAlias());
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_layer_manager);
        drawerlayout = (LinearLayout) findViewById(R.id.left_drawer);

        // Set the dpAdapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.item_list_multiple_choice, mLayerTitles));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        LayoutParams params = drawerlayout.getLayoutParams();
        params.width = newWidth;
        drawerlayout.setLayoutParams(params);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                toggleLayers();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                String visibleLayers = cf.getVisibleLayers();
                if (!visibleLayers.isEmpty()) {
                    String[] visibleArr = visibleLayers.split(",");
                    for (int i = 0; i < visibleArr.length; i++) {
                        mDrawerList.setItemChecked(Integer.parseInt(visibleArr[i]), true);
                    }
                }
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState(); // for button animation

        enableLabeling = cf.getEnableLabeling();

        try {
            // Loading map
            ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }

        Button btn_captureNewData = (Button) findViewById(R.id.btn_capture_new_data);
        btn_captureNewData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (role == User.ROLE_TRUSTED_INTERMEDIARY) {
                    DbController sqllite = DbController.getInstance(context);
                    if (sqllite.getClaimTypes(false).size() > 0) {
                        Intent intent = new Intent(MapViewerActivity.this, CaptureDataMapActivity.class);
                        startActivity(intent);
                    } else {
                        String info = getResources().getString(string.info);
                        String msg = getResources().getString(string.download_data_first);
                        cf.showMessage(context, info, msg);
                    }
                    sqllite.close();
                } else {
                    Toast.makeText(context, R.string.feature_not_allowed_msg, Toast.LENGTH_LONG).show();
                }
            }
        });

        Button btn_reviewData = (Button) findViewById(R.id.btn_review_data);
        btn_reviewData.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapViewerActivity.this, ReviewDataActivity.class);
                startActivity(intent);
            }
        });


        Button btn_verify = (Button) findViewById(R.id.btn_verify_data);
        btn_verify.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(MapViewerActivity.this, VerifyDataActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * function to load map If map is not created it will create it for you
     */
    @Override
    public void onMapReady(GoogleMap map) {
        if (googleMap == null) {
            googleMap = map;

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
            } else {
                // Changing map type
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // Showing / hiding your current location
                googleMap.setMyLocationEnabled(true);

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

                googleMap.setOnMapLoadedCallback(new mapLoadedCallBackListener());
                googleMap.setOnCameraIdleListener(new MapViewerActivity.mapCameraChangeListener());
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        handleMapClick(marker.getPosition());
                        return true;
                    }
                });
                loadUserSelectedLayers();
            }
        }
    }

    private class mapCameraChangeListener implements GoogleMap.OnCameraIdleListener {
        @Override
        public void onCameraIdle() {
            drawFeatures();
        }
    }

    private class mapLoadedCallBackListener implements OnMapLoadedCallback {
        @Override
        public void onMapLoaded() {
            GisUtility.zoomToMapExtent(googleMap);
        }
    }

    private class mapClickListener implements OnMapClickListener {
        @Override
        public void onMapClick(LatLng pointFromMap) {
            handleMapClick(pointFromMap);
        }
    }

    private void handleMapClick(LatLng pointFromMap){
        // Condition for MAP_MEASURE_LINE_MODE MODE
        if (MAP_MODE == MAP_MEASURE_LINE_MODE) {
            if (!contextualMenuShown)
                toolbar.startActionMode(myActionModeCallback);

            //for point
            Coordinate jtspoint = new Coordinate(pointFromMap.longitude, pointFromMap.latitude);
            jtspoints.add(jtspoint);
            points.add(pointFromMap);
            MarkerOptions marker = new MarkerOptions().position(pointFromMap);
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            marker.snippet(pointFromMap.latitude + "," + pointFromMap.longitude);
            Marker mark = googleMap.addMarker(marker);
            currMarkers.add(mark);

            if (points.size() > 1) {
                Polyline polyline;
                if (points.size() > 2) {
                    polyline = (Polyline) drawnFeature;
                    polyline.setPoints(points);
                } else {
                    //for polyline
                    PolylineOptions rectOptions = new PolylineOptions();

                    for (LatLng p : points) {
                        rectOptions.add(p);
                    }
                    rectOptions.zIndex(4);
                    polyline = googleMap.addPolyline(rectOptions);
                    drawnFeature = polyline;
                }

                //calculating Length
                Coordinate[] coordinates = jtspoints.toArray(new Coordinate[jtspoints.size()]);
                GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                LineString lineString = geometryFactory.createLineString(coordinates);
                double lineLength = lineString.getLength();
                double lengthMeter = lineLength * (111319.9) * Math.cos(Math.toRadians(jtspoints.get(0).y));
                String lineLengthStr = df.format(lengthMeter);

                if (actionMode != null) {
                    actionMode.setTitle(lineLengthStr + " m");
                }
            }
        }// Condition for MAP_MEASURE_POLYGON_MODE MODE
        else if (MAP_MODE == MAP_MEASURE_POLYGON_MODE) {
            if (!contextualMenuShown)
                toolbar.startActionMode(myActionModeCallback);

            //for point
            Coordinate jtspoint = new Coordinate(pointFromMap.longitude, pointFromMap.latitude);
            jtspoints.add(jtspoint);
            points.add(pointFromMap);
            MarkerOptions marker = new MarkerOptions().position(pointFromMap);
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            marker.snippet(pointFromMap.latitude + "," + pointFromMap.longitude);
            Marker mark = googleMap.addMarker(marker);
            currMarkers.add(mark);

            //for polygon
            if (points.size() > 2) {
                if (points.size() > 3) {
                    Polygon polygon = (Polygon) drawnFeature;
                    polygon.setPoints(points);
                } else {
                    PolygonOptions rectOptions = new PolygonOptions();
                    for (LatLng p : points) {
                        rectOptions.add(p);
                    }
                    rectOptions.fillColor(colorTransparent);
                    rectOptions.zIndex(4);
                    Polygon polygon = googleMap.addPolygon(rectOptions);
                    drawnFeature = polygon;
                }

                //calculating Length
                List<Coordinate> tmpJtsPoints = new ArrayList<Coordinate>(jtspoints);
                tmpJtsPoints.add(jtspoints.get(0)); // adding first point at last to create Ring
                Coordinate[] coordinates = tmpJtsPoints.toArray(new Coordinate[tmpJtsPoints.size()]);
                GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                LinearRing lr = geometryFactory.createLinearRing(coordinates);
                com.vividsolutions.jts.geom.Polygon polygonJts = geometryFactory.createPolygon(lr, null);
                double area = polygonJts.getArea();
                //String areaStr = df.format(area);
                double areaMeter = area * (Math.pow(111319.9, 2)) * Math.cos(Math.toRadians(tmpJtsPoints.get(0).y));
                String areaStr = df.format(areaMeter);
                if (actionMode != null) {
                    actionMode.setTitle(areaStr + " sqm");
                }
            }
        } else if (MAP_MODE == FEATURE_INFO_MODE) {
            processFeaturesInfo(pointFromMap);
            MAP_MODE = CLEAR_MODE;
        } else if (MAP_MODE == MEASURE_FEATURE_AREA_MODE) {
            processFeaturesForMeasurement(pointFromMap);
        } else if (MAP_MODE == MEASURE_FEATURE_LENGTH_MODE) {
            processFeaturesForMeasurement(pointFromMap);
        } else if (MAP_MODE == FETCH_XY_MODE) {
            showXYDialog(pointFromMap);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_viewer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_xy) {
            MAP_MODE = FETCH_XY_MODE;//showXYDialog();
            googleMap.setOnMapClickListener(new mapClickListener());
            Toast.makeText(context, "Tap on map to fetch XY", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.action_measure) {
            showMeasureDialog();
        } else if (id == R.id.action_gps) {
            Location location = googleMap.getMyLocation();

            if (location != null) {
                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 19));
            } else {
                Toast.makeText(context, R.string.no_location, Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.action_map_info) {
            MAP_MODE = FEATURE_INFO_MODE;
            googleMap.setOnMapClickListener(new mapClickListener()); //cdg
        } else if (id == R.id.action_show_bookmark) {
            showBookmarks();
        } else if (id == R.id.action_add_bookmark) {
            addNewBookmark();
        } else if (id == android.R.id.home) {
            //finish();
            return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mDrawerList.isItemChecked(position))
                mDrawerList.setItemChecked(position, true);
            else
                mDrawerList.setItemChecked(position, false);
        }
    }

    private void showXYDialog(LatLng pointFromMap) {
        final Dialog xyInputDialog = new Dialog(context, R.style.DialogTheme);
        xyInputDialog.setContentView(R.layout.dialog_x_y_input);
        xyInputDialog.setTitle(getResources().getString(R.string.xydialogTitle_mapViewer));
        xyInputDialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;

        ((EditText) xyInputDialog.findViewById(R.id.value_x)).setText(pointFromMap.longitude + "");
        ((EditText) xyInputDialog.findViewById(R.id.value_y)).setText(pointFromMap.latitude + "");
        ((EditText) xyInputDialog.findViewById(R.id.value_x)).setEnabled(false);
        ((EditText) xyInputDialog.findViewById(R.id.value_y)).setEnabled(false);

        Button btn_ok = (Button) xyInputDialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                xyInputDialog.dismiss();
                googleMap.setOnMapClickListener(null);
                MAP_MODE = CLEAR_MODE;
            }
        });
        xyInputDialog.show();
    }

    private void showMeasureDialog() {
        try {
            String[] measure_options = getResources().getStringArray(R.array.measure_options_arrays);

            final Dialog dialog = new Dialog(context, R.style.DialogTheme);
            dialog.setContentView(R.layout.dialog_show_list);
            dialog.setTitle(getResources().getString(R.string.measureDialogTitle));
            dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
            ListView listViewForMeasureoptions = (ListView) dialog.findViewById(R.id.commonlistview);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    R.layout.item_list_single_choice, measure_options);

            Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
            btn_ok.setVisibility(View.GONE);

            listViewForMeasureoptions.setAdapter(adapter);

            listViewForMeasureoptions.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent,
                                        View view, int position, long id) {
                    dialog.dismiss();
                    int itemPosition = position;
                    if (itemPosition == 0) // for polyline
                    {
                        MAP_MODE = MAP_MEASURE_LINE_MODE;
                        googleMap.setOnMapClickListener(new mapClickListener());
                    } else if (itemPosition == 1) // for polygon
                    {
                        MAP_MODE = MAP_MEASURE_POLYGON_MODE;
                        googleMap.setOnMapClickListener(new mapClickListener());
                    } else if (itemPosition == 2) // for polygon
                    {
                        MAP_MODE = MEASURE_FEATURE_AREA_MODE;
                        googleMap.setOnMapClickListener(new mapClickListener());
                    } else if (itemPosition == 3) // for polyline
                    {
                        MAP_MODE = MEASURE_FEATURE_LENGTH_MODE;
                        googleMap.setOnMapClickListener(new mapClickListener());
                    }
                }
            });

            dialog.show();
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }
    }

    private ActionMode.Callback myActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.map_create_features_contextual_menu, menu);
            contextualMenuShown = true;
            menu.removeItem(R.id.save_features);
            actionMode = mode;

            //removing plot by gps in case of other modes
            if (MAP_MODE != MAP_MEASURE_LINE_MODE || MAP_MODE != MAP_MEASURE_POLYGON_MODE)
                menu.removeItem(R.id.plot_by_gps);

            //hiding bottom menu
            hideBottomMenu();

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            contextualMenuShown = false;
            clearDrawnFeaturesFromMap();
            MAP_MODE = CLEAR_MODE;

            //showing bottom menu
            showBottomMenu();
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.clear_features:
                    clearDrawnFeaturesFromMap();
                    actionMode.setTitle("");
                    return true;
                default:
                    mode.finish();
                    return false;
            }
        }

        @Override
        public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
            return false;
        }
    };

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
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }

        googleMap.setOnMapClickListener(null);
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

                if (!currVisibleLayers.contains("1") && mapFeatures.size() < 1) {
                    loadFeaturesFromDB();
                    drawFeatures();
                }
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
                    //if(!currVisibleLayers.contains((i+2)+""))
                    loadOfflineData(i);
                }
            }
        }
        cf.saveVisibleLayers(visibleLayers.toString());
    }

    private void loadFeaturesFromDB() {
        try {
            clearMapFeatures();
            List<Feature> features = DbController.getInstance(context).fetchFeatures();

            if (features.size() == 0) {
                Toast.makeText(context, R.string.noFeaturesToLoad, Toast.LENGTH_SHORT).show();
                return;
            }

            for (Feature feature : features) {
                if(!StringUtility.isEmpty(feature.getCoordinates())) {
                    mapFeatures.add(new MapFeature(feature));
                }
            }
        } catch (Exception e) {
            cf.appLog("", e);
            Toast.makeText(context,"unable to load features from db", Toast.LENGTH_SHORT).show();
        }
    }

    private void drawFeatures(){
        // Add features on the map
        if(mapFeatures == null || mapFeatures.size() < 1)
            return;

        LatLngBounds bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        float zoom = googleMap.getCameraPosition().zoom;

        if(zoom >= 10 && !featuresAdded){
            featuresAdded = true;
            for (MapFeature mapFeature : mapFeatures) {
                if (mapFeature.getFeatureType() == MapFeature.TYPE.POLYGON) {
                    mapFeature.setMapPolygon(googleMap.addPolygon(mapFeature.getPolygon()));
                } else if (mapFeature.getFeatureType() == MapFeature.TYPE.LINE) {
                    mapFeature.setMapLine(googleMap.addPolyline(mapFeature.getLine()));
                } else if (mapFeature.getFeatureType() == MapFeature.TYPE.POINT) {
                    mapFeature.setMapPoint(googleMap.addCircle(mapFeature.getPoint()));
                }
            }
        }

        if(featuresAdded){
            // Draw labels
            if(enableLabeling && (lastZoom >= CommonFunctions.labelZoom || zoom >= CommonFunctions.labelZoom)){
                for (MapFeature mapFeature : mapFeatures) {
                    if (mapFeature.getFeatureType() == MapFeature.TYPE.POLYGON) {
                        if(zoom >= CommonFunctions.labelZoom) {
                            if(mapFeature.containsInBoundary(bounds) && mapFeature.getMapLabel() == null)
                                mapFeature.setMapLabel(googleMap.addMarker(mapFeature.getLabel()));
                        }
                        else
                            mapFeature.removeMapLabel();
                    }
                }
            }
        }

        lastZoom = zoom;
    }

    private void clearMapFeatures(){
        featuresAdded = false;
        for(MapFeature mapFeature : mapFeatures){
            mapFeature.removeFromMap();
        }
        mapFeatures.clear();
    }

    private void processFeaturesInfo(LatLng pointFromMap) {
        try {
            com.vividsolutions.jts.geom.Point pointClicked = new GeometryFactory().createPoint(new Coordinate(pointFromMap.longitude, pointFromMap.latitude));

            for(MapFeature mapFeature : mapFeatures){
                if (mapFeature.containsPoint(pointClicked)) {
                    Intent myIntent = new Intent(context, CaptureAttributesActivity.class);
                    myIntent.putExtra("featureid", mapFeature.getFeature().getId());
                    startActivity(myIntent);
                    return;
                }
            }

            Toast.makeText(context, R.string.noLocationFound, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }
    }

    private void loadOfflineData(int pos) {
        try {
            String extPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            String filepath = extPath + "/" + CommonFunctions.parentFolderName + "/" + CommonFunctions.dataFolderName
                    + "/" + offlineSpatialData.get(pos).getFile_Name();
            OfflineTileProvider provider = offlineSpatialData.get(pos).getProvider();
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

            // Sometime later when the map view is destroyed, close the provider.
            // This is important to prevent a leak of the backing SQLiteDatabase.
            provider.close();
        } catch (Exception e) {
            cf.appLog("", e);
            //String msg = getResources().getString(R.string.unableToLoadOfflineData);
            //Toast.makeText(context,msg+": "+offlineSpatialData.get(pos).getAlias(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showBottomMenu() {
        View bottomMenu = findViewById(R.id.bottom_menu);
        // Show the panel
        Animation bottomUp = AnimationUtils.loadAnimation(this, R.anim.bottom_up);

        bottomMenu.startAnimation(bottomUp);
        bottomMenu.setVisibility(View.VISIBLE);
    }

    private void hideBottomMenu() {
        View bottomMenu = findViewById(R.id.bottom_menu);
        // Hide the Panel
        Animation bottomDown = AnimationUtils.loadAnimation(this, R.anim.bottom_down);

        bottomMenu.startAnimation(bottomDown);
        bottomMenu.setVisibility(View.GONE);
    }

    private void loadUserSelectedLayers() {
        String visibleLayers = cf.getVisibleLayers();

        if (!visibleLayers.isEmpty()) {
            //Toast.makeText(context, "Loading user selected Layers...", Toast.LENGTH_SHORT).show();
            if (visibleLayers.contains("0")) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                mDrawerList.setItemChecked(0, true);
            }
            if (visibleLayers.contains("1")) {
                loadFeaturesFromDB();
                mDrawerList.setItemChecked(1, true);
            }

            for (int i = 0; i < offlineSpatialData.size(); i++) {
                if (visibleLayers.contains((i + 2) + "")) {
                    loadOfflineData(i);
                    //loadOfflineData(offlineSpatialData.size()-i-1);
                    mDrawerList.setItemChecked(((i + 2)), true);
                }
            }
        }
    }

    private void processFeaturesForMeasurement(LatLng pointFromMap) {
        try {
            boolean result = false;
            String geomtype = "";

            if (MAP_MODE == MEASURE_FEATURE_AREA_MODE) {
                geomtype = Feature.GEOM_POLYGON;
            } else if (MAP_MODE == MEASURE_FEATURE_LENGTH_MODE) {
                geomtype = Feature.GEOM_LINE;
            }

            DbController dbObj = DbController.getInstance(context);
            List<Feature> features = dbObj.fetchFeaturesByGeomtype(geomtype);
            dbObj.close();
            String coordinates = null;

            for (Feature feature : features) {
                if (feature.getGeomType().equalsIgnoreCase(Feature.GEOM_POLYGON)) {
                    com.vividsolutions.jts.geom.Point ptClicked = new GeometryFactory().createPoint(new Coordinate(pointFromMap.longitude, pointFromMap.latitude));
                    Geometry geom = new WKTReader().read("POLYGON ((" + feature.getCoordinates() + "))");

                    com.vividsolutions.jts.geom.Polygon poly = (com.vividsolutions.jts.geom.Polygon) geom;

                    result = GisUtility.IsPointInPolygon(ptClicked, poly);

                    if (result) {
                        coordinates = feature.getCoordinates();
                    }
                } else if (feature.getGeomType().equalsIgnoreCase(Feature.GEOM_LINE)) {
                    com.vividsolutions.jts.geom.Point ptClicked = new GeometryFactory().createPoint(new Coordinate(pointFromMap.longitude, pointFromMap.latitude));

                    Geometry geom = new WKTReader().read("LINESTRING (" + feature.getCoordinates() + ")");

                    com.vividsolutions.jts.geom.LineString line = (com.vividsolutions.jts.geom.LineString) geom;

                    result = GisUtility.IsPointIntersectsLine(ptClicked, line);

                    if (result) {
                        coordinates = feature.getCoordinates();
                    }
                }

                if (result)
                    break;
            }

            if (result) {
                googleMap.setOnMarkerClickListener(null);
                if (MAP_MODE == MEASURE_FEATURE_AREA_MODE && !TextUtils.isEmpty(coordinates)) {
                    Geometry geom = new WKTReader().read("POLYGON ((" + coordinates + "))");
                    com.vividsolutions.jts.geom.Polygon polygonJts = (com.vividsolutions.jts.geom.Polygon) geom;
                    double area = polygonJts.getArea();
                    double areaMeter = area * (Math.pow(111319.9, 2)) * Math.cos(Math.toRadians(polygonJts.getCoordinates()[0].y));
                    String areaStr = df.format(areaMeter);
                    showToast(getResources().getString(R.string.areaTxt) + " : " + areaStr + " Sqm", Toast.LENGTH_LONG, Gravity.CENTER);
                } else if (MAP_MODE == MEASURE_FEATURE_LENGTH_MODE && !TextUtils.isEmpty(coordinates)) {
                    Geometry geom = new WKTReader().read("LINESTRING (" + coordinates + ")");
                    LineString lineString = (LineString) geom;
                    double lineLength = lineString.getLength();
                    double lengthMeter = lineLength * (111319.9) * Math.cos(Math.toRadians(lineString.getCoordinates()[0].y));
                    String lineLengthStr = df.format(lengthMeter);
                    showToast(getResources().getString(R.string.areaTxt) + " : " + lineLengthStr + " meters", Toast.LENGTH_LONG, Gravity.CENTER);
                }
                googleMap.setOnMapClickListener(null);
                MAP_MODE = CLEAR_MODE;
            } else {
                Toast.makeText(context, R.string.noFeatureFoundMsg, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }
    }

    private void showToast(final String message, final int length, final int position) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(MapViewerActivity.this, message, length);

                if (position != 0)
                    toast.setGravity(position, 0, 0);

                toast.show();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void showBookmarks() {
        try {
            DbController db = DbController.getInstance(context);
            Object[] obj = db.fetchAllBookmarks();

            final List<Bookmark> bookmarks = (List<Bookmark>) obj[0];
            List<String> bookmarksStr = (List<String>) obj[1];

            if (bookmarks.size() > 0) {
                final Dialog dialog = new Dialog(context, R.style.DialogTheme);
                dialog.setContentView(R.layout.dialog_show_list);
                dialog.setTitle(getResources().getString(R.string.bookmarkDialogTitle));
                dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
                ListView listViewForMeasureoptions = (ListView) dialog.findViewById(R.id.commonlistview);

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_list_single_choice, bookmarksStr);

                Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
                btn_ok.setVisibility(View.GONE);

                listViewForMeasureoptions.setAdapter(adapter);

                listViewForMeasureoptions.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int position, long id) {
                        dialog.dismiss();
                        Bookmark bookmrk = bookmarks.get(position);

                        LatLng myLocation = new LatLng(bookmrk.getLatitude(), bookmrk.getLongitude());
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, bookmrk.getZoomlevel()));
                    }
                });
                dialog.show();
            } else {
                Toast.makeText(context, "No Bookmarks to show..!!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }
    }

    private void addNewBookmark() {
        final EditText input = new EditText(context);
        new AlertDialog.Builder(context)
                .setTitle("Enter Bookmark Name")
                //.setMessage(message)
                .setView(input)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String bookmarkName = input.getText().toString();

                        if (!TextUtils.isEmpty(bookmarkName)) {
                            Bookmark bookmark = new Bookmark();
                            bookmark.setName(bookmarkName);
                            bookmark.setZoomlevel(googleMap.getCameraPosition().zoom);
                            bookmark.setLatitude(googleMap.getCameraPosition().target.latitude);
                            bookmark.setLongitude(googleMap.getCameraPosition().target.longitude);
                            DbController db = DbController.getInstance(context);
                            boolean result = db.saveBookmark(bookmark);
                            db.close();
                            if (result) {
                                showToast("Bookmark saved..!!", Toast.LENGTH_SHORT,
                                        0);
                            } else {
                                showToast("Unable to save Bookmark", Toast.LENGTH_SHORT, 0);
                            }
                            dialog.dismiss();
                        } else {
                            showToast("Please enter bookmark name..!!", Toast.LENGTH_SHORT, 0);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        for (int i = 0; i < offlineSpatialData.size(); i++) {
            OfflineTileProvider provider = offlineSpatialData.get(i).getProvider();
            if (provider != null) provider.close();
        }
    }
}
