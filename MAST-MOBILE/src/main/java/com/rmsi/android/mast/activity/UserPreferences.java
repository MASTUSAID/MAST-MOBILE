package com.rmsi.android.mast.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.domain.AOI;
import com.rmsi.android.mast.util.CommonFunctions;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class UserPreferences extends AppCompatActivity {
    ListView listView;
    Context context = this;
    CommonFunctions cf = CommonFunctions.getInstance();
    String lang = "";
    String dct = "";
    private String corrdinates = null;

    Editor editor;
    Dialog dialog = null;
    boolean auto;
    SharedPreferences sharedpreferences;
    SparseBooleanArray checkedPos_cdct;
    InputStream in ;
    InputStreamReader isr ;
    private String drawFeatureByFlagGPS;
    CommonFunctions commonFunctions;
    boolean isReview=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Initializing context in common functions in case of a crash
        try {
            CommonFunctions.getInstance().Initialize(getApplicationContext());
        } catch (Exception e) {
        }
        cf.loadLocale(getApplicationContext());

        setContentView(R.layout.activity_user_preferences);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            isReview=extras.getBoolean("IsReview");


        }



        sharedpreferences = cf.getmMyPreferences();
        Resources res = getResources();
        String[] userPref = res.getStringArray(R.array.UserPref_arrays);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.userpref);
        if (toolbar != null)
            setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.mainListView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_text_view, R.id.text1, userPref);

        listView.setAdapter(adapter);
        lang = cf.getLocale(); // to get and set the value from shared preference.

        dct = cf.getDataCollectionTools();
        auto = cf.getAutoSync();
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (position == 0) {
                    try {
                        String[] language = getResources().getStringArray(R.array.Language_arrays);

                        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
                        dialog.setContentView(R.layout.dialog_show_list);
                        dialog.setTitle(getResources().getString(R.string.change_language));
                        dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
                        ListView listViewForLanguage = (ListView) dialog.findViewById(R.id.commonlistview);
                        Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                R.layout.item_list_single_choice, language);

                        listViewForLanguage.setAdapter(adapter);
                        listViewForLanguage.setItemChecked(0, true);

                        btn_ok.setOnClickListener(new OnClickListener() {
                            //Run when button is clicked
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                cf.saveLocale(lang);
                                recreate();
                                cf.loadLocale(getApplicationContext());

                            }
                        });

                        if (lang.equalsIgnoreCase("en")) {

                            listViewForLanguage.setItemChecked(0, true);
                        } else if (lang.equalsIgnoreCase("sw"))

                        {
                            listViewForLanguage.setItemChecked(1, true);
                        }

                        listViewForLanguage.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent,
                                                    View view, int position, long id) {
                                int itemPosition = position;
                                if (itemPosition == 0) // for english
                                {
                                    lang = "en";
                                } else if (itemPosition == 1) // for sw
                                {
                                    lang = "sw";
                                }
                            }
                        });

                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                }

