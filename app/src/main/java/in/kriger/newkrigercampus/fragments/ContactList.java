package in.kriger.newkrigercampus.fragments;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;


import es.dmoral.toasty.Toasty;
import in.kriger.newkrigercampus.activities.ContactDenyActivity;
import in.kriger.newkrigercampus.activities.HomeActivity;
import in.kriger.newkrigercampus.classes.Contacts;
import in.kriger.newkrigercampus.adapters.Adapter_Contacts;
import in.kriger.newkrigercampus.R;
import in.kriger.newkrigercampus.database.DatabaseHelper;
import in.kriger.newkrigercampus.extras.ContactInterface;
import in.kriger.newkrigercampus.services.KrigerConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactList extends Fragment {


    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;


    private RecyclerView recyclerView_contact;

    private ArrayList<Contacts> contactslist = new ArrayList<>();

    private Button invite_all;


    private TextView no_contacts;

    Dialog progress;

    DatabaseHelper db;

    EditText search_box;
    Adapter_Contacts adapter1;


    public ContactList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_contact_list, container, false);


        db = new DatabaseHelper(getContext());

        recyclerView_contact =  rootview.findViewById(R.id.recycler_view_contact);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());
        recyclerView_contact.setLayoutManager(mLayoutManager);
        recyclerView_contact.setItemAnimator(new DefaultItemAnimator());

        search_box =  rootview.findViewById(R.id.search_box);
        search_box.addTextChangedListener(filterTextWatcher);



        //Invite All button
        invite_all =  rootview.findViewById(R.id.btn_inviteall);

        final Animation myAnim = AnimationUtils.loadAnimation(getContext(), R.anim.bounce);
        invite_all.startAnimation(myAnim);

        invite_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (getContext() != null) {
                    progress = new Dialog(getContext());
                    progress.setContentView(R.layout.dialog_progress);
                    progress.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                    progress.setCancelable(false);
                    progress.show();

                }


                final DatabaseReference sentinviteRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Sent_Invite").child(user.getUid());

                final String key = sentinviteRef.push().getKey();

                ArrayList<HashMap<String, Object>> uploadlist = new ArrayList<>();

                for (int i = 0; i < contactslist.size(); i++) {
                    if (contactslist.get(i).getEmail()) {
                        Contacts contacts = contactslist.get(i);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("contact", contacts.getEph());
                        map.put("name", contacts.getName());
                        uploadlist.add(map);
                    }


                }

                sentinviteRef.child("email").setValue(uploadlist).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        sentinviteRef.child(key).child("inviteall").setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                String uid = user.getUid();
                                String link = "https://kriger.in/?invitedby=" + uid;
                                FirebaseDynamicLinks.getInstance().createDynamicLink()
                                        .setLink(Uri.parse(link))
                                        .setDomainUriPrefix("https://kriger.page.link")
                                        .setAndroidParameters(
                                                new DynamicLink.AndroidParameters.Builder("in.kriger.krigercampus")
                                                        .setMinimumVersion(1)
                                                        .build()).
                                        /*.setIosParameters(
                                                new DynamicLink.IosParameters.Builder("com.example.ios")
                                                        .setAppStoreId("123456789")
                                                        .setMinimumVersion("1.0.1")
                                                        .build())*/
                                                buildShortDynamicLink()
                                        .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                                            @Override
                                            public void onSuccess(ShortDynamicLink shortDynamicLink) {
                                                sentinviteRef.child("refer").setValue(shortDynamicLink.getShortLink().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progress.dismiss();
                                                        Toasty.custom(getContext(), "Invitations sent", R.drawable.edit_icon, R.color.colorPrimary, Toast.LENGTH_LONG, false,
                                                                true).show();
//
                                                    }
                                                });
                                            }
                                        });

                            }
                        });
                    }
                });


            }
        });

        //method for permission
        showDialog();



        no_contacts =  rootview.findViewById(R.id.no_contacts);

        return rootview;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showDialog();
            } else {
                Intent intent = new Intent(getActivity(), ContactDenyActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        search_box.removeTextChangedListener(filterTextWatcher);
    }


    private void showDialog() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);

            } else {
                // Android version is lesser than 6.0 or the permission is already granted.
                LoadContactsAyscn lca = new LoadContactsAyscn(getContext());

                lca.execute();
            }
        }else {
            // Android version is lesser than 6.0 or the permission is already granted.
            LoadContactsAyscn lca = new LoadContactsAyscn(getContext());

            lca.execute();
        }


    }





    public class LoadContactsAyscn extends AsyncTask<Void, Void, ArrayList<Contacts>> {
        ProgressDialog pd;
        public ContactInterface contactInterface;

        private Context context;

        public LoadContactsAyscn(Context context){
            this.context = context;
            this.contactInterface = (ContactInterface) context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pd = ProgressDialog.show(context, "Loading Contacts",
                    "Please Wait");
        }

        @Override
        protected ArrayList<Contacts> doInBackground(Void... params) {

            contactslist.clear();

            ArrayList<String> emlRecs = new ArrayList<String>();
            HashSet<String> emlRecsHS = new HashSet<String>();
            ArrayList<String> mobilenumber = new ArrayList<>();


            ContentResolver cr = context.getContentResolver();
            String[] PROJECTION = new String[]{ContactsContract.RawContacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_ID,
                    ContactsContract.CommonDataKinds.Email.DATA,
                    ContactsContract.CommonDataKinds.Photo.CONTACT_ID};
            String order = "CASE WHEN "
                    + ContactsContract.Contacts.DISPLAY_NAME
                    + " NOT LIKE '%@%' THEN 1 ELSE 2 END, "
                    + ContactsContract.Contacts.DISPLAY_NAME
                    + ", "
                    + ContactsContract.CommonDataKinds.Email.DATA
                    + " COLLATE NOCASE";
            String filter = ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE ''";
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            // use the cursor to access the contacts
            while (phones.moveToNext()) {
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                // get display name
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if (context instanceof HomeActivity) {
                    try {
                        phoneNumber = phoneNumber.replaceAll("\\s+", "");
                        phoneNumber = phoneNumber.substring(phoneNumber.length() - 10);
                    }catch (StringIndexOutOfBoundsException e){}
                }

                Boolean duplicate = false;

                for (int i = 0;i<mobilenumber.size();i++){
                    if (PhoneNumberUtils.compare(mobilenumber.get(i), phoneNumber)) {
                        duplicate = true;
                    }
                }


                if (!duplicate) {
                    mobilenumber.add(phoneNumber);
                    contactslist.add(new Contacts(name, phoneNumber, false));
                }
                // get phone number

            }

            phones.close();

            Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, filter, null, order);
            if (cur.moveToFirst()) {
                do {
                    // names comes in hand sometimes
                    String name = cur.getString(1);
                    String emlAddr = cur.getString(3);

                    // keep unique only
                    if (emlRecsHS.add(emlAddr.toLowerCase())) {
                        emlRecs.add(emlAddr);
                        contactslist.add(new Contacts(name, emlAddr, true));
                    }
                } while (cur.moveToNext());
            }

            cur.close();


            return contactslist;

        }

        @Override
        protected void onPostExecute(ArrayList<Contacts> contacts) {
            super.onPostExecute(contactslist);

            pd.cancel();


            if (context instanceof HomeActivity){

                contactInterface.onTaskComplete(contacts);
            }


            if (getActivity()!=null) {

                adapter1 = new Adapter_Contacts(contactslist, getContext());

                if (contactslist.size() == 0) {
                    no_contacts.setVisibility(View.VISIBLE);
                }
                recyclerView_contact.setAdapter(adapter1);
            }
            checksync();


        }

    }

    public void checksync() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference contactRef = KrigerConstants.synced_contactRef.child(user.getUid());

        contactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {

                    SyncContacts sync = new SyncContacts();
                    sync.execute();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    class SyncContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }


        @Override
        protected Void doInBackground(Void... params) {

            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("ContactsList").child(user.getUid());

            String json = new Gson().toJson(contactslist);
            UploadTask uploadTask = storageReference.putBytes(json.getBytes());
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    FirebaseDatabase.getInstance().getReferenceFromUrl("https://kriger-campus-32cf7-default-rtdb.firebaseio.com/").child("Synced_Contact").child(user.getUid()).setValue(true);


                }
            });


            return null;


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            adapter1.getFilter().filter(s);
        }

    };


}
