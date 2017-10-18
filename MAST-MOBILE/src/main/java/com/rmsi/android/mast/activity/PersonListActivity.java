package com.rmsi.android.mast.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.Fragment.PersonListFragment;
import com.rmsi.android.mast.Fragment.PoiListFragment;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Gender;
import com.rmsi.android.mast.domain.Person;
import com.rmsi.android.mast.domain.PersonOfInterest;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.domain.RelationshipType;
import com.rmsi.android.mast.domain.ShareType;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;

public class PersonListActivity extends ActionBarActivity {

    Button addnewPerson, btnNext, addPOI;
    Context context;
    Long featureId = 0L;
    CommonFunctions cf = CommonFunctions.getInstance();
    String msg, warning;
    int position;
    private boolean readOnly = false;
    String warningStr, infoStr, shareTypeStr;
    String saveStr, backStr;
    private Property property;
    private PersonListFragment personsFragment;
    private PoiListFragment poiFragment;
    private int personSubType;
    private TextView lblShareType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            CommonFunctions.getInstance().Initialize(getApplicationContext());
        } catch (Exception e) {
        }
        cf.loadLocale(getApplicationContext());

        context = this;

        warningStr = getResources().getString(R.string.warning);
        infoStr = getResources().getString(R.string.info);
        shareTypeStr = getResources().getString(R.string.shareType);
        saveStr = getResources().getString(R.string.save);
        backStr = getResources().getString(R.string.back);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            featureId = extras.getLong("featureid");
        }

        readOnly = CommonFunctions.isFeatureReadOnly(featureId);
        setContentView(R.layout.activity_list);

        lblShareType = (TextView) findViewById(R.id.tenureType_lbl);
        addnewPerson = (Button) findViewById(R.id.btn_addNewPerson);
        btnNext = (Button) findViewById(R.id.btnNext);
        addPOI = (Button) findViewById(R.id.btn_addNextKin);
        personsFragment = (PersonListFragment) getFragmentManager().findFragmentById(R.id.compPersonsList);
        poiFragment = (PoiListFragment) getFragmentManager().findFragmentById(R.id.compPoiList);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_person);
        if (toolbar != null)
            setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (readOnly) {
            addnewPerson.setVisibility(View.GONE);
            addPOI.setVisibility(View.GONE);
            btnNext.setText(backStr);
        }

        addnewPerson.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String[] person_subType = getResources().getStringArray(R.array.person_sub_type);
                    final Dialog dialog = new Dialog(context, R.style.DialogTheme);
                    dialog.setContentView(R.layout.dialog_show_list);
                    dialog.setTitle(getResources().getString(R.string.select_person_subtype));
                    dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;
                    ListView listViewForPersonSubtype = (ListView) dialog.findViewById(R.id.commonlistview);
                    Button save = (Button) dialog.findViewById(R.id.btn_ok);
                    save.setText(saveStr);

                    personSubType = Person.SUBTYPE_OWNER;
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                            R.layout.item_list_single_choice, person_subType);

                    listViewForPersonSubtype.setAdapter(adapter);
                    listViewForPersonSubtype.setItemChecked(0, false);

                    listViewForPersonSubtype.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {
                            int itemPosition = position;

                            if (itemPosition == 0) // for Owner
                            {
                                personSubType = Person.SUBTYPE_OWNER;
                            } else if (itemPosition == 1) // for Administrator
                            {
                                personSubType = Person.SUBTYPE_ADMINISTRATOR;
                            } else if (itemPosition == 2) // for Guardian
                            {
                                personSubType = Person.SUBTYPE_GUARDIAN;
                            }
                        }
                    });

                    save.setOnClickListener(new OnClickListener() {
                        //Run when button is clicked
                        @Override
                        public void onClick(View v) {
                            boolean ifAllowed = false;
                            int ownerCount = property.getRight().getOwnersCount();

                            if (property.getRight().getShareTypeId() == ShareType.TYPE_SINGLE_OCCUPANT) {
                                ifAllowed = checkSingleOccupancyType(personSubType, ownerCount);
                                if (ifAllowed) {
                                    Intent myIntent = new Intent(context, AddPersonActivity.class);
                                    myIntent.putExtra("groupid", 0L);
                                    myIntent.putExtra("featureid", featureId);
                                    myIntent.putExtra("rightId", property.getRight().getId());
                                    myIntent.putExtra("subTypeId", personSubType);
                                    startActivity(myIntent);
                                    dialog.dismiss();
                                }
                            } else if (property.getRight().getShareTypeId() == ShareType.TYPE_MUTIPLE_OCCUPANCY_JOINT) {
                                ifAllowed = checkMultipleOcuupany_Joint(personSubType, ownerCount);
                                if (ifAllowed) {
                                    Intent myIntent = new Intent(context, AddPersonActivity.class);
                                    myIntent.putExtra("groupid", 0L);
                                    myIntent.putExtra("featureid", featureId);
                                    myIntent.putExtra("rightId", property.getRight().getId());
                                    myIntent.putExtra("subTypeId", personSubType);
                                    startActivity(myIntent);
                                    dialog.dismiss();
                                }
                            } else if (property.getRight().getShareTypeId() == ShareType.TYPE_MUTIPLE_OCCUPANCY_IN_COMMON) {
                                ifAllowed = checkMultipleOcuupany_TenancyInCommon(personSubType);
                                if (ifAllowed) {
                                    Intent myIntent = new Intent(context, AddPersonActivity.class);
                                    myIntent.putExtra("groupid", 0L);
                                    myIntent.putExtra("featureid", featureId);
                                    myIntent.putExtra("rightId", property.getRight().getId());
                                    myIntent.putExtra("subTypeId", personSubType);
                                    startActivity(myIntent);
                                    dialog.dismiss();
                                }
                            } else if (property.getRight().getShareTypeId() == ShareType.TYPE_GUARDIAN) {
                                ifAllowed = checkOccupancyType_GuardianMinor(personSubType, ownerCount);
                                if (ifAllowed) {
                                    Intent myIntent = new Intent(context, AddPersonActivity.class);
                                    myIntent.putExtra("groupid", 0L);
                                    myIntent.putExtra("featureid", featureId);
                                    myIntent.putExtra("rightId", property.getRight().getId());
                                    myIntent.putExtra("subTypeId", personSubType);
                                    startActivity(myIntent);
                                    dialog.dismiss();
                                }
                            }
                        }
                    });

                    dialog.show();

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
            }
        });

        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!readOnly) {
                    if(property.validatePersonsList(context, true)){
                        Intent myIntent = new Intent(context, MediaListActivity.class);
                        myIntent.putExtra("featureid", featureId);
                        startActivity(myIntent);
                    }
                } else {
                    finish();
                }
            }
        });

        addPOI.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (property.getRight().hasPersonSubType(Person.SUBTYPE_OWNER)) {
                    if (property.getRight().getShareTypeId() == ShareType.TYPE_SINGLE_OCCUPANT ||
                            property.getRight().getShareTypeId() == ShareType.TYPE_MUTIPLE_OCCUPANCY_IN_COMMON ||
                            property.getRight().getShareTypeId() == ShareType.TYPE_MUTIPLE_OCCUPANCY_JOINT) {
                        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
                        dialog.setContentView(R.layout.dialog_person_of_interest);
                        dialog.setTitle(getResources().getString(R.string.nextKin));
                        dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;

                        Button save = (Button) dialog.findViewById(R.id.btn_ok);
                        final EditText firstName = (EditText) dialog.findViewById(R.id.editTextFirstName);
                        final EditText middleName = (EditText) dialog.findViewById(R.id.editTextMiddleName);
                        final EditText lastName = (EditText) dialog.findViewById(R.id.editTextLastName);
                        final Spinner genderSpinner = (Spinner) dialog.findViewById(R.id.spinnerGender);
                        final Spinner relSpinner = (Spinner) dialog.findViewById(R.id.spinnerRelationshipType);
                        final TextView txtDob = (TextView) dialog.findViewById(R.id.txtDob);
                        LinearLayout extraFields = (LinearLayout) dialog.findViewById(R.id.extraLayout);
                        extraFields.setVisibility(View.VISIBLE);

                        DbController db = DbController.getInstance(context);

                        genderSpinner.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, db.getGenders(true)));
                        relSpinner.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, db.getRelationshipTypes(true)));
                        ((ArrayAdapter) genderSpinner.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        ((ArrayAdapter) relSpinner.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                        txtDob.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                GuiUtility.showDatePicker(txtDob, "");
                            }
                        });

                        save.setText(saveStr);

                        save.setOnClickListener(new OnClickListener() {
                            //Run when button is clicked
                            @Override
                            public void onClick(View v) {
                                String poi_fName = firstName.getText().toString();
                                String poi_middleName = middleName.getText().toString();
                                String poi_lastName = lastName.getText().toString();
                                String name = firstName.getText().toString() + " " + middleName.getText().toString() + " " + lastName.getText().toString();

                                if (!TextUtils.isEmpty(poi_fName) || !TextUtils.isEmpty(poi_middleName) || !TextUtils.isEmpty(poi_lastName)) {
                                    PersonOfInterest poi = new PersonOfInterest();
                                    poi.setFeatureId(featureId);
                                    if(genderSpinner.getSelectedItem() != null)
                                        poi.setGenderId(((Gender)genderSpinner.getSelectedItem()).getCode());
                                    if(relSpinner.getSelectedItem() != null)
                                        poi.setRelationshipId(((RelationshipType)relSpinner.getSelectedItem()).getCode());
                                    poi.setDob(txtDob.getText().toString());
                                    poi.setName(name);

                                    boolean result = DbController.getInstance(context).savePersonOfInterest(poi);
                                    if (result) {
                                        property.getPersonOfInterests().add(poi);
                                        poiFragment.refresh();
                                        msg = getResources().getString(R.string.AddedSuccessfully);
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                    } else {
                                        warning = getResources().getString(R.string.UnableToSave);
                                        Toast.makeText(context, warning, Toast.LENGTH_LONG).show();
                                    }
                                    dialog.dismiss();
                                } else {
                                    msg = getResources().getString(R.string.enter_details);
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        dialog.show();
                    } else if (property.getRight().getShareTypeId() == ShareType.TYPE_GUARDIAN) {
                        msg = getResources().getString(R.string.can_not_add_poi);
                        cf.showMessage(context, warningStr, msg);
                    }
                } else {
                    msg = getResources().getString(R.string.add_owner_first);
                    cf.showMessage(context, warning, msg);
                }
            }

        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        DbController db = DbController.getInstance(context);
        property = db.getProperty(featureId);

        if (property == null || property.getRight() == null) {
            cf.showToast(context, R.string.RightNotFound, Toast.LENGTH_SHORT);
            return;
        }

        if (property.getRight().getShareTypeId() > 0) {
            ShareType shareType = db.getShareType(property.getRight().getShareTypeId());
            if (shareType != null)
                lblShareType.setText(shareTypeStr + ": " + shareType.toString());
        }

        personsFragment.setPersons(property.getRight().getNaturalPersons(), readOnly);
        poiFragment.setPersons(property.getPersonOfInterests(), readOnly);
    }

    public boolean checkSingleOccupancyType(int personSubType, int ownerCount) {
        boolean flag = false;
        if (ownerCount == 0) {
            //allow
            if (personSubType == Person.SUBTYPE_OWNER) {
                return flag = true;
            } else if (personSubType == Person.SUBTYPE_ADMINISTRATOR || personSubType == Person.SUBTYPE_GUARDIAN) {
                msg = getResources().getString(R.string.can_not_add_adminGuardian_in_SingleOccuapncy);
                cf.showMessage(context, infoStr, msg);
                return flag = false;
            }
        } else if (ownerCount > 0) {
            if (personSubType == Person.SUBTYPE_OWNER) {
                msg = getResources().getString(R.string.can_not_add_more_than_one_owner_in_SingleOccuapncy);
                cf.showMessage(context, infoStr, msg);
                return flag = false;
            } else if (personSubType == Person.SUBTYPE_ADMINISTRATOR || personSubType == Person.SUBTYPE_GUARDIAN) {
                msg = getResources().getString(R.string.can_not_add_adminGuardian_in_SingleOccuapncy);
                cf.showMessage(context, infoStr, msg);
                return flag = false;
            }
        }
        return flag;
    }

    public boolean checkMultipleOcuupany_Joint(int personSubType, int ownerCount)   //Only 2 owner can be added in case of Multiple Occupancy(Joint Tenancy)
    {
        boolean flag = false;
        if (ownerCount <= 1) {
            //allow
            if (personSubType == Person.SUBTYPE_OWNER) {
                return flag = true;
            } else if (personSubType == Person.SUBTYPE_ADMINISTRATOR || personSubType == Person.SUBTYPE_GUARDIAN) {
                msg = getResources().getString(R.string.can_not_add_adminGuardian_in_multipleOccuapncy_joint);
                cf.showMessage(context, infoStr, msg);
                return flag = false;
            }
        } else if (ownerCount == 2) {
            if (personSubType == Person.SUBTYPE_OWNER) {
                msg = getResources().getString(R.string.can_not_add_more_than_two_owners_in_multipleOccuapncy_joint);
                cf.showMessage(context, infoStr, msg);
                return flag = false;
            } else if (personSubType == Person.SUBTYPE_ADMINISTRATOR || personSubType == Person.SUBTYPE_GUARDIAN) {
                msg = getResources().getString(R.string.can_not_add_adminGuardian_in_multipleOccuapncy_joint);
                cf.showMessage(context, infoStr, msg);
                return flag = false;
            }
        }
        return flag;
    }

    public boolean checkMultipleOcuupany_TenancyInCommon(int personSubType)   //more than 2 owner only can be added in case of Multiple Occupancy(Tenancy in Common)
    {
        boolean flag = false;
        //allow
        if (personSubType == Person.SUBTYPE_OWNER) {
            return flag = true;
        } else if (personSubType == Person.SUBTYPE_ADMINISTRATOR || personSubType == Person.SUBTYPE_GUARDIAN) {
            msg = getResources().getString(R.string.can_not_add_adminGuardian_in_multipleOccuapncy_common);
            cf.showMessage(context, infoStr, msg);
            return flag = false;
        }
        return flag;
    }

    public boolean checkOccupancyType_GuardianMinor(int personSubType, int ownerCount)   //more than 2 owner only can be added in case of Multiple Occupancy(Tenancy in Common)
    {
        int minorCount = property.getRight().getMinorCount();
        int guardianCount = property.getRight().getGuardianCount();
        boolean flag = false;
        if (personSubType == Person.SUBTYPE_OWNER) {
            return flag = true;
        } else if (personSubType == Person.SUBTYPE_ADMINISTRATOR) {
            msg = getResources().getString(R.string.can_not_add_admin_in_cas_of_guardian_minor);
            cf.showMessage(context, infoStr, msg);
            return flag = false;
        } else if (personSubType == Person.SUBTYPE_GUARDIAN) {
            if (ownerCount >= 1) {
                if (guardianCount < ownerCount && guardianCount < 2) {
                    return flag = true;
                } else if (guardianCount == 2) {
                    msg = getResources().getString(R.string.guardian_can_not_more_than_two);
                    cf.showMessage(context, infoStr, msg);       //
                    return false;
                }
            } else {
                msg = getResources().getString(R.string.add_owner_first);
                cf.showMessage(context, infoStr, msg);
                return flag = false;
            }
        }
        return flag;
    }
}
