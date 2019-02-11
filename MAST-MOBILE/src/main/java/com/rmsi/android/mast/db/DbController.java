package com.rmsi.android.mast.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.domain.AOI;
import com.rmsi.android.mast.domain.AcquisitionType;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Bookmark;
import com.rmsi.android.mast.domain.ClaimType;
import com.rmsi.android.mast.domain.Classification;
import com.rmsi.android.mast.domain.ClassificationAttribute;
import com.rmsi.android.mast.domain.DeceasedPerson;
import com.rmsi.android.mast.domain.Dispute;
import com.rmsi.android.mast.domain.DisputeType;
import com.rmsi.android.mast.domain.Feature;
import com.rmsi.android.mast.domain.Gender;
import com.rmsi.android.mast.domain.Media;
import com.rmsi.android.mast.domain.NonNatural;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.domain.Person;
import com.rmsi.android.mast.domain.PersonOfInterest;
import com.rmsi.android.mast.domain.ProjectSpatialDataDto;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.domain.RefData;
import com.rmsi.android.mast.domain.RelationshipType;
import com.rmsi.android.mast.domain.ResourceAttribute;
import com.rmsi.android.mast.domain.ResourceCustomAttribute;
import com.rmsi.android.mast.domain.ResourceOwner;
import com.rmsi.android.mast.domain.ResourcePersonOfInterest;
import com.rmsi.android.mast.domain.ResourcePoiSync;
import com.rmsi.android.mast.domain.Right;
import com.rmsi.android.mast.domain.RightType;
import com.rmsi.android.mast.domain.ShareType;
import com.rmsi.android.mast.domain.SubClassificationAttribute;
import com.rmsi.android.mast.domain.Summary;
import com.rmsi.android.mast.domain.TenureInformation;
import com.rmsi.android.mast.domain.TenureType;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.domain.Village;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.StringUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class DbController extends SQLiteOpenHelper {
    Context contxt;
    private static DbController instance;
    private String res = "Resource";
    SQLiteDatabase db;

    static String DBPATH = "/" + CommonFunctions.parentFolderName + "/" + CommonFunctions.dbFolderName + "/mast_mobile.db";
    static String DB_FULL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + DBPATH;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH);

    private static int DB_VERSION = 6;
    CommonFunctions cf = CommonFunctions.getInstance();
//    CommonFunctions cf = null;

    public static synchronized DbController getInstance(Context context) {
        if (instance == null) {
            instance = new DbController(context.getApplicationContext());
        }
        return instance;
    }

    private DbController(Context applicationcontext) {
        super(applicationcontext, DB_FULL_PATH, null, DB_VERSION);
//        contxt.openOrCreateDatabase(DB_FULL_PATH, contxt.MODE_PRIVATE, null);
        this.contxt = applicationcontext;
        DB_FULL_PATH = "/" + CommonFunctions.parentFolderName + "/" + CommonFunctions.dbFolderName + "/mast_mobile.db";

        cf = CommonFunctions.getInstance();
        try {
            cf.Initialize(applicationcontext.getApplicationContext());
        } catch (Exception e) {
        }


        this.contxt = contxt;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Main tables

        String query_table1 = "CREATE TABLE SPATIAL_FEATURES (" +
                "FEATURE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "SERVER_FEATURE_ID TEXT," +
                "COORDINATES TEXT," +
                "GEOMTYPE TEXT," +
                "CREATEDTIME TEXT," +
                "STATUS TEXT," +
                "COMPLETEDTIME TEXT," +
                "IMEI TEXT," +
                "HAMLET_ID INTEGER," +
                "WITNESS_1 TEXT," +
                "WITNESS_2 TEXT," +
                "CLAIM_TYPE INTEGER," +
                "POLYGON_NUMBER TEXT," +
                "SURVEY_DATE TEXT," +
                "UKA_NUMBER TEXT," +
                "CLASSIFICATION_ID TEXT," +
                "SUBCLASSIFICATION_ID TEXT," +
                "TENURE_ID TEXT," +
                "FLAG TEXT," +
                "IP_NUMBER INTEGER," +
                "CLAIM_RIGHT TEXT," +
                "PLOT_NO TEXT," +
                "DOCUMENT TEXT," +
                "DOCUMENT_TYPE TEXT," +
                "DOCUMENT_DATE TEXT," +
                "DOCUMENT_REF_NO TEXT," +
                "IS_NATURAL INTEGER," +
                "VILLAGE_ID INTEGER," +
                "FEATURE_TYPE TEXT," +
                "FEATURE_DESCRIPTION TEXT" +
                ")";

        String query_table10 = "CREATE TABLE SOCIAL_TENURE(" +
                "ID INTEGER PRIMARY KEY," +
                "FEATURE_ID INTEGER," +
                "SERVER_PK INTEGER," +
                "SHARE_TYPE INTEGER," +
                "RELATIONSHIP_ID INTEGER," +
                "RIGHT_TYPE INTEGER," +
                "CERT_NUMBER TEXT," +
                "CERT_ISSUE_DATE TEXT," +
                "JURIDICAL_AREA REAL," +
                "Acquisition INTEGER" +
                ")";

        String query_table8 = "CREATE TABLE PERSON(" +
                "ID INTEGER PRIMARY KEY," +
                "SOCIAL_TENURE_ID INTEGER," +
                "DISPUTE_ID INTEGER," +
                "ACQUISITION_TYPE_ID INTEGER," +
                "SHARE TEXT," +
                "RESIDENT INTEGER," +
                "IS_NATURAL INTEGER," +
                "FEATURE_ID INTEGER," +
                "SERVER_PK INTEGER," +
                "PERSON_SUBTYPE INTEGER" +
                ")";

        String query_table16 = "CREATE TABLE NEXT_KIN_DETAILS(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NEXT_KIN_NAME TEXT," +
                "DOB TEXT," +
                "GENDER_ID INTEGER," +
                "RELATIONSHIP_ID INTEGER," +
                "FEATURE_ID INTEGER" +
                ")";

        String query_table17 = "CREATE TABLE DECEASED_PERSON(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "FIRST_NAME TEXT," +
                "MIDDLE_NAME TEXT," +
                "LAST_NAME TEXT," +
                "FEATURE_ID INTEGER" +
                ")";

        String tableDispute = "CREATE TABLE DISPUTE(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "SERVER_ID INTEGER," +
                "FEATURE_ID INTEGER," +
                "DISPUTE_TYPE INTEGER," +
                "DESCRIPTION TEXT," +
                "REG_DATE TEXT" +
                ")";

        String query_table7 = "CREATE TABLE MEDIA(" +
                "MEDIA_ID INTEGER PRIMARY KEY," +
                "FEATURE_ID INTEGER," +
                "PERSON_ID INTEGER," +
                "DISPUTE_ID INTEGER," +
                "TYPE TEXT," +
                "PATH TEXT," +
                "SYNCED INTEGER DEFAULT 0" +
                ")";

        // Attributes
        String query_table2 = "CREATE TABLE ATTRIBUTE_MASTER(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ATTRIB_ID INTEGER," +
                "ATTRIBUTE_TYPE STRING," +
                "ATTRIBUTE_TYPE_NAME STRING," +
                "ATTRIBUTE_CONTROLTYPE INTEGER," +
                "ATTRIBUTE_NAME TEXT," +
                "LISTING INTEGER," +
                "ATTRIBUTE_NAME_OTHER TEXT," +
                "VALIDATION TEXT," +
                "FLAG TEXT" +
                ")";

        String query_table3 = "CREATE TABLE OPTIONS(" +
                "OPTION_ID INTEGER," +
                "ATTRIB_ID INTEGER," +
                "OPTION_NAME TEXT," +
                "OPTION_NAME_OTHER TEXT)";

        String query_table4 = "CREATE TABLE FORM_VALUES(" +
                "GROUP_ID INTEGER," +
                "ATTRIB_ID INTEGER," +
                "ATTRIB_VALUE TEXT," +
                "FEATURE_ID TEXT," +
                "LABEL_NAME TEXT" +
                ")";

        // Ref data
        String query_table11 = "CREATE TABLE GROUPID_SEQ(VALUE INTEGER)";
        String query_table13 = "CREATE TABLE PROJECT_SPATIAL_DATA(SERVER_PK INTEGER,PROJECT_NAME_ID INTEGER,FILE_NAME TEXT,FILE_LOCATION TEXT,DESCRIPTION TEXT,CREATED_BY INTEGER,MODIFIED_BY INTEGER,CRAETED_DATE INTEGER,MODIFIED_DATE INTEGER,DOCUMENT_FORMAT_ID INTEGER,SIZE INTEGER,VILLAGE_NAME TEXT,ISACTIVE BOOLEAN DEFAULT true)";
        String query_table14 = "CREATE TABLE HAMLET_DETAILS(ID INTEGER PRIMARY KEY, HAMLET_NAME TEXT, HAMLET_LEADER TEXT)";
        String query_table15 = "CREATE TABLE ADJUDICATOR_DETAILS(ID INTEGER PRIMARY KEY,ADJUDICATOR_NAME TEXT)";

        String query_table18 = "CREATE TABLE CLAIM_TYPE(" +
                "CODE TEXT PRIMARY KEY," +
                "NAME TEXT," +
                "NAME_OTHER_LANG TEXT," +
                "ACTIVE BOOLEAN DEFAULT true" +
                ")";

        String query_table19 = "CREATE TABLE RELATIONSHIP_TYPE(" +
                "CODE INTEGER PRIMARY KEY," +
                "NAME TEXT," +
                "NAME_OTHER_LANG TEXT," +
                "ACTIVE BOOLEAN DEFAULT true" +
                ")";

        String query_table20 = "CREATE TABLE SHARE_TYPE(" +
                "CODE INTEGER PRIMARY KEY," +
                "NAME TEXT," +
                "NAME_OTHER_LANG TEXT," +
                "ACTIVE BOOLEAN DEFAULT true" +
                ")";

        String query_table21 = "CREATE TABLE RIGHT_TYPE(" +
                "CODE INTEGER PRIMARY KEY," +
                "NAME TEXT," +
                "NAME_OTHER_LANG TEXT," +
                "ACTIVE BOOLEAN DEFAULT true," +
                "FOR_ADJUDICATION INTEGER" +
                ")";

        String query_table22 = "CREATE TABLE GENDER(" +
                "CODE INTEGER PRIMARY KEY," +
                "NAME TEXT," +
                "NAME_OTHER_LANG TEXT," +
                "ACTIVE BOOLEAN DEFAULT true" +
                ")";

        String tableDisputeType = "CREATE TABLE DISPUTE_TYPE(" +
                "CODE INTEGER PRIMARY KEY," +
                "NAME TEXT," +
                "NAME_OTHER_LANG TEXT," +
                "ACTIVE INTEGER DEFAULT 1" +
                ")";

        String tableAcquisitionType = "CREATE TABLE ACQUISITION_TYPE(" +
                "CODE INTEGER PRIMARY KEY," +
                "NAME TEXT," +
                "NAME_OTHER_LANG TEXT," +
                "ACTIVE BOOLEAN DEFAULT true" +
                ")";

        // System
        String query_table5 = "CREATE TABLE USER(USER_ID TEXT,USER_NAME TEXT,PASSWORD TEXT,ROLE_ID TEXT,ROLE_NAME TEXT)";
        String query_table6 = "CREATE TABLE BOOKMARKS(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,LATITUDE TEXT,LONGITUDE TEXT,ZOOMLEVEL TEXT)";

        /**********************************************************************************Ambar**********************************/
        String query_classifi_table = "CREATE TABLE CLASSIFICATION(ATTRIID INTEGER,ATTRIVALUE TEXT)";
        String query_subclassifi_table = "CREATE TABLE SUB_CLASSIFICATION(ATTRIID INTEGER,ATTRIVALUE TEXT,ClassificationID INTEGER)";
        String query_resBasicInfo_table = "CREATE TABLE RESOURCE_BASISC_ATTRIBUTES(FEATURE_ID INTEGER ,VALUE TEXT,ID TEXT)";

        String query_Tenure_Information = "CREATE TABLE Tenure_Information(FEATURE_ID INTEGER PRIMARY KEY AUTOINCREMENT ,CID TEXT,SID TEXT,TID TEXT)";

        String query_table78 = "CREATE TABLE RESOURCE_ATTRIBUTE_MASTER(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ATTRIB_ID INTEGER," +
                "ATTRIBUTE_TYPE STRING," +
                "ATTRIBUTE_TYPE_NAME STRING," +
                "ATTRIBUTE_CONTROLTYPE INTEGER," +
                "ATTRIBUTE_NAME TEXT," +
                "LISTING INTEGER," +
                "ATTRIBUTE_NAME_OTHER TEXT," +
                "VALIDATION TEXT," +
                "SUBCLASSI_ID TEXT," +
                "FLAG TEXT" +
                ")";

        String query_table79 = "CREATE TABLE RESOURCE_FORM_VALUES(" +
                "GROUP_ID INTEGER," +
                "ATTRIB_ID INTEGER," +
                "ATTRIB_VALUE TEXT," +
                "SUBCLASSIFICATION_ID TEXT," +
                "FEATURE_ID TEXT," +
                "OPTION_ID INTEGER" +
                ")";

        String query_tenureType = "CREATE TABLE TENURE_TYPE(ATTRIID INTEGER,ATTRIVALUE TEXT,LISTING TEXT,FLAG TEXT)";


        String query_res_poi = "CREATE TABLE RESOURCE_POI(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ATTRIB_ID INTEGER," +
                "ATTRIBUTE_NAME STRING," +
                "ATTRIBUTE_NAME_OTHER STRING," +
                "ATTRIBUTE_CONTROLTYPE INTEGER," +
                "VALIDATION TEXT," +
                "LISTING INTEGER" +
                ")";

        String query_res_poi_value = "CREATE TABLE RESOURCE_POI_VALUE(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NEXT_KIN_NAME TEXT," +
                "DOB TEXT," +
                "GENDER_ID INTEGER," +
                "RELATIONSHIP_ID INTEGER," +
                "FEATURE_ID INTEGER" +
                ")";


        String query_res_poi_value_sync = "CREATE TABLE RESOURCE_POI_VALUE_SYNC(" +
                "GROUP_ID INTEGER," +
                "ATTRIB_ID TEXT," +
                "ATTRIB_VALUE TEXT," +
                "FEATURE_ID INTEGER" +
                ")";


        String query_AOI = "CREATE TABLE AOI (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "AOIID TEXT," +
                "AOINAME TEXT," +
                "USERID INTEGER," +
                "PROJECTNAME_ID TEXT," +
                "COORDINATES TEXT," +
                "ISACTIVE TEXT" +
                ")";


        String query_Non = "CREATE TABLE NON_NATURAL(" +
                "ATTRIB_ID INTEGER," +
                "ATTRIB_VALUE TEXT," +
                "FEATURE_ID TEXT," +
                "LABEL_NAME TEXT" +
                ")";

        String queryVillages = "CREATE TABLE VILLAGE(ID INTEGER, NAME TEXT, NAME_EN TEXT)";

        try {
            dropTable(db, "SPATIAL_FEATURES");
            db.execSQL(query_table1);
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }
        try {
            dropTable(db, "ATTRIBUTE_MASTER");
            dropTable(db, "OPTIONS");
            db.execSQL(query_table2);

        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }
        try {
            db.execSQL(query_table3);
            db.execSQL(query_table4);
            db.execSQL(query_table5);
            db.execSQL(query_table6);
            db.execSQL(query_table7);
            db.execSQL(query_table8);

            db.execSQL(query_table10);
            db.execSQL(query_table11);
            db.execSQL(query_table13);
            db.execSQL(query_table14);
            db.execSQL(query_table15);
            db.execSQL(query_table16);
            db.execSQL(query_table17);
            db.execSQL(query_table18);
            db.execSQL(query_table19);
            db.execSQL(query_table20);
            db.execSQL(query_table21);
            db.execSQL(query_table22);
            db.execSQL(tableAcquisitionType);
            db.execSQL(tableDisputeType);
            db.execSQL(tableDispute);
            db.execSQL(query_subclassifi_table);
            db.execSQL(query_classifi_table);
            db.execSQL(query_resBasicInfo_table);
            db.execSQL(query_Tenure_Information);
            db.execSQL(query_table78);
            db.execSQL(query_table79);
            db.execSQL(query_tenureType);
            db.execSQL(query_res_poi);
            db.execSQL(query_res_poi_value);
            db.execSQL(query_res_poi_value_sync);
            db.execSQL(query_AOI);
            db.execSQL(query_Non);
            db.execSQL(queryVillages);
            //query_AOI
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
        }
    }

    private SQLiteDatabase getDb() {

        cf.createLogfolder();

        if (db == null || !db.isOpen()) {
            db = getWritableDatabase();
        }
        return db;
    }


