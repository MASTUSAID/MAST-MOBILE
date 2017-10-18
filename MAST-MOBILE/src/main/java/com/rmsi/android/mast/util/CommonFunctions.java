package com.rmsi.android.mast.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Feature;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.domain.User;
import com.vividsolutions.jts.io.WKTReader;

public class CommonFunctions {
    private static CommonFunctions mInstance;
    private static Context mContext;
    private SharedPreferences mMyPreferences;
    public static final String parentFolderName = "MAST";
    public static final String dataFolderName = "spatialdata";
    public static final String dbFolderName = "database";
    public static final String mediaFolderName = "multimedia";      //add "." as prefix to the hide
    String appLogFileName = Environment.getExternalStorageDirectory() + "/MAST/MASTApp_LOG.txt";
    String syncLogFileName = Environment.getExternalStorageDirectory() + "/MAST/MASTSync_LOG.txt";
    List<LatLng> points;
    int MAP_MODE = 0;
    private WKTReader wktReader;

    private final String KEY_SERVER_ADDRESS = "server_address";
    private final String KEY_SNAP_TO_VERTEX = "snap_to_vertex";
    private final String KEY_ENABLE_LABELING = "enable_labeling";
    private final String KEY_ENABLE_VERTEX_DRAWING = "enable_vertex_drawing";
    private final String KEY_SNAP_TO_SEGMENT = "snap_to_segment";
    private final String KEY_SNAP_TOLERANCE = "snap_tolerance";
    private final String KEY_MAP_EXTENT = "map_extent";

    private static final int ESTIMATED_TOAST_HEIGHT_DIPS = 48;

    public static int MEDIA_SYNC_PENDING = 0;
    public static int MEDIA_SYNC_COMPLETED = 1;
    public static int MEDIA_SYNC_ERROR = 2;

    public static int polygonFillColor = Color.argb(0, 0, 0, 0);
    public static int polygonLineColor = Color.argb(255, 255, 200, 0);
    public static int lineColor = Color.argb(255, 102, 102, 255);
    public static int pointColor = Color.argb(255, 255, 255, 0);

    private static String roleStr;
    private static int roleId = -1;

    // Point in Tanzania
    public static double latitude = -7.8595;
    public static double longitude = 35.77981;

    // Default zooms
    public static float labelZoom = 16.5F;
    public static float vertexZoom = 17.5F;

    public static CommonFunctions getInstance() {
        if (mInstance == null)
            mInstance = new CommonFunctions();
        return mInstance;
    }

    public void Initialize(Context ctxt) {
        mContext = ctxt;
        mMyPreferences = mContext.getSharedPreferences("MASTMobilePref", Activity.MODE_PRIVATE);
        lineColor = getFeatureColor(getLineColor(), "line");
        pointColor = getFeatureColor(getPointColor(), "point");
        wktReader = new WKTReader();
    }

    public static Context getApplicationContext() {
        return mContext;
    }

