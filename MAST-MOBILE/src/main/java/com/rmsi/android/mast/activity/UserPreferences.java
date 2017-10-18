package com.rmsi.android.mast.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.util.CommonFunctions;


public class UserPreferences extends ActionBarActivity {
    ListView listView;
    Context context = this;
    CommonFunctions cf = CommonFunctions.getInstance();
    String lang = "";
    String dct = "";

    Editor editor;
    Dialog dialog = null;
    boolean auto;
    SharedPreferences sharedpreferences;
    SparseBooleanArray checkedPos_cdct;

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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

            }
        });
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