//                if (position == 1) {
//                    try {
//                        final String[] option = getResources().getStringArray(R.array.add_options_arrays);
//
//                        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
//                        dialog.setContentView(R.layout.dialog_show_list);
//                        dialog.setTitle(getResources().getString(R.string.configure_data));
//                        dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
//                        final ListView listViewForCapture = (ListView) dialog.findViewById(R.id.commonlistview);
//                        Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
//                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_list_multiple_choice, option);
//                        listViewForCapture.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//
//                        listViewForCapture.setAdapter(adapter);
//
//                        //listViewForCapture.setItemChecked(0, true);
//
//                        btn_ok.setOnClickListener(new OnClickListener() {
//                            //Run when button is clicked
//                            @Override
//                            public void onClick(View v) {
//                                dialog.dismiss();
//                                dct = "";
//                                checkedPos_cdct = listViewForCapture.getCheckedItemPositions();
//                                for (int i = 0; i < option.length; i++) {
//                                    if (checkedPos_cdct.indexOfKey(i) != -1) {
//                                        if (checkedPos_cdct.get(i)) {
//                                            if (!dct.isEmpty())
//                                                dct = dct + ",";
//
//                                            //dct = dct + option[i];
//                                            dct = dct + i;
//                                        }
//                                    }
//                                }
//                                cf.saveDataCollectionTools(dct);
//                            }
//
//                        });
//
//                        if (dct.contains("0")) {
//                            listViewForCapture.setItemChecked(0, true);
//                        }
//
//
//                        if (dct.contains("1")) {
//                            listViewForCapture.setItemChecked(1, true);
//                        }
//
//
//                        if (dct.contains("2")) {
//                            listViewForCapture.setItemChecked(2, true);
//                        }
//
//                        dialog.show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
                if (position == 1) {
                    try {
                        String[] sync = getResources().getStringArray(R.array.Sync_arrays);

                        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
                        dialog.setContentView(R.layout.dialog_show_list);
                        dialog.setTitle(getResources().getString(R.string.auto_sync));
                        dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
                        final ListView listViewForSync = (ListView) dialog.findViewById(R.id.commonlistview);
                        Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                                R.layout.item_list_multiple_choice, sync);

                        listViewForSync.setAdapter(adapter);
                        //listViewForSync.setItemChecked(0, true);
                        listViewForSync.setChoiceMode(listView.CHOICE_MODE_MULTIPLE);

                        btn_ok.setOnClickListener(new OnClickListener() {
                            //Run when button is clicked
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                auto = false;
                                SparseBooleanArray checkedPos = listViewForSync.getCheckedItemPositions();

                                if (checkedPos.get(0)) {
                                    auto = true;
                                }
                                cf.saveAutoSync(auto);
                            }
                        });

                        listViewForSync.setOnItemClickListener(new OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {

                                }
                            }
                        });

                        if (auto) {
                            listViewForSync.setItemChecked(0, true);
                        }
                        dialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(e.getMessage());
                    }
                }
                if (position == 2) {
                    Intent intent = new Intent(context, ConfigureMapDisplay.class);
                    startActivity(intent);
                }

                if (position == 3) {
                    final List<AOI> aoiList = cf.getAOIList();
                    final Dialog dialog = new Dialog(context, R.style.DialogTheme);
                    dialog.setContentView(R.layout.dialog_show_list);
                    dialog.setTitle(getResources().getString(R.string.selectAOI));
                    dialog.getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;
                    final ListView listViewForPersonSubtype = (ListView) dialog.findViewById(R.id.commonlistview);
                    Button save = (Button) dialog.findViewById(R.id.btn_ok);
                    save.setText("OK");

//
                    ArrayAdapter<AOI> adapter = new ArrayAdapter<AOI>(context,
                            R.layout.item_list_single_choice, aoiList);

                    listViewForPersonSubtype.setAdapter(adapter);
                    for (int i = 0; i < aoiList.size(); i++) {
                        if (aoiList.get(i).getCoOrdinates().equalsIgnoreCase(cf.getAOICoordinates())) {
                            listViewForPersonSubtype.setItemChecked(i, true);
                            break;
                        }
                    }

                    listViewForPersonSubtype.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {

                            corrdinates = aoiList.get(position).getCoOrdinates();


                        }
                    });

                    save.setOnClickListener(new OnClickListener() {
                        //Run when button is clicked
                        @Override
                        public void onClick(View v) {

                            if (corrdinates == null) {
                                String info = getResources().getString(R.string.info);
                                String msg = "Please Select any Option.";
                                cf.showMessage(context, info, msg);

                            } else {
                                dialog.dismiss();
                                cf.saveAOI(corrdinates);
                                recreate();
                                //listViewForPersonSubtype.setItemChecked(position, true);
                                // cf.loadLocale(getApplicationContext());


                            }
                        }
                    });

                    dialog.show();
                }
                if (position==4){
                    // this Constructor is used only for bluetooth
                    commonFunctions=new CommonFunctions(UserPreferences.this);

                    //Call the bluetooth function
                    commonFunctions.getConnectToGpsDevice(isReview);
                }

            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode==1) //bluetooth enable
        {
            commonFunctions.discoverDevice();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(context, LandingPageActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