    public void exitApplication(Context cntxt) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cntxt.startActivity(intent);
    }

    public void appLog(String Tag, Exception e) {
        try {
            PrintWriter pw = new PrintWriter(new File(appLogFileName));
            e.printStackTrace(pw);
            pw.append("##----------------------------------------------##");
            pw.append(new Date().toString());
            pw.close();
        } catch (Exception e1) {
        }
    }

    public void syncLog(String Tag, Exception e) {
        try {

            PrintWriter pw = new PrintWriter(new File(syncLogFileName));
            e.printStackTrace(pw);
            pw.append("##----------------------------------------------##");
            pw.append(new Date().toString());
            pw.close();
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
    }

    public SharedPreferences getmMyPreferences() {
        if (mMyPreferences == null) {
            Initialize(mContext.getApplicationContext());
        }
        return mMyPreferences;
    }

    public WKTReader getWktReader() {
        return wktReader;
    }

    public void addErrorMessage(String Tag, String message) {
        try {

            FileWriter fw = new FileWriter(new File(syncLogFileName));
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(Tag + " " + new Date().toString() + " :>>>> " + message);
            bw.append("##----------------------------------------------##");
            bw.close();
        } catch (Exception e1) {
            System.out.println(e1.getMessage());
        }
    }

    public void createLogfolder() {
        try {
            String extPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File parentdir = new File(extPath + "/" + parentFolderName);
            File datadir = new File(extPath + "/" + parentFolderName + "/" + dataFolderName);
            File dbdir = new File(extPath + "/" + parentFolderName + "/" + dbFolderName);
            File mediadir = new File(extPath + "/" + parentFolderName + "/" + mediaFolderName);

            if (!parentdir.exists() || !parentdir.isDirectory()) {
                parentdir.mkdirs();
            }

            if (!datadir.exists() || !datadir.isDirectory()) {
                datadir.mkdirs();
            }
            if (!dbdir.exists() || !dbdir.isDirectory()) {
                dbdir.mkdirs();
            }
            if (!mediadir.exists() || !mediadir.isDirectory()) {
                mediadir.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/*public void setLocale(String lang)
    {
		Locale myLocale = new Locale(lang);
		saveLocale(lang);	
		Locale.setDefault(myLocale);
		Configuration config = new Configuration();
		config.locale = myLocale;
		mContext.getResources().updateConfiguration(config,	mContext.getResources().getDisplayMetrics());
		//loadLocale(mContext);
	}*/

    //Save the current locale:
    public void saveLocale(String lang) {
        String langPref = "language";
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putString(langPref, lang);

        editor.commit();
    }

    public String getLocale() {
        String language = getmMyPreferences().getString("language", "en");
        return language;
    }

    public void loadLocale(Context context) {
        String language = getmMyPreferences().getString("language", "en");
        Locale myLocale = new Locale(language);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    public void saveDataCollectionTools(String dct) {
        String capturePref = "capture";
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putString(capturePref, dct);
        editor.commit();
    }

    public String getDataCollectionTools() {
        String capture = getmMyPreferences().getString("capture", "0,1,2");
        return capture;
    }


    public void saveAutoSync(boolean auto) {
        String syncPref = "Sync";
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putBoolean(syncPref, auto);
        editor.commit();
    }

    public boolean getAutoSync() {
        boolean Sync = getmMyPreferences().getBoolean("Sync", false);
        return Sync;
    }

    public void saveVisibleLayers(String layers) {
        String key = "visiblelayers";
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putString(key, layers);
        editor.commit();
    }

    public String getVisibleLayers() {
        String key = "visiblelayers";
        return getmMyPreferences().getString(key, "0");
    }

    public void saveGroupId(int groupId) {
        String id = "group_id";
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putInt(id, groupId);
        editor.commit();
    }

    public Long getGroupId() {
        return DbController.getInstance(mContext).getNewGroupId();
    }

    public void updatePolygonCount() {
        int polyCount = getPolygonCount();
        polyCount++;
        String key = "polygon_count";
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putInt(key, polyCount);
        editor.commit();
    }

    public int getPolygonCount() {
        int Id = getmMyPreferences().getInt("polygon_count", 0);
        return Id;
    }

    public void updateLineCount() {
        int lineCount = getPolygonCount();
        lineCount++;
        String key = "line_count";
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putInt(key, lineCount);
        editor.commit();
    }

    public int getLineCount() {
        int Id = getmMyPreferences().getInt("line_count", 0);
        return Id;
    }

    public void updatePointCount() {
        int pointCount = getPolygonCount();
        pointCount++;
        String key = "point_count";
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putInt(key, pointCount);
        editor.commit();
    }

    public int getPointCount() {
        int Id = getmMyPreferences().getInt("point_count", 0);
        return Id;
    }

    public void savePointColor(String color) {
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putString("pointcolor", color);
        editor.commit();
    }

    public String getPointColor() {
        return getmMyPreferences().getString("pointcolor", "yellow");
    }

    public void saveLineColor(String color) {
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putString("linecolor", color);
        editor.commit();
    }

    public String getLineColor() {
        return getmMyPreferences().getString("linecolor", "blue");
    }

    /** Returns unique application ID, generated per application installation. */
    public String getAppId(){
        String appId = getmMyPreferences().getString("app_id", "");
        if(StringUtility.isEmpty(appId)){
            // Generate new and save;
            appId = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = getmMyPreferences().edit();
            editor.putString("app_id", appId);
            editor.commit();
        }
        return appId;
    }

    public void savePolygonColor(String color) {
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putString("polycolor", color);
        editor.commit();
    }

    public void saveServerAddress(String address) {
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putString(KEY_SERVER_ADDRESS, address);
        editor.commit();
    }

    public String getServerAddress() {
        return getmMyPreferences().getString(KEY_SERVER_ADDRESS, "http://localhost");
    }

    public void saveSnapToVertex(boolean snap) {
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putBoolean(KEY_SNAP_TO_VERTEX, snap);
        editor.commit();
    }

    public boolean getSnapToVertex() {
        return getmMyPreferences().getBoolean(KEY_SNAP_TO_VERTEX, true);
    }

    public void saveSnapToSegment(boolean snap) {
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putBoolean(KEY_SNAP_TO_SEGMENT, snap);
        editor.commit();
    }

    public boolean getSnapToSegment() {
        return getmMyPreferences().getBoolean(KEY_SNAP_TO_SEGMENT, false);
    }

    public void saveSnapTolerance(int tolerance) {
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putInt(KEY_SNAP_TOLERANCE, tolerance);
        editor.commit();
    }

    public int getSnapTolerance() {
        return getmMyPreferences().getInt(KEY_SNAP_TOLERANCE, 30);
    }

    public void saveMapExtent(String extent) {
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putString(KEY_MAP_EXTENT, extent);
        editor.commit();
    }

    public void saveEnableVertexDrawing(boolean enable) {
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putBoolean(KEY_ENABLE_VERTEX_DRAWING, enable);
        editor.commit();
    }

    public boolean getEnableVertexDrawing() {
        return getmMyPreferences().getBoolean(KEY_ENABLE_VERTEX_DRAWING, true);
    }

    public void saveEnableLabeling(boolean enable) {
        SharedPreferences.Editor editor = getmMyPreferences().edit();
        editor.putBoolean(KEY_ENABLE_LABELING, enable);
        editor.commit();
    }

    public boolean getEnableLabeling() {
        return getmMyPreferences().getBoolean(KEY_ENABLE_LABELING, false);
    }

    public String getMapExtent() {
        return getmMyPreferences().getString(KEY_MAP_EXTENT, "");
    }

    public String getPolygonColor() {
        return getmMyPreferences().getString("polycolor", "yellow");
    }

    public int getFeatureColor(String selectedColor, String geomtype) {
        int alpha = 255;
        if (geomtype.equalsIgnoreCase("polygon"))
            alpha = 100;

        if (selectedColor.equalsIgnoreCase("Yellow")) {
            return Color.argb(alpha, 255, 255, 0);
        } else if (selectedColor.equalsIgnoreCase("Red")) {
            return Color.argb(alpha, 255, 145, 145);
        } else if (selectedColor.equalsIgnoreCase("Green")) {
            return Color.argb(alpha, 176, 255, 84);
        } else if (selectedColor.equalsIgnoreCase("White")) {
            return Color.argb(alpha, 255, 255, 255);
        } else if (selectedColor.equalsIgnoreCase("Cyan")) {
            return Color.argb(alpha, 0, 255, 255);
        } else if (selectedColor.equalsIgnoreCase("Blue")) {
            return Color.argb(alpha, 102, 102, 255);
        } else
            return Color.argb(alpha, 204, 255, 255);
    }

    public void showMessage(Context cntxt, String header, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cntxt, AlertDialog.THEME_HOLO_LIGHT);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle(header);
        String lang = getLocale();
        String ok = "Ok";
        if (lang.equalsIgnoreCase("sw")) {
            ok = "Sawa";
        }
        alertDialogBuilder.setNegativeButton(ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialogBuilder.show();
    }

    public void showToast(Context context, String message, int duration, int position) {
        Toast toast = Toast.makeText(context, message, duration);
        if (position != 0)
            toast.setGravity(position, 0, 0);
        toast.show();
    }

    public void showToast(Context context, int resourceId, int duration) {
        showToast(context, context.getResources().getString(resourceId), duration, Gravity.CENTER);
    }

    public String getIMEI() {
        TelephonyManager telephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }

    public boolean getConnectivityStatus() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }

    public void showIntenetSettingsAlert(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.internet_disabled_title);
        alertDialog.setMessage(R.string.internet_disabled_msg);
        // On pressing Settings button
        alertDialog.setPositiveButton(R.string.setting_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                context.startActivity(intent);
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    public void showGPSSettingsAlert(final Context context) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.gps_disabled_title);
        alertDialog.setMessage(R.string.gps_disabled_Msg);
        // On pressing Settings button
        alertDialog.setPositiveButton(R.string.setting_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private static boolean showToolTip(View view, CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        final int[] screenPos = new int[2]; // origin is device display
        final Rect displayFrame = new Rect(); // includes decorations (e.g. status bar)
        view.getLocationOnScreen(screenPos);
        view.getWindowVisibleDisplayFrame(displayFrame);

        final Context context = view.getContext();
        final int viewWidth = view.getWidth();
        final int viewHeight = view.getHeight();
        final int viewCenterX = screenPos[0] + viewWidth / 2;
        final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        final int estimatedToastHeight = (int) (ESTIMATED_TOAST_HEIGHT_DIPS
                * context.getResources().getDisplayMetrics().density);

        Toast cheatSheet = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        boolean showBelow = screenPos[1] < estimatedToastHeight;
        if (showBelow) {
            // Show below
            // Offsets are after decorations (e.g. status bar) are factored in
            cheatSheet.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                    viewCenterX - screenWidth / 2,
                    screenPos[1] - displayFrame.top + viewHeight);
        } else {
            // Show above
            // Offsets are after decorations (e.g. status bar) are factored in
            // NOTE: We can't use Gravity.BOTTOM because when the keyboard is up
            // its height isn't factored in.
            cheatSheet.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,
                    viewCenterX - screenWidth / 2,
                    screenPos[1] - displayFrame.top - estimatedToastHeight);
        }

        cheatSheet.show();
        return true;
    }

    //To set tooltip on longPress


    public void setup(View view, final CharSequence text) {
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return showToolTip(view, text);
            }
        });
    }

    public static boolean isFeatureReadOnly (long featureId){
        if(getRoleID() == User.ROLE_ADJUDICATOR)
            return true;

        Feature feature = DbController.getInstance(mContext).fetchFeaturebyID(featureId);
        if(feature != null && !StringUtility.empty(feature.getStatus()).equalsIgnoreCase(Property.CLIENT_STATUS_DRAFT))
            return true;

        return false;
    }

    public static int getRoleID() {
        return roleId;
    }

    public static void setRoleID(int id) {
        roleId = id;
    }

    // Added to scale down big images
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public Bitmap getSampleBitmapFromFile(String bitmapFilePath, int reqWidth, int reqHeight) {
        try {
            // calculating image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(new File(bitmapFilePath)), null, options);

            int scale = calculateInSampleSize(options, reqWidth, reqHeight);

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;

            return BitmapFactory.decodeStream(new FileInputStream(new File(bitmapFilePath)), null, o2);
        } catch (Exception e) {
            e.printStackTrace();
            appLog("", e);
            return null;
        }
    }

    public void saveGPSmode(int mAP_MODE, List<LatLng> gpspoints) {
        MAP_MODE = mAP_MODE;
        points = gpspoints;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public int getMAP_MODE() {
        return MAP_MODE;
    }

    // convert InputStream to String
    public static String getStringFromInputStream(InputStream is) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