//    private SQLiteDatabase getDb() {
//
//
//        if (db == null || !db.isOpen()) {
//            db = getWritableDatabase();
//        }
//        return db;
//    }

    private void cleanDb(SQLiteDatabase db) {
        db.delete("FORM_VALUES", null, null);
        db.delete("PERSON", null, null);
        db.delete("MEDIA", null, null);
        db.delete("SPATIAL_FEATURES", null, null);
        db.delete("SOCIAL_TENURE", null, null);
        db.delete("PROJECT_SPATIAL_DATA", null, null);
        db.delete("OPTIONS", null, null);
        db.delete("ATTRIBUTE_MASTER", null, null);

        db.delete(ClaimType.TABLE_NAME, null, null);
        db.delete(RightType.TABLE_NAME, null, null);
        db.delete(ShareType.TABLE_NAME, null, null);
        db.delete(RelationshipType.TABLE_NAME, null, null);
        db.delete(Gender.TABLE_NAME, null, null);
    }

    public void dropTable(SQLiteDatabase database, String tableName) {
        String query;
        query = "DROP TABLE IF EXISTS " + tableName;
        database.execSQL(query);
    }

    private List<Feature> getFeatures(String query) {
        List<Feature> features = new ArrayList<Feature>();
        Cursor cursor = null;

        try {
            cursor = getDb().rawQuery(query, null);
            if (cursor.moveToFirst()) {
                int indxId = cursor.getColumnIndex(Feature.COL_ID);
                int indxServerId = cursor.getColumnIndex(Feature.COL_SERVER_ID);
                int indxCoordinates = cursor.getColumnIndex(Feature.COL_COORDINATES);
                int indxGeomType = cursor.getColumnIndex(Feature.COL_GEOM_TYPE);
                int indxStatus = cursor.getColumnIndex(Feature.COL_STATUS);
                int indxPolygonNumber = cursor.getColumnIndex(Feature.COL_POLYGON_NUMBER);
                int indxSurveyDate = cursor.getColumnIndex(Feature.COL_SURVEY_DATE);
                int indxIpNumber = cursor.getColumnIndex(Property.COL_IP_NUMBER);

                do {
                    Feature feature = new Feature();
                    feature.setId(cursor.getLong(indxId));
                    feature.setServerId(cursor.getLong(indxServerId));
                    feature.setCoordinates(cursor.getString(indxCoordinates));
                    feature.setGeomType(cursor.getString(indxGeomType));
                    feature.setStatus(cursor.getString(indxStatus));
                    feature.setPolygonNumber(cursor.getString(indxPolygonNumber));
                    feature.setSurveyDate(cursor.getString(indxSurveyDate));
                    feature.setIpNumber(cursor.getInt(indxIpNumber));
                    features.add(feature);
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cursor.close();
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        return features;
    }

    public List<Feature> fetchFeatures() {
        String q = "SELECT * FROM SPATIAL_FEATURES";
        return getFeatures(q);
    }

    public List<Feature> fetchFeatures(String strFeatureType) {
        String q = "SELECT * FROM SPATIAL_FEATURES WHERE FLAG='"+strFeatureType+"'";
        return getFeatures(q);
    }

    public Long createFeature(String geomtype, String wKTStr, String imei, String flag,int iIndex) {
        Long featureId = 0L;
        try {
            // Inserting into Features
            String time = sdf.format(new Date());
            ContentValues value = new ContentValues();
            value.put(Feature.COL_COORDINATES, wKTStr);
            value.put(Feature.COL_GEOM_TYPE, geomtype);
            value.put(Property.COL_CREATION_DATE, time);
            value.put(Property.COL_STATUS, Property.CLIENT_STATUS_DRAFT);
            value.put(Property.COL_IMEI, imei);
            value.put(Property.COL_FLAG, flag);
            value.put(Property.COL_IP_NUMBER, iIndex);

            getDb().insert(Feature.TABLE_NAME, null, value);
            featureId = getGeneratedId(Feature.TABLE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            cf.appLog("", e);
            return featureId;
        }
        return featureId;
    }

    public boolean updateBoundaryPoint(Property prop) {
        try {
            ContentValues values = new ContentValues();
            values.put(Property.COL_FEATURE_TYPE, prop.getFeatureType());
            values.put(Property.COL_FEATURE_DESCRIPTION, prop.getFeatureDescription());
            if(prop.getVillageId() != null && prop.getVillageId() > 0) {
                values.put(Property.COL_VILLAGE_ID, prop.getVillageId());
            } else {
                values.put(Property.COL_VILLAGE_ID, (Integer)null);
            }

            return getDb().update(Feature.TABLE_NAME, values, "FEATURE_ID = " + prop.getId(), null) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            cf.appLog("", e);
            return false;
        }
    }

    public Long createFeatureResource(String geomtype, String wKTStr, String imei) {
        Long featureId = 0L;
        try {
            // Inserting into Features
            String time = sdf.format(new Date());
            ContentValues value = new ContentValues();
            value.put(Feature.COL_COORDINATES, wKTStr);
            value.put(Feature.COL_GEOM_TYPE, geomtype);
            value.put(Property.COL_CREATION_DATE, time);
            value.put(Property.COL_STATUS, Property.CLIENT_STATUS_DRAFT);
            value.put(Property.COL_IMEI, imei);

            getDb().insert(Feature.TABLE_NAME_RESO, null, value);
            featureId = getGeneratedId(Feature.TABLE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
            cf.appLog("", e);
            return featureId;
        }
        return featureId;
    }


    public boolean updateFeature(String wKTStr, Long featureId) {
        String whereClause = "FEATURE_ID = " + featureId;
        try {
            // updating  Features
            ContentValues value = new ContentValues();
            value.put("coordinates", wKTStr);
            getDb().update("SPATIAL_FEATURES", value, whereClause, null);
        } catch (Exception e) {
            e.printStackTrace();
            cf.appLog("", e);
            return false;
        }
        return true;
    }

    /**
     * Returns autogenerated ID for the given table.
     */
    private Long getGeneratedId(String tableName) {
        String sql = "select seq from sqlite_sequence where name='" + tableName + "'";
        Long id = null;
        Cursor cursor = getDb().rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getLong(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return id;
    }

    public List<Feature> fetchDraftFeatures() {
        String q = "SELECT * FROM " + Feature.TABLE_NAME + " WHERE " + Feature.COL_STATUS + " = '" + Property.CLIENT_STATUS_DRAFT + "'";
        return getFeatures(q);
    }

    public List<Feature> fetchCompletedFeatures() {
        String q = "SELECT * FROM " + Feature.TABLE_NAME + " WHERE " + Feature.COL_STATUS + "='" +
                Property.CLIENT_STATUS_COMPLETE + "' AND (" + Feature.COL_SERVER_ID + " = '' OR " + Feature.COL_SERVER_ID + " IS NULL)";
        return getFeatures(q);
    }

    public List<Feature> fetchSyncededFeatures() {
        String q = "SELECT * FROM " + Feature.TABLE_NAME + " WHERE " + Feature.COL_STATUS + "='" +
                Property.CLIENT_STATUS_COMPLETE + "' AND (" + Feature.COL_SERVER_ID + " != '' AND " + Feature.COL_SERVER_ID + " IS NOT NULL)";
        return getFeatures(q);
    }

    public List<Feature> fetchVerifiedFeatures() {
        String q = "SELECT * FROM " + Feature.TABLE_NAME + " WHERE " + Feature.COL_STATUS + " IN ('" +
                Property.CLIENT_STATUS_VERIFIED + "','" + Property.CLIENT_STATUS_VERIFIED_AND_SYNCHED + "')";
        return getFeatures(q);
    }

    public List<Feature> fetchFinalFeatures() {
        String q = "SELECT * FROM " + Feature.TABLE_NAME + " WHERE " + Feature.COL_STATUS + "='" +
                Property.CLIENT_STATUS_FINAL + "' AND (" + Feature.COL_SERVER_ID + " != '' AND " + Feature.COL_SERVER_ID + " IS NOT NULL)";
        return getFeatures(q);
    }

    public boolean deleteDownloadedFeatures() {
        String whereClause = Feature.COL_STATUS + "!='" + Property.CLIENT_STATUS_DRAFT + "' AND "
                + Feature.COL_STATUS + "!='" + Property.CLIENT_STATUS_COMPLETE + "'";
        try {
            getDb().delete(Feature.TABLE_NAME, whereClause, null);
            getDb().delete(Attribute.TABLE_ATTRIBUTE_VALUE_NAME, whereClause, null);
            getDb().delete(Person.TABLE_NAME, whereClause, null);
            getDb().delete(DeceasedPerson.TABLE_NAME, whereClause, null);
            getDb().delete(PersonOfInterest.TABLE_NAME, whereClause, null);
            getDb().delete(Right.TABLE_NAME, whereClause, null);
            getDb().delete(Media.TABLE_NAME, whereClause, null);
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteFeature(Long featureid) {
        String whereClause = Feature.COL_ID + "=" + featureid;
        try {
            getDb().delete(Feature.TABLE_NAME, whereClause, null);
            getDb().delete(Attribute.TABLE_ATTRIBUTE_VALUE_NAME, whereClause, null);
            getDb().delete(Person.TABLE_NAME, whereClause, null);
            getDb().delete(DeceasedPerson.TABLE_NAME, whereClause, null);
            getDb().delete(PersonOfInterest.TABLE_NAME, whereClause, null);
            getDb().delete(Right.TABLE_NAME, whereClause, null);
            getDb().delete(Media.TABLE_NAME, whereClause, null);
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean markFeatureAsComplete(Long featureid) {
        ContentValues values = new ContentValues();
        String whereClause = Feature.COL_ID + " = " + featureid;
        try {
            String time = sdf.format(new Date());
            values.put(Feature.COL_STATUS, Property.CLIENT_STATUS_COMPLETE);
            values.put(Property.COL_COMPLETION_DATE, time);
            getDb().update(Feature.TABLE_NAME, values, whereClause, null);
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean markFeatureAsVerified(Long featureid) {
        ContentValues values = new ContentValues();
        String whereClause = Feature.COL_ID + " = " + featureid;
        try {
            String time = sdf.format(new Date());
            values.put(Feature.COL_STATUS, Property.CLIENT_STATUS_VERIFIED);
            values.put(Property.COL_COMPLETION_DATE, time);
            getDb().update(Feature.TABLE_NAME, values, whereClause, null);
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<Property> createPropertyList(String sql) {
        Cursor cur = null;
        List<Property> properties = new ArrayList<>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {

                int indxId = cur.getColumnIndex(Feature.COL_ID);
                int indxServerId = cur.getColumnIndex(Feature.COL_SERVER_ID);
                int indxCoordinates = cur.getColumnIndex(Feature.COL_COORDINATES);
                int indxGeomType = cur.getColumnIndex(Feature.COL_GEOM_TYPE);
                int indxStatus = cur.getColumnIndex(Feature.COL_STATUS);
                int indxPolygonNumber = cur.getColumnIndex(Feature.COL_POLYGON_NUMBER);
                int indxSurveyDate = cur.getColumnIndex(Feature.COL_SURVEY_DATE);
                int indxAdjudicator1 = cur.getColumnIndex(Property.COL_ADJUDICATOR1);
                int indxAdjudicator2 = cur.getColumnIndex(Property.COL_ADJUDICATOR2);
                int indxClaimType = cur.getColumnIndex(Property.COL_CLAIM_TYPE_CODE);
                int indxComletionDate = cur.getColumnIndex(Property.COL_COMPLETION_DATE);
                int indxCreationDate = cur.getColumnIndex(Property.COL_CREATION_DATE);
                int indxHamletId = cur.getColumnIndex(Property.COL_HAMLET_ID);
                int indxImei = cur.getColumnIndex(Property.COL_IMEI);
                int ukaNumber = cur.getColumnIndex(Property.COL_UKA_NUMBER);
                int ukaClasi = cur.getColumnIndex(Property.COL_CLASSIFICATION_ID);
                int ukaSubclassi = cur.getColumnIndex(Property.COL_SUBCLASSIFICATION_ID);
                int ukaTenure = cur.getColumnIndex(Property.COL_TENURE_ID);
                int flag = cur.getColumnIndex(Property.COL_FLAG);
                int ipNUMBER = cur.getColumnIndex(Property.COL_IP_NUMBER);
                int claimRight = cur.getColumnIndex(Property.COL_CLAIM_RIGHT);
                int plotNo = cur.getColumnIndex(Property.COL_PLOT_NO);
                int document = cur.getColumnIndex(Property.COL_DOCUMENT);
                int documentType = cur.getColumnIndex(Property.COL_DOCUMENT_TYPE);
                int documentDate = cur.getColumnIndex(Property.COL_DOCUMENT_DATE);
                int documentRefNo = cur.getColumnIndex(Property.COL_DOCUMENT_REF_NO);
                int IS_NATURAL = cur.getColumnIndex(Property.COL_IS_NATURAL);
                int villageId = cur.getColumnIndex(Property.COL_VILLAGE_ID);
                int featureType = cur.getColumnIndex(Property.COL_FEATURE_TYPE);
                int featureDescription = cur.getColumnIndex(Property.COL_FEATURE_DESCRIPTION);

                do {


                    Property property = new Property();
                    property.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxServerId))
                        property.setServerId(cur.getLong(indxServerId));
                    property.setCoordinates(cur.getString(indxCoordinates));
                    property.setGeomType(cur.getString(indxGeomType));
                    property.setStatus(cur.getString(indxStatus));
                    property.setPolygonNumber(cur.getString(indxPolygonNumber));
                    property.setSurveyDate(cur.getString(indxSurveyDate));
                    property.setAdjudicator1(cur.getString(indxAdjudicator1));
                    property.setAdjudicator2(cur.getString(indxAdjudicator2));
                    property.setClaimTypeCode(cur.getString(indxClaimType));
                    property.setCompletionDate(cur.getString(indxComletionDate));
                    property.setCreationDate(cur.getString(indxCreationDate));
                    property.setHamletId(cur.getLong(indxHamletId));
                    property.setImei(cur.getString(indxImei));
                    property.setClassificationId(cur.getString(ukaClasi));
                    property.setSubClassificationId(cur.getString(ukaSubclassi));
                    property.setTenureTypeID(cur.getString(ukaTenure));
                    property.setFlag(cur.getString(flag));
                    property.setIpNumber(cur.getInt(ipNUMBER));
                    if (!cur.isNull(villageId))
                        property.setVillageId(cur.getInt(villageId));
                    property.setFeatureType(cur.getString(featureType));
                    property.setFeatureDescription(cur.getString(featureDescription));

                    //Ambar

                    property.setClaimRight(cur.getString(claimRight));
                    property.setPlotNo(cur.getString(plotNo));
                    property.setDocument(cur.getString(document));
                    property.setDocumentType(cur.getString(documentType));
                    property.setDocumentDate(cur.getString(documentDate));
                    property.setDocumentRefNo(cur.getString(documentRefNo));
                    property.setIsNatural(cur.getInt(IS_NATURAL));
                    //Ambar

                    if (!cur.isNull(ukaNumber))
                        property.setUkaNumber(cur.getString(ukaNumber));

                    // property.setClassification(getResBasicAttr(property.getId()));
                    property.setClassificationAttributes(getAttriSYnData(property.getId()));

                    // property.setTenureInformation(getTenureInfo(property.getId()));
                    property.setDeceasedPerson(getDeceasedPersonByProp(property.getId()));
                    property.setMedia(getMediaByProp(property.getId()));
                    property.setRight(getRightByProp(property.getId()));
                    property.setPersonOfInterests(getPersonOfInterestsByProp(property.getId()));

                    property.setDispute(getDisputeByProp(property.getId()));
                    property.setResPersonOfInterests(getResPersonOfInterestsByProp(property.getId()));
                    // ClassificationAttribute classification=new ClassificationAttribute();
                    //String a=fetchcateid(property.getId());
                    property.setResPOI(getRESPOIAttriSYnData(property.getId()));

                    List<Attribute> attributes = getPropAttributes(property.getId());
                    property.setAttributes(attributes);

                    //comment :- to add option ids in table
//                    List<ResourceCustomAttribute> attributesRes = getResPropAttributes(property.getId());
                    List<ResourceCustomAttribute> attributesRes = getResSyncPropAttributes(property.getId());
                    property.setAttributesres(attributesRes);
                    // property.setAttributes(getAttributesRes(property.getId()));

                    properties.add(property);
                }
                while (cur.moveToNext());

            }
            return properties;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return properties;
        } finally {
            try {
                cur.close();
            } catch (Exception e) {
            }
        }
    }

//    private String fetchcateid(Long id) {
//        String value=null;
//        Cursor cur = null;
//        cur = getDb().rawQuery("SELECT * FROM " + Option.TABLE_NAME +
//                " WHERE " + Right.COL_FEATURE_ID + " = " + propId.toString(), null);
//
//
//        return  value;
//    }


//    private List<Attribute> getAttributesRes(Long id) {
//
//    }

    /**
     * Returns property object by id.
     */
    public Property getProperty(Long propId) {
        List<Property> properties = createPropertyList("SELECT * FROM " + Feature.TABLE_NAME +
                " WHERE " + Feature.COL_ID + " = " + propId.toString());
        if (properties.size() > 0)
            return properties.get(0);
        else
            return null;
    }

    /**
     * Checks if claim number is unique.
     *
     * @param propId      Property ID, to exclude from search results.
     * @param claimNumber Claim number to test
     */
    public boolean isClaimNumberUnique(Long propId, String claimNumber) {
        Cursor cur = null;
        try {
            String q = "SELECT 1 FROM " + Feature.TABLE_NAME + " WHERE " +
                    Feature.COL_ID + "!=" + propId + " AND " +
                    Feature.COL_POLYGON_NUMBER + "='" + StringUtility.empty(claimNumber) + "'";
            cur = getDb().rawQuery(q, null);
            if (cur.moveToFirst())
                return false;
            else
                return true;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }
    }

    /**
     * Returns ownership right by property
     */
    public Right getRightByProp(Long propId) {
        Cursor cur = null;
        Right right = null;

        try {
            cur = getDb().rawQuery("SELECT * FROM " + Right.TABLE_NAME +
                    " WHERE " + Right.COL_FEATURE_ID + " = " + propId.toString(), null);
            if (cur.moveToFirst()) {
                int indxId = cur.getColumnIndex(Right.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Right.COL_FEATURE_ID);
                int indxRightTypeId = cur.getColumnIndex(Right.COL_RIGHT_TYPE_ID);
                int indxRelationshipType = cur.getColumnIndex(Right.COL_RELATIONSHIP_ID);
                int indxServerId = cur.getColumnIndex(Right.COL_SERVER_ID);
                int indxShareTypeId = cur.getColumnIndex(Right.COL_SHARE_TYPE_ID);
                int indxCertNumber = cur.getColumnIndex(Right.COL_CERT_NUMBER);
                int indxCertDate = cur.getColumnIndex(Right.COL_CERT_DATE);
                int indxJuridicalArea = cur.getColumnIndex(Right.COL_JURIDICAL_AREA);
                int Acquisition_ID = cur.getColumnIndex(Right.COL_Acquisition_ID);

                right = new Right();
                right.setId(cur.getLong(indxId));
                right.setFeatureId(cur.getLong(indxFeatureId));
                right.setRightTypeId(cur.getInt(indxRightTypeId));
                right.setServerId(cur.getLong(indxServerId));
                right.setRelationshipId(cur.getInt(indxRelationshipType));
                right.setShareTypeId(cur.getInt(indxShareTypeId));
                right.setCertNumber(cur.getString(indxCertNumber));
                right.setCertDate(cur.getString(indxCertDate));
                right.setJuridicalArea(cur.getDouble(indxJuridicalArea));
                right.setAcquisitionTypeId(cur.getInt(Acquisition_ID));

                right.setNaturalPersons(getNaturalPersonsByRight(right.getId()));
                right.setNonNaturalPerson(getNonNaturalPersonsByRight(right.getId()));


                //for non-natural persons in every case of collective which means list type
               // right.setNonNaturalPersons(getNonNaturalPersonsByRight(right.getId()));


                right.setAttributes(getAttributesByGroupId(right.getId()));
            }
            return right;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return right;
        } finally {
            try {
                cur.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Returns dispute by property
     */
    public Dispute getDisputeByProp(Long propId) {
        Cursor cur = null;
        Dispute dispute = null;

        try {
            cur = getDb().rawQuery("SELECT * FROM " + Dispute.TABLE_NAME +
                    " WHERE " + Dispute.COL_FEATURE_ID + " = " + propId.toString(), null);
            if (cur.moveToFirst()) {
                int indxId = cur.getColumnIndex(Dispute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Dispute.COL_FEATURE_ID);
                int indxServerId = cur.getColumnIndex(Dispute.COL_SERVER_ID);
                int indxTypeId = cur.getColumnIndex(Dispute.COL_DISPUTE_TYPE_ID);
                int indxDescription = cur.getColumnIndex(Dispute.COL_DESCRIPTION);
                int indxRegDate = cur.getColumnIndex(Dispute.COL_REG_DATE);

                dispute = new Dispute();
                dispute.setId(cur.getLong(indxId));
                dispute.setFeatureId(cur.getLong(indxFeatureId));
                dispute.setServerId(cur.getLong(indxServerId));
                dispute.setDisputeTypeId(cur.getInt(indxTypeId));
                dispute.setDescription(cur.getString(indxDescription));
                dispute.setRegDate(cur.getString(indxRegDate));

                dispute.setDisputingPersons(getDisputingPersons(dispute.getId()));
                dispute.setMedia(getMediaByDispute(dispute.getId()));
            }
            return dispute;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return dispute;
        } finally {
            try {
                cur.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Saves dispute
     */
    public boolean saveDispute(Dispute dispute) {
        try {
            if (dispute == null) {
                return true;
            }

            if (dispute.getId() == null || dispute.getId() < 1) {
                dispute.setId(getNewGroupId());
            } else {
                // Delete from dispute
                getDb().delete(Dispute.TABLE_NAME, Person.COL_ID + "=" + dispute.getId(), null);
            }

            // Insert person
            ContentValues row = new ContentValues();
            row.put(Dispute.COL_ID, dispute.getId());
            row.put(Dispute.COL_DISPUTE_TYPE_ID, dispute.getDisputeTypeId());
            row.put(Dispute.COL_FEATURE_ID, dispute.getFeatureId());
            row.put(Dispute.COL_SERVER_ID, dispute.getServerId());
            row.put(Dispute.COL_DESCRIPTION, dispute.getDescription());
            row.put(Dispute.COL_REG_DATE, dispute.getRegDate());

            getDb().insert(Dispute.TABLE_NAME, null, row);

            return true;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }

    private List<Person> createNonPersonsList(String sql) {
        Cursor cur = null;
        List<Person> persons = new ArrayList<Person>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                int indxId = cur.getColumnIndex(Person.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Person.COL_FEATURE_ID);
                int indxServerId = cur.getColumnIndex(Person.COL_SERVER_ID);
                int indxRightId = cur.getColumnIndex(Person.COL_RIGHT_ID);
                int indxIsNatural = cur.getColumnIndex(Person.COL_IS_NATURAL);
                int indxResident = cur.getColumnIndex(Person.COL_RESIDENT);
                int indxSubTypeId = cur.getColumnIndex(Person.COL_SUBTYPE);
                int indxShareSize = cur.getColumnIndex(Person.COL_SHARE);
                int indxDisputeId = cur.getColumnIndex(Person.COL_DISPUTE_ID);
                int indxAcquisitionTypeId = cur.getColumnIndex(Person.COL_ACQUISITION_TYPE_ID);

                do {
                    Person person = new Person();
                    person.setId(cur.getLong(indxId));
                    person.setFeatureId(cur.getLong(indxFeatureId));
                    person.setRightId(cur.getLong(indxRightId));
                    person.setServerId(cur.getLong(indxServerId));
                    person.setIsNatural(cur.getInt(indxIsNatural));
                    person.setResident(cur.getInt(indxResident));
                    person.setSubTypeId(cur.getInt(indxSubTypeId));
                    person.setShare(cur.getString(indxShareSize));
                    person.setDisputeId(cur.getLong(indxDisputeId));
                    person.setAcquisitionTypeId(cur.getInt(indxAcquisitionTypeId));

                    person.setAttributes(getAttributesNonPersonByGroupId(person.getId()));
                    person.setMedia(getMediaByPerson(person.getId()));
                    persons.add(person);
                } while (cur.moveToNext());
            }

            return persons;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return persons;
        } finally {
            try {
                cur.close();
            } catch (Exception e) {
            }
        }
    }



    private List<Person> createPersonsListBySingleTenancy(String sql) {
        Cursor cur = null;
        List<Person> persons = new ArrayList<Person>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                int indxId = cur.getColumnIndex(Person.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Person.COL_FEATURE_ID);
                int indxServerId = cur.getColumnIndex(Person.COL_SERVER_ID);
                int indxRightId = cur.getColumnIndex(Person.COL_RIGHT_ID);
                int indxIsNatural = cur.getColumnIndex(Person.COL_IS_NATURAL);
                int indxResident = cur.getColumnIndex(Person.COL_RESIDENT);
                int indxSubTypeId = cur.getColumnIndex(Person.COL_SUBTYPE);
                int indxShareSize = cur.getColumnIndex(Person.COL_SHARE);
                int indxDisputeId = cur.getColumnIndex(Person.COL_DISPUTE_ID);
                int indxAcquisitionTypeId = cur.getColumnIndex(Person.COL_ACQUISITION_TYPE_ID);

                do {
                    Person person = new Person();
                    person.setId(cur.getLong(indxId));
                    person.setFeatureId(cur.getLong(indxFeatureId));
                    person.setRightId(cur.getLong(indxRightId));
                    person.setServerId(cur.getLong(indxServerId));
                    person.setIsNatural(cur.getInt(indxIsNatural));
                    person.setResident(cur.getInt(indxResident));
                    person.setSubTypeId(cur.getInt(indxSubTypeId));
                    person.setShare(cur.getString(indxShareSize));
                    person.setDisputeId(cur.getLong(indxDisputeId));
                    person.setAcquisitionTypeId(cur.getInt(indxAcquisitionTypeId));

                    person.setAttributes(getAttributesByGroupIdSingleTennacy(person.getId()));
                    person.setMedia(getMediaByPerson(person.getId()));
                    persons.add(person);
                } while (cur.moveToNext());
            }

            return persons;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return persons;
        } finally {
            try {
                cur.close();
            } catch (Exception e) {
            }
        }
    }




    private List<Person> createPersonsList(String sql) {
        Cursor cur = null;
        List<Person> persons = new ArrayList<Person>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                int indxId = cur.getColumnIndex(Person.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Person.COL_FEATURE_ID);
                int indxServerId = cur.getColumnIndex(Person.COL_SERVER_ID);
                int indxRightId = cur.getColumnIndex(Person.COL_RIGHT_ID);
                int indxIsNatural = cur.getColumnIndex(Person.COL_IS_NATURAL);
                int indxResident = cur.getColumnIndex(Person.COL_RESIDENT);
                int indxSubTypeId = cur.getColumnIndex(Person.COL_SUBTYPE);
                int indxShareSize = cur.getColumnIndex(Person.COL_SHARE);
                int indxDisputeId = cur.getColumnIndex(Person.COL_DISPUTE_ID);
                int indxAcquisitionTypeId = cur.getColumnIndex(Person.COL_ACQUISITION_TYPE_ID);

                do {
                    Person person = new Person();
                    person.setId(cur.getLong(indxId));
                    person.setFeatureId(cur.getLong(indxFeatureId));
                    person.setRightId(cur.getLong(indxRightId));
                    person.setServerId(cur.getLong(indxServerId));
                    person.setIsNatural(cur.getInt(indxIsNatural));
                    person.setResident(cur.getInt(indxResident));
                    person.setSubTypeId(cur.getInt(indxSubTypeId));
                    person.setShare(cur.getString(indxShareSize));
                    person.setDisputeId(cur.getLong(indxDisputeId));
                    person.setAcquisitionTypeId(cur.getInt(indxAcquisitionTypeId));

                    person.setAttributes(getAttributesByGroupId(person.getId()));
                    person.setMedia(getMediaByPerson(person.getId()));
                    persons.add(person);
                } while (cur.moveToNext());
            }

            return persons;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return persons;
        } finally {
            try {
                cur.close();
            } catch (Exception e) {
            }
        }
    }



    private List<Person> createPersonsListOtherCases(String sql, Long featureID) {
        Cursor cur = null;
        List<Person> persons = new ArrayList<Person>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                int indxId = cur.getColumnIndex(Person.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Person.COL_FEATURE_ID);
                int indxServerId = cur.getColumnIndex(Person.COL_SERVER_ID);
                int indxRightId = cur.getColumnIndex(Person.COL_RIGHT_ID);
                int indxIsNatural = cur.getColumnIndex(Person.COL_IS_NATURAL);
                int indxResident = cur.getColumnIndex(Person.COL_RESIDENT);
                int indxSubTypeId = cur.getColumnIndex(Person.COL_SUBTYPE);
                int indxShareSize = cur.getColumnIndex(Person.COL_SHARE);
                int indxDisputeId = cur.getColumnIndex(Person.COL_DISPUTE_ID);
                int indxAcquisitionTypeId = cur.getColumnIndex(Person.COL_ACQUISITION_TYPE_ID);

                do {
                    Person person = new Person();
                    person.setId(cur.getLong(indxId));
                    person.setFeatureId(cur.getLong(indxFeatureId));
                    person.setRightId(cur.getLong(indxRightId));
                    person.setServerId(cur.getLong(indxServerId));
                    person.setIsNatural(cur.getInt(indxIsNatural));
                    person.setResident(cur.getInt(indxResident));
                    person.setSubTypeId(cur.getInt(indxSubTypeId));
                    person.setShare(cur.getString(indxShareSize));
                    person.setDisputeId(cur.getLong(indxDisputeId));
                    person.setAcquisitionTypeId(cur.getInt(indxAcquisitionTypeId));

                    person.setAttributes(getAttributesByGroupIdOtherCase(person.getId(),featureID));
                    person.setMedia(getMediaByPerson(person.getId()));
                    persons.add(person);
                } while (cur.moveToNext());
            }

            return persons;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return persons;
        } finally {
            try {
                cur.close();
            } catch (Exception e) {
            }
        }
    }


    /**
     * Returns person by id
     */
    public Person getPersonBySingleTenancy(Long personId) {
        String sql = "SELECT * FROM " + Person.TABLE_NAME + " WHERE " + Person.COL_ID + " = " + personId.toString();
        List<Person> persons = createPersonsListBySingleTenancy(sql);
        if (persons != null && persons.size() > 0)
            return persons.get(0);
        else
            return null;
    }
    public Person getPerson(Long personId, Long featureId) {
        String sql = "SELECT * FROM " + Person.TABLE_NAME + " WHERE " + Person.COL_ID + " = " + personId.toString();
        List<Person> persons = createPersonsListOtherCases(sql,featureId);
        if (persons != null && persons.size() > 0)
            return persons.get(0);
        else
            return null;
    }

    public Person getNonPerson(Long personId) {
        String sql = "SELECT * FROM " + Person.TABLE_NAME + " WHERE " + Person.COL_ID + " = " + personId.toString();
        List<Person> persons = createNonPersonsList(sql);
        if (persons != null && persons.size() > 0)
            return persons.get(0);
        else
            return null;
    }

    //for editattrirubut to get Acquisition type
    public Person getPersonAcquisition(Long personId) {
        String sql = "SELECT * FROM " + Person.TABLE_NAME + " WHERE " + Person.COL_FEATURE_ID + " = " + personId;
        List<Person> persons = createPersonsList(sql);
        if (persons != null && persons.size() > 0)
            return persons.get(0);
        else
            return null;
    }

    /**
     * Returns natural persons by ownership right
     */
    public List<Person> getNaturalPersonsByRight(Long rightId) {
        String sql = "SELECT * FROM " + Person.TABLE_NAME + " WHERE " +
                Person.COL_RIGHT_ID + " = " + rightId.toString() + " AND " + Person.COL_IS_NATURAL + "=1";
        return createPersonsList(sql);
    }

    /**
     * Returns non-natural person by ownership right
     */
    public Person getNonNaturalPersonsByRight(Long rightId) {
        String sql = "SELECT * FROM " + Person.TABLE_NAME + " WHERE " +
                Person.COL_RIGHT_ID + " = " + rightId.toString() + " AND " + Person.COL_IS_NATURAL + "=2";//0 is non-Natural for old data database
        List<Person> persons = createPersonsList(sql);
        if (persons != null && persons.size() > 0)
            return persons.get(0);
        else
            return null;
    }

//    public List<Person> getNonNaturalPersonsByRight(Long rightId) {
//        String sql = "SELECT * FROM " + Person.TABLE_NAME + " WHERE " +
//                Person.COL_RIGHT_ID + " = " + rightId.toString() + " AND " + Person.COL_IS_NATURAL + "=2";//0 is non-Natural for old data database
//        List<Person> persons = createPersonsList(sql);
//        if (persons != null && persons.size() > 0)
//            return persons;
//        else
//            return null;
//    }

    public List<Person> getNONNaturalPersonsByRight(Long rightId) {
        String sql = "SELECT * FROM " + Person.TABLE_NAME + " WHERE " +
                Person.COL_FEATURE_ID + " = " + rightId.toString() + " AND " + Person.COL_IS_NATURAL + "=2";
        return createPersonsList(sql);
    }

    /**
     * Returns disputing persons by dispute id
     */
    public List<Person> getDisputingPersons(Long disputeId) {
        String sql = "SELECT * FROM " + Person.TABLE_NAME + " WHERE " +
                Person.COL_DISPUTE_ID + " = " + disputeId.toString() + " AND " + Person.COL_IS_NATURAL + "=1";
        return createPersonsList(sql);
    }

    /**
     * Deletes person by id
     */
    public boolean deletePerson(Long id) {
        try {
            getDb().delete(Attribute.TABLE_ATTRIBUTE_VALUE_NAME, Attribute.COL_VALUE_GROUP_ID + "=" + id, null);
            getDb().delete(Media.TABLE_NAME, Media.COL_PERSON_ID + "=" + id, null);
            int deletedCount = getDb().delete(Person.TABLE_NAME, Person.COL_ID + "=" + id, null);
            return deletedCount > 0;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteOwner(Long id) {
        try {
            //getDb().delete(Attribute.TABLE_ATTRIBUTE_VALUE_NAME, Attribute.COL_VALUE_GROUP_ID + "=" + id, null);

            int deletedCount =  getDb().delete(Attribute.TABLE_ATTRIBUTE_VALUE_NAME, Attribute.COL_VALUE_GROUP_ID + "=" + id, null);
            return deletedCount > 0;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }

    private DeceasedPerson createDeceasedPerson(String sql) {
        Cursor cur = null;
        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                int indxId = cur.getColumnIndex(DeceasedPerson.COL_ID);
                int indxFeatureId = cur.getColumnIndex(DeceasedPerson.COL_FEATURE_ID);
                int indxFirstName = cur.getColumnIndex(DeceasedPerson.COL_FIRST_NAME);
                int indxLastName = cur.getColumnIndex(DeceasedPerson.COL_LAST_NAME);
                int indxMiddleName = cur.getColumnIndex(DeceasedPerson.COL_MIDDLE_NAME);

                DeceasedPerson person = new DeceasedPerson();
                person.setId(cur.getLong(indxId));
                person.setFeatureId(cur.getLong(indxFeatureId));
                person.setFirstName(cur.getString(indxFirstName));
                person.setLastName(cur.getString(indxLastName));
                person.setMiddleName(cur.getString(indxMiddleName));
                return person;
            } else {
                return null;
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return null;
        } finally {
            try {
                cur.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Returns deceased person by id.
     */
    public DeceasedPerson getDeceasedPerson(Long id) {
        String sql = "SELECT * FROM " + DeceasedPerson.TABLE_NAME + " WHERE " + DeceasedPerson.COL_ID + " = " + id.toString();
        return createDeceasedPerson(sql);
    }

    /**
     * Returns deceased person by property id.
     */
    public DeceasedPerson getDeceasedPersonByProp(Long propId) {
        String sql = "SELECT * FROM " + DeceasedPerson.TABLE_NAME + " WHERE " + DeceasedPerson.COL_FEATURE_ID + "=" + propId.toString();
        return createDeceasedPerson(sql);
    }

    /**
     * Saves deceased person
     */
    public boolean saveDeceasedPerson(DeceasedPerson person) {
        try {
            if (person == null) {
                return true;
            }

            if (person.getId() == null || person.getId() < 1) {
                person.setId(getNewGroupId());
            } else {
                // Delete from deceased person
                getDb().delete(DeceasedPerson.TABLE_NAME, DeceasedPerson.COL_ID + "=" + person.getId(), null);
            }

            // Insert person
            ContentValues row = new ContentValues();
            row.put(DeceasedPerson.COL_ID, person.getId());
            row.put(DeceasedPerson.COL_FEATURE_ID, person.getFeatureId());
            row.put(DeceasedPerson.COL_FIRST_NAME, person.getFirstName());
            row.put(DeceasedPerson.COL_LAST_NAME, person.getLastName());
            row.put(DeceasedPerson.COL_MIDDLE_NAME, person.getMiddleName());

            return getDb().insert(DeceasedPerson.TABLE_NAME, null, row) > 0;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes deceased person by id
     */
    public boolean deleteDeceasedPerson(Long id) {
        try {
            int deletedCount = getDb().delete(DeceasedPerson.TABLE_NAME, DeceasedPerson.COL_ID + "=" + id, null);
            return deletedCount > 0;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }

    private List<PersonOfInterest> createPersonOfInterestsList(String sql) {
        Cursor cur = null;
        List<PersonOfInterest> persons = new ArrayList<PersonOfInterest>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                int indxId = cur.getColumnIndex(PersonOfInterest.COL_ID);
                int indxFeatureId = cur.getColumnIndex(PersonOfInterest.COL_FEATURE_ID);
                int indxName = cur.getColumnIndex(PersonOfInterest.COL_NAME);
                int indxDob = cur.getColumnIndex(PersonOfInterest.COL_DOB);
                int indxGenderId = cur.getColumnIndex(PersonOfInterest.COL_GENDER_ID);
                int indxRelationshipId = cur.getColumnIndex(PersonOfInterest.COL_RELATIONSHIP_ID);

                do {
                    PersonOfInterest person = new PersonOfInterest();
                    person.setId(cur.getLong(indxId));
                    person.setFeatureId(cur.getLong(indxFeatureId));
                    person.setName(cur.getString(indxName));
                    person.setDob(cur.getString(indxDob));
                    person.setGenderId(cur.getInt(indxGenderId));
                    person.setRelationshipId(cur.getInt(indxRelationshipId));
                    persons.add(person);
                } while (cur.moveToNext());
            }

            return persons;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return persons;
        } finally {
            try {
                cur.close();
            } catch (Exception e) {
            }
        }
    }


    private List<NonNatural> createNonNaturalList(String sql) {
        Cursor cur = null;
        List<NonNatural> persons = new ArrayList<NonNatural>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                int indxId = cur.getColumnIndex(NonNatural.COL_VALUE_ATTRIBUTE_ID);
                int indxFeatureId = cur.getColumnIndex(NonNatural.COL_VALUE_FEATURE_ID);
                int indxName = cur.getColumnIndex(NonNatural.COL_VALUE_VALUE);
                int indxDob = cur.getColumnIndex(NonNatural.COL_VALUE_LABEL_NAME);

                do {
                    NonNatural person = new NonNatural();
                    person.setId(cur.getLong(indxId));
                    person.setFeatureId(cur.getLong(indxFeatureId));
                    person.setName(cur.getString(indxDob));
                    person.setValue(cur.getString(indxName));

                    persons.add(person);
                } while (cur.moveToNext());
            }

            return persons;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return persons;
        } finally {
            try {
                cur.close();
            } catch (Exception e) {
            }
        }
    }

    //Ambar
    private List<ResourcePersonOfInterest> createResPersonOfInterestsList(String sql) {
        Cursor cur = null;
        List<ResourcePersonOfInterest> persons = new ArrayList<ResourcePersonOfInterest>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                int indxId = cur.getColumnIndex(ResourcePersonOfInterest.COL_ID);
                int indxFeatureId = cur.getColumnIndex(ResourcePersonOfInterest.COL_FEATURE_ID);
                int indxName = cur.getColumnIndex(ResourcePersonOfInterest.COL_NAME);
                int indxDob = cur.getColumnIndex(ResourcePersonOfInterest.COL_DOB);
                int indxGenderId = cur.getColumnIndex(ResourcePersonOfInterest.COL_GENDER_ID);
                int indxRelationshipId = cur.getColumnIndex(ResourcePersonOfInterest.COL_RELATIONSHIP_ID);

                do {
                    ResourcePersonOfInterest person = new ResourcePersonOfInterest();
                    person.setId(cur.getLong(indxId));
                    person.setFeatureId(cur.getLong(indxFeatureId));
                    person.setName(cur.getString(indxName));
                    person.setDob(cur.getString(indxDob));
                    person.setGenderId(cur.getInt(indxGenderId));
                    person.setRelationshipId(cur.getInt(indxRelationshipId));
                    persons.add(person);
                } while (cur.moveToNext());
            }

            return persons;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return persons;
        } finally {
            try {
                cur.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * Returns list of person of interest by property ID
     */
    public List<PersonOfInterest> getPersonOfInterestsByProp(Long propId) {
        return createPersonOfInterestsList("SELECT * FROM " + PersonOfInterest.TABLE_NAME +
                " WHERE " + PersonOfInterest.COL_FEATURE_ID + "=" + propId);
    }


    public List<ResourcePersonOfInterest> getResPersonOfInterestsByProp(Long propId) {
        return createResPersonOfInterestsList("SELECT * FROM " + ResourcePersonOfInterest.TABLE_NAME +
                " WHERE " + ResourcePersonOfInterest.COL_FEATURE_ID + "=" + propId);
    }

    /**
     * Saves person of interest
     */
    public boolean savePersonOfInterest(PersonOfInterest person) {
        try {
            if (person == null) {
                return true;
            }

            if (person.getId() == null || person.getId() < 1) {
                person.setId(getNewGroupId());
            } else {
                // Delete from person of interest
                getDb().delete(PersonOfInterest.TABLE_NAME, PersonOfInterest.COL_ID + "=" + person.getId(), null);
            }

            // Insert person
            ContentValues row = new ContentValues();
            row.put(PersonOfInterest.COL_ID, person.getId());
            row.put(PersonOfInterest.COL_FEATURE_ID, person.getFeatureId());
            row.put(PersonOfInterest.COL_DOB, person.getDob());
            row.put(PersonOfInterest.COL_GENDER_ID, person.getGenderId());
            row.put(PersonOfInterest.COL_RELATIONSHIP_ID, person.getRelationshipId());
            row.put(PersonOfInterest.COL_NAME, person.getName());

            getDb().insert(PersonOfInterest.TABLE_NAME, null, row);
            return true;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveResPersonOfInterest(ResourcePersonOfInterest person) {
        try {
            if (person == null) {
                return true;
            }

            if (person.getId() == null || person.getId() < 1) {
                person.setId(getNewGroupId());
            } else {
                // Delete from person of interest
                getDb().delete(ResourcePersonOfInterest.TABLE_NAME, ResourcePersonOfInterest.COL_ID + "=" + person.getId(), null);
            }

            // Insert person
            ContentValues row = new ContentValues();
            row.put(ResourcePersonOfInterest.COL_ID, person.getId());
            row.put(ResourcePersonOfInterest.COL_FEATURE_ID, person.getFeatureId());
            row.put(ResourcePersonOfInterest.COL_DOB, person.getDob());
            row.put(ResourcePersonOfInterest.COL_GENDER_ID, person.getGenderId());
            row.put(ResourcePersonOfInterest.COL_RELATIONSHIP_ID, person.getRelationshipId());
            row.put(ResourcePersonOfInterest.COL_NAME, person.getName());

            getDb().insert(ResourcePersonOfInterest.TABLE_NAME, null, row);
            return true;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes person of interest by id
     */
    public boolean deletePersonOfInterest(Long id) {
        try {
            int deletedCount = getDb().delete(PersonOfInterest.TABLE_NAME, PersonOfInterest.COL_ID + "=" + id, null);
            return deletedCount > 0;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }

    //Ambar
    public boolean deleteResPersonOfInterest(Long id) {
        try {
            getDb().delete("RESOURCE_POI_VALUE_SYNC", "GROUP_ID " + "=" + id, null);
            int deletedCount = getDb().delete(ResourcePersonOfInterest.TABLE_NAME, PersonOfInterest.COL_ID + "=" + id, null);
            return deletedCount > 0;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }

    private List<Media> createMediaList(String sql) {
        Cursor cur = null;
        List<Media> mediaList = new ArrayList<Media>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                int indxId = cur.getColumnIndex(Media.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Media.COL_FEATURE_ID);
                int indxPath = cur.getColumnIndex(Media.COL_PATH);
                int indxType = cur.getColumnIndex(Media.COL_TYPE);
                int indxPersonId = cur.getColumnIndex(Media.COL_PERSON_ID);
                int indxSynced = cur.getColumnIndex(Media.COL_SYNCED);
                int indxDisputeId = cur.getColumnIndex(Media.COL_DISPUTE_ID);

                do {
                    Media media = new Media();
                    media.setId(cur.getLong(indxId));
                    media.setFeatureId(cur.getLong(indxFeatureId));
                    media.setPath(cur.getString(indxPath));
                    media.setType(cur.getString(indxType));
                    if (!cur.isNull(indxPersonId))
                        media.setPersonId(cur.getLong(indxPersonId));
                    if (!cur.isNull(indxDisputeId))
                        media.setDisputeId(cur.getLong(indxDisputeId));
                    media.setSynced(cur.getInt(indxSynced));
                    media.setAttributes(getAttributesByGroupId(media.getId()));

                    mediaList.add(media);
                } while (cur.moveToNext());
            }
            return mediaList;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return mediaList;
        } finally {
            try {
                cur.close();
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns media by id.
     */
    public Media getMedia(Long id) {
        String sql = "SELECT * FROM " + Media.TABLE_NAME + " WHERE " + Media.COL_ID + " = " + id.toString();
        List<Media> list = createMediaList(sql);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * Returns media list by property id.
     */
    public List<Media> getMediaByProp(Long propId) {
        String sql = "SELECT * FROM " + Media.TABLE_NAME +
                " WHERE " + Media.COL_FEATURE_ID + " = " + propId.toString() +
                " AND (" + Media.COL_PERSON_ID + " IS NULL" +
                "  OR " + Media.COL_PERSON_ID + " = 0)" +
                " AND (" + Media.COL_DISPUTE_ID + " IS NULL" +
                "  OR " + Media.COL_DISPUTE_ID + " = 0)";
        return createMediaList(sql);
    }

    /**
     * Returns media list by boundary id.
     */
    public List<Media> getMediaByBoundary(Long propId) {
        String sql = "SELECT * FROM " + Media.TABLE_NAME +
                " WHERE " + Media.COL_FEATURE_ID + " = " + propId.toString();
        return createMediaList(sql);
    }

    /**
     * Returns media list by person id.
     */
    public List<Media> getMediaByPerson(Long personId) {
        String sql = "SELECT * FROM " + Media.TABLE_NAME + " WHERE " + Media.COL_PERSON_ID + " = " + personId.toString();
        return createMediaList(sql);
    }

    /**
     * Returns media list by dispute.
     */
    public List<Media> getMediaByDispute(Long disputeId) {
        String sql = "SELECT * FROM " + Media.TABLE_NAME + " WHERE " + Media.COL_DISPUTE_ID + " = " + disputeId.toString();
        return createMediaList(sql);
    }

    public Feature fetchFeaturebyID(Long featureId) {
        String q = "SELECT * FROM " + Feature.TABLE_NAME + " WHERE " + Feature.COL_ID + "=" + featureId;
        List<Feature> features = getFeatures(q);
        if (features != null && features.size() > 0) {
            return features.get(0);
        }
        return null;
    }

    /**
     * Deletes media
     */
    public boolean deleteMedia(Long mediaId) {
        try {
            getDb().delete(Attribute.TABLE_ATTRIBUTE_VALUE_NAME, Attribute.COL_VALUE_GROUP_ID + "=" + mediaId, null);
            int deletedCount = getDb().delete(Media.TABLE_NAME, Media.COL_ID + "=" + mediaId, null);
            return deletedCount > 0;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes media by person
     */
    public boolean deleteMediaByPerson(Long personId) {
        try {
            int deletedCount = getDb().delete(Media.TABLE_NAME, Media.COL_PERSON_ID + "=" + personId, null);
            return deletedCount > 0;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }


    private List<Attribute> createAttributeListEditOtherCases(String sql, Long groudId, Long featureId) {
        Cursor cur = null;
        List<Attribute> attributes = new ArrayList<Attribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(Attribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Attribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(Attribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(Attribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(Attribute.COL_NAME);
                int indxNameOtherLang = cur.getColumnIndex(Attribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(Attribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(Attribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(Attribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(Attribute.COL_VALUE_VALUE);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;

                do {
                    Attribute attribute = new Attribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName(contxt.getResources().getString(R.string.select));
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }

                                int iGID=0;
                                int ifeatureCount=0;


                                String selectGID = "SELECT COUNT(*) FROM FORM_VALUES"  + " WHERE GROUP_ID ="+ groudId +" AND ATTRIB_ID=1156 AND ATTRIB_VALUE=1186";
                                String selectGIDfeature = "SELECT COUNT(*) FROM FORM_VALUES"  + " WHERE FEATURE_ID ="+ featureId +" AND ATTRIB_ID=1156 AND ATTRIB_VALUE=1186";
                                Cursor cursorgid = getDb().rawQuery(selectGID, null);
                                Cursor cursorfeature = getDb().rawQuery(selectGIDfeature, null);
                                if (cursorgid.moveToFirst() && cursorfeature.moveToFirst()) {
                                    try {
                                        do {
                                            iGID = cursorgid.getInt(0);
                                            ifeatureCount=cursorfeature.getInt(0);

                                        } while (cursorgid.moveToNext());
                                    } catch (Exception e) {
                                        cf.appLog("", e);
                                        e.printStackTrace();
                                    }
                                }
                                cursorgid.close();
                                if (attribute.getId()==1156){

                                    if (ifeatureCount==1) {
                                        if (iGID == 1) {

                                            optionList.add(option);

                                        } else {

                                            if (option.getId() == 1187) {
                                                optionList.add(option);
                                            }

                                        }
                                    }else {
                                        optionList.add(option);
                                    }

                                }else {
                                    optionList.add(option);
                                }

//                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);

                    }

                    attributes.add(attribute);


                } while (cur.moveToNext());

            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }



    private List<Attribute> createAttributeListOtherCases(String sql, Long featurID) {
        Cursor cur = null;
        List<Attribute> attributes = new ArrayList<Attribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(Attribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Attribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(Attribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(Attribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(Attribute.COL_NAME);
                int indxNameOtherLang = cur.getColumnIndex(Attribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(Attribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(Attribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(Attribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(Attribute.COL_VALUE_VALUE);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;

                do {
                    Attribute attribute = new Attribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName(contxt.getResources().getString(R.string.select));
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }

                                int iGID=0;


                                String selectGID = "SELECT COUNT(*) FROM FORM_VALUES"  + " WHERE FEATURE_ID ="+ featurID +" AND ATTRIB_ID=1156 AND ATTRIB_VALUE=1186";
                                Cursor cursorgid = getDb().rawQuery(selectGID, null);
                                if (cursorgid.moveToFirst()) {
                                    try {
                                        do {
                                            iGID = cursorgid.getInt(0);

                                        } while (cursorgid.moveToNext());
                                    } catch (Exception e) {
                                        cf.appLog("", e);
                                        e.printStackTrace();
                                    }
                                }
                                cursorgid.close();
                                if (attribute.getId()==1156){

                                    if (iGID==1){
                                        if (option.getId()==1187) {
                                            optionList.add(option);
                                        }
                                    }else {
                                        optionList.add(option);
                                    }

                                }else {
                                    optionList.add(option);
                                }




                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);

                    }

                    attributes.add(attribute);


                } while (cur.moveToNext());

            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }

    private List<Attribute> createAttributeListHideOwnerType(String sql) {
        Cursor cur = null;
        List<Attribute> attributes = new ArrayList<Attribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(Attribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Attribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(Attribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(Attribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(Attribute.COL_NAME);
                int indxNameOtherLang = cur.getColumnIndex(Attribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(Attribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(Attribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(Attribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(Attribute.COL_VALUE_VALUE);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;

                do {
                    Attribute attribute = new Attribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName(contxt.getResources().getString(R.string.select));
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }
                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);

                    }



                    if (attribute.getId()!=1156){
                        attributes.add(attribute);
                    }

                  //  attributes.add(attribute);


                } while (cur.moveToNext());

            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }


    private List<Attribute> createAttributeListPrivateIndividual(String sql) {
        Cursor cur = null;
        List<Attribute> attributes = new ArrayList<Attribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(Attribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Attribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(Attribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(Attribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(Attribute.COL_NAME);
                int indxNameOtherLang = cur.getColumnIndex(Attribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(Attribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(Attribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(Attribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(Attribute.COL_VALUE_VALUE);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;

                do {
                    Attribute attribute = new Attribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName(contxt.getResources().getString(R.string.select));
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }

                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);

                    }

                    if (attribute.getId()!=1158 && attribute.getId()!=1162 && attribute.getId()!=1161 && attribute.getId()!=1165){
                        attributes.add(attribute);
                    }
//                    attributes.add(attribute);


                } while (cur.moveToNext());

            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }

    private List<Attribute> createAttributeListResourceOwner(String sql, Long featureID) {
        Cursor cur = null;
        List<Attribute> attributes = new ArrayList<Attribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(Attribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Attribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(Attribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(Attribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(Attribute.COL_NAME);
                int indxNameOtherLang = cur.getColumnIndex(Attribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(Attribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(Attribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(Attribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(Attribute.COL_VALUE_VALUE);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;

                do {
                    Attribute attribute = new Attribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName(contxt.getResources().getString(R.string.select));
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }

                                int iGID=0;


//                                String selectGID = "SELECT COUNT(*) FROM FORM_VALUES"  + " WHERE FEATURE_ID ="+ featureID +" AND ATTRIB_ID= "+attribute.getId()+" AND ATTRIB_VALUE in (1191,1193,1199,1201)";
                                String selectGID = "SELECT COUNT(*) FROM FORM_VALUES"  + " WHERE FEATURE_ID ="+ featureID +" AND ATTRIB_ID= "+attribute.getId()+" AND ATTRIB_VALUE='Primary occupant /Point of contact'";
                                Cursor cursorgid = getDb().rawQuery(selectGID, null);
                                if (cursorgid.moveToFirst()) {
                                    try {
                                        do {
                                            iGID = cursorgid.getInt(0);

                                        } while (cursorgid.moveToNext());
                                    } catch (Exception e) {
                                        cf.appLog("", e);
                                        e.printStackTrace();
                                    }
                                }
                                cursorgid.close();
                                if (attribute.getId()==1163 || attribute.getId()==1164 || attribute.getId()==1159 || attribute.getId()==1160){

                                    if (iGID==1){
                                        if (option.getId()==1199 || option.getId()==1201 || option.getId()==1191 || option.getId()==1193) {
                                            optionList.add(option);
                                        }
                                    }else {
                                        optionList.add(option);
                                    }

                                }else {
                                    optionList.add(option);
                                }



//                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);

                    }

                    attributes.add(attribute);


                } while (cur.moveToNext());

            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }

    private List<Attribute> createAttributeListResourceEdit(String sql, Long featureID, int orderID) {
        Cursor cur = null;
        List<Attribute> attributes = new ArrayList<Attribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(Attribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Attribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(Attribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(Attribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(Attribute.COL_NAME);
                int indxNameOtherLang = cur.getColumnIndex(Attribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(Attribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(Attribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(Attribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(Attribute.COL_VALUE_VALUE);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;

                do {
                    Attribute attribute = new Attribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName(contxt.getResources().getString(R.string.select));
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }

                                int iGID=0;
                                int ifeatureCount=0;


                                String selectGID = "SELECT COUNT(*) FROM FORM_VALUES"  + " WHERE GROUP_ID ="+ orderID +" AND ATTRIB_ID= "+attribute.getId()+" AND ATTRIB_VALUE='Primary occupant /Point of contact'";
                                String selectGIDfeature = "SELECT COUNT(*) FROM FORM_VALUES"  + " WHERE FEATURE_ID ="+ featureID +" AND ATTRIB_ID= "+attribute.getId()+" AND ATTRIB_VALUE='Primary occupant /Point of contact'";
                                Cursor cursorgid = getDb().rawQuery(selectGID, null);
                                Cursor cursorfeature = getDb().rawQuery(selectGIDfeature, null);
                                if (cursorgid.moveToFirst() && cursorfeature.moveToFirst()) {
                                    try {
                                        do {
                                            iGID = cursorgid.getInt(0);
                                            ifeatureCount=cursorfeature.getInt(0);

                                        } while (cursorgid.moveToNext());
                                    } catch (Exception e) {
                                        cf.appLog("", e);
                                        e.printStackTrace();
                                    }
                                }
                                cursorgid.close();
                                if (attribute.getId()==1163 || attribute.getId()==1164 || attribute.getId()==1159 || attribute.getId()==1160){

                                    if (ifeatureCount==1) {
                                        if (iGID == 1) {

                                            optionList.add(option);

                                        } else {

                                            if (option.getId()==1199 || option.getId()==1201 || option.getId()==1191 || option.getId()==1193) {
                                                optionList.add(option);
                                            }

                                        }
                                    }else {
                                        optionList.add(option);
                                    }

                                }else {
                                    optionList.add(option);
                                }


//                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();


                        attribute.setOptionsList(optionList);

                    }

                    attributes.add(attribute);


                } while (cur.moveToNext());

            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }


    private List<Attribute> createAttributeList(String sql) {
        Cursor cur = null;
        List<Attribute> attributes = new ArrayList<Attribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(Attribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Attribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(Attribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(Attribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(Attribute.COL_NAME);
                int indxNameOtherLang = cur.getColumnIndex(Attribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(Attribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(Attribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(Attribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(Attribute.COL_VALUE_VALUE);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;

                do {
                    Attribute attribute = new Attribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName(contxt.getResources().getString(R.string.select));
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }

                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);

                    }

                    attributes.add(attribute);


                } while (cur.moveToNext());

            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }



    private List<Attribute> createAttributeNonPersonList(String sql) {
        Cursor cur = null;
        List<Attribute> attributes = new ArrayList<Attribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(Attribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Attribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(Attribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(Attribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(Attribute.COL_NAME);
                int indxNameOtherLang = cur.getColumnIndex(Attribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(Attribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(Attribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(Attribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(Attribute.COL_VALUE_VALUE);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;

                do {
                    Attribute attribute = new Attribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName(contxt.getResources().getString(R.string.select));
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }
                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);

                    }


                    if (!attribute.getName().equalsIgnoreCase("Institution Type") && !attribute.getName().equalsIgnoreCase("Institution name")) {
                        attributes.add(attribute);
                    }


                } while (cur.moveToNext());

            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }


    //Client requirement fro non natural

    private List<Attribute> createAttributeListForNonNatural(String sql) {
        Cursor cur = null;
        List<Attribute> attributes = new ArrayList<Attribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(Attribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Attribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(Attribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(Attribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(Attribute.COL_NAME);
                int indxNameOtherLang = cur.getColumnIndex(Attribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(Attribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(Attribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(Attribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(Attribute.COL_VALUE_VALUE);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;

                do {
                    Attribute attribute = new Attribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName(contxt.getResources().getString(R.string.select));
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }
                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);

                    }

                    if (attribute.getName().equalsIgnoreCase("Institution name") || attribute.getName().equalsIgnoreCase("Institution Type")) {
                        attributes.add(attribute);

                    }


                } while (cur.moveToNext());

            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }

    //

    private List<Attribute> createAttributeListForNonNaturalOtherValues(String sql) {
        Cursor cur = null;
        List<Attribute> attributes = new ArrayList<Attribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(Attribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Attribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(Attribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(Attribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(Attribute.COL_NAME);
                int indxNameOtherLang = cur.getColumnIndex(Attribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(Attribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(Attribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(Attribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(Attribute.COL_VALUE_VALUE);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;

                do {
                    Attribute attribute = new Attribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName(contxt.getResources().getString(R.string.select));
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }
                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);

                    }

                    if (!attribute.getName().equalsIgnoreCase("Institution name") && !attribute.getName().equalsIgnoreCase("Institution Type")) {
                        attributes.add(attribute);

                    }


                } while (cur.moveToNext());

            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }


    private List<Attribute> createAttributeListOwn2(String sql) {
        Cursor cur = null;
        List<Attribute> attributes = new ArrayList<Attribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(Attribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(Attribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(Attribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(Attribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(Attribute.COL_NAME);
                int indxNameOtherLang = cur.getColumnIndex(Attribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(Attribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(Attribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(Attribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(Attribute.COL_VALUE_VALUE);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;

                do {
                    Attribute attribute = new Attribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName(contxt.getResources().getString(R.string.select));
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }
                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);

                    }

                    attributes.add(attribute);


                } while (cur.moveToNext());

            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }

    private List<ResourceCustomAttribute> createResSyncAttributeList(String sql) {
        Cursor cur = null;
        List<ResourceCustomAttribute> attributes = new ArrayList<ResourceCustomAttribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(ResourceCustomAttribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(ResourceCustomAttribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(ResourceCustomAttribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(ResourceCustomAttribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(ResourceCustomAttribute.COL_NAME);
                int indxSubclasification = cur.getColumnIndex(ResourceCustomAttribute.COL_SUBCLASSIFICATION);
                int indxNameOtherLang = cur.getColumnIndex(ResourceCustomAttribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(ResourceCustomAttribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(ResourceCustomAttribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(ResourceCustomAttribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(ResourceCustomAttribute.COL_VALUE_VALUE);
                int indxOption = cur.getColumnIndex(ResourceCustomAttribute.COL_VALUE_OPTION_ID);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;

                do {
                    ResourceCustomAttribute attribute = new ResourceCustomAttribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (!cur.isNull(indxSubclasification))
                        attribute.setSubclassificationid(cur.getString(indxSubclasification));
                    else
                        attribute.setSubclassificationid("null");

                    //
                    if (!cur.isNull(indxOption))
                        attribute.setResID(cur.getLong(indxOption));
                    else
                        attribute.setResID(0L);


                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName("Select custom attribute");
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }
                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);
                    }

                    attributes.add(attribute);

                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }


    private List<ResourceCustomAttribute> createResAttributeList(String sql) {
        Cursor cur = null;
        List<ResourceCustomAttribute> attributes = new ArrayList<ResourceCustomAttribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(ResourceCustomAttribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(ResourceCustomAttribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(ResourceCustomAttribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(ResourceCustomAttribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(ResourceCustomAttribute.COL_NAME);
                int indxSubclasification = cur.getColumnIndex(ResourceCustomAttribute.COL_SUBCLASSIFICATION);
                int indxNameOtherLang = cur.getColumnIndex(ResourceCustomAttribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(ResourceCustomAttribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(ResourceCustomAttribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(ResourceCustomAttribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(ResourceCustomAttribute.COL_VALUE_VALUE);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;

                do {
                    ResourceCustomAttribute attribute = new ResourceCustomAttribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (!cur.isNull(indxSubclasification))
                        attribute.setSubclassificationid(cur.getString(indxSubclasification));
                    else
                        attribute.setSubclassificationid("null");


                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName("Select custom attribute");
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }
                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);
                    }

                    attributes.add(attribute);

                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }


    private List<ResourceCustomAttribute> createResAttributeAttrIDNull(String sql) {
        Cursor cur = null;
        List<ResourceCustomAttribute> attributes = new ArrayList<ResourceCustomAttribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(ResourceCustomAttribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(ResourceCustomAttribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(ResourceCustomAttribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(ResourceCustomAttribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(ResourceCustomAttribute.COL_NAME);
                int indxSubclasification = cur.getColumnIndex(ResourceCustomAttribute.COL_SUBCLASSIFICATION);
                int indxNameOtherLang = cur.getColumnIndex(ResourceCustomAttribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(ResourceCustomAttribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(ResourceCustomAttribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(ResourceCustomAttribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(ResourceCustomAttribute.COL_VALUE_VALUE);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;
                int a=-1;

                do {
                    ResourceCustomAttribute attribute = new ResourceCustomAttribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (!cur.isNull(indxSubclasification))
                        attribute.setSubclassificationid(cur.getString(indxSubclasification));
                    else
                        attribute.setSubclassificationid("null");


                   /* if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
//                        option.setId(0L);
//                        option.setName(contxt.getResources().getString(R.string.select));
//                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                a=cur2.getColumnIndex(Option.COL_ATTRIBUTE_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(a));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }
                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);
                    }*/

                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName("Select custom attribute");
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId() +" ORDER BY OPTION_ID";

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                a=cur2.getColumnIndex(Option.COL_ATTRIBUTE_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(a));
                                option.setOptionID(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }
                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);
                    }

                    attributes.add(attribute);

                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }


    private List<ResourceCustomAttribute> createResAttributeAttrID(String sql) {
        Cursor cur = null;
        List<ResourceCustomAttribute> attributes = new ArrayList<ResourceCustomAttribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();
                int indxId = cur.getColumnIndex(ResourceCustomAttribute.COL_ID);
                int indxFeatureId = cur.getColumnIndex(ResourceCustomAttribute.COL_VALUE_FEATURE_ID);
                int indxType = cur.getColumnIndex(ResourceCustomAttribute.COL_TYPE);
                int indxControlType = cur.getColumnIndex(ResourceCustomAttribute.COL_CONTROL_TYPE);
                int indxName = cur.getColumnIndex(ResourceCustomAttribute.COL_NAME);
                int indxSubclasification = cur.getColumnIndex(ResourceCustomAttribute.COL_SUBCLASSIFICATION);
                int indxNameOtherLang = cur.getColumnIndex(ResourceCustomAttribute.COL_NAME_OTHER_LANG);
                int indxListing = cur.getColumnIndex(ResourceCustomAttribute.COL_LISTING);
                int indxValidate = cur.getColumnIndex(ResourceCustomAttribute.COL_VALIDATE);
                int indxGroupId = cur.getColumnIndex(ResourceCustomAttribute.COL_VALUE_GROUP_ID);
                int indxValue = cur.getColumnIndex(ResourceCustomAttribute.COL_VALUE_VALUE);

                int indxOptionId = -1;
                int indxOptionName = -1;
                int indxOptionNameOtherLang = -1;
                int a=-1;

                do {
                    ResourceCustomAttribute attribute = new ResourceCustomAttribute();
                    attribute.setId(cur.getLong(indxId));
                    if (!cur.isNull(indxFeatureId))
                        attribute.setFeatureId(cur.getLong(indxFeatureId));
                    else
                        attribute.setFeatureId(0L);
                    attribute.setType(cur.getString(indxType));
                    attribute.setControlType(cur.getInt(indxControlType));
                    if (!cur.isNull(indxListing))
                        attribute.setListing(cur.getInt(indxListing));
                    else
                        attribute.setListing(0);
                    attribute.setValidate(cur.getString(indxValidate));
                    if (!cur.isNull(indxGroupId))
                        attribute.setGroupId(cur.getLong(indxGroupId));
                    else
                        attribute.setGroupId(0L);
                    if (!cur.isNull(indxValue))
                        attribute.setValue(cur.getString(indxValue));

                    ///ambar
                    if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur.getString(indxName))) {
                        attribute.setName(cur.getString(indxName));
                    } else {
                        attribute.setName(cur.getString(indxName));
                    }

                    if (!cur.isNull(indxSubclasification))
                        attribute.setSubclassificationid(cur.getString(indxSubclasification));
                    else
                        attribute.setSubclassificationid("null");


                   /* if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
//                        option.setId(0L);
//                        option.setName(contxt.getResources().getString(R.string.select));
//                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                a=cur2.getColumnIndex(Option.COL_ATTRIBUTE_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(a));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }
                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);
                    }*/

                    if (attribute.getControlType() == 5) { // Spinner
                        List<Option> optionList = new ArrayList<Option>();
                        Option option = new Option();
                        option.setId(0L);
                        option.setName("Select custom attribute");
                        optionList.add(option);
                        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + attribute.getId();

                        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

                        if (cur2.moveToFirst()) {
                            if (indxOptionId < 0) {
                                //indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                a=cur2.getColumnIndex(Option.COL_ATTRIBUTE_ID);
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(a));
                                option.setOptionID(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("en") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    option.setName(cur2.getString(indxOptionNameOtherLang));
                                } else {
                                    option.setName(cur2.getString(indxOptionName));
                                }
                                if (!TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
                                    multiLnagText.append(cur2.getString(indxOptionNameOtherLang));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                } else {
                                    multiLnagText.append(cur2.getString(indxOptionName));
                                    multiLnagText.append("&#&" + cur2.getString(indxOptionName));
                                    option.setNameOtherLang(multiLnagText.toString());
                                }
                                optionList.add(option);
                            } while (cur2.moveToNext());
                        }
                        cur2.close();
                        attribute.setOptionsList(optionList);
                    }

                    attributes.add(attribute);

                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
        }

        return attributes;
    }


    private String getAttributeSelectQuery(String wherePart) {
        String sql = "SELECT AM." + Attribute.COL_ID +
                ", AM." + Attribute.COL_TYPE +
                ", AM." + Attribute.COL_CONTROL_TYPE +
                ", AM." + Attribute.COL_NAME +
                ", AM." + Attribute.COL_NAME_OTHER_LANG +
                ", AM." + Attribute.COL_LISTING +
                ", AM." + Attribute.COL_VALIDATE +
                ", AM." + Attribute.COL_FLAG +
                ", AV." + Attribute.COL_VALUE_FEATURE_ID +
                ", AV." + Attribute.COL_VALUE_GROUP_ID +
                ", AV." + Attribute.COL_VALUE_VALUE +
                " FROM " + Attribute.TABLE_NAME + " AS AM LEFT JOIN " +
                Attribute.TABLE_ATTRIBUTE_VALUE_NAME + " AS AV ON " +
                "AM." + Attribute.COL_ID + " = AV." + Attribute.COL_VALUE_ATTRIBUTE_ID;
        if (!StringUtility.isEmpty(wherePart)) {
            sql = sql + " WHERE " + wherePart;
        }
        sql = sql + " ORDER BY AM." + Attribute.COL_LISTING + ", AM." + Attribute.COL_ID;

//        sql = sql + " ORDER BY AM." + Attribute.COL_LISTING + ", AM." + Attribute.COL_NAME;


        return sql;
    }

    private String getOwner2AttributeSelectQuery(String wherePart) {
        String sql = "SELECT AM." + Attribute.COL_ID +
                ", AM." + Attribute.COL_TYPE +
                ", AM." + Attribute.COL_CONTROL_TYPE +
                ", AM." + Attribute.COL_NAME +
                ", AM." + Attribute.COL_NAME_OTHER_LANG +
                ", AM." + Attribute.COL_LISTING +
                ", AM." + Attribute.COL_VALIDATE +
                ", AM." + Attribute.COL_FLAG +
                ", AV." + Attribute.COL_VALUE_FEATURE_ID +
                ", AV." + Attribute.COL_VALUE_GROUP_ID +
                ", AV." + Attribute.COL_VALUE_VALUE +
                " FROM " + Attribute.TABLE_NAME + " AS AM LEFT JOIN " +
                Attribute.TABLE_ATTRIBUTE_VALUE_NAME + " AS AV ON " +
                "AM." + Attribute.COL_ID + " = AV." + Attribute.COL_VALUE_ATTRIBUTE_ID;
        if (!StringUtility.isEmpty(wherePart)) {
            sql = sql + " WHERE " + wherePart;
        }
        sql = sql + " ORDER BY AM." + Attribute.COL_LISTING + ", AM." + Attribute.COL_NAME;
        return sql;
    }

    private String getResSyncAttributeSelectQuery(String wherePart) {
        String sql = "SELECT AM." + ResourceCustomAttribute.COL_ID +
                ", AM." + ResourceCustomAttribute.COL_TYPE +
                ", AM." + ResourceCustomAttribute.COL_CONTROL_TYPE +
                ", AM." + ResourceCustomAttribute.COL_NAME +
                ", AM." + ResourceCustomAttribute.COL_NAME_OTHER_LANG +
                ", AM." + ResourceCustomAttribute.COL_LISTING +
                ", AM." + ResourceCustomAttribute.COL_VALIDATE +
                ", AM." + ResourceCustomAttribute.COL_SUBCLASSIFICATION +
                ", AM." + ResourceCustomAttribute.COL_FLAG +
                ", AV." + ResourceCustomAttribute.COL_VALUE_FEATURE_ID +
                ", AV." + ResourceCustomAttribute.COL_VALUE_GROUP_ID +
                ", AV." + ResourceCustomAttribute.COL_VALUE_VALUE +
                ", AV." + ResourceCustomAttribute.COL_VALUE_OPTION_ID +
                " FROM " + ResourceCustomAttribute.TABLE_NAME + " AS AM LEFT JOIN " +
                ResourceCustomAttribute.TABLE_ATTRIBUTE_VALUE_NAME + " AS AV ON " +
                "AM." + ResourceCustomAttribute.COL_ID + " = AV." + ResourceCustomAttribute.COL_VALUE_ATTRIBUTE_ID;
        if (!StringUtility.isEmpty(wherePart)) {
            sql = sql + " WHERE " + wherePart;
        }
        sql = sql + " ORDER BY AM." + ResourceCustomAttribute.COL_LISTING + ", AM." + ResourceCustomAttribute.COL_NAME;
        return sql;

//        String sql = "SELECT AM." + ResourceCustomAttribute.COL_ID +
//                ", AM." + ResourceCustomAttribute.COL_TYPE +
//                ", AM." + ResourceCustomAttribute.COL_CONTROL_TYPE +
//                ", AM." + ResourceCustomAttribute.COL_NAME +
//                ", AM." + ResourceCustomAttribute.COL_NAME_OTHER_LANG +
//                ", AM." + ResourceCustomAttribute.COL_LISTING +
//                ", AM." + ResourceCustomAttribute.COL_VALIDATE +
//                ", AM." + ResourceCustomAttribute.COL_SUBCLASSIFICATION +
//                ", AM." + ResourceCustomAttribute.COL_FLAG +
//                ", AV." + ResourceCustomAttribute.COL_VALUE_FEATURE_ID +
//                ", AV." + ResourceCustomAttribute.COL_VALUE_GROUP_ID +
//                ", AV." + ResourceCustomAttribute.COL_VALUE_VALUE +
//                ", OP." + ResourceCustomAttribute.COL_VALUE_OPTION_ID +
//                " FROM " + ResourceCustomAttribute.TABLE_NAME + " AS AM LEFT JOIN " +
//                ResourceCustomAttribute.TABLE_ATTRIBUTE_VALUE_NAME + " AS AV LEFT JOIN " +
//                " OPTIONS " + " AS OP ON " +
//                "AM." + ResourceCustomAttribute.COL_ID + " = AV." + ResourceCustomAttribute.COL_VALUE_ATTRIBUTE_ID;
//        if (!StringUtility.isEmpty(wherePart)) {
//            sql = sql + " WHERE " + wherePart;
//        }
//        sql = sql + " ORDER BY AM." + ResourceCustomAttribute.COL_LISTING + ", AM." + ResourceCustomAttribute.COL_NAME;
//        return sql;
    }


    private String getResAttributeSelectQuery(String wherePart) {
        String sql = "SELECT AM." + ResourceCustomAttribute.COL_ID +
                ", AM." + ResourceCustomAttribute.COL_TYPE +
                ", AM." + ResourceCustomAttribute.COL_CONTROL_TYPE +
                ", AM." + ResourceCustomAttribute.COL_NAME +
                ", AM." + ResourceCustomAttribute.COL_NAME_OTHER_LANG +
                ", AM." + ResourceCustomAttribute.COL_LISTING +
                ", AM." + ResourceCustomAttribute.COL_VALIDATE +
                ", AM." + ResourceCustomAttribute.COL_SUBCLASSIFICATION +
                ", AM." + ResourceCustomAttribute.COL_FLAG +
                ", AV." + ResourceCustomAttribute.COL_VALUE_FEATURE_ID +
                ", AV." + ResourceCustomAttribute.COL_VALUE_GROUP_ID +
                ", AV." + ResourceCustomAttribute.COL_VALUE_VALUE +
                " FROM " + ResourceCustomAttribute.TABLE_NAME + " AS AM LEFT JOIN " +
                ResourceCustomAttribute.TABLE_ATTRIBUTE_VALUE_NAME + " AS AV ON " +
                "AM." + ResourceCustomAttribute.COL_ID + " = AV." + ResourceCustomAttribute.COL_VALUE_ATTRIBUTE_ID;
        if (!StringUtility.isEmpty(wherePart)) {
            sql = sql + " WHERE " + wherePart;
        }
        sql = sql + " ORDER BY AM." + ResourceCustomAttribute.COL_LISTING + ", AM." + ResourceCustomAttribute.COL_NAME;
        return sql;

//        String sql = "SELECT AM." + ResourceCustomAttribute.COL_ID +
//                ", AM." + ResourceCustomAttribute.COL_TYPE +
//                ", AM." + ResourceCustomAttribute.COL_CONTROL_TYPE +
//                ", AM." + ResourceCustomAttribute.COL_NAME +
//                ", AM." + ResourceCustomAttribute.COL_NAME_OTHER_LANG +
//                ", AM." + ResourceCustomAttribute.COL_LISTING +
//                ", AM." + ResourceCustomAttribute.COL_VALIDATE +
//                ", AM." + ResourceCustomAttribute.COL_SUBCLASSIFICATION +
//                ", AM." + ResourceCustomAttribute.COL_FLAG +
//                ", AV." + ResourceCustomAttribute.COL_VALUE_FEATURE_ID +
//                ", AV." + ResourceCustomAttribute.COL_VALUE_GROUP_ID +
//                ", AV." + ResourceCustomAttribute.COL_VALUE_VALUE +
//                ", OP." + ResourceCustomAttribute.COL_VALUE_OPTION_ID +
//                " FROM " + ResourceCustomAttribute.TABLE_NAME + " AS AM LEFT JOIN " +
//                ResourceCustomAttribute.TABLE_ATTRIBUTE_VALUE_NAME + " AS AV LEFT JOIN " +
//                " OPTIONS " + " AS OP ON " +
//                "AM." + ResourceCustomAttribute.COL_ID + " = AV." + ResourceCustomAttribute.COL_VALUE_ATTRIBUTE_ID;
//        if (!StringUtility.isEmpty(wherePart)) {
//            sql = sql + " WHERE " + wherePart;
//        }
//        sql = sql + " ORDER BY AM." + ResourceCustomAttribute.COL_LISTING + ", AM." + ResourceCustomAttribute.COL_NAME;
//        return sql;
    }

    private String getResAttributeSelectQueryByType(String typeId, String subid) {
        String sql = "SELECT " +
                ResourceCustomAttribute.COL_ID +
                ", " + ResourceCustomAttribute.COL_TYPE +
                ", " + ResourceCustomAttribute.COL_CONTROL_TYPE +
                ", " + ResourceCustomAttribute.COL_NAME +
                ", " + ResourceCustomAttribute.COL_NAME_OTHER_LANG +
                ", " + ResourceCustomAttribute.COL_LISTING +
                ", " + ResourceCustomAttribute.COL_VALIDATE +
                ", " + ResourceCustomAttribute.COL_SUBCLASSIFICATION +
                ", 0 AS " + ResourceCustomAttribute.COL_VALUE_FEATURE_ID +
                ", 0 AS " + ResourceCustomAttribute.COL_VALUE_GROUP_ID +
                ", '' AS " + ResourceCustomAttribute.COL_VALUE_VALUE +
                " FROM " + ResourceCustomAttribute.TABLE_NAME +
                " WHERE " + ResourceCustomAttribute.COL_SUBCLASSIFICATION + "='" + subid + "'" + " AND ATTRIBUTE_TYPE "+ "='" + typeId + "'"+
                " ORDER BY " + ResourceCustomAttribute.COL_LISTING + ", " + ResourceCustomAttribute.COL_NAME;
        return sql;
    }

    //Nitin
    private String getResAttributeNewSelectQueryByType(String typeId, String subid) {
        String sql = "SELECT " +
                ResourceCustomAttribute.COL_ID +
                ", " + ResourceCustomAttribute.COL_TYPE +
                ", " + ResourceCustomAttribute.COL_CONTROL_TYPE +
                ", " + ResourceCustomAttribute.COL_NAME +
                ", " + ResourceCustomAttribute.COL_NAME_OTHER_LANG +
                ", " + ResourceCustomAttribute.COL_LISTING +
                ", " + ResourceCustomAttribute.COL_VALIDATE +
                ", " + ResourceCustomAttribute.COL_SUBCLASSIFICATION +
                ", 0 AS " + ResourceCustomAttribute.COL_VALUE_FEATURE_ID +
                ", 0 AS " + ResourceCustomAttribute.COL_VALUE_GROUP_ID +
                ", '' AS " + ResourceCustomAttribute.COL_VALUE_VALUE +
                " FROM " + ResourceCustomAttribute.TABLE_NAME +
                " WHERE " + ResourceCustomAttribute.COL_SUBCLASSIFICATION + "='" + subid + "'"+
                " ORDER BY " + ResourceCustomAttribute.COL_LISTING + ", " + ResourceCustomAttribute.COL_NAME;
        return sql;
    }

    //Ambar
    private String getResAttributeSelectQueryByTypeSize(String typeId) {
        String sql = "SELECT " +
                ResourceCustomAttribute.COL_ID +
                ", " + ResourceCustomAttribute.COL_TYPE +
                ", " + ResourceCustomAttribute.COL_CONTROL_TYPE +
                ", " + ResourceCustomAttribute.COL_NAME +
                ", " + ResourceCustomAttribute.COL_NAME_OTHER_LANG +
                ", " + ResourceCustomAttribute.COL_LISTING +
                ", " + ResourceCustomAttribute.COL_VALIDATE +
                ", " + ResourceCustomAttribute.COL_SUBCLASSIFICATION +
                ", 0 AS " + ResourceCustomAttribute.COL_VALUE_FEATURE_ID +
                ", 0 AS " + ResourceCustomAttribute.COL_VALUE_GROUP_ID +
                ", '' AS " + ResourceCustomAttribute.COL_VALUE_VALUE +
                " FROM " + ResourceCustomAttribute.TABLE_NAME +
                " WHERE " + ResourceCustomAttribute.COL_TYPE + "='" + typeId + "'" +
                " ORDER BY " + ResourceCustomAttribute.COL_LISTING + ", " + ResourceCustomAttribute.COL_NAME;
        return sql;
    }


    private String getAttributeSelectQueryByType(String typeId) {
        String sql = "SELECT " +
                Attribute.COL_ID +
                ", " + Attribute.COL_TYPE +
                ", " + Attribute.COL_CONTROL_TYPE +
                ", " + Attribute.COL_NAME +
                ", " + Attribute.COL_NAME_OTHER_LANG +
                ", " + Attribute.COL_LISTING +
                ", " + Attribute.COL_VALIDATE +
                ", 0 AS " + Attribute.COL_VALUE_FEATURE_ID +
                ", 0 AS " + Attribute.COL_VALUE_GROUP_ID +
                ", '' AS " + Attribute.COL_VALUE_VALUE +
                " FROM " + Attribute.TABLE_NAME +
                " WHERE " + Attribute.COL_TYPE + "='" + typeId + "'" +
                " ORDER BY " + Attribute.COL_LISTING + ", " + Attribute.COL_NAME;
        return sql;
    }

    // Nitin
   private String getJointAttributeSelectQueryByType(String typeId) {
       String sql = "SELECT " +
               Attribute.COL_ID +
               ", " + Attribute.COL_TYPE +
               ", " + Attribute.COL_CONTROL_TYPE +
               ", " + Attribute.COL_NAME +
               ", " + Attribute.COL_NAME_OTHER_LANG +
               ", " + Attribute.COL_LISTING +
               ", " + Attribute.COL_VALIDATE +
               ", 0 AS " + Attribute.COL_VALUE_FEATURE_ID +
               ", 0 AS " + Attribute.COL_VALUE_GROUP_ID +
               ", '' AS " + Attribute.COL_VALUE_VALUE +
               " FROM " + Attribute.TABLE_NAME +
               " WHERE " + Attribute.COL_TYPE + "='" + typeId + "' And " +  Attribute.COL_NAME_OTHER_LANG + " not like '%-Own2%'"+
               " ORDER BY " + Attribute.COL_LISTING ;
       return sql;
   }

    // Nitin
    private String getJointOwn2AttributeSelectQueryByType(String typeId) {
        String sql = "SELECT " +
                Attribute.COL_ID +
                ", " + Attribute.COL_TYPE +
                ", " + Attribute.COL_CONTROL_TYPE +
                ", " + Attribute.COL_NAME +
                ", " + Attribute.COL_NAME_OTHER_LANG +
                ", " + Attribute.COL_LISTING +
                ", " + Attribute.COL_VALIDATE +
                ", 0 AS " + Attribute.COL_VALUE_FEATURE_ID +
                ", 0 AS " + Attribute.COL_VALUE_GROUP_ID +
                ", '' AS " + Attribute.COL_VALUE_VALUE +
                " FROM " + Attribute.TABLE_NAME +
                " WHERE " + Attribute.COL_TYPE + "='" + typeId + "' And " +  Attribute.COL_NAME_OTHER_LANG + " like '%-Own2%'"+
                " ORDER BY " + Attribute.COL_LISTING ;
        return sql;
    }

    /**
     * Returns list of attributes by group ID (person, media, etc)
     */
    public List<Attribute> getAttributesByGroupId(Long groupId) {
        String wherePart = " AV." + Attribute.COL_VALUE_GROUP_ID + "=" + groupId;
        return createAttributeList(getAttributeSelectQuery(wherePart));
    }

    public List<Attribute> getAttributesByGroupIdSingleTennacy(Long groupId) {
        String wherePart = " AV." + Attribute.COL_VALUE_GROUP_ID + "=" + groupId;
        return createAttributeListHideOwnerType(getAttributeSelectQuery(wherePart));
    }


    public List<Attribute> getAttributesNonPersonByGroupId(Long groupId) {
        String wherePart = " AV." + Attribute.COL_VALUE_GROUP_ID + "=" + groupId;
        return createAttributeNonPersonList(getAttributeSelectQuery(wherePart));
    }

    public List<Attribute> getAttributesByGroupIdOtherCase(Long groupId, Long featureId) {
        String wherePart = " AV." + Attribute.COL_VALUE_GROUP_ID + "=" + groupId;
        return createAttributeListEditOtherCases(getAttributeSelectQuery(wherePart),groupId,featureId);
    }


//createAttributeListEditOtherCases
    /**
     * Returns property attributes by type. Expected types are as follows: GENERAL, GENERAL_PROPERTY, CUSTOM
     */
    public List<Attribute> getPropAttributesByType(Long featureId, String attributeType) {
        String wherePart = " AM." + Attribute.COL_TYPE + " = '" + attributeType + "' " +
                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId;


        return createAttributeList(getAttributeSelectQuery(wherePart));
    }

    public List<Attribute> getOwner1PropAttributesByType(Long featureId, String attributeType) {
        /*String wherePart = " AM." + Attribute.COL_TYPE + " = '" + attributeType + "' " +
                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId;*/

        //Nitin
        String wherePart = " AM." + Attribute.COL_TYPE + " = '" + attributeType + "' " +
                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId +" And AM.ATTRIBUTE_NAME_OTHER not like '%-Own2%'";
        return createAttributeList(getAttributeSelectQuery(wherePart));
    }

//    createAttributeListPrivateIndividual
//
    //Ambar
    public List<Attribute> getOwnerPropAttributesByGroupId(Long featureId, String attributeType, int groupID) {
       //Nitin
        String wherePart = " AM." + Attribute.COL_TYPE + " = '" + attributeType + "' " +
                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId +" AND AV." + Attribute.COL_VALUE_GROUP_ID + "=" + groupID;
        return createAttributeList(getAttributeSelectQuery(wherePart));
    }

    public List<Attribute> getOwnerPropAttributesByGroupIdResource(Long featureId, String attributeType, int groupID) {
        //Nitin
        String wherePart = " AM." + Attribute.COL_TYPE + " = '" + attributeType + "' " +
                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId +" AND AV." + Attribute.COL_VALUE_GROUP_ID + "=" + groupID;
        return createAttributeListResourceEdit(getAttributeSelectQuery(wherePart),featureId,groupID);
    }

    public List<Attribute> getOwnerPropAttributesSingleByGroupId(Long featureId, String attributeType, int groupID) {
        //Nitin
        String wherePart = " AM." + Attribute.COL_TYPE + " = '" + attributeType + "' " +
                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId +" AND AV." + Attribute.COL_VALUE_GROUP_ID + "=" + groupID;
        return createAttributeListPrivateIndividual(getAttributeSelectQuery(wherePart));
    }
    //Ambar
    public List<Attribute> getOwner2PropAttributesByType(Long featureId, String attributeType) {
        String wherePart = " AM." + Attribute.COL_TYPE + " = '" + attributeType + "' " +
                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId  +" And AM.ATTRIBUTE_NAME_OTHER like '%-Own2%'";
        return createAttributeListOwn2(getAttributeSelectQuery(wherePart));
    }

//    public List<ResourceCustomAttribute> getPropResAttributesByType(Long featureId, String attributeType, String subID) {
////        String wherePart = " AM." + ResourceCustomAttribute.COL_TYPE + " = '" + attributeType + "' " +
////                "AND AV." + ResourceCustomAttribute.COL_VALUE_FEATURE_ID + "=" + featureId;
//        //Ambar
//        String wherePart = " AM." + ResourceCustomAttribute.COL_TYPE + " = '" + attributeType + "' " +
//                "AND AV." + ResourceCustomAttribute.COL_VALUE_FEATURE_ID + "=" + featureId+ " AND AV." + ResourceCustomAttribute.COL_VALUE_SUBID + " = '" + subID + "' " ;
//        return createResAttributeList(getResAttributeSelectQuery(wherePart));
//    }


    //Ambar
    public List<ResourceCustomAttribute> getPropResAttributesByType(Long featureId, String attributeType, String subID) {
//        String wherePart = " AM." + ResourceCustomAttribute.COL_TYPE + " = '" + attributeType + "' " +
//                "AND AV." + ResourceCustomAttribute.COL_VALUE_FEATURE_ID + "=" + featureId;
        //Ambar
        String wherePart = " AM." + ResourceCustomAttribute.COL_TYPE + " = '" + attributeType + "' " +
                "AND AV." + ResourceCustomAttribute.COL_VALUE_FEATURE_ID + "=" + featureId+ " AND AV." + ResourceCustomAttribute.COL_VALUE_SUBID + " = '" + subID + "' " + " AND OP." + ResourceCustomAttribute.COL_VALUE_OPTION_ID + "=" + "AV." + ResourceCustomAttribute.COL_VALUE_OPTION_ID;
        return createResAttributeList(getResAttributeSelectQuery(wherePart));
    }

    public List<ResourceCustomAttribute> getResAttributesByFlag(String attributeType, String subID) {
        return createResAttributeList(getResAttributeSelectQueryByType(attributeType, subID));
    }

    //Ambar

    public List<ResourceCustomAttribute> getResAttributesSize(String attributeType) {
        return createResAttributeList(getResAttributeSelectQueryByTypeSize(attributeType));
    }

    public List<ResourceCustomAttribute> getResAttributesByAttrbuteID(String attributeType, String subID) {
        return createResAttributeAttrID(getResAttributeSelectQueryByType(attributeType, subID));
    }

    public List<ResourceCustomAttribute> getResAttributesByAttrbuteIDNull(String attributeType, String subID) {
        return createResAttributeAttrIDNull(getResAttributeSelectQueryByType(attributeType, subID));
    }

    /**
     * Returns property attributes, including GENERAL, GENERAL_PROPERTY, CUSTOM
     */
    public List<Attribute> getPropAttributes(Long featureId) {
//        String wherePart = " AM." + Attribute.COL_TYPE + " IN ('" + Attribute.TYPE_GENERAL +
//                "','" + Attribute.TYPE_GENERAL_PROPERTY + "','" + Attribute.TYPE_CUSTOM + "') " +
//                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId;
        String wherePart = " AM." + Attribute.COL_TYPE + " IN ('" + Attribute.TYPE_GENERAL +
                "','" + Attribute.TYPE_GENERAL_PROPERTY + "','" + Attribute.TYPE_CUSTOM +"','" + 18 + "','" + 17 +"','" + 10 + "','" + 11 + "','" + 12  + "','" + 9 + "','" + 13 + "','" + 14 + "','" + 15 + "','" + 16 + "') " +
                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId;
        return createAttributeList(getAttributeSelectQuery(wherePart));
    }

    public List<ResourceCustomAttribute> getResSyncPropAttributes(Long featureId) {
//        String wherePart = " AM." + Attribute.COL_TYPE + " IN ('" + Attribute.TYPE_GENERAL +
//                "','" + Attribute.TYPE_GENERAL_PROPERTY + "','" + Attribute.TYPE_CUSTOM + "') " +
//                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId;
        String wherePart = " AM." + Attribute.COL_TYPE + " IN ('" + Attribute.TYPE_GENERAL +
                "','" + Attribute.TYPE_GENERAL_PROPERTY + "','" + Attribute.TYPE_CUSTOM +"','" + 18 + "','" + 17 +"','" + 10 + "','" + 11 + "','" + 12  + "','" + 9 + "','" + 13 + "','" + 14 + "','" + 15 + "','" + 16 + "') " +
                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId;
        return createResSyncAttributeList(getResSyncAttributeSelectQuery(wherePart));
    }


    //createResSyncAttributeList
    public List<ResourceCustomAttribute> getResPropAttributes(Long featureId) {
//        String wherePart = " AM." + Attribute.COL_TYPE + " IN ('" + Attribute.TYPE_GENERAL +
//                "','" + Attribute.TYPE_GENERAL_PROPERTY + "','" + Attribute.TYPE_CUSTOM + "') " +
//                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId;
        String wherePart = " AM." + Attribute.COL_TYPE + " IN ('" + Attribute.TYPE_GENERAL +
                "','" + Attribute.TYPE_GENERAL_PROPERTY + "','" + Attribute.TYPE_CUSTOM +"','" + 18 + "','" + 17 +"','" + 10 + "','" + 11 + "','" + 12  + "','" + 9 + "','" + 13 + "','" + 14 + "','" + 15 + "','" + 16 + "') " +
                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId;
        return createResAttributeList(getResAttributeSelectQuery(wherePart));
    }

    /**
     * Returns attributes list by type. Returned list doesn't conatain actual values and should be used for new objects.
     */
    public List<Attribute> getAttributesByType(String attributeType) {
        return createAttributeList(getAttributeSelectQueryByType(attributeType));
    }
    public List<Attribute> getAttributesByTypeOtherCases(String attributeType, Long featureID) {
        return createAttributeListOtherCases(getAttributeSelectQueryByType(attributeType),featureID);
    }


    public List<Attribute> getAttributesByTypeHideOwnerType(String attributeType) {
        return createAttributeListHideOwnerType(getAttributeSelectQueryByType(attributeType));
    }

    public List<Attribute> getAttributesByTypeNonNatural(String attributeType) {
        return createAttributeListForNonNatural(getAttributeSelectQueryByType(attributeType));
    }

    public List<Attribute> getAttributesByTypeNonNaturalOthersValue(String attributeType) {
        return createAttributeListForNonNaturalOtherValues(getAttributeSelectQueryByType(attributeType));
    }



    public List<Attribute> getAttributesByFlag(String attributeType) {
        return createAttributeList(getAttributeSelectQueryByType(attributeType));
    }
    public List<Attribute> getAttributesOthersByFlag(String attributeType, Long featureID) {
        return createAttributeListResourceOwner(getAttributeSelectQueryByType(attributeType),featureID);
    }

    public List<Attribute> getAttributesByFlagPrivateIndvidual(String attributeType) {
        return createAttributeListPrivateIndividual(getAttributeSelectQueryByType(attributeType));
    }

    //Nitin
    public List<Attribute> getJointAttributesByFlag(String attributeType, Long featureID) {
        return createAttributeListResourceOwner(getJointAttributeSelectQueryByType(attributeType),featureID);
    }

    //Nitin
    public List<Attribute> getJointOwn2AttributesByFlag(String attributeType) {
        return createAttributeListOwn2(getJointOwn2AttributeSelectQueryByType(attributeType));
    }



    /**
     * Saves property attributes such as General or Custom.
     *
     * @param attributes List of attributes to save
     * @param propId     Property ID to link with attributs
     */
    public boolean savePropAttributes(List<Attribute> attributes, Long propId) {
        if (attributes == null || attributes.size() < 1) {
            return true;
        }

        // Get group ID from the first element. It's supposed that all elemets have the same group ID.
        Long groupId = attributes.get(0).getGroupId();

        if (groupId == null || groupId < 1) {
            groupId = getNewGroupId();
        }

        for (Attribute attribute : attributes) {
            attribute.setGroupId(groupId);
            attribute.setFeatureId(propId);
        }

        return saveAttributesList(attributes);
    }

    public boolean saveResPropAttributes(List<ResourceCustomAttribute> attributes, Long propId) {
        if (attributes == null || attributes.size() < 1) {
            return true;
        }

        // Get group ID from the first element. It's supposed that all elemets have the same group ID.
        Long groupId = attributes.get(0).getGroupId();

        if (groupId == null || groupId < 1) {
            groupId = getNewGroupId();
        }

        for (ResourceCustomAttribute attribute : attributes) {
            attribute.setGroupId(groupId);
            attribute.setFeatureId(propId);
        }

        return saveResAttributesList(attributes);
    }

    /**
     * Saves provided attributes list
     */
    private boolean saveAttributesList(List<Attribute> attributes) {
        if (attributes == null || attributes.size() < 1) {
            return true;
        }

        // Get group ID from the first element. It's supposed that all elemets have the same group ID.
        Long groupId = attributes.get(0).getGroupId();

        if (groupId != 0) {
            try {
                String whereGroupId = Attribute.COL_VALUE_GROUP_ID + "=" + groupId;
                getDb().delete(Attribute.TABLE_ATTRIBUTE_VALUE_NAME, whereGroupId, null);

                for (Attribute attribute : attributes) {
                    if (attribute.getValue() != null) {
                        ContentValues row = new ContentValues();
                        row.put(Attribute.COL_VALUE_GROUP_ID, groupId);
                        row.put(Attribute.COL_VALUE_ATTRIBUTE_ID, attribute.getId());
                        row.put(Attribute.COL_VALUE_VALUE, attribute.getValue());
                        row.put(Attribute.COL_VALUE_FEATURE_ID, attribute.getFeatureId());
                        row.put(Attribute.COL_VALUE_LABEL_NAME, attribute.getName());
                        getDb().insert(Attribute.TABLE_ATTRIBUTE_VALUE_NAME, null, row);
                    }
                }
                return true;
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private boolean saveAttributesNonList(List<NonNatural> attributes) {
        if (attributes == null || attributes.size() < 1) {
            return true;
        }

        // Get group ID from the first element. It's supposed that all elemets have the same group ID.
        Long groupId = attributes.get(0).getGroupId();

        if (groupId != 0) {
            try {
//                String whereGroupId = Attribute.COL_VALUE_GROUP_ID + "=" + groupId;
//                getDb().delete(Attribute.TABLE_ATTRIBUTE_VALUE_NAME, whereGroupId, null);

                for (NonNatural attribute : attributes) {
                    if (attribute.getValue() != null) {
                        ContentValues row = new ContentValues();
                        row.put(Attribute.COL_VALUE_GROUP_ID, groupId);
                        row.put(Attribute.COL_VALUE_ATTRIBUTE_ID, attribute.getId());
                        row.put(Attribute.COL_VALUE_VALUE, attribute.getValue());
                        row.put(Attribute.COL_VALUE_FEATURE_ID, attribute.getFeatureId());
                        row.put(Attribute.COL_VALUE_LABEL_NAME, attribute.getName());
                        getDb().insert(Attribute.TABLE_ATTRIBUTE_VALUE_NAME, null, row);
                    }
                }
                attributes.clear();
                return true;
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    private boolean saveResAttributesList(List<ResourceCustomAttribute> attributes) {
        Cursor cursor = null;
        if (attributes == null || attributes.size() < 1) {
            return true;
        }

        // Get group ID from the first element. It's supposed that all elemets have the same group ID.
        Long groupId = attributes.get(0).getGroupId();

        if (groupId != 0) {
            try {
                String whereGroupId = ResourceCustomAttribute.COL_VALUE_GROUP_ID + "=" + groupId;
                getDb().delete(ResourceCustomAttribute.TABLE_ATTRIBUTE_VALUE_NAME, whereGroupId, null);

                for (ResourceCustomAttribute attribute : attributes) {
                    if (attribute.getValue() != null) {
                        ContentValues row = new ContentValues();
                        row.put(ResourceCustomAttribute.COL_VALUE_GROUP_ID, groupId);
                        row.put(ResourceCustomAttribute.COL_VALUE_ATTRIBUTE_ID, attribute.getId());
                        row.put(ResourceCustomAttribute.COL_VALUE_VALUE, attribute.getValue());
                        row.put(ResourceCustomAttribute.COL_VALUE_OPTION_ID,attribute.getResID());
//                        String subID="SELECT SUBCLASSIFICATION_ID FROM RESOURCE_ATTRIBUTE_MASTER WHERE ATTRIB_ID ="+attribute.getId().toString();
                        String subID = "SELECT SUBCLASSI_ID FROM " + ResourceCustomAttribute.TABLE_NAME + " WHERE " + ResourceCustomAttribute.COL_ID + "=" + attribute.getId();
                        cursor = getDb().rawQuery(subID, null);
                        if (cursor.moveToFirst()) {

                            row.put(ResourceCustomAttribute.COL_VALUE_SUBID, cursor.getString(0));
                            do {

                            } while (cursor.moveToNext());
                        }

                        row.put(ResourceCustomAttribute.COL_VALUE_FEATURE_ID, attribute.getFeatureId());
                        getDb().insert(ResourceCustomAttribute.TABLE_ATTRIBUTE_VALUE_NAME, null, row);
                    }
                }
                return true;
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public List<Feature> fetchFeaturesByGeomtype(String geomtype) {
        String q = "SELECT * FROM " + Feature.TABLE_NAME + " WHERE " + Feature.COL_GEOM_TYPE + "='" + geomtype + "'";
        return getFeatures(q);
    }

    public void insertValues(List<ContentValues> valueList, String tableName) {
        int rows = getDb().delete(tableName, "1", null);
        System.out.println(rows + " rows deleted from table " + tableName);
        try {
            for (ContentValues contentValues : valueList) {
                getDb().insert(tableName, null, contentValues);
            }
            System.out.println("Data Inserted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //0LLiveTI4testmeROLE_TRUSTED_INTERMEDIARY
    public User getLoggedUser() {
        User user = null;
        String selectSQLUser = "SELECT * FROM " + User.TABLE_NAME;
        Cursor cursor = getDb().rawQuery(selectSQLUser, null);

        if (cursor.moveToFirst()) {
            user = new User();
            user.setUserId(cursor.getLong(0));
            user.setUserName(cursor.getString(1));
            user.setPassword(cursor.getString(2));
            user.setRoleName(cursor.getString(4));
        }
        cursor.close();
        return user;
    }

    public boolean checkUserExist() {
        boolean userExist=false;
        User user = null;
        String selectSQLUser = "SELECT * FROM " + User.TABLE_NAME;
        Cursor cursor = getDb().rawQuery(selectSQLUser, null);

        if (cursor.moveToFirst()) {
            userExist=true;
        }
        cursor.close();
        return userExist;
    }


    public Object[] fetchAllBookmarks() {
        String q = "SELECT * FROM BOOKMARKS";

        List<Bookmark> bookmarks = new ArrayList<Bookmark>();
        List<String> bookmarksStr = new ArrayList<String>();
        try {
            Cursor cursor = getDb().rawQuery(q, null);
            if (cursor.moveToFirst()) {
                do {
                    Bookmark bookmark = new Bookmark();
                    bookmark.setName(cursor.getString(1));
                    bookmark.setLatitude(cursor.getDouble(2));
                    bookmark.setLongitude(cursor.getDouble(3));
                    bookmark.setZoomlevel(cursor.getFloat(4));
                    bookmarksStr.add(bookmark.getName());
                    bookmarks.add(bookmark);
                }
                while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }
        return new Object[]{bookmarks, bookmarksStr};
    }

    public boolean saveBookmark(Bookmark bkmrk) {
        try {
            // query to remove the oldest bookmarks if more than 5 are in the database
            String removeSql = "id = (SELECT id FROM BOOKMARKS order by id desc LIMIT 1 OFFSET 5)";
            ContentValues values = new ContentValues();
            values.put("NAME", bkmrk.getName());
            values.put("LATITUDE", bkmrk.getLatitude());
            values.put("LONGITUDE", bkmrk.getLongitude());
            values.put("ZOOMLEVEL", bkmrk.getZoomlevel());

            getDb().insert("BOOKMARKS", null, values);
            getDb().delete("BOOKMARKS", removeSql, null);
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Long getNewGroupId() {
        String sql = "SELECT * FROM GROUPID_SEQ";
        Long groupid = 0L;
        Cursor cursor = getDb().rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            groupid = cursor.getLong(0);
        }
        cursor.close();

        if (groupid == 0) {
            groupid++;
            ContentValues value = new ContentValues();
            value.put("value", groupid);
            getDb().insert("GROUPID_SEQ", null, value);
        } else {
            groupid++;
            ContentValues value = new ContentValues();
            value.put("value", groupid);
            getDb().update("GROUPID_SEQ", value, null, null);
        }
        return groupid;
    }

    public boolean saveProjectData(String data) {
        try {
            JSONObject projectdata = new JSONObject(data);
            getDb().delete("PROJECT_SPATIAL_DATA", null, null);
            getDb().delete("OPTIONS", null, null);
            getDb().delete(Attribute.TABLE_NAME, null, null);
            getDb().delete("HAMLET_DETAILS", null, null);
            getDb().delete("ADJUDICATOR_DETAILS", null, null);
            getDb().delete(RelationshipType.TABLE_NAME, null, null);
            getDb().delete(ClaimType.TABLE_NAME, null, null);
            getDb().delete(ShareType.TABLE_NAME, null, null);
            getDb().delete(RightType.TABLE_NAME, null, null);
            getDb().delete(Gender.TABLE_NAME, null, null);
            getDb().delete(DisputeType.TABLE_NAME, null, null);
            getDb().delete(AcquisitionType.TABLE_NAME, null, null);
            getDb().delete(ClassificationAttribute.TABLE_NAME, null, null);
            getDb().delete(SubClassificationAttribute.TABLE_NAME, null, null);
            getDb().delete(ResourceCustomAttribute.TABLE_NAME, null, null);
            getDb().delete(TenureType.TABLE_NAME, null, null);
            getDb().delete(AOI.TABLE_NAME, null, null);
            getDb().delete(Village.TABLE_NAME, null, null);

            if (projectdata.has("Extent")) {
                String mapExtent = projectdata.getString("Extent");
                if (mapExtent != null && !mapExtent.equals("")) {
                    cf.saveMapExtent(mapExtent);
                }
            }

            if (projectdata.has("SpatialData")) {
                ContentValues projectValues = new ContentValues();
                JSONArray project_info = projectdata.getJSONArray("SpatialData");
                if (project_info.length() > 0) {
                    for (int i = 0; i < project_info.length(); i++) {

                        JSONObject project_detail = new JSONObject(project_info.get(i).toString());
                         projectValues.put("SERVER_PK", project_detail.getInt("id"));
                        projectValues.put("PROJECT_NAME_ID", project_detail.getInt("projectnameid"));
                        projectValues.put("FILE_NAME", project_detail.getString("fileName"));
                        projectValues.put("FILE_LOCATION", project_detail.getString("fileLocation"));
                        projectValues.put("DESCRIPTION", project_detail.getString("description"));
                        projectValues.put("CREATED_BY", project_detail.getInt("createdby"));
                        projectValues.put("MODIFIED_BY", project_detail.getInt("modifiedby"));
                        projectValues.put("CRAETED_DATE", project_detail.getInt("createddate"));
                        projectValues.put("MODIFIED_DATE", project_detail.getInt("modifieddate"));
                        projectValues.put("SIZE", project_detail.getInt("size"));
                        projectValues.put("DOCUMENT_FORMAT_ID", project_detail.getInt("documentformatid"));
                        projectValues.put("ISACTIVE", project_detail.getBoolean("isactive"));
//                        projectValues.put("ALIAS", project_detail.getString("alias"));
                        if (projectdata.has("Village")) {
                            projectValues.put("VILLAGE_NAME", projectdata.getString("Village"));
                        }

                        getDb().insert("PROJECT_SPATIAL_DATA", null, projectValues);
                    }
                }
            }

            if (projectdata.has("ClaimType")) {
                ContentValues claimTypes = new ContentValues();
                JSONArray claimTypesArray = projectdata.getJSONArray("ClaimType");
                if (claimTypesArray.length() > 0) {
                    for (int i = 0; i < claimTypesArray.length(); i++) {
                        JSONObject claimType = new JSONObject(claimTypesArray.get(i).toString());
                        claimTypes.put("CODE", claimType.getString("code"));
                        claimTypes.put("NAME", claimType.getString("name"));
                        claimTypes.put("NAME_OTHER_LANG", claimType.getString("nameOtherLang"));
                        claimTypes.put("ACTIVE", claimType.getBoolean("active"));
                         getDb().insert(ClaimType.TABLE_NAME, null, claimTypes);
                    }
                }
            }

            if (projectdata.has("Villages")) {
                ContentValues villages = new ContentValues();
                JSONArray villagesArray = projectdata.getJSONArray("Villages");
                if (villagesArray.length() > 0) {
                    for (int i = 0; i < villagesArray.length(); i++) {
                        JSONObject village = new JSONObject(villagesArray.get(i).toString());
                        villages.put(Village.COL_ID, village.getString("hierarchyid"));
                        villages.put(Village.COL_NAME, village.getString("name"));
                        villages.put(Village.COL_NAME_EN, village.getString("nameEn"));
                        getDb().insert(Village.TABLE_NAME, null, villages);
                    }
                }
            }

            if (projectdata.has("AOI")) {
                ContentValues aoiTypes = new ContentValues();
                JSONArray aoiTypesArray = projectdata.getJSONArray("AOI");
                int iCount=1;
                if (aoiTypesArray.length() > 0) {
                    for (int i = 0; i < aoiTypesArray.length(); i++) {
                        JSONObject aoi = new JSONObject(aoiTypesArray.get(i).toString());
                        aoiTypes.put("AOIID", aoi.getString("aoiid"));
                       // aoiTypes.put("AOINAME", aoi.getString("name"));
                        aoiTypes.put("AOINAME", "AOI - "+""+iCount);
                        aoiTypes.put("USERID", aoi.getInt("userid"));
                        aoiTypes.put("PROJECTNAME_ID", aoi.getInt("projectnameid"));
//                        aoiTypes.put("COORDINATES", aoi.getString("geomStr").replace("POLYGON","").replace("(","").replace(")",""));
                        aoiTypes.put("COORDINATES", aoi.getString("geomStr"));
                        aoiTypes.put("ISACTIVE", aoi.getString("isactive"));
                        getDb().insert(AOI.TABLE_NAME, null, aoiTypes);
                        iCount=iCount+1;
                    }
                }


            }

//
            if (projectdata.has("DisputeType")) {
                ContentValues types = new ContentValues();
                JSONArray typesArray = projectdata.getJSONArray("DisputeType");
                if (typesArray.length() > 0) {
                    for (int i = 0; i < typesArray.length(); i++) {
                        JSONObject type = new JSONObject(typesArray.get(i).toString());
                        types.put(RefData.COL_CODE, type.getInt("disputetypeid"));
                        types.put(RefData.COL_NAME, type.getString("disputetype"));
                        types.put(RefData.COL_NAME_OTHER_LANG, type.getString("disputetypeEn"));
//                        types.put(RefData.COL_ACTIVE, type.getBoolean("active") ? 1 : 0);
                        types.put(RefData.COL_ACTIVE, type.getBoolean("isactive"));
                        getDb().insert(DisputeType.TABLE_NAME, null, types);
                    }
                }
            }

            if (projectdata.has("AcquisitionType")) {
                ContentValues types = new ContentValues();
                JSONArray typesArray = projectdata.getJSONArray("AcquisitionType");
                if (typesArray.length() > 0) {
                    for (int i = 0; i < typesArray.length(); i++) {
                        JSONObject type = new JSONObject(typesArray.get(i).toString());
                        types.put(RefData.COL_CODE, type.getInt("acquisitiontypeid"));
                        types.put(RefData.COL_NAME, type.getString("acquisitiontype"));
                        types.put(RefData.COL_NAME_OTHER_LANG, type.getString("acquisitiontypeEn"));
                        types.put(RefData.COL_ACTIVE, type.getBoolean("isactive"));
                        getDb().insert(AcquisitionType.TABLE_NAME, null, types);
                    }
                }
            }

            if (projectdata.has("RelationshipType")) {
                ContentValues relTypes = new ContentValues();
                JSONArray relTypesArray = projectdata.getJSONArray("RelationshipType");
                if (relTypesArray.length() > 0) {
                    for (int i = 0; i < relTypesArray.length(); i++) {
                        JSONObject relType = new JSONObject(relTypesArray.get(i).toString());
                        relTypes.put("CODE", relType.getInt("relationshiptypeid"));
                        relTypes.put("NAME", relType.getString("relationshiptype"));
                        relTypes.put("NAME_OTHER_LANG", relType.getString("relationshiptypeEn"));
                        relTypes.put("ACTIVE", relType.getBoolean("isactive"));
                        getDb().insert(RelationshipType.TABLE_NAME, null, relTypes);
                    }
                }
            }

            if (projectdata.has("ShareType")) {
                ContentValues shareTypes = new ContentValues();
                JSONArray shareTypesArray = projectdata.getJSONArray("ShareType");
                if (shareTypesArray.length() > 0) {
                    for (int i = 0; i < shareTypesArray.length(); i++) {
                        JSONObject shareType = new JSONObject(shareTypesArray.get(i).toString());
                        shareTypes.put("CODE", shareType.getInt("landsharetypeid"));
                        shareTypes.put("NAME", shareType.getString("landsharetype"));
                        shareTypes.put("NAME_OTHER_LANG", shareType.getString("landsharetypeEn"));
                        shareTypes.put("ACTIVE", shareType.getBoolean("isactive"));
                        getDb().insert(ShareType.TABLE_NAME, null, shareTypes);
                    }
                }
            }

            if (projectdata.has("RightType")) {
                ContentValues rightTypes = new ContentValues();
                JSONArray rightTypesArray = projectdata.getJSONArray("RightType");
                if (rightTypesArray.length() > 0) {
                    for (int i = 0; i < rightTypesArray.length(); i++) {
                        JSONObject rightType = new JSONObject(rightTypesArray.get(i).toString());
                        rightTypes.put("CODE", rightType.getInt("tenureclassid"));
                        rightTypes.put("NAME", rightType.getString("tenureclass"));
                        rightTypes.put("NAME_OTHER_LANG", rightType.getString("tenureclassEn"));
                        rightTypes.put("ACTIVE", rightType.getBoolean("isactive"));
                        rightTypes.put("FOR_ADJUDICATION", 1);
                        getDb().insert(RightType.TABLE_NAME, null, rightTypes);
                    }
                }
            }

            if (projectdata.has("Genders")) {
                ContentValues genders = new ContentValues();
                JSONArray gendersArray = projectdata.getJSONArray("Genders");
                if (gendersArray.length() > 0) {
                    for (int i = 0; i < gendersArray.length(); i++) {
                        JSONObject gender = new JSONObject(gendersArray.get(i).toString());
                        genders.put("CODE", gender.getInt("genderId"));
                        genders.put("NAME", gender.getString("gender"));
                        genders.put("NAME_OTHER_LANG", gender.getString("gender_en"));
                        genders.put("ACTIVE", gender.getBoolean("active"));
                        getDb().insert(Gender.TABLE_NAME, null, genders);
                    }
                }
            }

            if (projectdata.has("Adjudicator")) {
                ContentValues projectValues = new ContentValues();
                JSONArray project_info = projectdata.getJSONArray("Adjudicator");
                if (project_info.length() > 0) {
                    for (int i = 0; i < project_info.length(); i++) {
                        JSONObject project_detail = new JSONObject(project_info.get(i).toString());
                        projectValues.put("ID", project_detail.getInt("id"));
                        projectValues.put("ADJUDICATOR_NAME", project_detail.getString("adjudicatorName"));
                        getDb().insert("ADJUDICATOR_DETAILS", null, projectValues);
                    }
                }
            }
            //(ATTRIID INTEGER,ATTRIVALUE TEXT,LISTING TEXT,FLAG TEXT)
            if (projectdata.has("TenureType")) {
                ContentValues projectValues = new ContentValues();
                JSONArray project_info = projectdata.getJSONArray("TenureType");
                if (project_info.length() > 0) {
                    for (int i = 0; i < project_info.length(); i++) {
                        JSONObject project_detail = new JSONObject(project_info.get(i).toString());
                        projectValues.put("ATTRIID", project_detail.getInt("attributecategoryid"));
                        projectValues.put("ATTRIVALUE", project_detail.getString("categoryName"));
                        projectValues.put("LISTING", project_detail.getString("categorydisplayorder"));
                        if (project_detail.has("categorytype")){
                            JSONObject jsonObjectFlag = project_detail.getJSONObject("categorytype");
                            projectValues.put("FLAG", jsonObjectFlag.getString("typename"));
                        }
                        getDb().insert("TENURE_TYPE", null, projectValues);
                    }
                }
            }


            if (projectdata.has("Attributes")) {
                ContentValues attributeValues = new ContentValues();
                JSONArray attribute_info = projectdata.getJSONArray("Attributes");
                if (attribute_info.length() > 0) {
                    for (int i = 0; i < attribute_info.length(); i++) {
                        JSONObject attribute_detail = new JSONObject(attribute_info.get(i).toString());
                        attributeValues.put("ATTRIB_ID", attribute_detail.getInt("attributemasterid"));
                        if (attribute_detail.has("laExtAttributecategory")) {

                            //ATTRIBUTE_TYPE_NAME
                            JSONObject attributeCategory = attribute_detail.getJSONObject("laExtAttributecategory");
                            attributeValues.put("ATTRIBUTE_TYPE", attributeCategory.getString("attributecategoryid"));
                            attributeValues.put("ATTRIBUTE_TYPE_NAME", attributeCategory.getString("categoryName"));

                            if (attributeCategory.has("categorytype")) {
                                JSONObject attributecategoryType = attributeCategory.getJSONObject("categorytype");
                                attributeValues.put("FLAG", attributecategoryType.getString("typename"));


                            }
                        }

                        if (attribute_detail.has("laExtAttributedatatype")) {
                            JSONObject datatypeIdBean = attribute_detail.getJSONObject("laExtAttributedatatype");
                            attributeValues.put("ATTRIBUTE_CONTROLTYPE", datatypeIdBean.getInt("datatypeId"));
                            if (datatypeIdBean.getInt("datatypeId") == 5) {
                                if (!attribute_detail.getString("options").equalsIgnoreCase("null")) {

                                    JSONArray attributeOptions = attribute_detail.getJSONArray("options");
                                    if (attributeOptions.length() > 0) {
                                        for (int j = 0; j < attributeOptions.length(); j++) {
                                            ContentValues option_value = new ContentValues();
                                            JSONObject optionValues = attributeOptions.getJSONObject(j);
                                            option_value.put("OPTION_ID", optionValues.getInt("attributeoptionsid"));
                                            option_value.put("ATTRIB_ID", attribute_detail.getInt("attributemasterid"));
                                            option_value.put("OPTION_NAME", optionValues.getString("optiontext"));
//                                            option_value.put("OPTION_NAME_OTHER", optionValues.getString("optiontext_second_language"));
                                            getDb().insert("OPTIONS", null, option_value);
                                            option_value.clear();
                                        }
                                    }
                                }
                            }
                        }
                        attributeValues.put("ATTRIBUTE_NAME", attribute_detail.getString("fieldaliasname"));
                        if (attribute_detail.has("listing") && !TextUtils.isEmpty(attribute_detail.getString("listing")) && !attribute_detail.getString("listing").equalsIgnoreCase("null")) {
                            attributeValues.put("LISTING", attribute_detail.getInt("listing"));
                        }
                        if (attribute_detail.has("mandatory") && !TextUtils.isEmpty(attribute_detail.getString("mandatory")) && !attribute_detail.getString("mandatory").equalsIgnoreCase("null")) {
                            attributeValues.put("VALIDATION", attribute_detail.getString("mandatory"));
                        }
                        if (attribute_detail.has("fieldname") && !TextUtils.isEmpty(attribute_detail.getString("fieldname")) && !attribute_detail.getString("fieldname").equalsIgnoreCase("null")) {
                            attributeValues.put("ATTRIBUTE_NAME_OTHER", attribute_detail.getString("fieldname"));
                        }
                        getDb().insert("ATTRIBUTE_MASTER", null, attributeValues);
                        attributeValues.clear();
                    }
                }
            }

            if (projectdata.has("ResourceClassification")) {
                ContentValues projectValues = new ContentValues();
                JSONArray project_info = projectdata.getJSONArray("ResourceClassification");
                if (project_info.length() > 0) {
                    for (int i = 0; i < project_info.length(); i++) {
                        JSONObject project_detail = new JSONObject(project_info.get(i).toString());
                        projectValues.put("ATTRIID", project_detail.getInt("classificationid"));
                        projectValues.put("ATTRIVALUE", project_detail.getString("classificationname"));
                        getDb().insert(ClassificationAttribute.TABLE_NAME, null, projectValues);
                    }
                }
            }

            if (projectdata.has("ResourceSubClassification")) {
                ContentValues projectValues = new ContentValues();
                JSONArray project_info = projectdata.getJSONArray("ResourceSubClassification");
                if (project_info.length() > 0) {
                    for (int i = 0; i < project_info.length(); i++) {
                        JSONObject project_detail = new JSONObject(project_info.get(i).toString());
                        projectValues.put("ATTRIID", project_detail.getInt("subclassificationid"));
                        projectValues.put("ATTRIVALUE", project_detail.getString("subclassificationname"));


                        if (project_detail.has("classificationid")) {
                            JSONObject classificationId = project_detail.getJSONObject("classificationid");
                            projectValues.put("ClassificationID", classificationId.getString("classificationid"));
                        }
                        getDb().insert(SubClassificationAttribute.TABLE_NAME, null, projectValues);
                    }
                }
            }


            if (projectdata.has("ResourceCustomAttributes")) {
                ContentValues attributeValues = new ContentValues();
                JSONArray attribute_info = projectdata.getJSONArray("ResourceCustomAttributes");
                if (attribute_info.length() > 0) {
                    for (int i = 0; i < attribute_info.length(); i++) {
                        JSONObject attribute_detail = new JSONObject(attribute_info.get(i).toString());
                        attributeValues.put("ATTRIB_ID", attribute_detail.getInt("customattributeid"));
                        attributeValues.put("SUBCLASSI_ID", attribute_detail.getString("subclassificationid"));
                        if (attribute_detail.has("attributecategoryid")) {

                            //ATTRIBUTE_TYPE_NAME
                            JSONObject attributeCategory = attribute_detail.getJSONObject("attributecategoryid");
                            attributeValues.put("ATTRIBUTE_TYPE", attributeCategory.getString("attributecategoryid"));
                            attributeValues.put("ATTRIBUTE_TYPE_NAME", attributeCategory.getString("categoryName"));

                            if (attributeCategory.has("categorytype")) {
                                JSONObject attributecategoryType = attributeCategory.getJSONObject("categorytype");
                                attributeValues.put("FLAG", attributecategoryType.getString("typename"));


                            }
                        }

                        if (attribute_detail.has("datatypemasterid")) {
                            JSONObject datatypeIdBean = attribute_detail.getJSONObject("datatypemasterid");
                            attributeValues.put("ATTRIBUTE_CONTROLTYPE", datatypeIdBean.getInt("datatypeId"));
                            if (datatypeIdBean.getInt("datatypeId") == 5) {
                                if (!attribute_detail.getString("options").equalsIgnoreCase("null")) {

                                    JSONArray attributeOptions = attribute_detail.getJSONArray("options");
                                    if (attributeOptions.length() > 0) {
                                        for (int j = 0; j < attributeOptions.length(); j++) {
                                            ContentValues option_value = new ContentValues();
                                            JSONObject optionValues = attributeOptions.getJSONObject(j);
                                            option_value.put("OPTION_ID", optionValues.getInt("attributeoptionsid"));
                                            option_value.put("ATTRIB_ID", attribute_detail.getInt("customattributeid"));
                                            option_value.put("OPTION_NAME", optionValues.getString("optiontext"));
//                                            option_value.put("OPTION_NAME_OTHER", optionValues.getString("optiontext_second_language"));
                                            getDb().insert("OPTIONS", null, option_value);
                                            option_value.clear();
                                        }
                                    }
                                }
                            }
                        }
                        attributeValues.put("ATTRIBUTE_NAME", attribute_detail.getString("fieldaliasname"));
                        if (attribute_detail.has("listing") && !TextUtils.isEmpty(attribute_detail.getString("listing")) && !attribute_detail.getString("listing").equalsIgnoreCase("null")) {
                            attributeValues.put("LISTING", attribute_detail.getInt("listing"));
                        }
                        if (attribute_detail.has("mandatory") && !TextUtils.isEmpty(attribute_detail.getString("mandatory")) && !attribute_detail.getString("mandatory").equalsIgnoreCase("null")) {
                            attributeValues.put("VALIDATION", attribute_detail.getString("mandatory"));
                        }
                        if (attribute_detail.has("fieldname") && !TextUtils.isEmpty(attribute_detail.getString("fieldname")) && !attribute_detail.getString("fieldname").equalsIgnoreCase("null")) {
                            attributeValues.put("ATTRIBUTE_NAME_OTHER", attribute_detail.getString("fieldname"));
                        }
                        getDb().insert("RESOURCE_ATTRIBUTE_MASTER", null, attributeValues);
                        attributeValues.clear();
                    }
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
            cf.syncLog("", e);
            return false;
        }
        return true;
    }

    public List<ProjectSpatialDataDto> getProjectSpatialData() {
        List<ProjectSpatialDataDto> projectSpatialList = new ArrayList<ProjectSpatialDataDto>();


        String selectQueryQues = "SELECT * from PROJECT_SPATIAL_DATA where ISACTIVE=1 order by SERVER_PK";
//        String selectQueryQues = "SELECT * from PROJECT_SPATIAL_DATA order by SERVER_PK";
        Cursor cursor = getDb().rawQuery(selectQueryQues, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    ProjectSpatialDataDto projectSpatialData = new ProjectSpatialDataDto();
                    projectSpatialData.setServer_Pk(cursor.getInt(0));//Server Pk
                    projectSpatialData.setProject_Name_Id(cursor.getInt(1));//Project NAme ID
                    projectSpatialData.setFile_Name(cursor.getString(2));// File_name
//                    projectSpatialData.setFile_Ext(cursor.getString(3));
                    projectSpatialData.setAlias(cursor.getString(3));//Alias
                    projectSpatialList.add(projectSpatialData);
                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();
        return projectSpatialList;
    }

    /**
     * Saves ownership right
     */
    public boolean saveRight(Right right) {
        try {
            if (right == null) {
                return true;
            }

            if (right.getId() == null || right.getId() < 1) {
                right.setId(getNewGroupId());
            } else {
                // Delete from right
                getDb().delete(Right.TABLE_NAME, Right.COL_ID + "=" + right.getId(), null);
            }

            // Insert right
            ContentValues row = new ContentValues();
            row.put(Right.COL_ID, right.getId());
            row.put(Right.COL_FEATURE_ID, right.getFeatureId());
            row.put(Right.COL_SERVER_ID, right.getServerId());
            row.put(Right.COL_RELATIONSHIP_ID, right.getRelationshipId());
            row.put(Right.COL_CERT_DATE, right.getCertDate());
            row.put(Right.COL_CERT_NUMBER, right.getCertNumber());
            row.put(Right.COL_JURIDICAL_AREA, right.getJuridicalArea());
            row.put(Right.COL_RIGHT_TYPE_ID, right.getRightTypeId());
            row.put(Right.COL_SHARE_TYPE_ID, right.getShareTypeId());
            row.put(Right.COL_Acquisition_ID, right.getAcquisitionTypeId());

            getDb().insert(Right.TABLE_NAME, null, row);

            // Save attributes
            if (right.getAttributes() != null && right.getAttributes().size() > 0) {
                for (Attribute attribute : right.getAttributes()) {
                    attribute.setGroupId(right.getId());
                    attribute.setFeatureId(right.getFeatureId());
                }
                saveAttributesList(right.getAttributes());
            }

            return true;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves person
     */
    public boolean savePerson(Person person) {
        try {
            if (person == null) {
                return true;
            }

            if (person.getId() == null || person.getId() < 1) {
                person.setId(getNewGroupId());
            } else {
                // Delete from person
                getDb().delete(Person.TABLE_NAME, Person.COL_ID + "=" + person.getId(), null);
            }

            // Insert person
            ContentValues row = new ContentValues();
            row.put(Person.COL_ID, person.getId());
            row.put(Person.COL_RIGHT_ID, person.getRightId());
            row.put(Person.COL_FEATURE_ID, person.getFeatureId());
            row.put(Person.COL_SERVER_ID, person.getServerId());
            row.put(Person.COL_IS_NATURAL, person.getIsNatural());
            row.put(Person.COL_RESIDENT, person.getResident());
            row.put(Person.COL_SUBTYPE, person.getSubTypeId());
            row.put(Person.COL_SHARE, person.getShare());
            row.put(Person.COL_DISPUTE_ID, person.getDisputeId());
            row.put(Person.COL_ACQUISITION_TYPE_ID, person.getAcquisitionTypeId());

            getDb().insert(Person.TABLE_NAME, null, row);

            // Save attributes
            if (person.getAttributes() != null && person.getAttributes().size() > 0) {
                for (Attribute attribute : person.getAttributes()) {
                    attribute.setGroupId(person.getId());
                    attribute.setFeatureId(person.getFeatureId());
                }
                saveAttributesList(person.getAttributes());
            }

            return true;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveNonPerson(Person person) {
        try {
            if (person == null) {
                return true;
            }

            if (person.getId() == null || person.getId() < 1) {
                person.setId(getNewGroupId());
            } else {
                // Delete from person
                getDb().delete(Person.TABLE_NAME, Person.COL_ID + "=" + person.getId(), null);
            }

            // Insert person
            ContentValues row = new ContentValues();
            row.put(Person.COL_ID, person.getId());
            row.put(Person.COL_RIGHT_ID, person.getRightId());
            row.put(Person.COL_FEATURE_ID, person.getFeatureId());
            row.put(Person.COL_SERVER_ID, person.getServerId());
            row.put(Person.COL_IS_NATURAL, person.getIsNatural());
            row.put(Person.COL_RESIDENT, person.getResident());
            row.put(Person.COL_SUBTYPE, person.getSubTypeId());
            row.put(Person.COL_SHARE, person.getShare());
            row.put(Person.COL_DISPUTE_ID, person.getDisputeId());
            row.put(Person.COL_ACQUISITION_TYPE_ID, person.getAcquisitionTypeId());

            getDb().insert(Person.TABLE_NAME, null, row);

            List<NonNatural> nonNaturalList=getNonNaturalList(person.getFeatureId());
            // Save attributes

            if (person.getAttributes() != null && person.getAttributes().size() > 0) {
                for (Attribute attribute : person.getAttributes()) {
                    attribute.setGroupId(person.getId());
                    attribute.setFeatureId(person.getFeatureId());
                }
                saveAttributesList(person.getAttributes());
            }

            if (nonNaturalList!= null && nonNaturalList.size() > 0) {
                for (NonNatural attribute : nonNaturalList) {
                    attribute.setGroupId(person.getId());
                    attribute.setFeatureId(person.getFeatureId());
                }
                saveAttributesNonList(nonNaturalList);

            }


            return true;

        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }

    private List<NonNatural> getNonNaturalList(Long featureId) {
        return createNonNaturalList("SELECT * FROM " + NonNatural.TABLE_NAME +
                " WHERE " + NonNatural.COL_VALUE_FEATURE_ID + "=" + featureId);
    }

    /**
     * Saves media
     */
    public boolean saveMedia(Media media) {
        try {
            if (media == null) {
                return true;
            }

            if (media.getId() == null || media.getId() < 1) {
                media.setId(getNewGroupId());
            } else {
                // Delete from media
                getDb().delete(Media.TABLE_NAME, Media.COL_ID + "=" + media.getId(), null);
            }

            // Insert media
            ContentValues row = new ContentValues();
            row.put(Media.COL_ID, media.getId());
            row.put(Media.COL_FEATURE_ID, media.getFeatureId());
            row.put(Media.COL_PATH, media.getPath());
            row.put(Media.COL_TYPE, media.getType());
            row.put(Media.COL_PERSON_ID, media.getPersonId());
            row.put(Media.COL_DISPUTE_ID, media.getDisputeId());
            row.put(Media.COL_SYNCED, media.getSynced());

            getDb().insert(Media.TABLE_NAME, null, row);

            // Save attributes
            if (media.getAttributes() != null && media.getAttributes().size() > 0) {
                for (Attribute attribute : media.getAttributes()) {
                    attribute.setGroupId(media.getId());
                    attribute.setFeatureId(media.getFeatureId());
                }
                saveAttributesList(media.getAttributes());
            }

            return true;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;
        }
    }

    public String getProjectname() {
        String projectName = "";
        String q = "select project_name_id from PROJECT_SPATIAL_DATA LIMIT 1";
        Cursor cursor = getDb().rawQuery(q, null);

        if (cursor.moveToFirst()) {
            projectName = Integer.toString(cursor.getInt(0));
            cursor.close();
        }
        return projectName;
    }

    //Check there is any status  completed or not
    public boolean hasCompletedClaims(){
        boolean isStatus=false;

        String spatialFeatureSql ="SELECT * FROM " + Property.TABLE_NAME +
                    " WHERE " + Property.COL_STATUS + " = '" + Property.CLIENT_STATUS_COMPLETE + "' AND (" +
                    Property.COL_SERVER_ID + " IS NULL OR " + Property.COL_SERVER_ID + " = '')";
        Cursor cursor = getDb().rawQuery(spatialFeatureSql, null);
        if (cursor.moveToFirst()) {
            isStatus = true;
        }
        cursor.close();

        return isStatus;
    }

    public String getProjectDataForUpload(String ParamType) {
        try {
            List<Property> properties = new ArrayList<>();

            if (ParamType != "" && ParamType != null) {
                properties = createPropertyList("SELECT * FROM " + Property.TABLE_NAME +
                        " WHERE " + Property.COL_STATUS + " = '" + Property.CLIENT_STATUS_COMPLETE + "' AND (" +
                        Property.COL_SERVER_ID + " IS NULL OR " + Property.COL_SERVER_ID + " = '')" + " AND (" +
                        " FLAG " + " = '" + ParamType + "')");
            }

            if (properties == null || properties.size() < 1)
                return "";

            Gson gson = new Gson();
            Type type = new TypeToken<List<Property>>() {
            }.getType();
            return gson.toJson(properties, type);
        } catch (Exception e) {
            cf.syncLog("", e);
            cf.showToast(contxt, R.string.FailedToFormJson, Toast.LENGTH_SHORT);
            e.printStackTrace();
            return "";
        }
    }

    public void updateServerFeatureId(String data) throws JSONException {
        if (data != null) {
            JSONObject jsonObj = new JSONObject(data);
            Iterator<String> iterator = jsonObj.keys();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                String featureId = key;
                String whereClause = "FEATURE_ID = " + featureId;
                try {
                    if (jsonObj.get(key) instanceof JSONArray) {
                        JSONArray arry = jsonObj.getJSONArray(key);
                        int size = arry.length();
                        for (int i = 0; i < size; i++) {
                            arry.getJSONObject(i);
                            String server_featureId = arry.getJSONObject(i).toString();
                            // updating  Features
                            ContentValues value = new ContentValues();
                            value.put("SERVER_FEATURE_ID", server_featureId);
                            getDb().update("SPATIAL_FEATURES", value, whereClause, null);
                        }
                    } else if (jsonObj.get(key) instanceof JSONObject) {
                        jsonObj.getJSONObject(key);
                        String server_featureId = jsonObj.getJSONObject(key).toString();
                        // updating  Featureso
                        ContentValues value = new ContentValues();
                        value.put("SERVER_FEATURE_ID", server_featureId);
                        getDb().update("SPATIAL_FEATURES", value, whereClause, null);

                    } else {
                        System.out.println("" + key + " : " + jsonObj.optString(key));
                        String server_featureId = jsonObj.optString(key);
                        // updating  Features
                        ContentValues value = new ContentValues();
                        value.put("SERVER_FEATURE_ID", server_featureId);
                        int row = getDb().update("SPATIAL_FEATURES", value, whereClause, null);
                        if (row < 1) {
                            Toast.makeText(contxt, "0 rows updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    System.out.println("" + key + " : " + jsonObj.optString(key));
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean hasMediaToUpload(){
        boolean hasMedia=false;

        String sql = "SELECT " +
                "MV.MEDIA_ID " +
                "FROM MEDIA AS MV INNER JOIN SPATIAL_FEATURES AS SF ON MV.FEATURE_ID =  SF.FEATURE_ID " +
                "WHERE SF.STATUS = '" + Property.CLIENT_STATUS_COMPLETE + "' and MV.SYNCED=0 Limit 1";

        Cursor cursor = getDb().rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            hasMedia = true;
        }
        cursor.close();
        return hasMedia;
    }

    public JSONArray getMultimediaforUpload() {
        JSONArray mediaAttribsObj = new JSONArray();
        try {
            int mediaId = 0;
            long personId = 0;

            String fetchMediaAttributeSql = "SELECT  AM.attrib_id,FM.GROUP_ID,FM.ATTRIB_VALUE FROM ATTRIBUTE_MASTER AS AM "
                    + " LEFT OUTER JOIN FORM_VALUES AS FM ON AM.ATTRIB_ID = FM.ATTRIB_ID "
                    + " WHERE AM.ATTRIBUTE_TYPE = '" + Attribute.TYPE_MULTIMEDIA + "' AND GROUP_ID = <group_id> and (FM.ATTRIB_VALUE != '' OR FM.ATTRIB_VALUE != NULL) order by group_id";

            String fetchFromMediaSql = "SELECT " +
                    "MV.MEDIA_ID," +
                    "MV.FEATURE_ID, " +
                    "MV.PERSON_ID, " +
                    "MV.DISPUTE_ID, " +
                    "MV.PATH," +
                    "SF.SERVER_FEATURE_ID," +
                    "MV.TYPE, SF.FLAG "
                    + "FROM MEDIA AS MV INNER JOIN SPATIAL_FEATURES AS SF ON MV.FEATURE_ID =  SF.FEATURE_ID "
                    + "WHERE SF.STATUS = '" + Property.CLIENT_STATUS_COMPLETE + "' and MV.SYNCED=0 Limit 1";

            String userId = getLoggedUser().getUserId().toString();

            JSONArray medias = new JSONArray();
            JSONArray mediasAttributes = new JSONArray();

            // Fetching media for spatial unit
            Cursor cursor = getDb().rawQuery(fetchFromMediaSql, null);
            if (cursor.moveToFirst()) {
                mediaId = cursor.getInt(0);
                medias.put(0, cursor.getLong(5)); //usin
                if (cursor.isNull(2))
                    medias.put(1, "");//person id
                else {
                    personId = cursor.getLong(2);
                    medias.put(1, personId);//person id
                }
                medias.put(2, mediaId);//media id
                medias.put(3, cursor.getString(4));// media path
                medias.put(4, cursor.getString(6));// media type


                if (cursor.isNull(3))
                    medias.put(5, "");//dispute id
                else {
                    medias.put(5, cursor.getLong(3));//dispute id
                }
                medias.put(6, userId);
                medias.put(7, cursor.getString(7));
            }
            cursor.close();

            if (mediaId != 0) {
                if (personId == 0) {
                    String final_sql = fetchMediaAttributeSql.replace("<group_id>", mediaId + "");
                    Cursor cursor_attrib = getDb().rawQuery(final_sql, null);
                    if (cursor_attrib.moveToFirst()) {
                        do {
                            JSONArray mediaValuesArr = new JSONArray();
                            mediaValuesArr.put(0, cursor_attrib.getString(0));//attribID
                            mediaValuesArr.put(1, cursor_attrib.getString(2));//attribvalue


                            mediasAttributes.put(mediaValuesArr);
                        } while (cursor_attrib.moveToNext());
                    }
                    cursor_attrib.close();
                }
                mediaAttribsObj.put(0, medias);
                mediaAttribsObj.put(1, mediasAttributes);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaAttribsObj;
    }

    public boolean updateMediaSyncedStatus(String mediaId, int syncStatus) {

        String whereClauseForMedia = "MEDIA_ID = " + mediaId;
        ContentValues value = new ContentValues();
        value.put("SYNCED", syncStatus);

        int updatedMediaRow = getDb().update("MEDIA", value, whereClauseForMedia, null);

        if (updatedMediaRow < 1) {
            return false;
        } else {
            return true;
        }
    }

    public List<Feature> fetchRejectedFeatures() {
        String q = "SELECT * FROM SPATIAL_FEATURES where status = '" + Property.CLIENT_STATUS_REJECTED + "'";
        return getFeatures(q);
    }

    public boolean checkPendingDraftAndCompletedRecordsToSync() {
        boolean flag = false;
        String spatialFeatureSql = "SELECT * FROM SPATIAL_FEATURES where status = '" + Property.CLIENT_STATUS_DRAFT
                + "' OR status = '" + Property.CLIENT_STATUS_COMPLETE + "' and SERVER_FEATURE_ID IS NULL OR SERVER_FEATURE_ID = ''";
        Cursor cursor = getDb().rawQuery(spatialFeatureSql, null);
        if (cursor.moveToFirst()) {
            flag = true;
        }
        cursor.close();
        return flag;
    }

    public boolean setRejectedStatus(String json_string) {
        try {
            String sptialids = json_string.substring(1, json_string.length() - 1);
            ContentValues values = new ContentValues();
            values.put("STATUS", Property.CLIENT_STATUS_REJECTED);
            String sqlwhere = " SERVER_FEATURE_ID in (" + sptialids + ")";
            getDb().update("SPATIAL_FEATURES", values, sqlwhere, null);
        } catch (Exception e) {
            cf.syncLog("", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean saveDownloadedProperties(String data) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Property>>() {
        }.getType();
        List<Property> properties = gson.fromJson(data, type);

        if (properties != null && properties.size() > 0) {
            // Get lodged user
            String userId = "";
            User user = getLoggedUser();
            if (user != null)
                userId = user.getUserId().toString();

            // Clean downloaded feature from DB
            deleteDownloadedFeatures();

            for (Property prop : properties) {
                String status = "";

                // Skip feature if status is rejected
                if (StringUtility.empty(prop.getStatus()).equalsIgnoreCase(Feature.SERVER_STATUS_REJECTED)) {
                    continue;
                }

                status = StringUtility.empty(prop.getStatus());

                // Set appropriate client status to the property
                if (status.equalsIgnoreCase(Feature.SERVER_STATUS_APPROVED)) {
                    prop.setStatus(Feature.CLIENT_STATUS_FINAL);
                } else if (status.equalsIgnoreCase(Feature.SERVER_STATUS_VALIDATED)) {
                    prop.setStatus(Feature.CLIENT_STATUS_VERIFIED_AND_SYNCHED);
                } else {
                    prop.setStatus(Feature.CLIENT_STATUS_DOWNLOADED);
                }

                // Insert property received from server
                insertProperty(prop);
            }
        }
        return true;
    }

    public Property insertProperty(Property prop) {
        // Insert spatial feature
        ContentValues values = new ContentValues();
        values.put(Property.COL_CLAIM_TYPE_CODE, prop.getClaimTypeCode());
        values.put(Property.COL_POLYGON_NUMBER, prop.getPolygonNumber());
        values.put(Property.COL_SURVEY_DATE, prop.getSurveyDate());
        values.put(Property.COL_HAMLET_ID, prop.getHamletId());
        values.put(Property.COL_ADJUDICATOR1, prop.getAdjudicator1());
        values.put(Property.COL_ADJUDICATOR2, prop.getAdjudicator2());
        values.put(Property.COL_SERVER_ID, prop.getServerId());
        values.put(Property.COL_STATUS, prop.getStatus());
        values.put(Property.COL_GEOM_TYPE, prop.getGeomType());
        values.put(Property.COL_COORDINATES, prop.getCoordinates());
        values.put(Property.COL_COMPLETION_DATE, prop.getCompletionDate());
        values.put(Property.COL_CREATION_DATE, prop.getCreationDate());
        values.put(Property.COL_IMEI, prop.getImei());
        values.put(Property.COL_UKA_NUMBER, prop.getUkaNumber());
        values.put(Property.COL_VILLAGE_ID, prop.getVillageId());
        values.put(Property.COL_FEATURE_TYPE, prop.getFeatureType());
        values.put(Property.COL_FEATURE_DESCRIPTION, prop.getFeatureDescription());

        getDb().insert(Feature.TABLE_NAME, null, values);
        long featureId = getGeneratedId(Feature.TABLE_NAME);
        prop.setId(featureId);
        savePropAttributes(prop.getAttributes(), featureId);

        // Insert tenure
        if (prop.getRight() != null) {
            Right right = prop.getRight();
            // reset right id so that system generates new one
            right.setId(null);
            right.setFeatureId(featureId);
            saveRight(right);

            // Insert non-natural person
            if (right.getNonNaturalPerson() != null) {
                Person person = right.getNonNaturalPerson();
                person.setId(null);
                person.setRightId(right.getId());
                person.setFeatureId(featureId);
                savePerson(person);
            }

            // Insert natural persons
            if (right.getNaturalPersons() != null && right.getNaturalPersons().size() > 0) {
                for (Person person : right.getNaturalPersons()) {
                    person.setId(null);
                    person.setRightId(right.getId());
                    person.setFeatureId(featureId);
                    savePerson(person);
                }
            }
        }

        // Insert POI
        if (prop.getPersonOfInterests() != null && prop.getPersonOfInterests().size() > 0) {
            for (PersonOfInterest poi : prop.getPersonOfInterests()) {
                poi.setId(null);
                poi.setFeatureId(featureId);
                savePersonOfInterest(poi);
            }
        }

        // Insert Deceased
        if (prop.getDeceasedPerson() != null) {
            prop.getDeceasedPerson().setId(null);
            prop.getDeceasedPerson().setFeatureId(featureId);
            saveDeceasedPerson(prop.getDeceasedPerson());
        }

        // Insert Media
        if (prop.getMedia() != null && prop.getMedia().size() > 0) {
            for (Media media : prop.getMedia()) {
                media.setId(null);
                media.setFeatureId(featureId);
                media.setSynced(1);
                saveMedia(media);
            }
        }

        // Insert dispute
        if (prop.getDispute() != null) {
            Dispute dispute = prop.getDispute();
            dispute.setId(null);
            dispute.setFeatureId(featureId);
            saveDispute(dispute);

            // Insert disputing parties
            if (dispute.getDisputingPersons() != null) {
                for (Person person : dispute.getDisputingPersons()) {
                    person.setId(null);
                    person.setDisputeId(dispute.getId());
                    person.setFeatureId(featureId);
                    savePerson(person);
                }
            }

            // Insert dispute documents
            if (dispute.getMedia() != null && dispute.getMedia().size() > 0) {
                for (Media media : dispute.getMedia()) {
                    media.setId(null);
                    media.setFeatureId(featureId);
                    media.setDisputeId(dispute.getId());
                    media.setSynced(1);
                    saveMedia(media);
                }
            }
        }

        return prop;
    }

    public boolean resetMediaStatus() {
        String whereClauseForMedia = "SYNCED = " + CommonFunctions.MEDIA_SYNC_ERROR;
        ContentValues value = new ContentValues();
        value.put("SYNCED", CommonFunctions.MEDIA_SYNC_PENDING);

        int updatedMediaRow = getDb().update("MEDIA", value, whereClauseForMedia, null);

        if (updatedMediaRow < 1) {
            return false;
        } else {
            return true;
        }
    }

    public String getVerifiedFeaturesForUpload() {
        String q = "SELECT SERVER_FEATURE_ID FROM SPATIAL_FEATURES where status = '" + Property.CLIENT_STATUS_VERIFIED + "'";
        JSONObject json_obj = new JSONObject();
        JSONArray usins = new JSONArray();
        try {
            Long userid = getLoggedUser().getUserId();
            Cursor cursor = getDb().rawQuery(q, null);
            if (cursor.moveToFirst()) {
                do {
                    usins.put(cursor.getString(0));
                }
                while (cursor.moveToNext());
            }
            cursor.close();
            if (usins.length() > 0) {
                json_obj.put(userid.toString(), usins);
            } else
                return "";
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        }
        return json_obj.toString();
    }

    public boolean updateSyncedVerifiedStatus(String data) {
        try {
            String sptialids = data.substring(1, data.length() - 1);
            ContentValues values = new ContentValues();
            values.put("STATUS", Property.CLIENT_STATUS_VERIFIED_AND_SYNCHED);
            String sqlwhere = " SERVER_FEATURE_ID in (" + sptialids + ")";
            getDb().update("SPATIAL_FEATURES", values, sqlwhere, null);
        } catch (Exception e) {
            cf.syncLog("", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public int getCount(String status) {
        int count = 0;
        String selectQueryQues = null;

        if (status.equalsIgnoreCase(Property.CLIENT_STATUS_DRAFT)) {

            selectQueryQues = "SELECT * FROM SPATIAL_FEATURES where status = '" + Property.CLIENT_STATUS_DRAFT + "'";
        } else if (status.equalsIgnoreCase(Property.CLIENT_STATUS_COMPLETE)) {
            selectQueryQues = "SELECT * FROM SPATIAL_FEATURES where status = '" + Property.CLIENT_STATUS_COMPLETE + "' and (SERVER_FEATURE_ID = '' or SERVER_FEATURE_ID is null)";
        } else if (status.equalsIgnoreCase(Property.CLIENT_STATUS_SYNCED)) {
            selectQueryQues = "SELECT * FROM SPATIAL_FEATURES where STATUS='" + Property.CLIENT_STATUS_COMPLETE + "' and (SERVER_FEATURE_ID IS not NULL OR SERVER_FEATURE_ID != '')";
        } else if (status.equalsIgnoreCase(Property.CLIENT_STATUS_REJECTED)) {
            selectQueryQues = "SELECT * FROM SPATIAL_FEATURES where status = '" + Property.CLIENT_STATUS_REJECTED + "'";
        }

        Cursor cursor = getDb().rawQuery(selectQueryQues, null);

        if (cursor.moveToFirst()) {
            do {
                count++;
            }
            while (cursor.moveToNext());
        }
        cursor.close();
        return count;
    }

    public List<Option> getHamletOptions() {
        List<Option> optionList = new ArrayList<Option>();
        Option option = null;

        option = new Option();
        option.setId(0L);
        option.setName(contxt.getResources().getString(R.string.select));
        optionList.add(option);

        String selectQueryOptions = "SELECT * from HAMLET_DETAILS";
        Cursor cursor = getDb().rawQuery(selectQueryOptions, null);

        if (cursor.moveToFirst()) {
            do {
                option = new Option();
                option.setId(cursor.getLong(0));
                if (!StringUtility.isEmpty(cursor.getString(2))) {
                    option.setName(cursor.getString(1) + " (" + cursor.getString(2) + ")");
                } else {
                    option.setName(cursor.getString(1));
                }
                optionList.add(option);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return optionList;
    }

    public List<Option> getAdjudicators() {
        List<Option> optionList = new ArrayList<Option>();
        Option option = null;

        option = new Option();
        option.setId(0L);
        option.setName(contxt.getResources().getString(R.string.select));
        optionList.add(option);

        String selectQueryOptions = "SELECT * from ADJUDICATOR_DETAILS";
        Cursor cursor = getDb().rawQuery(selectQueryOptions, null);
        if (cursor.moveToFirst()) {
            do {
                option = new Option();
                option.setId(cursor.getLong(0));
                option.setName(cursor.getString(1));
                optionList.add(option);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return optionList;
    }

    /**
     * Updates property static fields, shown on the home screen of the claim.
     */
    public boolean updatePropertyBasicInfo(Property prop) {
        try {
            ContentValues values = new ContentValues();
            values.put(Property.COL_CLAIM_TYPE_CODE, prop.getClaimTypeCode());
            values.put(Property.COL_POLYGON_NUMBER, prop.getPolygonNumber());
            values.put(Property.COL_UKA_NUMBER, prop.getUkaNumber());
            values.put(Property.COL_SURVEY_DATE, prop.getSurveyDate());
            values.put(Property.COL_HAMLET_ID, prop.getHamletId());
            values.put(Property.COL_ADJUDICATOR1, prop.getAdjudicator1());
            values.put(Property.COL_ADJUDICATOR2, prop.getAdjudicator2());
            values.put(Property.COL_CLAIM_RIGHT, prop.getClaimRight());
            values.put(Property.COL_PLOT_NO, prop.getPlotNo());
            values.put(Property.COL_DOCUMENT, prop.getDocument());
            values.put(Property.COL_DOCUMENT_TYPE, prop.getDocumentType());
            values.put(Property.COL_DOCUMENT_DATE, prop.getDocumentDate());
            values.put(Property.COL_DOCUMENT_REF_NO, prop.getDocumentRefNo());
            values.put(Property.COL_IS_NATURAL, prop.getIsNatural());
            values.put(Property.COL_VILLAGE_ID, prop.getVillageId());
            values.put(Property.COL_FEATURE_TYPE, prop.getFeatureType());
            values.put(Property.COL_FEATURE_DESCRIPTION, prop.getFeatureDescription());

            getDb().update(Feature.TABLE_NAME, values, Property.COL_ID + " = " + prop.getId(), null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns list of generic reference data types.
     */
    private <T extends RefData> List<T> getRefDataTypes(Class<T> classType, boolean addDummy) {
        Cursor cursor = null;
        List<T> types = new ArrayList<T>();

        try {
            T type = (T) classType.newInstance();
            if (type.getTableName()=="RELATIONSHIP_TYPE")
            {
                cursor = getDb().rawQuery("SELECT * FROM " + type.getTableName() + " where ACTIVE=1 ORDER BY Code", null);
            }
            else {
                cursor = getDb().rawQuery("SELECT * FROM " + type.getTableName() + " where ACTIVE=1 ORDER BY " + RefData.COL_NAME, null);
            }

            //cursor = getDb().rawQuery("SELECT * FROM " + type.getTableName() + " where ACTIVE=1 ORDER BY " + RefData.COL_NAME, null);
            if (cursor.moveToFirst()) {
                int indxCode = cursor.getColumnIndex(RefData.COL_CODE);
                int indxName = cursor.getColumnIndex(RefData.COL_NAME);
                int indxNameOtherLang = cursor.getColumnIndex(RefData.COL_NAME_OTHER_LANG);
                int indxActive = cursor.getColumnIndex(RefData.COL_ACTIVE);

                do {
                    type = (T) classType.newInstance();
                    type.setCode(cursor.getInt(indxCode));
                    type.setName(cursor.getString(indxName));
                    type.setNameOtherLang(cursor.getString(indxNameOtherLang));
                    type.setActive(cursor.getInt(indxActive));
                    types.add(type);
                } while (cursor.moveToNext());

                if (addDummy) {
                    type = (T) classType.newInstance();
                    type.setCode(0);
                    type.setName(contxt.getResources().getString(R.string.SelectOption));
                    type.setNameOtherLang(contxt.getResources().getString(R.string.SelectOption));
                    type.setActive(1);
                    types.add(0, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return types;
        } finally {
            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return types;
    }

    private <T extends RefData> List<T> getRefShareDataTypes(Class<T> classType, boolean addDummy) {
        Cursor cursor = null;
        List<T> types = new ArrayList<T>();

        try {
            T type = (T) classType.newInstance();
            cursor = getDb().rawQuery("SELECT * FROM " + type.getTableName() + " WHERE ACTIVE=1 ORDER BY " + RefData.COL_CODE, null);
            if (cursor.moveToFirst()) {
                int indxCode = cursor.getColumnIndex(RefData.COL_CODE);
                int indxName = cursor.getColumnIndex(RefData.COL_NAME);
                int indxNameOtherLang = cursor.getColumnIndex(RefData.COL_NAME_OTHER_LANG);
                int indxActive = cursor.getColumnIndex(RefData.COL_ACTIVE);

                do {
                    type = (T) classType.newInstance();
                    type.setCode(cursor.getInt(indxCode));
                    type.setName(cursor.getString(indxName));
                    type.setNameOtherLang(cursor.getString(indxNameOtherLang));
                    type.setActive(cursor.getInt(indxActive));
                    types.add(type);
                } while (cursor.moveToNext());

                if (addDummy) {
                    type = (T) classType.newInstance();
                    type.setCode(0);
                    type.setName(contxt.getResources().getString(R.string.SelectOption));
                    type.setNameOtherLang(contxt.getResources().getString(R.string.SelectOption));
                    type.setActive(1);
                    types.add(0, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return types;
        } finally {
            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return types;
    }


    //ambar

    public boolean saveAttributesListValues(List<Attribute> attributes) {
        if (attributes == null || attributes.size() < 1) {
            return true;
        }

        // Get group ID from the first element. It's supposed that all elemets have the same group ID.
        // Long groupId = attributes.get(0).getGroupId();


        try {


            for (Attribute attribute : attributes) {
                if (attribute.getValue() != null) {
                    ContentValues row = new ContentValues();
                    row.put(Attribute.COL_VALUE_GROUP_ID, 5);
                    row.put(Attribute.COL_VALUE_ATTRIBUTE_ID, attribute.getId());
                    row.put(Attribute.COL_VALUE_VALUE, attribute.getValue());
                    row.put(Attribute.COL_VALUE_FEATURE_ID, attribute.getFeatureId());
                    getDb().insert(Attribute.TABLE_ATTRIBUTE_VALUE_NAME, null, row);
                }
            }
            return true;
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
            return false;

        }

    }


//     try {
//        cursor = getDb().rawQuery(sql, null);
//        if (cursor.moveToFirst()) {
//            int indxCode = cursor.getColumnIndex(RefData.COL_CODE);
//            int indxName = cursor.getColumnIndex(RefData.COL_NAME);
//            int indxNameOtherLang = cursor.getColumnIndex(RefData.COL_NAME_OTHER_LANG);
//
//            do {
//                ClaimType claimType = new ClaimType();
//                claimType.setCode(cursor.getString(indxCode));
//                // claimType.setCode(cursor.getString(indxCode));
//                claimType.setName(cursor.getString(indxName));
//                claimType.setNameOtherLang(cursor.getString(indxNameOtherLang));
//                claimTypes.add(claimType);
//            } while (cursor.moveToNext());
//
//            if (addDummy) {
//                ClaimType claimType = new ClaimType();
//                claimType.setCode("");
//                claimType.setName(contxt.getResources().getString(R.string.SelectOption));
//                claimType.setNameOtherLang(contxt.getResources().getString(R.string.SelectOption));
//                claimTypes.add(0, claimType);
//            }
//        }
//

    private <T extends ResourceAttribute> List<T> getResValue(Class<T> classType, boolean addDummy) {
        Cursor cursor = null;
        List<T> types = new ArrayList<T>();

        try {
            T type = (T) classType.newInstance();
            cursor = getDb().rawQuery("SELECT * FROM " + type.getTableName() + " ORDER BY " + ResourceAttribute.COL_ATTRIBVALUE, null);
            if (cursor.moveToFirst()) {
                int indxCode = cursor.getColumnIndex(ResourceAttribute.COL_ATTRIBID);
                int indxName = cursor.getColumnIndex(ResourceAttribute.COL_ATTRIBVALUE);
//                int indxNameOtherLang = cursor.getColumnIndex(RefData.COL_NAME_OTHER_LANG);
//                int indxActive = cursor.getColumnIndex(RefData.COL_ACTIVE);

                do {
                    type = (T) classType.newInstance();
                    type.setAttribID(cursor.getString(indxCode));
                    type.setAttribValue(cursor.getString(indxName));
//                    type.setNameOtherLang(cursor.getString(indxNameOtherLang));
//                    type.setActive(cursor.getInt(indxActive));
                    types.add(type);
                } while (cursor.moveToNext());

                if (addDummy) {
                    type = (T) classType.newInstance();
                    type.setAttribID("");
                    type.setAttribValue(contxt.getResources().getString(R.string.SelectOption));
//                    type.setNameOtherLang(contxt.getResources().getString(R.string.SelectOption));
//                    type.setActive(1);
                    types.add(0, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return types;
        } finally {
            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return types;
    }

    private <T extends ResourceAttribute> List<T> getResValue(Class<T> classType, boolean addDummy, String classificationID) {
        Cursor cursor = null;
        List<T> types = new ArrayList<T>();

//        properties = createPropertyList("SELECT * FROM " + Property.TABLE_NAME +
//                " WHERE " + Property.COL_STATUS + " = '" + Property.CLIENT_STATUS_COMPLETE + "' AND (" +
//                Property.COL_SERVER_ID + " IS NULL OR " + Property.COL_SERVER_ID + " = '')"+ " AND (" +
//                " FLAG "+ " = '"+ParamType+"')");

        try {
            T type = (T) classType.newInstance();
            cursor = getDb().rawQuery("SELECT * FROM " + type.getTableName() + " WHERE ClassificationID " + "=" + classificationID, null);
            if (cursor.moveToFirst()) {
                int indxCode = cursor.getColumnIndex(ResourceAttribute.COL_ATTRIBID);
                int indxName = cursor.getColumnIndex(ResourceAttribute.COL_ATTRIBVALUE);
//                int indxNameOtherLang = cursor.getColumnIndex(RefData.COL_NAME_OTHER_LANG);
//                int indxActive = cursor.getColumnIndex(RefData.COL_ACTIVE);

                do {
                    type = (T) classType.newInstance();
                    type.setAttribID(cursor.getString(indxCode));
                    type.setAttribValue(cursor.getString(indxName));
//                    type.setNameOtherLang(cursor.getString(indxNameOtherLang));
//                    type.setActive(cursor.getInt(indxActive));
                    types.add(type);
                } while (cursor.moveToNext());

                if (addDummy) {
                    type = (T) classType.newInstance();
                    type.setAttribID("0");
                    type.setAttribValue(contxt.getResources().getString(R.string.SelectOption));
//                    type.setNameOtherLang(contxt.getResources().getString(R.string.SelectOption));
//                    type.setActive(1);
                    types.add(0, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return types;
        } finally {
            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return types;
    }

//    private <T extends ResourceAttribute> List<T> getTenureValue(Class<T> classType, boolean addDummy) {
//        Cursor cursor = null;
//        List<T> types = new ArrayList<T>();
//
//
//        try {
//
//            T type = (T) classType.newInstance();
//            String selectQueryOptions = "SELECT ATTRIBUTE_TYPE,ATTRIBUTE_TYPE_NAME FROM" + " " + type.getTableName() + " WHERE FLAG " + "=" + "'Resource'" + " GROUP BY " + Attribute.COL_TYPE + "," + Attribute.COL_TYPE_NAME;
//            //String select = "SELECT * FROM " + type.getTableName() +"WHERE "+type.getTableName().FLAG+"="+ res + " GROUP BY " + Attribute.COL_TYPE_NAME;
//            //cursor = getDb().rawQuery("SELECT * FROM " + type.getTableName() + " GROUP BY " + Attribute.COL_TYPE_NAME, null);
//            cursor = getDb().rawQuery(selectQueryOptions, null);
//            if (cursor.moveToFirst()) {
//
//
//                do {
//                    type = (T) classType.newInstance();
//                    type.setAttribID(cursor.getString(0));
//                    type.setAttribValue(cursor.getString(1));
////                    type.setNameOtherLang(cursor.getString(indxNameOtherLang));
////                    type.setActive(cursor.getInt(indxActive));
//                    types.add(type);
//                } while (cursor.moveToNext());
//
//                if (addDummy) {
//                    type = (T) classType.newInstance();
//                    type.setAttribID("0");
//                    type.setAttribValue(contxt.getResources().getString(R.string.SelectOption));
////                    type.setNameOtherLang(contxt.getResources().getString(R.string.SelectOption));
////                    type.setActive(1);
//                    types.add(0, type);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return types;
//        } finally {
//            try {
//                if (cursor != null)
//                    cursor.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return types;
//    }

    private <T extends ResourceAttribute> List<T> getTenureValue(Class<T> classType, boolean addDummy) {
        Cursor cursor = null;
        List<T> types = new ArrayList<T>();


        try {

            T type = (T) classType.newInstance();
            String selectQueryOptions = "SELECT * FROM" + " " + type.getTableName() + " WHERE FLAG " + "=" + "'Resource'" +" ORDER BY LISTING";
            //String select = "SELECT * FROM " + type.getTableName() +"WHERE "+type.getTableName().FLAG+"="+ res + " GROUP BY " + Attribute.COL_TYPE_NAME;
            //cursor = getDb().rawQuery("SELECT * FROM " + type.getTableName() + " GROUP BY " + Attribute.COL_TYPE_NAME, null);
            cursor = getDb().rawQuery(selectQueryOptions, null);
            if (cursor.moveToFirst()) {


                do {
                    type = (T) classType.newInstance();
                    type.setAttribID(cursor.getString(0));
                    type.setAttribValue(cursor.getString(1));
//                    type.setNameOtherLang(cursor.getString(indxNameOtherLang));
//                    type.setActive(cursor.getInt(indxActive));
                    types.add(type);
                } while (cursor.moveToNext());

                if (addDummy) {
                    type = (T) classType.newInstance();
                    type.setAttribID("0");
                    type.setAttribValue(contxt.getResources().getString(R.string.SelectOption));
//                    type.setNameOtherLang(contxt.getResources().getString(R.string.SelectOption));
//                    type.setActive(1);
                    types.add(0, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return types;
        } finally {
            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return types;
    }


    /**
     * Returns reference data type by code.
     */
    private <T extends RefData> T getRefDataType(Class<T> classType, int code) {
        try {
            T typeTmp = (T) classType.newInstance();
            return getRefDataType(classType, "SELECT * FROM " + typeTmp.getTableName() + " WHERE CODE=" + code);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns reference data type by code.
     */
    private <T extends RefData> T getRefDataType(Class<T> classType, String sql) {
        Cursor cursor = null;
        T type = null;

        try {
            T typeTmp = (T) classType.newInstance();
            cursor = getDb().rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                int indxCode = cursor.getColumnIndex(RefData.COL_CODE);
                int indxName = cursor.getColumnIndex(RefData.COL_NAME);
                int indxNameOtherLang = cursor.getColumnIndex(RefData.COL_NAME_OTHER_LANG);
                int indxActive = cursor.getColumnIndex(RefData.COL_ACTIVE);

                type = (T) classType.newInstance();
                type.setCode(cursor.getInt(indxCode));
                type.setName(cursor.getString(indxName));
                type.setNameOtherLang(cursor.getString(indxNameOtherLang));
                type.setActive(cursor.getInt(indxActive));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return type;
        } finally {
            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return type;
    }

    private List<ClaimType> createClaimTypesList(String sql, boolean addDummy) {
        Cursor cursor = null;
        List<ClaimType> claimTypes = new ArrayList<ClaimType>();

        try {
            cursor = getDb().rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                int indxCode = cursor.getColumnIndex(RefData.COL_CODE);
                int indxName = cursor.getColumnIndex(RefData.COL_NAME);
                int indxNameOtherLang = cursor.getColumnIndex(RefData.COL_NAME_OTHER_LANG);

                do {
                    ClaimType claimType = new ClaimType();
                    claimType.setCode(cursor.getString(indxCode));
                    // claimType.setCode(cursor.getString(indxCode));
                    claimType.setName(cursor.getString(indxName));
                    claimType.setNameOtherLang(cursor.getString(indxNameOtherLang));
                    claimTypes.add(claimType);
                } while (cursor.moveToNext());

                if (addDummy) {
                    ClaimType claimType = new ClaimType();
                    claimType.setCode("");
                    claimType.setName(contxt.getResources().getString(R.string.SelectOption));
                    claimType.setNameOtherLang(contxt.getResources().getString(R.string.SelectOption));
                    claimTypes.add(0, claimType);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return claimTypes;
        } finally {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return claimTypes;
    }

    /**
     * Returns list of claim types.
     */
    public List<ClaimType> getClaimTypes(boolean addDummy) {
        return createClaimTypesList("SELECT * FROM " + ClaimType.TABLE_NAME +" WHERE ACTIVE = 1 ORDER BY CODE", addDummy);
    }

    /**
     * Returns claim type by code.
     */
    public ClaimType getClaimType(String code) {
        List<ClaimType> claimTypes = createClaimTypesList("SELECT * FROM " + ClaimType.TABLE_NAME +
                " WHERE CODE='" + code + "'", false);
        if (claimTypes != null && claimTypes.size() > 0)
            return claimTypes.get(0);
        else
            return null;
    }

    /**
     * Returns claim type of the property.
     */
    public ClaimType getPropClaimType(Long propId) {
        List<ClaimType> claimTypes = createClaimTypesList("SELECT ct.* FROM " + ClaimType.TABLE_NAME +
                " ct INNER JOIN " + Feature.TABLE_NAME +
                " f ON ct." + RefData.COL_CODE + " = f." + Property.COL_CLAIM_TYPE_CODE +
                " WHERE f." + Property.COL_ID + "=" + propId, false);
        if (claimTypes != null && claimTypes.size() > 0)
            return claimTypes.get(0);
        else
            return null;
    }

    private List<RightType> createRightTypesList(String sql, boolean addDummy) {
        Cursor cursor = null;
        List<RightType> rightTypes = new ArrayList<RightType>();

        try {
            cursor = getDb().rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                int indxCode = cursor.getColumnIndex(RefData.COL_CODE);
                int indxName = cursor.getColumnIndex(RefData.COL_NAME);
                int indxNameOtherLang = cursor.getColumnIndex(RefData.COL_NAME_OTHER_LANG);
                int indxActive = cursor.getColumnIndex(RefData.COL_ACTIVE);
//                int indxForAdjudication = cursor.getColumnIndex(RightType.COL_FOR_ADJUDICATION);

                do {
                    RightType rightType = new RightType();
                    rightType.setCode(cursor.getInt(indxCode));
                    rightType.setName(cursor.getString(indxName));
                    rightType.setNameOtherLang(cursor.getString(indxNameOtherLang));
                    rightType.setActive(cursor.getInt(indxActive));
                    // rightType.setForAdjudication(cursor.getInt(indxForAdjudication));
                    rightTypes.add(rightType);
                } while (cursor.moveToNext());

                if (addDummy) {
                    RightType rightType = new RightType();
                    rightType.setCode(0);
                    rightType.setName(contxt.getResources().getString(R.string.SelectOption));
                    rightType.setActive(1);
                    rightType.setForAdjudication(1);
                    rightTypes.add(0, rightType);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return rightTypes;
        } finally {
            try {
                if (cursor != null)
                    cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rightTypes;
    }

    /**
     * Returns list of right types.
     */
    public List<RightType> getRightTypes(boolean addDummy) {
        return createRightTypesList("SELECT * FROM " + RightType.TABLE_NAME + " WHERE " +
                RightType.COL_ACTIVE + " = 1 ORDER BY " + RightType.COL_NAME, addDummy);
    }

    /**
     * Returns list of right types relevant for claim type.
     */
    public List<RightType> getRightTypesByClaimType(String claimTypeCode, boolean addDummy) {
        if (!StringUtility.isEmpty(claimTypeCode) && claimTypeCode.equalsIgnoreCase(ClaimType.TYPE_EXISTING_CLAIM))
            return createRightTypesList("SELECT * FROM " + RightType.TABLE_NAME + " WHERE " +
                    RightType.COL_ACTIVE + " = 1 ORDER BY " + RightType.COL_NAME, addDummy);
        else
            return createRightTypesList("SELECT * FROM " + RightType.TABLE_NAME +
                    " WHERE " + RightType.COL_FOR_ADJUDICATION + " = 1 AND " +
                    RightType.COL_ACTIVE + " = 1 ORDER BY " + RightType.COL_NAME, addDummy);
    }

    /**
     * Returns right type by code.
     */
    public RightType getRightType(int code) {
        List<RightType> rightTypes = createRightTypesList("SELECT * FROM CLAIM_TYPE WHERE CODE='" + code + "'", false);
        if (rightTypes != null && rightTypes.size() > 0) {
            return rightTypes.get(0);
        } else {
            return null;
        }
    }

    /**
     * Returns list of share types
     */
    public List<ShareType> getShareTypes(boolean addDummy) {
        return getRefShareDataTypes(ShareType.class, addDummy);
        //return getRefDataTypes(ShareType.class, addDummy);
    }

    /**
     * Returns share type by code
     */
    public ShareType getShareType(int code) {
        return getRefDataType(ShareType.class, code);
    }

    /**
     * Returns share type by right id
     */
    public ShareType getShareTypeByRight(Long rightId) {
        return getRefDataType(ShareType.class,
                "SELECT st.* FROM " + ShareType.TABLE_NAME +
                        " st INNER JOIN " + Right.TABLE_NAME +
                        " r ON st." + RefData.COL_CODE + " = r." + Right.COL_SHARE_TYPE_ID +
                        " WHERE r." + Right.COL_ID + "=" + rightId
        );
    }

    /**
     * Returns list of relationship types
     */
    public List<RelationshipType> getRelationshipTypes(boolean addDummy) {
        return getRefDataTypes(RelationshipType.class, addDummy);
    }

    /**
     * Returns relationship type by code
     */
    public RelationshipType getRelationshipType(int code) {
        return getRefDataType(RelationshipType.class, code);
    }

    /**
     * Returns list of genders
     */
    public List<Gender> getGenders(boolean addDummy) {
        return getRefDataTypes(Gender.class, addDummy);
    }

    //ambar  getClassification for res
    public List<ClassificationAttribute> getClassification(boolean addDummy) {
        return getResValue(ClassificationAttribute.class, addDummy);
    }

    public List<SubClassificationAttribute> getSubClassification(boolean addDummy, String classificationId) {
        return getResValue(SubClassificationAttribute.class, addDummy, classificationId);
    }


//    public List<OptionAttributes> gettenureType(boolean addDummy) {
//        return getTenureValue(OptionAttributes.class, addDummy);
//    }

    public List<TenureType> gettenureType(boolean addDummy) {
        return getTenureValue(TenureType.class, addDummy);
    }


    /**
     * Returns list of dispute types
     */
    public List<DisputeType> getDisputeTypes(boolean addDummy) {
        return getRefDataTypes(DisputeType.class, addDummy);
    }

    /**
     * Returns dispute type by code
     */
    public DisputeType getDisputeType(int code) {
        return getRefDataType(DisputeType.class, code);
    }


    /**
     * Returns list of acquisition types
     */
    public List<AcquisitionType> getAcquisitionTypes(boolean addDummy) {
        return getRefDataTypes(AcquisitionType.class, addDummy);
    }

    /**
     * Returns list of villages.
     */
    public List<Village> getVillages(boolean addDummy) {
        Cursor cursor = null;
        List<Village> villages = new ArrayList<Village>();

        try {
            cursor = getDb().rawQuery("SELECT * FROM " + Village.TABLE_NAME, null);
            if (cursor.moveToFirst()) {
                int indxId = cursor.getColumnIndex(Village.COL_ID);
                int indxName = cursor.getColumnIndex(Village.COL_NAME);
                int indxNameEn = cursor.getColumnIndex(Village.COL_NAME_EN);

                do {
                    Village village = new Village();
                    village.setId(cursor.getInt(indxId));
                    village.setName(cursor.getString(indxName));
                    village.setNameEn(cursor.getString(indxNameEn));
                    villages.add(village);
                } while (cursor.moveToNext());

                if (addDummy) {
                    Village village = new Village();
                    village.setId(0);
                    village.setName(contxt.getResources().getString(R.string.SelectOption));
                    village.setNameEn(contxt.getResources().getString(R.string.SelectOption));
                    villages.add(0, village);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return villages;
        } finally {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return villages;
    }

    /**
     * Returns gender type by code
     */
    public Gender getGender(int code) {
        return getRefDataType(Gender.class, code);
    }

    public String villageName() {
        String villageName = "";

        String selectSQL = "SELECT VILLAGE_NAME FROM PROJECT_SPATIAL_DATA ";
        Cursor cursor = getDb().rawQuery(selectSQL, null);

        if (cursor.moveToFirst()) {
            villageName = cursor.getString(0);
        }
        cursor.close();
        return villageName;
    }


//    public boolean insertClassification(List<String> classification) {
//
//        try {
//
//            for (int i = 0; i < classification.size(); i++) {
//
//                ContentValues values = new ContentValues();
//                values.put(ResourceAttribute.COL_ATTRIBVALUE, classification.get(i));
//               // values.put(ResourceAttribute.COL_ATTRIBVALUE, poly);
//                getDb().insert(Classification.TABLE_NAME,null, values);
//            }
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public boolean insertSubClassification(List<String> classification) {
//
//        try {
//
//            for (int i = 0; i < classification.size(); i++) {
//
//                ContentValues values = new ContentValues();
//                values.put(ResourceAttribute.COL_ATTRIBVALUE, classification.get(i));
//
//                getDb().insert("SUB_CLASSIFICATION",null, values);
//            }
//
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }

//    public List<Classification> getClassification(boolean addDummy) {
//        List<Classification> optionList = new ArrayList<Classification>();
//        //Classification classification=new Classification();
//
//        String selectQueryOptions = "SELECT * from CLASSIFICATION";
//        Cursor cursor = getDb().rawQuery(selectQueryOptions, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                Classification classification=new Classification();
//                classification.setClassificationID(cursor.getString(0));
//                classification.setClassificationName(cursor.getString(2));
//
//                optionList.add(classification);
//
//            } while (cursor.moveToNext());
//
////            if (addDummy) {
////                Classification classification1=new Classification();
////                classification1.setClassificationID("10");
////                classification1.setClassificationName(contxt.getResources().getString(R.string.select));
////                optionList.add(0,classification1);
////            }
//        }
//
//
//        cursor.close();
//        return optionList;
//
//    }

//    public List<Classification> getClassification(boolean addDummy) {
//        Cursor cursor = null;
//        List<Classification> claimTypes = new ArrayList<Classification>();
//        String selectQueryOptions = "SELECT * from CLASSIFICATION";
//        try {
//            cursor = getDb().rawQuery(selectQueryOptions, null);
//            if (cursor.moveToFirst()) {
//                int indxCode = cursor.getColumnIndex(Classification.COL_CLASSIFICATION_ID);
//                int indxName = cursor.getColumnIndex(Classification.COL_CLASSIFICATION_NAME);
//                int indxNameOtherLang = cursor.getColumnIndex(Classification.COL_PLOYTYPE);
//
//                do {
//                    Classification claimType=new Classification();
//                    claimType.setClassificationID(cursor.getString(indxCode));
//                    // claimType.setCode(cursor.getString(indxCode));
//                    claimType.setClassificationName(cursor.getString(indxName));
//                    claimType.setPolyName(cursor.getString(indxNameOtherLang));
//                    claimTypes.add(claimType);
//                } while (cursor.moveToNext());
//
//                if (addDummy) {
//                    Classification claimType=new Classification();
//                    claimType.setClassificationID("");
//                    claimType.setClassificationName(contxt.getResources().getString(R.string.SelectOption));
//                   claimType.setPolyName(contxt.getResources().getString(R.string.SelectOption));
//                    claimTypes.add(0, claimType);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return claimTypes;
//        } finally {
//            try {
//                cursor.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return claimTypes;
//    }


//    public List<SubClassification> getSubClassification(boolean addDummy) {
//        List<SubClassification> optionList = new ArrayList<SubClassification>();
//
//
//
//        //optionList.add(contxt.getResources().getString(R.string.select));
//
//        String selectQueryOptions = "SELECT * from SUB_CLASSIFICATION";
//        Cursor cursor = getDb().rawQuery(selectQueryOptions, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                SubClassification classification=new SubClassification();
//                classification.setSubclassificationId(cursor.getString(0));
//                classification.setSubClassi(cursor.getString(1));
//
//                optionList.add(classification);
//
//            } while (cursor.moveToNext());
//            if (addDummy) {
//                SubClassification classification=new SubClassification();
//                classification.setSubclassificationId("");
//                classification.setSubClassi(contxt.getResources().getString(R.string.select));
//                optionList.add(0,classification);
//            }
//        }
//        cursor.close();
//        return optionList;
//    }

    //"CREATE TABLE RESOURCE_BASISC_ATTRIBUTES(ID INTEGER PRIMARY KEY AUTOINCREMENT,c TEXT,SUBCLASSIFICATION TEXT,TENURE_TYPE TEXT,FIRST_NAME TEXT,MIDDLE_NAME TEXT,LAST_NAME TEXT)";
    public boolean insertResourceAtrr(List<Property> classification, Long featureId) {
        try {

            String whereGroupId = "FEATURE_ID" + "=" + featureId;
            getDb().delete("RESOURCE_BASISC_ATTRIBUTES", whereGroupId, null);

            for (Property attribute1 : classification) {
                List<ClassificationAttribute> classificationAttributes = new ArrayList<>();
                classificationAttributes = attribute1.getClassificationAttributes();

                for (ClassificationAttribute attribute : classificationAttributes) {


                    if (attribute.getAttribValue().equalsIgnoreCase(contxt.getResources().getString(R.string.SelectOption))) {
                    } else {

                        ContentValues values = new ContentValues();
                        values.put("FEATURE_ID", featureId);
                        values.put("VALUE", attribute.getAttribValue());
                        values.put("ID", attribute.getAttribID());
                        getDb().insert("RESOURCE_BASISC_ATTRIBUTES", null, values);
                    }
                }

            }



//            for (int i=0;i<classification.size();i++) {
//
//
////            values.put("FIRST_NAME", classification.getfirstName());
////            values.put("MIDDLE_NAME", classification.getMiddelName());
////            values.put("LAST_NAME", classification.getLastName());
//
//                getDb().insert("RESOURCE_BASISC_ATTRIBUTES", null, values);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean insertResourceAtrrValue(ClassificationAttribute classification, Long featureId) {
        try {

//            String whereGroupId = "FEATURE_ID" + "=" + featureId;
//            getDb().delete("RESOURCE_BASISC_ATTRIBUTES", whereGroupId, null);



                    if (classification.getAttribValue().equalsIgnoreCase(contxt.getResources().getString(R.string.SelectOption))) {
                    } else {

                        ContentValues values = new ContentValues();
                        values.put("FEATURE_ID", featureId);
                        values.put("VALUE", classification.getAttribValue());
                        values.put("ID", classification.getAttribID());
                        getDb().insert("RESOURCE_BASISC_ATTRIBUTES", null, values);
                    }




            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public List<Option> getMaritalStatus(int a, boolean addDummy) {

//

        int indxOptionId = -1;
        int indxOptionName = -1;
        int indxOptionNameOtherLang = -1;
        List<Option> optionList = new ArrayList<Option>();
        Option option = new Option();
//        option.setId(0L);
//        option.setName(contxt.getResources().getString(R.string.select));
//        optionList.add(option);
        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + a;

        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

        if (cur2.moveToFirst()) {
            if (indxOptionId < 0) {

                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
            }

            do {
                option = new Option();
                option.setName(cur2.getString(indxOptionName));


                optionList.add(option);
            } while (cur2.moveToNext());
            if (addDummy) {
                option = new Option();
                option.setId(0L);
                option.setName(contxt.getResources().getString(R.string.SelectOption));
                option.setNameOtherLang(contxt.getResources().getString(R.string.SelectOption));
                optionList.add(0, option);
            }
        }
        cur2.close();

        return optionList;
    }


    public List<Option> getGenderStatus(int a, boolean addDummy) {

        int indxOptionId = -1;
        int indxOptionName = -1;
        int indxOptionNameOtherLang = -1;
        List<Option> optionList = new ArrayList<Option>();
        Option option = new Option();
//        option.setId(0L);
//        option.setName(contxt.getResources().getString(R.string.select));
//        optionList.add(option);
        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + a;

        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

        if (cur2.moveToFirst()) {
            if (indxOptionId < 0) {

                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
            }

            do {
                option = new Option();
                option.setName(cur2.getString(indxOptionName));


                optionList.add(option);
            } while (cur2.moveToNext());
            if (addDummy) {
                option = new Option();
                option.setId(0L);
                option.setName(contxt.getResources().getString(R.string.SelectOption));
                option.setNameOtherLang(contxt.getResources().getString(R.string.SelectOption));
                optionList.add(0, option);
            }
        }
        cur2.close();

        return optionList;
    }

    public List<Option> tenureType(int a, boolean addDummy) {

        int indxOptionId = -1;
        int indxOptionName = -1;
        int indexID = -1;
        int indxOptionNameOtherLang = -1;
        List<Option> optionList = new ArrayList<Option>();
        Option option = new Option();
//        option.setId(0L);
//        option.setName(contxt.getResources().getString(R.string.select));
//        optionList.add(option);
        String selectQueryOptions = "SELECT  * FROM " + Option.TABLE_NAME +
                " WHERE " + Option.COL_ATTRIBUTE_ID + "=" + a;

        Cursor cur2 = getDb().rawQuery(selectQueryOptions, null);

        if (cur2.moveToFirst()) {
            if (indxOptionId < 0) {

                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                indexID = cur2.getColumnIndex(Option.COL_ATTRIBUTE_ID);

            }

            do {
                option = new Option();
                option.setName(cur2.getString(indxOptionName));
                option.setId(cur2.getLong(indexID));


                optionList.add(option);
            } while (cur2.moveToNext());
            if (addDummy) {
                option = new Option();
                option.setId(0L);
                option.setName(contxt.getResources().getString(R.string.SelectOption));
                option.setNameOtherLang(contxt.getResources().getString(R.string.SelectOption));
                optionList.add(0, option);
            }
        }
        cur2.close();

        return optionList;
    }

    //"CREATE TABLE Tenure_Information(ID INTEGER PRIMARY KEY AUTOINCREMENT,FIRST_NAME TEXT,MIDDLE_NAME TEXT,LAST_NAME TEXT,GENDER TEXT,AGE INTEGER,MARITAL_STATUS TEXT,CITISHIP TEXT,ETHINICITY TEXT,RESIDENTIAL TEXT,ADRRESS_STREET TEXT,COMMUNITY TEXT,REGION TEXT,COUNTRY TEXT,MOBILE_NO TEXT)";
    public boolean insertTenureResourceAtrr(Property tenureInformation, Long featureID) {
        try {
            ContentValues values = new ContentValues();
            values.put("FEATURE_ID", featureID);
            values.put("FIRST_NAME", tenureInformation.getFirstName());
            values.put("MIDDLE_NAME", tenureInformation.getMiddelName());
            values.put("LAST_NAME", tenureInformation.getLastName());
            values.put("GENDER", tenureInformation.getGender());
            values.put("AGE", tenureInformation.getAge());
            values.put("MARITAL_STATUS", tenureInformation.getMaritalstatus());

            values.put("CITISHIP", tenureInformation.getCityzenship());
            values.put("ETHINICITY", tenureInformation.getEthinicity());
            values.put("RESIDENTIAL", tenureInformation.getResidential());

            values.put("ADRRESS_STREET", tenureInformation.getAddress());
            values.put("COMMUNITY", tenureInformation.getCommunity());
            values.put("REGION", tenureInformation.getRegion());

            values.put("COUNTRY", tenureInformation.getCountry());
            values.put("MOBILE_NO", tenureInformation.getMob_no());


            getDb().insert("Tenure_Information", null, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Classification> getResBasicAttr(long featureID) {

        List<Classification> classificationList = new ArrayList<Classification>();
        String selectQueryQues = "SELECT VALUE from RESOURCE_BASISC_ATTRIBUTES where FEATURE_ID =" + featureID;
        Cursor cursor = getDb().rawQuery(selectQueryQues, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    Classification classification = new Classification();
                    classification.setClassificationName(cursor.getString(0));
//                    classification.setSubClassi(cursor.getString(2));
//                    classification.setTenureType(cursor.getString(3));
//
                    classificationList.add(classification);
                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();
        return classificationList;
    }

    public List<String> getResBasic(long featureID) {

        List<String> classificationList = new ArrayList<String>();
        String selectQueryQues = "SELECT VALUE from RESOURCE_BASISC_ATTRIBUTES where FEATURE_ID =" + featureID;
        Cursor cursor = getDb().rawQuery(selectQueryQues, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    //Classification classification = new Classification();
                    String abc = cursor.getString(0);
                    // classification.setClassificationName(cursor.getString(0));
//                    classification.setSubClassi(cursor.getString(2));
//                    classification.setTenureType(cursor.getString(3));
//
                    classificationList.add(abc);
                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();
        return classificationList;
    }

    public List<ClassificationAttribute> getAttriSYnData(long featureID) {

        List<ClassificationAttribute> classificationList = new ArrayList<ClassificationAttribute>();
        String selectQueryQues = "SELECT * from RESOURCE_BASISC_ATTRIBUTES where FEATURE_ID =" + featureID;
        Cursor cursor = getDb().rawQuery(selectQueryQues, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    ClassificationAttribute classification = new ClassificationAttribute();
//                    classification.setClassificationName(cursor.getString(1));
                    classification.setAttribValue(cursor.getString(1));
                    classification.setAttribID(cursor.getString(2));
//
                    classificationList.add(classification);
                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();
        return classificationList;
    }


    public String getGeoTypeFromSpatial(Long featureId) {
        String geoType = null;
        Cursor cursor = null;
        String q = "SELECT * FROM SPATIAL_FEATURES where FEATURE_ID=" + featureId;
        try {
            cursor = getDb().rawQuery(q, null);
            if (cursor.moveToFirst()) {


                int indxGeomType = cursor.getColumnIndex(Feature.COL_GEOM_TYPE);


                do {
                    geoType = cursor.getString(indxGeomType);

                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cursor.close();
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        return geoType;


    }

    public String getGeoCordinates(Long featureId) {

        String geoType = null;
        Cursor cursor = null;
        String q = "SELECT * FROM SPATIAL_FEATURES where FEATURE_ID =" + featureId;
        try {
            cursor = getDb().rawQuery(q, null);
            if (cursor.moveToFirst()) {


                int indxGeomType = cursor.getColumnIndex(Feature.COL_COORDINATES);


                do {
                    geoType = cursor.getString(indxGeomType);

                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cursor.close();
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        return geoType;

    }

    public boolean updatePropertyBasic(Property prop) {
        try {
            ContentValues values = new ContentValues();
            values.put(Property.COL_CLASSIFICATION_ID, prop.getClassificationId());
            values.put(Property.COL_SUBCLASSIFICATION_ID, prop.getSubClassificationId());
            values.put(Property.COL_TENURE_ID, prop.getTenureTypeID());
            getDb().update(Feature.TABLE_NAME, values, Property.COL_ID + " = " + prop.getId(), null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateTenureBasic(Property prop, Long featureId) {
        try {
            ContentValues values = new ContentValues();

            values.put("CID", prop.getClassificationValue());
            values.put("SID", prop.getSubClassificationValue());
            values.put("TID", prop.getTenureTypeValue());
            getDb().update("Tenure_Information", values, "FEATURE_ID" + " = " + prop.getId(), null);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertFeature(Long featureId) {
        try {
            ContentValues values = new ContentValues();
            values.put("FEATURE_ID", featureId);

            getDb().insert("Tenure_Information", null, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



//    public List<ProjectSpatialDataDto> getProjectSpatialData() {
//        List<ProjectSpatialDataDto> projectSpatialList = new ArrayList<ProjectSpatialDataDto>();
//        String selectQueryQues = "SELECT * from PROJECT_SPATIAL_DATA order by SERVER_PK";
//        Cursor cursor = getDb().rawQuery(selectQueryQues, null);
//        if (cursor.moveToFirst()) {
//            try {
//                do {
//                    ProjectSpatialDataDto projectSpatialData = new ProjectSpatialDataDto();
//                    projectSpatialData.setServer_Pk(cursor.getInt(0));//Server Pk
//                    projectSpatialData.setProject_Name_Id(cursor.getInt(1));//Project NAme ID
//                    projectSpatialData.setFile_Name(cursor.getString(2));// File_name
////                    projectSpatialData.setFile_Ext(cursor.getString(3));
//                    projectSpatialData.setAlias(cursor.getString(3));//Alias
//                    projectSpatialList.add(projectSpatialData);
//                } while (cursor.moveToNext());
//            } catch (Exception e) {
//                cf.appLog("", e);
//                e.printStackTrace();
//            }
//        }
//        cursor.close();
//        return projectSpatialList;
//    }

    public List<Summary> getResourceTenureInfo(Long featureId) {


        List<Long> longs=new ArrayList<>();
        String sql1 = "SELECT GROUP_ID FROM FORM_VALUES WHERE FEATURE_ID=" +featureId +" AND ATTRIB_VALUE='Primary occupant /Point of contact'";

        Cursor cur1 = null;

        try {
            cur1 = getDb().rawQuery(sql1, null);
            if (cur1.moveToFirst()) {

                do {
                    longs.add(cur1.getLong(0));
                } while (cur1.moveToNext());
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur1.close();
            } catch (Exception ex) {
            }
        }


        String sqloccupant = "SELECT GROUP_ID FROM FORM_VALUES WHERE FEATURE_ID=" +featureId +" AND ATTRIB_VALUE='occupant'";

        Cursor cur1occupant = null;

        try {
            cur1occupant = getDb().rawQuery(sqloccupant, null);
            if (cur1occupant.moveToFirst()) {

                do {
                    longs.add(cur1occupant.getLong(0));

                } while (cur1occupant.moveToNext());
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur1occupant.close();
            } catch (Exception ex) {
            }
        }
        List<Summary> attributes = new ArrayList<Summary>();

        if(longs.size()!=0) {
            for (int i = 0; i < longs.size(); i++) {
                String sql = "SELECT " +
                        "ATTRIBUTE_MASTER." + Attribute.COL_NAME +
                        ", FORM_VALUES." + Attribute.COL_VALUE_VALUE +
                        " FROM " + Attribute.TABLE_NAME +
                        ", " + Attribute.TABLE_ATTRIBUTE_VALUE_NAME +
                        " WHERE " + "FORM_VALUES." + "GROUP_ID" + "=" + longs.get(i) + " AND " +
                        "ATTRIBUTE_MASTER." + Attribute.COL_ID + "=" + "FORM_VALUES." + Attribute.COL_VALUE_ATTRIBUTE_ID;


//        String sql = "SELECT " +
//                "ATTRIBUTE_MASTER." + Attribute.COL_NAME +
//                ", FORM_VALUES." + Attribute.COL_VALUE_VALUE +
//                " FROM " + Attribute.TABLE_NAME +
//                ", " + Attribute.TABLE_ATTRIBUTE_VALUE_NAME +
//                " WHERE " + "FORM_VALUES." +Attribute.COL_VALUE_FEATURE_ID+ "=" + featureId + " AND " +
//                "ATTRIBUTE_MASTER." + Attribute.COL_ID + "=" + "FORM_VALUES." + Attribute.COL_VALUE_ATTRIBUTE_ID;
                Cursor cur = null;


                try {
                    cur = getDb().rawQuery(sql, null);
                    if (cur.moveToFirst()) {
                        String lang = cf.getLocale();


                        do {
                            Summary attribute = new Summary();

                            attribute.setnameLabel(cur.getString(0));
                            attribute.setvalue(cur.getString(1));


                            attributes.add(attribute);

                        } while (cur.moveToNext());
                    }
                } catch (Exception e) {
                    cf.appLog("", e);
                    e.printStackTrace();
                } finally {
                    try {
                        cur.close();
                    } catch (Exception ex) {
                    }
                }
            }
        }
            return attributes;


    }

    public List<Summary> getResourceCustomInfo(Long featureId) {

//        String sql = "SELECT " +
//                "RESOURCE_ATTRIBUTE_MASTER." + ResourceCustomAttribute.COL_NAME +
//                ", RESOURCE_FORM_VALUES." + ResourceCustomAttribute.COL_VALUE_VALUE +
//                " FROM " +ResourceCustomAttribute.TABLE_NAME +
//                ", " + ResourceCustomAttribute.TABLE_ATTRIBUTE_VALUE_NAME +
//                " WHERE " + "RESOURCE_FORM_VALUES." + ResourceCustomAttribute.COL_VALUE_FEATURE_ID + "=" + featureId + " AND " +
//                "RESOURCE_ATTRIBUTE_MASTER." + ResourceCustomAttribute.COL_ID + "=" + "RESOURCE_FORM_VALUES." + ResourceCustomAttribute.COL_VALUE_ATTRIBUTE_ID;

        String sql = "SELECT " +
                "OPTIONS." + Option.COL_NAME +
                ", RESOURCE_FORM_VALUES." + ResourceCustomAttribute.COL_VALUE_VALUE +
                " FROM OPTIONS " +
                ", " + ResourceCustomAttribute.TABLE_ATTRIBUTE_VALUE_NAME +
                " WHERE " + "RESOURCE_FORM_VALUES." + ResourceCustomAttribute.COL_VALUE_FEATURE_ID + "=" + featureId + " AND " +
                "OPTIONS." + ResourceCustomAttribute.COL_VALUE_OPTION_ID + "=" + "RESOURCE_FORM_VALUES." + ResourceCustomAttribute.COL_VALUE_OPTION_ID;
        Cursor cur = null;
        List<Summary> attributes = new ArrayList<Summary>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();



                do {
                    Summary attribute = new Summary();

                    attribute.setnameLabel(cur.getString(0));
                    attribute.setvalue(cur.getString(1));


                    if (!attribute.getnameLabel().equalsIgnoreCase("Plant Date (Primary Crop)") &&
                            !attribute.getnameLabel().equalsIgnoreCase("Duration (Primary Crop, Months)")
                            && !attribute.getnameLabel().equalsIgnoreCase("Plant Date (Secondary Crop)")
                            && !attribute.getnameLabel().equalsIgnoreCase("Duration (Secondary Crop, Months)")
                            && !attribute.getnameLabel().equalsIgnoreCase("Total Expenditures (Farmer, LRD)")
                            && !attribute.getnameLabel().equalsIgnoreCase("Total Sales (Farmer, LRD)") ) {

                        attributes.add(attribute);
                    }

                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
            return attributes;
        }
    }


    public List<ResourceCustomAttribute> getResourceCustomInfoCustom(Long featureId, String SubID) {

//        String sql = "SELECT " +
//                "RESOURCE_ATTRIBUTE_MASTER." + ResourceCustomAttribute.COL_NAME +
//                ", RESOURCE_FORM_VALUES." + ResourceCustomAttribute.COL_VALUE_VALUE +
//                " FROM " +ResourceCustomAttribute.TABLE_NAME +
//                ", " + ResourceCustomAttribute.TABLE_ATTRIBUTE_VALUE_NAME +
//                " WHERE " + "RESOURCE_FORM_VALUES." + ResourceCustomAttribute.COL_VALUE_FEATURE_ID + "=" + featureId + " AND " +
//                "RESOURCE_ATTRIBUTE_MASTER." + ResourceCustomAttribute.COL_ID + "=" + "RESOURCE_FORM_VALUES." + ResourceCustomAttribute.COL_VALUE_ATTRIBUTE_ID;

        String sql = "SELECT " +
                "OPTIONS." + Option.COL_NAME +
                ",OPTIONS.OPTION_ID,OPTIONS.ATTRIB_ID"+
                ", RESOURCE_FORM_VALUES." + ResourceCustomAttribute.COL_VALUE_VALUE +
                " FROM OPTIONS " +
                ", " + ResourceCustomAttribute.TABLE_ATTRIBUTE_VALUE_NAME +
                " WHERE " + "RESOURCE_FORM_VALUES." + ResourceCustomAttribute.COL_VALUE_FEATURE_ID + "=" + featureId + " AND " +
                "OPTIONS." + ResourceCustomAttribute.COL_VALUE_OPTION_ID + "=" + "RESOURCE_FORM_VALUES." + ResourceCustomAttribute.COL_VALUE_OPTION_ID +" AND RESOURCE_FORM_VALUES."+ ResourceCustomAttribute.COL_VALUE_SUBID +"='" + SubID + "'";
        Cursor cur = null;
        List<ResourceCustomAttribute> attributes = new ArrayList<ResourceCustomAttribute>();

        try {
            cur = getDb().rawQuery(sql, null);
            if (cur.moveToFirst()) {
                String lang = cf.getLocale();



                do {
                    ResourceCustomAttribute attribute = new ResourceCustomAttribute();

                    attribute.setName(cur.getString(0));
                    attribute.setValue(cur.getString(3));
                    attribute.setId(cur.getLong(2));
                    attribute.setResID(cur.getLong(1));


                    attributes.add(attribute);

                } while (cur.moveToNext());
            }
        } catch (Exception e) {
            cf.appLog("", e);
            e.printStackTrace();
        } finally {
            try {
                cur.close();
            } catch (Exception ex) {
            }
            return attributes;
        }
    }

    public Property getClassi(long featureID) {

        Property property=new Property();
        String selectQueryQues = "SELECT * from Tenure_Information where FEATURE_ID =" + featureID;
        Cursor cursor = getDb().rawQuery(selectQueryQues, null);
        if (cursor.moveToFirst()) {
            try {
                do {

//
                    property.setClassificationValue(cursor.getString(1));
                    property.setSubClassificationValue(cursor.getString(2));
                    property.setTenureTypeValue(cursor.getString(3));
//

                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();
        return property;
    }

    public String getTenureID(String tenureTypeValue) {
        String tenureID=null;
        String selectQueryOptions = "SELECT ATTRIID FROM TENURE_TYPE"  + " WHERE FLAG " + "=" + "'Resource' AND ATTRIVALUE = " +"'"+tenureTypeValue+"'";
        Cursor cursor = getDb().rawQuery(selectQueryOptions, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    tenureID=cursor.getString(0);

                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();
        return  tenureID;
    }


    public boolean saveResPOIPropAttributes(List<ResourcePoiSync> attributes, Long propId) {
        if (attributes == null || attributes.size() < 1) {
            return true;
        }

        // Get group ID from the first element. It's supposed that all elemets have the same group ID.
        Long groupId = attributes.get(0).getGroupId();

        if (groupId == null || groupId < 1) {
            groupId = getNewGroupId();
        }

        for (ResourcePoiSync attribute : attributes) {
            attribute.setGroupId(groupId);
            attribute.setFeatureId(propId);
        }

        return saveAttributesPOIList(attributes);
    }

    /**
     * Saves provided attributes list
     */
    private boolean saveAttributesPOIList(List<ResourcePoiSync> attributes) {
        if (attributes == null || attributes.size() < 1) {
            return true;
        }

        // Get group ID from the first element. It's supposed that all elemets have the same group ID.
        Long groupId = attributes.get(0).getGroupId();

        if (groupId != 0) {
            try {
                String whereGroupId = ResourcePoiSync.COL_VALUE_GROUP_ID + "=" + groupId;
                getDb().delete("RESOURCE_POI_VALUE_SYNC", whereGroupId, null);

                for (ResourcePoiSync attribute : attributes) {
                    if (attribute.getValue() != null) {
                        ContentValues row = new ContentValues();
                        row.put(ResourcePoiSync.COL_VALUE_GROUP_ID, groupId);
                        row.put(ResourcePoiSync.COL_VALUE_ATTRIBUTE_ID, attribute.getId());
                        row.put(ResourcePoiSync.COL_VALUE_VALUE, attribute.getValue());
                        row.put(ResourcePoiSync.COL_VALUE_FEATURE_ID, attribute.getFeatureId());
                        getDb().insert(ResourcePoiSync.TABLE_ATTRIBUTE_VALUE_NAME, null, row);
                    }
                }
                return true;
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public List<ResourcePoiSync> getRESPOIAttriSYnData(long featureID) {

        List<ResourcePoiSync> classificationList = new ArrayList<ResourcePoiSync>();
        String selectQueryQues = "SELECT * from RESOURCE_POI_VALUE_SYNC where FEATURE_ID =" + featureID;
        Cursor cursor = getDb().rawQuery(selectQueryQues, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    ResourcePoiSync classification = new ResourcePoiSync();
                    classification.setGroupId(cursor.getLong(0));
                    classification.setId(cursor.getLong(1));
                    classification.setValue(cursor.getString(2));
//
                    classificationList.add(classification);
                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();
        return classificationList;
    }

    public String getResorceOwner1FName(String tenureType,int ownerCount,long featureid) {
        String ownerName=null;
        int iGID=0;


        String selectGID = "SELECT DISTINCT GROUP_ID FROM FORM_VALUES"  + " WHERE FEATURE_ID = " +featureid +" ORDER BY GROUP_ID DESC";
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    iGID = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();

        String selectQueryOptions = "SELECT ATTRIB_VALUE FROM FORM_VALUES"  + " WHERE LABEL_NAME " + "=" + "'First Name' AND FEATURE_ID = " +featureid +" AND GROUP_ID ="+iGID;
        Cursor cursor = getDb().rawQuery(selectQueryOptions, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    ownerName=cursor.getString(0);

                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();

        return  ownerName;
    }

    public String getResorceOwner2FName(String tenureType,int ownerCount,long featureid) {
        String ownerName=null;
        int iGID=0;


        String selectGID = "SELECT DISTINCT GROUP_ID FROM FORM_VALUES"  + " WHERE FEATURE_ID = " +featureid +" ORDER BY GROUP_ID ASC";
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    iGID = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();

        String selectQueryOptions = "SELECT ATTRIB_VALUE FROM FORM_VALUES"  + " WHERE LABEL_NAME " + "=" + "'First Name' AND FEATURE_ID = " +featureid +" AND GROUP_ID ="+iGID;
        Cursor cursor = getDb().rawQuery(selectQueryOptions, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    ownerName=cursor.getString(0);

                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();

        return  ownerName;
    }

    public String getResorceOwner1MName(String tenureType,int ownerCount,long featureid) {
        String ownerName=null;

        int iGID=0;


        String selectGID = "SELECT DISTINCT GROUP_ID FROM FORM_VALUES"  + " WHERE FEATURE_ID = " +featureid +" ORDER BY GROUP_ID DESC";
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    iGID = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();

        String selectQueryOptions = "SELECT ATTRIB_VALUE FROM FORM_VALUES"  + " WHERE LABEL_NAME " + "=" + "'Middle Name' AND FEATURE_ID = " +featureid+" AND GROUP_ID ="+iGID;;
        Cursor cursor = getDb().rawQuery(selectQueryOptions, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    ownerName=cursor.getString(0);

                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();

        return  ownerName;
    }

    public String getResorceOwner2MName(String tenureType,int ownerCount,long featureid) {
        String ownerName=null;
        int iGID=0;


        String selectGID = "SELECT DISTINCT GROUP_ID FROM FORM_VALUES"  + " WHERE FEATURE_ID = " +featureid +" ORDER BY GROUP_ID ASC";
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    iGID = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();

        String selectQueryOptions = "SELECT ATTRIB_VALUE FROM FORM_VALUES"  + " WHERE LABEL_NAME " + "=" + "'Middle Name' AND FEATURE_ID = " +featureid+" AND GROUP_ID ="+iGID;;
        Cursor cursor = getDb().rawQuery(selectQueryOptions, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    ownerName=cursor.getString(0);

                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();

        return  ownerName;
    }

    public String getResorceOwner1LName(String tenureType,int ownerCount,long featureid) {
        String ownerName=null;
        int iGID=0;


        String selectGID = "SELECT DISTINCT GROUP_ID FROM FORM_VALUES"  + " WHERE FEATURE_ID = " +featureid +" ORDER BY GROUP_ID DESC";
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    iGID = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();

        String selectQueryOptions = "SELECT ATTRIB_VALUE FROM FORM_VALUES"  + " WHERE LABEL_NAME " + "=" + "'Last Name' AND FEATURE_ID = " +featureid+" AND GROUP_ID ="+iGID;;
        Cursor cursor = getDb().rawQuery(selectQueryOptions, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    ownerName=cursor.getString(0);

                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();

        return  ownerName;
    }
    public String getResorceOwner2LName(String tenureType,int ownerCount,long featureid) {
        String ownerName=null;
        int iGID=0;


        String selectGID = "SELECT DISTINCT GROUP_ID FROM FORM_VALUES"  + " WHERE FEATURE_ID = " +featureid +" ORDER BY GROUP_ID ASC";
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    iGID = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();

        String selectQueryOptions = "SELECT ATTRIB_VALUE FROM FORM_VALUES"  + " WHERE LABEL_NAME " + "=" + "'Last Name' AND FEATURE_ID = " +featureid+" AND GROUP_ID ="+iGID;;
        Cursor cursor = getDb().rawQuery(selectQueryOptions, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    ownerName=cursor.getString(0);

                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();

        return  ownerName;
    }


    public List<ResourceOwner> getResorceMultipleOwnerName(long featureid) {
        String ownerName=null;
        String FName=null;
        String MName=null;
        String LName=null;
        List<ResourceOwner> lstOwnerName= new ArrayList<ResourceOwner>();

        int iGID=0;


        String selectGID = "SELECT DISTINCT GROUP_ID FROM FORM_VALUES"  + " WHERE FEATURE_ID = " +featureid +" ORDER BY GROUP_ID ASC";
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {

                    iGID = cursorgid.getInt(0);

                    //------------
                    String selectQueryOptions = "Select (SELECT ATTRIB_VALUE FROM FORM_VALUES  WHERE LABEL_NAME ='First Name' AND FEATURE_ID = " +featureid +"  AND GROUP_ID ="+iGID+") as FName,\n" +
                            "(SELECT ATTRIB_VALUE FROM FORM_VALUES  WHERE LABEL_NAME ='Middle Name' AND FEATURE_ID = " +featureid +"  AND GROUP_ID ="+iGID+") as MName,\n" +
                            "(SELECT ATTRIB_VALUE FROM FORM_VALUES  WHERE LABEL_NAME ='Last Name' AND FEATURE_ID = " +featureid +"  AND GROUP_ID ="+iGID+") as LName";
                    Cursor cursor = getDb().rawQuery(selectQueryOptions, null);
                    if (cursor.moveToFirst()) {
                        try {
                            do {
                                ResourceOwner objRresourceOwner=new ResourceOwner();

                                ownerName=cursor.getString(0) +" "+cursor.getString(1)+" "+cursor.getString(2);
                                objRresourceOwner.setOwnerName(ownerName);
                                objRresourceOwner.setFeatureID(featureid);
                                objRresourceOwner.setGroupId(iGID);
                                objRresourceOwner.setMedia(getMediaByPerson((long) objRresourceOwner.getGroupId()));
                                lstOwnerName.add(objRresourceOwner);
                                objRresourceOwner=null;
                            } while (cursor.moveToNext());
                        } catch (Exception e) {
                            cf.appLog("", e);
                            e.printStackTrace();
                        }
                    }
                    cursor.close();
                    //------------
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();

        return  lstOwnerName;
    }


    public int getOwnerCount(Long featureId) {
        int iGID=0;

        String selectGID = "SELECT COUNT(DISTINCT GROUP_ID) FROM FORM_VALUES"  + " WHERE FEATURE_ID = " +featureId +" ORDER BY GROUP_ID DESC";
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    iGID = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();

        return iGID;
    }


    public int getPOICount(Long featureId) {
        int iGID=0;

        String selectGID = "SELECT COUNT(DISTINCT GROUP_ID) FROM RESOURCE_POI_VALUE_SYNC"  + " WHERE FEATURE_ID = " +featureId +" ORDER BY GROUP_ID DESC";
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    iGID = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();

        return iGID;
    }


//    "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
//            "AOIID INTEGER," +
//            "AOINAME TEXT," +
//            "USERID INTEGER," +
//            "PROJECTNAME_ID TEXT," +
//            "COORDINATES TEXT," +
//            "ISACTIVE TEXT" +
//            ")";
    public List<AOI> getAOIList() {

        List<AOI> AOIList = new ArrayList<AOI>();
        //String selectQueryQues = "SELECT AOIID,AOINAME from AOI" ;
        String selectQueryQues = "SELECT AOIID,COORDINATES,AOINAME from AOI WHERE ISACTIVE='true'" ;
       // String selectQueryQues = "SELECT AOIID,AOINAME,COORDINATES from AOI" ;
        Cursor cursor = getDb().rawQuery(selectQueryQues, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    AOI lstAoi = new AOI();

                    lstAoi.setAoiID(cursor.getString(0));
                    lstAoi.setAoiName(cursor.getString(2));
                    lstAoi.setCoOrdinates(cursor.getString(1));

                    AOIList.add(lstAoi);

                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();
        return AOIList;
    }

    public int getNexBoundaryNumber() {
        int nextNumber=0;
        String sql = "SELECT MAX(IP_NUMBER) FROM SPATIAL_FEATURES"  + " WHERE FLAG='B'";
        Cursor cur = getDb().rawQuery(sql, null);
        if (cur.moveToFirst()) {
            try {
                nextNumber = cur.getInt(0);
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cur.close();
        return nextNumber+1;
    }

    public int getFeatureCount(String strType) {
        int iGID=0;
        String selectGID = "SELECT COUNT(*) FROM SPATIAL_FEATURES"  + " WHERE (STATUS ='draft' OR STATUS ='complete') AND FLAG='" +strType +"' ORDER BY FEATURE_ID";
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    iGID = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();

        iGID=iGID+1;
        return iGID;
    }

    public int getpersonType(Long featureId){
        int isNatural=3;

        String selectGID = "SELECT IS_NATURAL FROM PERSON"  + " WHERE FEATURE_ID = "+ featureId;
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    isNatural = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();


        return isNatural;
    }


    public int getpersonTypefromFeature(Long featureId){
        int isNatural=3;

        String selectGID = "SELECT IS_NATURAL FROM SPATIAL_FEATURES"  + " WHERE FEATURE_ID = "+ featureId;
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    isNatural = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();


        return isNatural;
    }

    public int getDisputpersonTypefromFeature(Long featureId){
        int IsDIspute=0;

        String selectGID = "SELECT DISPUTE_TYPE FROM DISPUTE"  + " WHERE FEATURE_ID = "+ featureId;
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    IsDIspute = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();


        return IsDIspute;
    }

    public int getDisPuteType(long grpID) {
        int disputeType=0;



        String selectQueryOptions = "SELECT ATTRIB_VALUE FROM FORM_VALUES"  + " WHERE LABEL_NAME " + "=" + "'Disputed PersonType' AND GROUP_ID ="+grpID;;
        Cursor cursor = getDb().rawQuery(selectQueryOptions, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    disputeType=cursor.getInt(0);

                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();


        return disputeType ;
    }

    public boolean saveNonNaturalInstitute(List<Attribute> attributes) {
        if (attributes == null || attributes.size() < 1) {
            return true;
        }

        if (attributes != null && attributes.size() > 0) {
            for (Attribute attribute : attributes) {

                attribute.setFeatureId(attribute.getFeatureId());
            }
        }
        Long featureId = attributes.get(0).getFeatureId();

        if (featureId != 0){

            try {
                String whereGroupId = NonNatural.COL_VALUE_FEATURE_ID + "=" + featureId;
                getDb().delete(NonNatural.TABLE_NAME, whereGroupId, null);
                for (Attribute attribute : attributes) {
                    if (attribute.getValue() != null) {
                        ContentValues row = new ContentValues();

                        row.put(NonNatural.COL_VALUE_ATTRIBUTE_ID, attribute.getId());
                        row.put(NonNatural.COL_VALUE_VALUE, attribute.getValue());
                        row.put(NonNatural.COL_VALUE_FEATURE_ID, attribute.getFeatureId());
                        row.put(NonNatural.COL_VALUE_LABEL_NAME, attribute.getName());
                        getDb().insert(NonNatural.TABLE_NAME, null, row);
                    }
                }
                return true;
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
                return false;
            }

    }
    return false;
    }

    public List<Attribute> getDataFromNonNatural(Long groupId) {
        String wherePart = " AV." + NonNatural.COL_VALUE_FEATURE_ID + "=" + groupId;
        return createAttributeList(getAttributeNonSelectQuery(wherePart));
    }
    private String getAttributeNonSelectQuery(String wherePart) {
        String sql = "SELECT AM." + Attribute.COL_ID +
                ", AM." + Attribute.COL_TYPE +
                ", AM." + Attribute.COL_CONTROL_TYPE +
                ", AM." + Attribute.COL_NAME +
                ", AM." + Attribute.COL_NAME_OTHER_LANG +
                ", AM." + Attribute.COL_LISTING +
                ", AM." + Attribute.COL_VALIDATE +
                ", AM." + Attribute.COL_FLAG +
                ", AV." + NonNatural.COL_VALUE_FEATURE_ID +
                ", AV." + NonNatural.COL_VALUE_ATTRIBUTE_ID +
                ", AV." + NonNatural.COL_VALUE_VALUE +
                " FROM " + Attribute.TABLE_NAME + " AS AM LEFT JOIN " +
                NonNatural.TABLE_NAME + " AS AV ON " +
                "AM." + Attribute.COL_ID + " = AV." + NonNatural.COL_VALUE_ATTRIBUTE_ID;
        if (!StringUtility.isEmpty(wherePart)) {
            sql = sql + " WHERE " + wherePart;
        }
        sql = sql + " ORDER BY AM." + Attribute.COL_LISTING + ", AM." + Attribute.COL_ID;

//        sql = sql + " ORDER BY AM." + Attribute.COL_LISTING + ", AM." + Attribute.COL_NAME;


        return sql;
    }

    public boolean deleteResource(Long featureId) {
        String whereGroupId = "FEATURE_ID" + "=" + featureId;
        getDb().delete("RESOURCE_BASISC_ATTRIBUTES", whereGroupId, null);
        return true;

    }
 public boolean deleteCustomResource(Long featureId) {
        String whereGroupId = "FEATURE_ID" + "=" + featureId;
        getDb().delete("RESOURCE_FORM_VALUES", whereGroupId, null);
        return true;

    }

    public int getPrimaryCount(Long strType) {
        int iGID=0;


        String selectGID = "SELECT COUNT(*) FROM FORM_VALUES"  + " WHERE FEATURE_ID ="+ strType +" AND ATTRIB_ID=1156 AND ATTRIB_VALUE=1186";
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    iGID = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();




        return iGID;
    }

    public int getPrimaryOWnerForYesCount(Long strType) {
        int iGID=0;


        String selectGID = "SELECT COUNT(*) FROM FORM_VALUES"  + " WHERE FEATURE_ID ="+ strType +" AND ATTRIB_ID=1156 AND ATTRIB_VALUE=1186";
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    iGID = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();




        return iGID;
    }


    public int getShareIdByFeatureID(Long featureId) {

        int shareID=0;
        String selectGID = "SELECT SHARE_TYPE FROM SOCIAL_TENURE"  + " WHERE FEATURE_ID ="+ featureId ;
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    shareID = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();

        return  shareID;
    }

    public int getTenureByFeatureID(Long featureId) {

        int shareID=0;
        String selectGID = "SELECT ID FROM RESOURCE_BASISC_ATTRIBUTES"  + " WHERE FEATURE_ID ="+ featureId ;
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    shareID = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();

        return  shareID;
    }

    public int getPrimaryOccupant(Long strType) {
        int iGID=0;


        String selectGID = "SELECT COUNT(*) FROM FORM_VALUES"  + " WHERE FEATURE_ID ="+ strType +" AND ATTRIB_VALUE='Primary occupant /Point of contact'";
        Cursor cursorgid = getDb().rawQuery(selectGID, null);
        if (cursorgid.moveToFirst()) {
            try {
                do {
                    iGID = cursorgid.getInt(0);
                } while (cursorgid.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursorgid.close();




        return iGID;
    }

    public List<ClassificationAttribute> checkTenureInfo(Long featureId) {

        List<ClassificationAttribute> classificationAttributes = new ArrayList<ClassificationAttribute>();
        //String selectQueryQues = "SELECT AOIID,AOINAME from AOI" ;
        String selectQueryQues = "SELECT VALUE,ID from RESOURCE_BASISC_ATTRIBUTES where FEATURE_ID ="+featureId ;
        // String selectQueryQues = "SELECT AOIID,AOINAME,COORDINATES from AOI" ;
        Cursor cursor = getDb().rawQuery(selectQueryQues, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    ClassificationAttribute classificationAttribute = new ClassificationAttribute();

                    classificationAttribute.setAttribValue(cursor.getString(0));
                    classificationAttribute.setAttribID(cursor.getString(1));


                    classificationAttributes.add(classificationAttribute);

                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();
        return classificationAttributes;


    }

    public List<Attribute> checkOwnerInfo(Long featureId) {

        List<Attribute> attributes = new ArrayList<Attribute>();

        String selectQueryQues = "SELECT GROUP_ID,ATTRIB_ID,ATTRIB_VALUE from FORM_VALUES where FEATURE_ID ="+featureId ;

        Cursor cursor = getDb().rawQuery(selectQueryQues, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    Attribute attribute = new Attribute();

                    attribute.setGroupId(cursor.getLong(0));
                    attribute.setId(cursor.getLong(1));
                    attribute.setValue(cursor.getString(2));


                    attributes.add(attribute);

                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();
        return attributes;
    }

    public List<ResourceCustomAttribute> checkCustomAttributesInfo(Long featureId) {


        List<ResourceCustomAttribute> attributes = new ArrayList<ResourceCustomAttribute>();

        String selectQueryQues = "SELECT GROUP_ID,ATTRIB_ID,ATTRIB_VALUE from RESOURCE_FORM_VALUES where FEATURE_ID ="+featureId ;

        Cursor cursor = getDb().rawQuery(selectQueryQues, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    ResourceCustomAttribute attribute = new ResourceCustomAttribute();

                    attribute.setGroupId(cursor.getLong(0));
                    attribute.setId(cursor.getLong(1));
                    attribute.setValue(cursor.getString(2));


                    attributes.add(attribute);

                } while (cursor.moveToNext());
            } catch (Exception e) {
                cf.appLog("", e);
                e.printStackTrace();
            }
        }
        cursor.close();
        return attributes;
    }
    }
