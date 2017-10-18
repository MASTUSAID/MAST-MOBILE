package com.rmsi.android.mast.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuInflater;
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
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.Fragment.PersonListFragment;
import com.rmsi.android.mast.Fragment.PoiListFragment;
import com.rmsi.android.mast.adapter.DeceasedPersonListAdapter;
import com.rmsi.android.mast.adapter.SpinnerAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.DeceasedPerson;
import com.rmsi.android.mast.domain.Gender;
import com.rmsi.android.mast.domain.Person;
import com.rmsi.android.mast.domain.PersonOfInterest;
import com.rmsi.android.mast.domain.Property;
import com.rmsi.android.mast.domain.RelationshipType;
import com.rmsi.android.mast.domain.ShareType;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.GuiUtility;
import com.rmsi.android.mast.util.StringUtility;

public class PersonListWithDPActivity extends ActionBarActivity {
    Button addnewPerson, btnNext, addPOI, addDeceased;
    Context context;
    ListView listOfDeceasedPerson;
    DeceasedPersonListAdapter dpAdapter;
    Long featureId = 0L;
    CommonFunctions cf = CommonFunctions.getInstance();
    String msg, info, warning;
    int position;
    String warningStr, infoTenancyInProbateStr, personStr, person_of_InterestStr, deceasedPersonStr, personPhotoStr, saveStr, backStr;
    private Property property;
    private PersonListFragment personsFragment;
    private PoiListFragment poiFragment;
    private int personSubType;
    private List<DeceasedPerson> deceasedPersons = new ArrayList<>();
    private boolean firstRun = true;
    private boolean readOnly = false;

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
        infoTenancyInProbateStr = getResources().getString(R.string.warning_tenancyProbate);
        personStr = getResources().getString(R.string.person);
        person_of_InterestStr = getResources().getString(R.string.person_of_interest);
        deceasedPersonStr = getResources().getString(R.string.deceased_person);
        saveStr = getResources().getString(R.string.save);
        backStr = getResources().getString(R.string.back);

