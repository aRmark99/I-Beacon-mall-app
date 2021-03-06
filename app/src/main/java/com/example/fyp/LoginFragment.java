package com.example.fyp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static android.text.TextUtils.isEmpty;
import static com.google.firebase.Timestamp.now;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private final String TAG = "Test";

    String sname, spassword;
    Button btnlogin, btnregister;
    EditText name, password;
    TextView tvregister;

    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        final SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        View root;
        root = inflater.inflate(R.layout.fragment_login, container, false);

        btnlogin = root.findViewById(R.id.btnLogin);
        btnregister = root.findViewById(R.id.btnRegister);
        tvregister = root.findViewById(R.id.tvregister);

        tvregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new RegisterFragment());
                ft.commit();
            }
        });

//            btnregister.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new RegisterFragment());
//                    ft.commit();
//                }
//            });


        btnlogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)throws NullPointerException {
                name = (EditText) getView().findViewById(R.id.eName);
                password = (EditText) getView().findViewById(R.id.ePassword);


                if (isEmpty(name.getText().toString())) {
                    Toast.makeText(getActivity(), "Please input your phone number", Toast.LENGTH_SHORT).show();
                }if(name.length() != 8){
                    Toast.makeText(getActivity(), "Please input proper phone number example: 6262 6262", Toast.LENGTH_SHORT).show();
                } else if (isEmpty(password.getText().toString())) {
                    Toast.makeText(getActivity(), "Please input your password", Toast.LENGTH_LONG).show();
                } else {
                    sname = name.getText().toString();
                    spassword = password.getText().toString();
                    Log.d(TAG, "sname" + sname);
                    Log.d(TAG, "spassword" + spassword);
                    final FirebaseFirestore db = FirebaseFirestore.getInstance();

                    //auth
                    DocumentReference docRef = db.collection("users").document(sname);

                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.d(TAG, "Listen failed.", e);
                                return;
                            }

                            if (snapshot != null && snapshot.exists()) {
                                String pw = snapshot.get("Password").toString();
                                Boolean lock = snapshot.getBoolean("Lock");
                                if (spassword.equals(pw) && lock == false) {
                                    Log.d(TAG, "Logged in: " + snapshot.getData());
                                    Toast.makeText(getActivity(), "Logged in", Toast.LENGTH_SHORT).show();

                                    //save user info
                                    preferences.edit()
                                            .putString("uid", sname)
                                            .putString("userbirsd", snapshot.get("d").toString())
                                            .putString("userbirm", snapshot.get("m").toString())
                                            .putBoolean("Login", true)
                                            .apply();
                                    Log.d(TAG, "UserID: " + preferences
                                            .getString("uid", "") + " Name: " + preferences
                                            .getString("uname", "") + " Password: " + preferences
                                            .getString("upassword", ""));
                                    //change page
                                    FragmentTransaction ft = getFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new MemberFragment());
                                    ft.commit();

                                }else if(lock == true){
                                    Toast.makeText(getActivity(), "Your account is locked", Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.d(TAG, "Wrong password" + snapshot.getData());
                                    Toast.makeText(getActivity(), "Wrong Password", Toast.LENGTH_SHORT).show();

                                }

                            } else {
                                Log.d(TAG, "No user exist");
                                Toast.makeText(getActivity(), "Wrong Password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        return root;
    }
}
