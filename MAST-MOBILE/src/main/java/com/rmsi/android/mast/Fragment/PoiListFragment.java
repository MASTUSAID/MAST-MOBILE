package com.rmsi.android.mast.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmsi.android.mast.activity.ListActivity;
import com.rmsi.android.mast.activity.PersonListActivity;
import com.rmsi.android.mast.activity.R;
import com.rmsi.android.mast.adapter.PersonOfInterestListAdapter;
import com.rmsi.android.mast.db.DbController;
import com.rmsi.android.mast.domain.Gender;
import com.rmsi.android.mast.domain.PersonOfInterest;
import com.rmsi.android.mast.domain.RelationshipType;
import com.rmsi.android.mast.domain.User;
import com.rmsi.android.mast.util.CommonFunctions;
import com.rmsi.android.mast.util.DateUtility;
import com.rmsi.android.mast.util.GuiUtility;

import java.util.List;

public class PoiListFragment extends ListFragment implements ListActivity {
    private Context context;
    private PersonOfInterestListAdapter adapter;
    private List<PersonOfInterest> persons;
    private boolean readOnly = false;
    public PoiListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        return view;
    }

    /**
     * Sets persons list
     */
    public void setPersons(List<PersonOfInterest> persons, boolean readOnly) {
        this.persons = persons;
        this.readOnly = readOnly;
        adapter = new PersonOfInterestListAdapter(context, this, persons);
        setListAdapter(adapter);
        refresh();
    }

    @Override
    public void showPopup(View v, int position) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();

        inflater.inflate(R.menu.attribute_listing_options_for_poi, popup.getMenu());

        final PersonOfInterest person = persons.get(position);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        edit(person);
                        return true;
                    case R.id.delete_entry:
                        delete(person);
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

    private void edit(final PersonOfInterest person) {
        final Dialog dialog = new Dialog(context, R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog_person_of_interest);
        dialog.setTitle(getResources().getString(R.string.nextKin));
        dialog.getWindow().getAttributes().width = ViewGroup.LayoutParams.MATCH_PARENT;

        Button save = (Button) dialog.findViewById(R.id.btn_ok);
        final EditText firstName = (EditText) dialog.findViewById(R.id.editTextFirstName);
        final EditText middleName = (EditText) dialog.findViewById(R.id.editTextMiddleName);
        final EditText lastName = (EditText) dialog.findViewById(R.id.editTextLastName);
        final Spinner genderSpinner = (Spinner) dialog.findViewById(R.id.spinnerGender);
        final Spinner relSpinner = (Spinner) dialog.findViewById(R.id.spinnerRelationshipType);
        final TextView txtDob = (TextView) dialog.findViewById(R.id.txtDob);
        LinearLayout extraFields = (LinearLayout) dialog.findViewById(R.id.extraLayout);
        extraFields.setVisibility(View.VISIBLE);

        save.setText(getResources().getString(R.string.save));

        DbController db = DbController.getInstance(context);

        List<RelationshipType> relTypes = db.getRelationshipTypes(true);
        List<Gender> genders = db.getGenders(true);

        genderSpinner.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, genders));
        relSpinner.setAdapter(new ArrayAdapter(context, android.R.layout.simple_spinner_item, relTypes));
        ((ArrayAdapter) genderSpinner.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ((ArrayAdapter) relSpinner.getAdapter()).setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        txtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuiUtility.showDatePicker(txtDob, person.getDob());
            }
        });

        // Init fields
        txtDob.setText(DateUtility.formatDateString(person.getDob()));

        for (int i = 0; i < relTypes.size(); i++) {
            if (relTypes.get(i).getCode() > 0 && relTypes.get(i).getCode() == person.getRelationshipId()) {
                relSpinner.setSelection(i);
                break;
            }
        }

        for (int i = 0; i < genders.size(); i++) {
            if (genders.get(i).getCode() > 0 && genders.get(i).getCode() == person.getGenderId()) {
                genderSpinner.setSelection(i);
                break;
            }
        }

        String[] separated = person.getName().split(" ");

        if (separated.length == 1) {
            String fName = separated[0];
            firstName.setText(fName);
            middleName.setText("");
            lastName.setText("");
        } else if (separated.length == 2) {
            String fName = separated[0];
            String mName = separated[1];
            firstName.setText(fName);
            middleName.setText(mName);
            lastName.setText("");
        } else if (separated.length == 3) {
            String fName = separated[0];
            String mName = separated[1];
            String lName = separated[2];
            firstName.setText(fName);
            middleName.setText(mName);
            lastName.setText(lName);
        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = firstName.getText().toString() + " " + middleName.getText().toString() + " " + lastName.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    person.setName(name);
                    if(genderSpinner.getSelectedItem() != null)
                        person.setGenderId(((Gender)genderSpinner.getSelectedItem()).getCode());
                    else
                        person.setGenderId(0);
                    if(relSpinner.getSelectedItem() != null)
                        person.setRelationshipId(((RelationshipType)relSpinner.getSelectedItem()).getCode());
                    else
                    person.setRelationshipId(0);
                    person.setDob(txtDob.getText().toString());

                    if (DbController.getInstance(context).savePersonOfInterest(person)) {
                        Toast.makeText(context, "Edited", Toast.LENGTH_LONG).show();
                        refresh();
                    } else {
                        Toast.makeText(context, getResources().getString(R.string.UnableToSave), Toast.LENGTH_LONG).show();
                    }
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, getResources().getString(R.string.nextOfKin), Toast.LENGTH_LONG).show();
                }
            }
        });

        dialog.show();
    }

    private void delete(PersonOfInterest person)
    {
        if (DbController.getInstance(context).deletePersonOfInterest(person.getId())) {
            persons.remove(person);
            Toast.makeText(context,getResources().getString(R.string.deleted), Toast.LENGTH_LONG).show();
            refresh();
        } else {
            Toast.makeText(context, getResources().getString(R.string.unable_delete), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Refreshes the list
     */
    public void refresh() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