        final DbController db = DbController.getInstance(context);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            featureId = extras.getLong("featureid");
        }
        readOnly = CommonFunctions.isFeatureReadOnly(featureId);

        setContentView(R.layout.activity_list_with_dp);

        TextView lblShareType = (TextView) findViewById(R.id.tenureType_lbl);
        addnewPerson = (Button) findViewById(R.id.btn_addNewPerson);
        btnNext = (Button) findViewById(R.id.btnNext);
        addPOI = (Button) findViewById(R.id.btn_addNextKin);
        addDeceased = (Button) findViewById(R.id.btn_addDP);
        listOfDeceasedPerson = (ListView) findViewById(R.id.list_of_deceased_person);
        personsFragment = (PersonListFragment) getFragmentManager().findFragmentById(R.id.compPersonsList);
        poiFragment = (PoiListFragment) getFragmentManager().findFragmentById(R.id.compPoiList);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_person);
        if (toolbar != null)
            setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        property = db.getProperty(featureId);

        if(property == null || property.getRight() == null){
            cf.showToast(context, R.string.RightNotFound, Toast.LENGTH_SHORT);
            return;
        }

        if (property.getRight().getShareTypeId() > 0) {
            ShareType shareType = db.getShareType(property.getRight().getShareTypeId());
            if (shareType != null)
                lblShareType.setText(getResources().getString(R.string.shareType) + ": " + shareType.getName());
        }

        personsFragment.setPersons(property.getRight().getNaturalPersons(), readOnly);
        poiFragment.setPersons(property.getPersonOfInterests(), readOnly);

        if (property.getDeceasedPerson() != null) {
            deceasedPersons.add(property.getDeceasedPerson());
        }

        dpAdapter = new DeceasedPersonListAdapter(context, this, deceasedPersons);
        listOfDeceasedPerson.setAdapter(dpAdapter);

        if (readOnly) {
            addnewPerson.setVisibility(View.GONE);
            addPOI.setVisibility(View.GONE);
            addDeceased.setVisibility(View.GONE);
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
                            int adminCount = property.getRight().getAdministratorCount();
                            if (property.getRight().getShareTypeId() == ShareType.TYPE_TENANCY_IN_PROBATE) {
                                ifAllowed = checkOccupancyType_tenancy_inProbate(personSubType, ownerCount, adminCount);
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
                boolean isAdminExist = property.getRight().hasPersonSubType(Person.SUBTYPE_ADMINISTRATOR);
                boolean isOwnerExist = property.getRight().hasPersonSubType(Person.SUBTYPE_OWNER);

                if (isOwnerExist || isAdminExist) {
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

                } else {
                    msg = getResources().getString(R.string.add_owner_first);
                    warning = getResources().getString(R.string.warning);
                    cf.showMessage(context, warning, msg);
                }
            }
        });

        addDeceased.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean isAdminExist = property.getRight().hasPersonSubType(Person.SUBTYPE_ADMINISTRATOR);
                boolean isOwnerExist = property.getRight().hasPersonSubType(Person.SUBTYPE_OWNER);

                if (isOwnerExist || isAdminExist) {
                    if (property.getDeceasedPerson() == null) {
                        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
                        dialog.setContentView(R.layout.dialog_person_of_interest);
                        dialog.setTitle(getResources().getString(R.string.tittle_addDeceased));
                        dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;

                        Button save = (Button) dialog.findViewById(R.id.btn_ok);
                        final EditText firstName = (EditText) dialog.findViewById(R.id.editTextFirstName);
                        final EditText middleName = (EditText) dialog.findViewById(R.id.editTextMiddleName);
                        final EditText lastName = (EditText) dialog.findViewById(R.id.editTextLastName);
                        LinearLayout extraFields = (LinearLayout) dialog.findViewById(R.id.extraLayout);
                        extraFields.setVisibility(View.GONE);

                        save.setText(saveStr);

                        save.setOnClickListener(new OnClickListener() {
                            //Run when button is clicked
                            @Override
                            public void onClick(View v) {
                                String dp_fName = firstName.getText().toString();
                                String dp_middleName = middleName.getText().toString();
                                String dp_lastName = lastName.getText().toString();

                                if (!TextUtils.isEmpty(dp_fName) || !TextUtils.isEmpty(dp_middleName) || !TextUtils.isEmpty(dp_lastName)) {
                                    DeceasedPerson person = new DeceasedPerson();
                                    person.setFeatureId(featureId);
                                    person.setFirstName(dp_fName);
                                    person.setLastName(dp_lastName);
                                    person.setMiddleName(dp_middleName);

                                    boolean result = db.saveDeceasedPerson(person);
                                    if (result) {
                                        property.setDeceasedPerson(person);
                                        deceasedPersons.add(person);

                                        msg = getResources().getString(R.string.AddedSuccessfully);
                                        Toast.makeText(PersonListWithDPActivity.this, msg, Toast.LENGTH_LONG).show();
                                        dpAdapter.notifyDataSetChanged();
                                    } else {
                                        warning = getResources().getString(R.string.UnableToSave);
                                        Toast.makeText(PersonListWithDPActivity.this, warning, Toast.LENGTH_LONG).show();
                                    }
                                    dialog.dismiss();
                                } else {
                                    msg = getResources().getString(R.string.enter_details);
                                    Toast.makeText(PersonListWithDPActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        dialog.show();

                    } else {
                        msg = getResources().getString(R.string.can_not_add_moreThanOne_dp_tenancy_inProbate);
                        warning = getResources().getString(R.string.warning);
                        cf.showMessage(context, warning, msg);
                    }
                } else {
                    msg = getResources().getString(R.string.add_owner_first);
                    warning = getResources().getString(R.string.warning);
                    cf.showMessage(context, warning, msg);
                }
            }
        });
    }

    public void showPopupForDP(View v, int position) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.attribute_listing_options_for_poi, popup.getMenu());

        final DeceasedPerson person = deceasedPersons.get(position);

        popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        edit_DP(person);
                        return true;
                    case R.id.delete_entry:
                        delete_DP(person);
                        return true;
                    default:
                        return false;
                }
            }
        });
        if (!readOnly) {
            popup.show();
        }
    }

    private void refereshList() {
        personsFragment.refresh();
        poiFragment.refresh();
        dpAdapter.notifyDataSetChanged();
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

        if (!firstRun) {
            // Refresh persons list
            List<Person> persons = DbController.getInstance(context).getNaturalPersonsByRight(property.getRight().getId());
            property.getRight().getNaturalPersons().clear();
            property.getRight().getNaturalPersons().addAll(persons);
        }
        firstRun = false;
        personsFragment.setPersons(property.getRight().getNaturalPersons(), readOnly);
    }

    public void edit_DP(final DeceasedPerson person) {
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_person_of_interest);
        dialog.setTitle(getResources().getString(R.string.nextKin));
        dialog.getWindow().getAttributes().width = LayoutParams.MATCH_PARENT;

        Button save = (Button) dialog.findViewById(R.id.btn_ok);
        final EditText firstName = (EditText) dialog.findViewById(R.id.editTextFirstName);
        final EditText middleName = (EditText) dialog.findViewById(R.id.editTextMiddleName);
        final EditText lastName = (EditText) dialog.findViewById(R.id.editTextLastName);
        LinearLayout extraFields = (LinearLayout) dialog.findViewById(R.id.extraLayout);
        extraFields.setVisibility(View.GONE);
        save.setText("Save");

        firstName.setText(StringUtility.empty(person.getFirstName()));
        middleName.setText(StringUtility.empty(person.getMiddleName()));
        lastName.setText(StringUtility.empty(person.getLastName()));

        save.setOnClickListener(new OnClickListener() {
            //Run when button is clicked
            @Override
            public void onClick(View v) {
                if (!StringUtility.isEmpty(firstName.getText().toString()) ||
                        !StringUtility.isEmpty(lastName.getText().toString()) ||
                        !StringUtility.isEmpty(middleName.getText().toString())) {

                    person.setFirstName(firstName.getText().toString());
                    person.setLastName(lastName.getText().toString());
                    person.setMiddleName(middleName.getText().toString());

                    boolean result = DbController.getInstance(context).saveDeceasedPerson(person);

                    if (result) {
                        dpAdapter.notifyDataSetChanged();
                        Toast.makeText(PersonListWithDPActivity.this, "Edited", Toast.LENGTH_LONG).show();
                    } else {
                        warning = getResources().getString(R.string.UnableToSave);
                        Toast.makeText(PersonListWithDPActivity.this, warning, Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                } else {
                    msg = getResources().getString(R.string.nextOfKin);
                    Toast.makeText(PersonListWithDPActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();
    }

    public void delete_DP(DeceasedPerson person) {
        msg = getResources().getString(R.string.Please_delete_Owner_first);
        warning = getResources().getString(R.string.warning);
        info = getResources().getString(R.string.unable_delete);

        boolean result = DbController.getInstance(context).deleteDeceasedPerson(person.getId());

        if (result) {
            deceasedPersons.remove(person);
            property.setDeceasedPerson(null);
            dpAdapter.notifyDataSetChanged();
            Toast.makeText(context, getResources().getString(R.string.deleted), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkOccupancyType_tenancy_inProbate(int personSubType, int ownerCount, int adminCount)   //Only 2 owner can be added in case of TENANCY IN PROBATE)
    {
        boolean flag = false;

        if (personSubType == Person.SUBTYPE_GUARDIAN || personSubType == Person.SUBTYPE_OWNER) {
            msg = getResources().getString(R.string.you_can_not_add_Guardian_in_tenancy_in_Probate);
            warning = getResources().getString(R.string.warning);
            cf.showMessage(context, warning, msg);
            return false;
        }

        if (personSubType == Person.SUBTYPE_ADMINISTRATOR) {
            if (adminCount < ownerCount) {
                if (adminCount == 2) {
                    msg = getResources().getString(R.string.Administrator_can_not_be_more_than_two_in_tenancy_in_Probate);
                    warning = getResources().getString(R.string.warning);
                    cf.showMessage(context, warning, msg);
                    return flag = false;
                } else if (adminCount < 2) {
                    return flag = true;
                }
            } else if (adminCount == 2) {
                msg = getResources().getString(R.string.Administrator_can_not_be_more_than_two_in_tenancy_in_Probate);
                warning = getResources().getString(R.string.warning);
                cf.showMessage(context, warning, msg);
                return flag = false;
            } else if (adminCount < 2) {
                return flag = true;
            } else {
                msg = getResources().getString(R.string.administrator_can_not_be_more_tha_Owner);
                warning = getResources().getString(R.string.warning);
                cf.showMessage(context, warning, msg);
                return flag = false;
            }
        }
        return flag;
    }
}
