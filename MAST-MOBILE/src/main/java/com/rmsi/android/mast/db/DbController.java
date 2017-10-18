package com.rmsi.android.mast.db;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.rmsi.android.mast.domain.AcquisitionType;
import com.rmsi.android.mast.domain.Attribute;
import com.rmsi.android.mast.domain.Bookmark;
import com.rmsi.android.mast.domain.ClaimType;
import com.rmsi.android.mast.domain.DeceasedPerson;
import com.rmsi.android.mast.domain.Dispute;
import com.rmsi.android.mast.domain.DisputeType;
import com.rmsi.android.mast.domain.Feature;
import com.rmsi.android.mast.domain.Gender;
import com.rmsi.android.mast.domain.Media;
import com.rmsi.android.mast.domain.Option;
import com.rmsi.android.mast.domain.Person;
import com.rmsi.android.mast.domain.PersonOfInterest;
import com.rmsi.android.mast.domain.ProjectSpatialDataDto;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.domain.RefData;
import com.rmsi.android.mast.domain.RelationshipType;
import com.rmsi.android.mast.domain.Right;
import com.rmsi.android.mast.domain.RightType;
import com.rmsi.android.mast.domain.ShareType;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.StringUtility;

public class DbController extends SQLiteOpenHelper {
    Context contxt;
    private static DbController instance;
    SQLiteDatabase db;

    static String DBPATH = "/" + CommonFunctions.parentFolderName + "/" + CommonFunctions.dbFolderName + "/mast_mobile.db";
    static String DB_FULL_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + DBPATH;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a", Locale.ENGLISH);

    private static int DB_VERSION = 6;
    CommonFunctions cf = null;

    public static synchronized DbController getInstance(Context context) {
        if (instance == null) {
            instance = new DbController(context.getApplicationContext());
        }
        return instance;
    }

    private DbController(Context applicationcontext) {
        super(applicationcontext, DB_FULL_PATH, null, DB_VERSION);
        this.contxt = applicationcontext;
        cf = CommonFunctions.getInstance();
        try {
            cf.Initialize(applicationcontext.getApplicationContext());
        } catch (Exception e) {
        }
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
                "UKA_NUMBER TEXT" +
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
                "JURIDICAL_AREA REAL" +
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
                "ATTRIBUTE_CONTROLTYPE INTEGER," +
                "ATTRIBUTE_NAME TEXT," +
                "LISTING INTEGER," +
                "ATTRIBUTE_NAME_OTHER TEXT," +
                "VALIDATION TEXT" +
                ")";

        String query_table3 = "CREATE TABLE OPTIONS(" +
                "OPTION_ID TEXT," +
                "ATTRIB_ID INTEGER," +
                "OPTION_NAME TEXT," +
                "OPTION_NAME_OTHER TEXT)";

        String query_table4 = "CREATE TABLE FORM_VALUES(" +
                "GROUP_ID INTEGER," +
                "ATTRIB_ID INTEGER," +
                "ATTRIB_VALUE TEXT," +
                "FEATURE_ID TEXT" +
                ")";

        // Ref data
        String query_table11 = "CREATE TABLE GROUPID_SEQ(VALUE INTEGER)";
        String query_table13 = "CREATE TABLE PROJECT_SPATIAL_DATA(SERVER_PK INTEGER,PROJECT_NAME TEXT,FILE_NAME TEXT,FILE_EXT TEXT,ALIAS TEXT,VILLAGE_NAME TEXT)";
        String query_table14 = "CREATE TABLE HAMLET_DETAILS(ID INTEGER PRIMARY KEY, HAMLET_NAME TEXT, HAMLET_LEADER TEXT)";
        String query_table15 = "CREATE TABLE ADJUDICATOR_DETAILS(ID INTEGER PRIMARY KEY,ADJUDICATOR_NAME TEXT)";

        String query_table18 = "CREATE TABLE CLAIM_TYPE(" +
                "CODE TEXT PRIMARY KEY," +
                "NAME TEXT," +
                "NAME_OTHER_LANG TEXT" +
                ")";

        String query_table19 = "CREATE TABLE RELATIONSHIP_TYPE(" +
                "CODE INTEGER PRIMARY KEY," +
                "NAME TEXT," +
                "NAME_OTHER_LANG TEXT," +
                "ACTIVE INTEGER" +
                ")";

        String query_table20 = "CREATE TABLE SHARE_TYPE(" +
                "CODE INTEGER PRIMARY KEY," +
                "NAME TEXT," +
                "NAME_OTHER_LANG TEXT," +
                "ACTIVE INTEGER DEFAULT 1" +
                ")";

        String query_table21 = "CREATE TABLE RIGHT_TYPE(" +
                "CODE INTEGER PRIMARY KEY," +
                "NAME TEXT," +
                "NAME_OTHER_LANG TEXT," +
                "ACTIVE INTEGER," +
                "FOR_ADJUDICATION INTEGER" +
                ")";

        String query_table22 = "CREATE TABLE GENDER(" +
                "CODE INTEGER PRIMARY KEY," +
                "NAME TEXT," +
                "NAME_OTHER_LANG TEXT," +
                "ACTIVE INTEGER DEFAULT 1" +
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
                "ACTIVE INTEGER DEFAULT 1" +
                ")";

        // System
        String query_table5 = "CREATE TABLE USER(USER_ID TEXT,USER_NAME TEXT,PASSWORD TEXT,ROLE_ID TEXT,ROLE_NAME TEXT)";
        String query_table6 = "CREATE TABLE BOOKMARKS(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,LATITUDE TEXT,LONGITUDE TEXT,ZOOMLEVEL TEXT)";


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
        if (db == null || !db.isOpen()) {
            db = getWritableDatabase();
        }
        return db;
    }

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

