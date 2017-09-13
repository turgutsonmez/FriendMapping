package com.wissen.mesut.j6_7friendmapping;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by TurgutSonmez on 8.09.2017.
 */

public class BaseActivity extends AppCompatActivity{
  ProgressDialog mProgressDialog;
  FirebaseAuth mAuth;

  public boolean validateForm(EditText txtEmail, EditText txtPass) {
    boolean valid = true;
    String email = txtEmail.getText().toString();
    if (TextUtils.isEmpty(email)) {
      txtEmail.setError("Gerekli!");
      valid = false;
    } else {
      txtEmail.setError(null);
    }
    String password = txtPass.getText().toString();
    if (TextUtils.isEmpty(password)) {
      txtPass.setError("Gerekli.");
      valid = false;
    } else {
      txtPass.setError(null);
    }
    if (txtPass.getText().length() < 6) {
      txtPass.setError("Şifre en az 6 karakter olmalı.");
      valid = false;
    } else {
      txtPass.setError(null);
    }
    return valid;
  }

  public void showProgressDialog(String title, String message) {
    if (mProgressDialog == null) {
      mProgressDialog = new ProgressDialog(this);
      mProgressDialog.setTitle(title);
      mProgressDialog.setMessage(message);
      mProgressDialog.setIndeterminate(true);
    }

    mProgressDialog.show();
  }

  public void hideProgressDialog() {
    if (mProgressDialog != null && mProgressDialog.isShowing()) {
      mProgressDialog.dismiss();
    }
  }
}
