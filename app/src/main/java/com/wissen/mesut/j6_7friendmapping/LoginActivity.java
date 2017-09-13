package com.wissen.mesut.j6_7friendmapping;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wissen.mesut.j6_7friendmapping.model.Kisi;

/**
 * Created by TurgutSonmez on 8.09.2017.
 */

public class LoginActivity extends BaseActivity {
  Button btnLogin, btnRegister, btnSifremiUnuttum;
  EditText txtEmail, txtPassword;
  FirebaseDatabase database;
  DatabaseReference myRef;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    btnSifremiUnuttum = (Button) findViewById(R.id.btnSifremiUnuttum_login);
    btnLogin = (Button) findViewById(R.id.btnGirisYap_login);
    btnRegister = (Button) findViewById(R.id.btnKayitOl_login);
    txtEmail = (EditText) findViewById(R.id.txt_Email_login);
    txtPassword = (EditText) findViewById(R.id.txtPassword_login);
    btnRegister.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
      }
    });
    btnLogin.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        girisYap(txtEmail.getText().toString(), txtPassword.getText().toString());
      }
    });
    btnSifremiUnuttum.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        mAuth = FirebaseAuth.getInstance();
        if (TextUtils.isEmpty(txtEmail.getText()))
          Toast.makeText(LoginActivity.this, "Mail Adresini yazınız", Toast.LENGTH_SHORT).show();
        else
          mAuth.sendPasswordResetEmail(txtEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
              Toast.makeText(LoginActivity.this, txtEmail.getText().toString() + " adresine parola sıfırlama linki gönderildi", Toast.LENGTH_LONG).show();
            }
          });
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();
    showProgressDialog("Giriş", "Lütfen Bekleyin");
    mAuth = FirebaseAuth.getInstance();
    final FirebaseUser user = mAuth.getCurrentUser();
    if (user != null) {
      if (user.isEmailVerified()) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("uyeler");
        Query query = myRef.child(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
            if (!dataSnapshot.exists()) {
              Kisi yeniKisi = new Kisi();
              yeniKisi.setEmail(user.getEmail());
              yeniKisi.setId(user.getUid());
              myRef.child(user.getUid()).setValue(yeniKisi);
            }
          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
        });

        Toast.makeText(this, "Hoşgeldiniz", Toast.LENGTH_SHORT).show();
        hideProgressDialog();
        startActivity(new Intent(this, MainActivity.class));
      }
    }
    hideProgressDialog();
  }

  private void girisYap(String email, String pass) {
    if (!validateForm(txtEmail, txtPassword)) return;
    showProgressDialog("Giriş", "Sisteme giriş yapılıyor");
    mAuth = FirebaseAuth.getInstance();
    mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
      @Override
      public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
          FirebaseUser user = mAuth.getCurrentUser();
          if (user.isEmailVerified()) {
            Toast.makeText(LoginActivity.this, "Hoşgeldin", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
          } else {
            Toast.makeText(LoginActivity.this, "Email adresinizi doğrulayın!", Toast.LENGTH_SHORT).show();
            kullaniciDogrula();
          }
        } else if (!task.isSuccessful()) {
          Toast.makeText(LoginActivity.this, "Kullanıcı adı veya şifre hatalı", Toast.LENGTH_SHORT).show();
        } else {
          Toast.makeText(LoginActivity.this, "Giriş başarısız", Toast.LENGTH_SHORT).show();
        }
        hideProgressDialog();
      }
    });
  }

  private void kullaniciDogrula() {
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    if (user != null) {
      user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
          Toast.makeText(LoginActivity.this, "Posta kutunuzu kontrol edin", Toast.LENGTH_SHORT).show();
          finish();
        }
      });
    }
  }
}