                do {
                    Feature feature = new Feature();
                    feature.setId(cursor.getLong(indxId));
                    feature.setServerId(cursor.getLong(indxServerId));
                    feature.setCoordinates(cursor.getString(indxCoordinates));
                    feature.setGeomType(cursor.getString(indxGeomType));
                    feature.setStatus(cursor.getString(indxStatus));
                    feature.setPolygonNumber(cursor.getString(indxPolygonNumber));
                    feature.setSurveyDate(cursor.getString(indxSurveyDate));
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

    public Long createFeature(String geomtype, String wKTStr, String imei) {
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

            getDb().insert(Feature.TABLE_NAME, null, value);
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

                do{
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
                    if (!cur.isNull(ukaNumber))
                        property.setUkaNumber(cur.getString(ukaNumber));

                    property.setDeceasedPerson(getDeceasedPersonByProp(property.getId()));
                    property.setMedia(getMediaByProp(property.getId()));
                    property.setRight(getRightByProp(property.getId()));
                    property.setPersonOfInterests(getPersonOfInterestsByProp(property.getId()));
                    property.setDispute(getDisputeByProp(property.getId()));

                    List<Attribute> attributes = getPropAttributes(property.getId());
                    property.setAttributes(attributes);

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

    /**
     * Returns property object by id.
     */
    public Property getProperty(Long propId) {
        List<Property> properties = createPropertyList("SELECT * FROM " + Feature.TABLE_NAME +
                " WHERE " + Feature.COL_ID + " = " + propId.toString());
        if(properties.size() > 0)
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

                right.setNaturalPersons(getNaturalPersonsByRight(right.getId()));
                right.setNonNaturalPerson(getNonNaturalPersonsByRight(right.getId()));
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

    /**
     * Returns person by id
     */
    public Person getPerson(Long personId) {
        String sql = "SELECT * FROM " + Person.TABLE_NAME + " WHERE " + Person.COL_ID + " = " + personId.toString();
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
                Person.COL_RIGHT_ID + " = " + rightId.toString() + " AND " + Person.COL_IS_NATURAL + "=0";
        List<Person> persons = createPersonsList(sql);
        if (persons != null && persons.size() > 0)
            return persons.get(0);
        else
            return null;
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

    /**
     * Returns list of person of interest by property ID
     */
    public List<PersonOfInterest> getPersonOfInterestsByProp(Long propId) {
        return createPersonOfInterestsList("SELECT * FROM " + PersonOfInterest.TABLE_NAME +
                " WHERE " + PersonOfInterest.COL_FEATURE_ID + "=" + propId);
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
                    if (lang.equalsIgnoreCase("sw") && !TextUtils.isEmpty(cur.getString(indxNameOtherLang))) {
                        attribute.setName(cur.getString(indxNameOtherLang));
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
                                indxOptionId = cur2.getColumnIndex(Option.COL_ID);
                                indxOptionName = cur2.getColumnIndex(Option.COL_NAME);
                                indxOptionNameOtherLang = cur2.getColumnIndex(Option.COL_NAME_OTHER_LANG);
                            }

                            do {
                                option = new Option();
                                option.setId(cur2.getLong(indxOptionId));
                                StringBuffer multiLnagText = new StringBuffer();
                                if (lang.equalsIgnoreCase("sw") && !TextUtils.isEmpty(cur2.getString(indxOptionNameOtherLang))) {
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

    /**
     * Returns list of attributes by group ID (person, media, etc)
     */
    public List<Attribute> getAttributesByGroupId(Long groupId) {
        String wherePart = " AV." + Attribute.COL_VALUE_GROUP_ID + "=" + groupId;
        return createAttributeList(getAttributeSelectQuery(wherePart));
    }

    /**
     * Returns property attributes by type. Expected types are as follows: GENERAL, GENERAL_PROPERTY, CUSTOM
     */
    public List<Attribute> getPropAttributesByType(Long featureId, String attributeType) {
        String wherePart = " AM." + Attribute.COL_TYPE + " = '" + attributeType + "' " +
                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId;
        return createAttributeList(getAttributeSelectQuery(wherePart));
    }

    /**
     * Returns property attributes, including GENERAL, GENERAL_PROPERTY, CUSTOM
     */
    public List<Attribute> getPropAttributes(Long featureId) {
        String wherePart = " AM." + Attribute.COL_TYPE + " IN ('" + Attribute.TYPE_GENERAL +
                "','" + Attribute.TYPE_GENERAL_PROPERTY + "','" + Attribute.TYPE_CUSTOM + "') " +
                "AND AV." + Attribute.COL_VALUE_FEATURE_ID + "=" + featureId;
        return createAttributeList(getAttributeSelectQuery(wherePart));
    }

    /**
     * Returns attributes list by type. Returned list doesn't conatain actual values and should be used for new objects.
     */
    public List<Attribute> getAttributesByType(String attributeType) {
        return createAttributeList(getAttributeSelectQueryByType(attributeType));
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
                        projectValues.put("PROJECT_NAME", project_detail.getString("name"));
                        projectValues.put("FILE_NAME", project_detail.getString("fileName"));
                        projectValues.put("FILE_EXT", project_detail.getString("fileExtension"));
                        projectValues.put("ALIAS", project_detail.getString("alias"));
                        if (projectdata.has("Village")) {
                            projectValues.put("VILLAGE_NAME", projectdata.getString("Village"));
                        }

                        getDb().insert("PROJECT_SPATIAL_DATA", null, projectValues);
                    }
                }
            }

            if (projectdata.has("Hamlet")) {
                ContentValues hamlets = new ContentValues();
                JSONArray hamletsArray = projectdata.getJSONArray("Hamlet");
                if (hamletsArray.length() > 0) {
                    for (int i = 0; i < hamletsArray.length(); i++) {
                        JSONObject hamlet = new JSONObject(hamletsArray.get(i).toString());
                        hamlets.put("ID", hamlet.getInt("id"));
                        hamlets.put("HAMLET_NAME", hamlet.getString("hamletName"));
                        hamlets.put("HAMLET_LEADER", hamlet.getString("hamletLeaderName"));
                        getDb().insert("HAMLET_DETAILS", null, hamlets);
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
                        getDb().insert(ClaimType.TABLE_NAME, null, claimTypes);
                    }
                }
            }

            if (projectdata.has("DisputeType")) {
                ContentValues types = new ContentValues();
                JSONArray typesArray = projectdata.getJSONArray("DisputeType");
                if (typesArray.length() > 0) {
                    for (int i = 0; i < typesArray.length(); i++) {
                        JSONObject type = new JSONObject(typesArray.get(i).toString());
                        types.put(RefData.COL_CODE, type.getInt("code"));
                        types.put(RefData.COL_NAME, type.getString("name"));
                        types.put(RefData.COL_NAME_OTHER_LANG, type.getString("nameOtherLang"));
                        types.put(RefData.COL_ACTIVE, type.getBoolean("active") ? 1 : 0);
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
                        types.put(RefData.COL_CODE, type.getInt("code"));
                        types.put(RefData.COL_NAME, type.getString("name"));
                        types.put(RefData.COL_NAME_OTHER_LANG, type.getString("nameOtherLang"));
                        types.put(RefData.COL_ACTIVE, type.getBoolean("active") ? 1 : 0);
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
                        relTypes.put("CODE", relType.getInt("code"));
                        relTypes.put("NAME", relType.getString("name"));
                        relTypes.put("NAME_OTHER_LANG", relType.getString("nameOtherLang"));
                        relTypes.put("ACTIVE", relType.getBoolean("active") ? 1 : 0);
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
                        shareTypes.put("CODE", shareType.getInt("gid"));
                        shareTypes.put("NAME", shareType.getString("shareType"));
                        shareTypes.put("NAME_OTHER_LANG", shareType.getString("shareType_sw"));
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
                        rightTypes.put("CODE", rightType.getInt("tenureId"));
                        rightTypes.put("NAME", rightType.getString("tenureClass"));
                        rightTypes.put("NAME_OTHER_LANG", rightType.getString("tenureClassSw"));
                        rightTypes.put("ACTIVE", rightType.getBoolean("active") ? 1 : 0);
                        rightTypes.put("FOR_ADJUDICATION", rightType.getBoolean("forAdjudication") ? 1 : 0);
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
                        genders.put("NAME_OTHER_LANG", gender.getString("gender_sw"));
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

            if (projectdata.has("Attributes")) {
                ContentValues attributeValues = new ContentValues();
                JSONArray attribute_info = projectdata.getJSONArray("Attributes");
                if (attribute_info.length() > 0) {
                    for (int i = 0; i < attribute_info.length(); i++) {
                        JSONObject attribute_detail = new JSONObject(attribute_info.get(i).toString());
                        attributeValues.put("ATTRIB_ID", attribute_detail.getInt("id"));
                        if (attribute_detail.has("attributeCategory")) {
                            JSONObject attributeCategory = attribute_detail.getJSONObject("attributeCategory");
                            attributeValues.put("ATTRIBUTE_TYPE", attributeCategory.getString("attributecategoryid"));
                        }
                        if (attribute_detail.has("datatypeIdBean")) {
                            JSONObject datatypeIdBean = attribute_detail.getJSONObject("datatypeIdBean");
                            attributeValues.put("ATTRIBUTE_CONTROLTYPE", datatypeIdBean.getInt("datatypeId"));
                            if (datatypeIdBean.getInt("datatypeId") == 5) {
                                if (!attribute_detail.getString("attributeOptions").equalsIgnoreCase("null")) {

                                    JSONArray attributeOptions = attribute_detail.getJSONArray("attributeOptions");
                                    if (attributeOptions.length() > 0) {
                                        for (int j = 0; j < attributeOptions.length(); j++) {
                                            ContentValues option_value = new ContentValues();
                                            JSONObject optionValues = attributeOptions.getJSONObject(j);
                                            option_value.put("OPTION_ID", optionValues.getString("id"));
                                            option_value.put("ATTRIB_ID", optionValues.getInt("attributeId"));
                                            option_value.put("OPTION_NAME", optionValues.getString("optiontext"));
                                            option_value.put("OPTION_NAME_OTHER", optionValues.getString("optiontext_second_language"));
                                            getDb().insert("OPTIONS", null, option_value);
                                            option_value.clear();
                                        }
                                    }
                                }
                            }
                        }
                        attributeValues.put("ATTRIBUTE_NAME", attribute_detail.getString("alias"));
                        if (attribute_detail.has("listing") && !TextUtils.isEmpty(attribute_detail.getString("listing")) && !attribute_detail.getString("listing").equalsIgnoreCase("null")) {
                            attributeValues.put("LISTING", attribute_detail.getInt("listing"));
                        }
                        if (attribute_detail.has("mandatory") && !TextUtils.isEmpty(attribute_detail.getString("mandatory")) && !attribute_detail.getString("mandatory").equalsIgnoreCase("null")) {
                            attributeValues.put("VALIDATION", attribute_detail.getString("mandatory"));
                        }
                        if (attribute_detail.has("alias_second_language") && !TextUtils.isEmpty(attribute_detail.getString("alias_second_language")) && !attribute_detail.getString("alias_second_language").equalsIgnoreCase("null")) {
                            attributeValues.put("ATTRIBUTE_NAME_OTHER", attribute_detail.getString("alias_second_language"));
                        }
                        getDb().insert("ATTRIBUTE_MASTER", null, attributeValues);
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
        String selectQueryQues = "SELECT * from PROJECT_SPATIAL_DATA order by SERVER_PK";
        Cursor cursor = getDb().rawQuery(selectQueryQues, null);
        if (cursor.moveToFirst()) {
            try {
                do {
                    ProjectSpatialDataDto projectSpatialData = new ProjectSpatialDataDto();
                    projectSpatialData.setServer_Pk(cursor.getInt(0));
                    projectSpatialData.setProject_Name(cursor.getString(1));
                    projectSpatialData.setFile_Name(cursor.getString(2));
                    projectSpatialData.setFile_Ext(cursor.getString(3));
                    projectSpatialData.setAlias(cursor.getString(4));
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
        String q = "select project_name from PROJECT_SPATIAL_DATA LIMIT 1";
        Cursor cursor = getDb().rawQuery(q, null);

        if (cursor.moveToFirst()) {
            projectName = cursor.getString(0);
            cursor.close();
        }
        return projectName;
    }

    public String getProjectDataForUpload() {
        try{
            List<Property> properties = createPropertyList("SELECT * FROM " + Property.TABLE_NAME +
                    " WHERE " + Property.COL_STATUS + " = '" + Property.CLIENT_STATUS_COMPLETE + "' AND (" +
                    Property.COL_SERVER_ID + " IS NULL OR " + Property.COL_SERVER_ID + " = '')");

            if(properties == null || properties.size() < 1)
                return "";

            Gson gson = new Gson();
            Type type = new TypeToken<List<Property>>() {}.getType();
            return gson.toJson(properties, type);
        } catch (Exception e) {
            cf.syncLog("", e);
            cf.showToast(contxt, R.string.FailedToFormJson, Toast.LENGTH_SHORT);
            e.printStackTrace();
            return "";
        }
    }

    public boolean updateServerFeatureId(String data) throws JSONException {
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
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
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
                    "MV.TYPE "
                    + "FROM MEDIA AS MV INNER JOIN SPATIAL_FEATURES AS SF ON MV.FEATURE_ID =  SF.FEATURE_ID "
                    + "WHERE SF.STATUS = '" + Property.CLIENT_STATUS_COMPLETE + "' and MV.SYNCED=0 Limit 1";

            JSONArray medias = new JSONArray();
            JSONArray mediasAttributes = new JSONArray();

            // Fetching media for spatial unit
            Cursor cursor = getDb().rawQuery(fetchFromMediaSql, null);
            if (cursor.moveToFirst()) {
                mediaId = cursor.getInt(0);
                medias.put(0, cursor.getLong(5)); //usin
                if(cursor.isNull(2))
                    medias.put(1, "");//person id
                else {
                    personId = cursor.getLong(2);
                    medias.put(1, personId);//person id
                }
                medias.put(2, mediaId);//media id
                medias.put(3, cursor.getString(4));// media path
                medias.put(4, cursor.getString(6));// media type
                if(cursor.isNull(3))
                    medias.put(5, "");//dispute id
                else {
                    medias.put(5, cursor.getLong(3));//dispute id
                }
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
        Type type = new TypeToken<List<Property>>() {}.getType();
        List<Property> properties = gson.fromJson(data, type);

        if(properties != null && properties.size() > 0){
            // Get lodged user
            String userId = "";
            User user = getLoggedUser();
            if(user != null)
                userId = user.getUserId().toString();

            // Clean downloaded feature from DB
            deleteDownloadedFeatures();

            for(Property prop : properties){
                String status = "";

                // Skip feature if status is rejected
                if(StringUtility.empty(prop.getStatus()).equalsIgnoreCase(Feature.SERVER_STATUS_REJECTED)){
                    continue;
                }

                status = StringUtility.empty(prop.getStatus());

                // Set appropriate client status to the property
                if(status.equalsIgnoreCase(Feature.SERVER_STATUS_APPROVED)){
                    prop.setStatus(Feature.CLIENT_STATUS_FINAL);
                } else if(status.equalsIgnoreCase(Feature.SERVER_STATUS_VALIDATED)){
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

    public Property insertProperty(Property prop){
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

        getDb().insert(Feature.TABLE_NAME, null, values);
        long featureId = getGeneratedId(Feature.TABLE_NAME);
        prop.setId(featureId);
        savePropAttributes(prop.getAttributes(), featureId);

        // Insert tenure
        if(prop.getRight() != null) {
            Right right = prop.getRight();
            // reset right id so that system generates new one
            right.setId(null);
            right.setFeatureId(featureId);
            saveRight(right);

            // Insert non-natural person
            if(right.getNonNaturalPerson() != null) {
                Person person = right.getNonNaturalPerson();
                person.setId(null);
                person.setRightId(right.getId());
                person.setFeatureId(featureId);
                savePerson(person);
            }

            // Insert natural persons
            if(right.getNaturalPersons() != null && right.getNaturalPersons().size() > 0) {
                for (Person person : right.getNaturalPersons()) {
                    person.setId(null);
                    person.setRightId(right.getId());
                    person.setFeatureId(featureId);
                    savePerson(person);
                }
            }
        }

        // Insert POI
        if(prop.getPersonOfInterests() != null && prop.getPersonOfInterests().size() > 0){
            for(PersonOfInterest poi : prop.getPersonOfInterests()){
                poi.setId(null);
                poi.setFeatureId(featureId);
                savePersonOfInterest(poi);
            }
        }

        // Insert Deceased
        if(prop.getDeceasedPerson() != null){
            prop.getDeceasedPerson().setId(null);
            prop.getDeceasedPerson().setFeatureId(featureId);
            saveDeceasedPerson(prop.getDeceasedPerson());
        }

        // Insert Media
        if(prop.getMedia() != null && prop.getMedia().size() > 0){
            for(Media media : prop.getMedia()){
                media.setId(null);
                media.setFeatureId(featureId);
                media.setSynced(1);
                saveMedia(media);
            }
        }

        // Insert dispute
        if(prop.getDispute() != null){
            Dispute dispute = prop.getDispute();
            dispute.setId(null);
            dispute.setFeatureId(featureId);
            saveDispute(dispute);

            // Insert disputing parties
            if(dispute.getDisputingPersons() != null){
                for(Person person : dispute.getDisputingPersons()){
                    person.setId(null);
                    person.setDisputeId(dispute.getId());
                    person.setFeatureId(featureId);
                    savePerson(person);
                }
            }

            // Insert dispute documents
            if(dispute.getMedia() != null && dispute.getMedia().size() > 0){
                for(Media media : dispute.getMedia()){
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
            cursor = getDb().rawQuery("SELECT * FROM " + type.getTableName() + " ORDER BY " + RefData.COL_NAME, null);
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
        return createClaimTypesList("SELECT * FROM " + ClaimType.TABLE_NAME, addDummy);
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
                int indxForAdjudication = cursor.getColumnIndex(RightType.COL_FOR_ADJUDICATION);

                do {
                    RightType rightType = new RightType();
                    rightType.setCode(cursor.getInt(indxCode));
                    rightType.setName(cursor.getString(indxName));
                    rightType.setNameOtherLang(cursor.getString(indxNameOtherLang));
                    rightType.setActive(cursor.getInt(indxActive));
                    rightType.setForAdjudication(cursor.getInt(indxForAdjudication));
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
        return getRefDataTypes(ShareType.class, addDummy);
    }

    /**
     * Returns share type by code
     */
    public ShareType getShareType(int code) {
        return getRefDataType(ShareType.class, code);
    }

    /** Returns share type by right id */
    public ShareType getShareTypeByRight(Long rightId){
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
}
