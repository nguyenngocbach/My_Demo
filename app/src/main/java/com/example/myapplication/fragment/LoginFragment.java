package com.example.myapplication.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.util.SharedPreferencesUntilt;
import com.example.myapplication.util.Util;
import com.google.android.gms.common.util.SharedPreferencesUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.myapplication.fragment.LoginFragment.*;

public class LoginFragment extends Fragment {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    protected FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private ICheckUsername mICheckUsername = new ICheckUsername() {
        @Override
        public void checkUserName(boolean b) {

        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment,container,false);
        mPasswordEditText = view.findViewById(R.id.username_edt);
        mPasswordEditText = view.findViewById(R.id.password_edt);
        mLoginButton = view.findViewById(R.id.login_btn);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUsernameEditText.getText().toString().equals("") || mPasswordEditText.getText().toString().equals("")) {
                    AlertDialog.Builder builder  = new AlertDialog.Builder(getContext())
                            .setTitle("Thông báo")
                            .setMessage("Vui long nhập lại Tài khoàn và Mật khẩu")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    builder.create().show();
                } else {
                    mFireStore.collection("User").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()){
                                    //todo something
                                    String username = (String) document.get("Username");
                                    String password = (String) document.get("Password");
                                    if (username.equals(mUsernameEditText.getText().toString()) && password.equals(mPasswordEditText.getText().toString())){
                                        mICheckUsername.checkUserName(true);
                                        //todo : save gi day vao.
                                        SharedPreferencesUntilt.saveString(getContext(), Util.USERNAME,username);
                                        SharedPreferencesUntilt.saveString(getContext(),Util.PASSWORD,password);
                                        SharedPreferencesUntilt.saveBoolean(getContext(),Util.IS_LOGIN,true);
                                        return;
                                    }
                                }
                            }
                        }
                    });
                }

            }
        });
        return view;
    }


     public interface ICheckUsername{
        void checkUserName(boolean b);
     }
}
