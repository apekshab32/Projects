package com.hari.autotasx;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.*;
import com.github.clans.fab.FloatingActionButton;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.hari.autotasx.ActionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.hari.autotasx.ActionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActionFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int PICK_CONTACT = 3;
    private CheckBox silent;
    private CheckBox wifi;
    private CheckBox sms;
    private CheckBox rem;
    private CheckBox bluetooth;
    //private Button add;
    public static int silVar;
    public static int wifiVar;
    public static int smsVar;
    public static int remVar;
    public static int blueVar;
    public static String smsNo;
    String id;
    private OnFragmentInteractionListener mListener;
    EditText name,remMsg=null;
    TextView remLabel;
    public  static GeoFence geoFence = new GeoFence();

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ActionFragment newInstance(int sectionNumber) {
        ActionFragment fragment = new ActionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ActionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_action, container, false);
        // setting handlers for EditText View
        name =  (EditText) rootView.findViewById(R.id.editText);
        remMsg =  (EditText) rootView.findViewById(R.id.editText2);

        // setting handlers for Checkbox View
        rem =   (CheckBox) rootView.findViewById(R.id.rem);
        silent= (CheckBox)rootView.findViewById(R.id.silent);
        wifi= (CheckBox)rootView.findViewById(R.id.wifi);
        sms= (CheckBox)rootView.findViewById(R.id.sms);
        bluetooth= (CheckBox)rootView.findViewById(R.id.bluetooth);
        //add =(Button)rootView.findViewById(R.id.add);
        // setting handlers for TextView
        remLabel= (TextView)rootView.findViewById(R.id.textView1);

        //checking what actions are set
        silent.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {
                    silVar=1;
                }
                else {
                    silVar=0;
                }

            }
        });
        wifi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {
                    wifiVar=1;
                }
                else{
                    wifiVar=0;
                }

            }
        });
        sms.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {
                    smsVar=1;
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                    startActivityForResult(intent, PICK_CONTACT);
                }
                else{
                    smsVar=0;

                }

            }
        });

        rem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {

                    remMsg.setVisibility(View.VISIBLE);
                    remLabel.setVisibility(View.VISIBLE);
                    remVar=1;
                }
                else{

                    remMsg.setVisibility(View.INVISIBLE);
                    remLabel.setVisibility(View.INVISIBLE);
                    remVar=0;
                }

            }
        });
        bluetooth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {
                    blueVar=1;
                }
                else{
                    blueVar=0;

                }

            }
        });
        // setting handlers for Floating Button
       com.github.clans.fab.FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.menu_item);

        //Floating action button handler
       fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), MapsActivity.class);

                if(name.getText().toString().equals("")){

                    Toast.makeText(getActivity(),"Plese enter a profile name",Toast.LENGTH_SHORT).show();

                }
                else {
                    geoFence.setNameLoc(name.getText().toString());
                    geoFence.setRemmsg(remMsg.getText().toString());
                    //Toast.makeText(getActivity(),"jvjahjvhwagajhwaa"+geoFence,Toast.LENGTH_SHORT).show();
                    ManageDB autodb = new ManageDB(getActivity());
                    autodb.open();
                    autodb.addLocation(ActionFragment.geoFence);
                    autodb.close();
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }
    public void onActivityResult(int reqCode, int resultCode, Intent data){
        super.onActivityResult(reqCode, resultCode, data);
        //String res_cont=null;
        switch(reqCode){
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK){
                    Uri contactData = data.getData();
                    Cursor c = getActivity().getContentResolver().query(contactData, null, null, null, null);
                    //int idIdx =c.getColumnIndexOrThrow (ContactsContract.CommonDataKinds.Phone._ID);

                    if (c.moveToFirst()){
                           String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                           smsNo = c.getString(c.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), smsNo, Toast.LENGTH_SHORT).show();
                            //insert no into
                    }
                }
        }
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }
}
