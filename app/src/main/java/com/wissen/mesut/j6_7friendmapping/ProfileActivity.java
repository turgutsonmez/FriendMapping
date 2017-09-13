package com.wissen.mesut.j6_7friendmapping;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.OnImagePickedListener;
import com.wissen.mesut.j6_7friendmapping.model.Kisi;
import com.wissen.mesut.j6_7friendmapping.tools.AppTool;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by TurgutSonmez on 8.09.2017.
 */

public class ProfileActivity extends BaseActivity{
  EditText txtAd, txtSoyad, txtEmail,txtlat,txtlng;
  TextView txtDogumTarihi;
  Button btnGuncelle, btnTakvim;
  FirebaseUser user;
  FirebaseDatabase database;
  DatabaseReference myRef;
  Kisi kullanici;
  Date seciliTarih;
  ImageView imgProfil;
  ImagePicker imagePicker;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profile);
    imgProfil = (ImageView) findViewById(R.id.profil_duzenle_imgview);
    txtAd = (EditText) findViewById(R.id.profil_duzenle_txtad);
    txtSoyad = (EditText) findViewById(R.id.profil_duzenle_txtsoyad);
    txtEmail = (EditText) findViewById(R.id.profil_duzenle_txtemail);
    btnGuncelle = (Button) findViewById(R.id.profil_duzenle_btnguncelle);
    txtDogumTarihi = (TextView) findViewById(R.id.profil_duzenle_txtDogumTarihi);
    txtlat= (EditText) findViewById(R.id.profil_duzenle_txtlat);
    txtlng= (EditText) findViewById(R.id.profil_duzenle_txtlng);
    btnTakvim = (Button) findViewById(R.id.profil_duzenle_btnTakvim);
    btnTakvim.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker;
        datePicker = new DatePickerDialog(ProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
          @Override
          public void onDateSet(DatePicker view, int year, int monthOfYear,
                                int dayOfMonth) {
            seciliTarih = new Date(year - 1900, monthOfYear, dayOfMonth);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy");

            txtDogumTarihi.setText(simpleDateFormat.format(seciliTarih));
          }
        }, year, month, day);
        datePicker.setTitle("Doğum Tarihinizi Seçiniz");
        datePicker.setButton(DatePickerDialog.BUTTON_POSITIVE, "Seç", datePicker);
        datePicker.setButton(DatePickerDialog.BUTTON_NEGATIVE, "İptal", datePicker);

        datePicker.show();
      }
    });
    showProgressDialog("Lütfen Bekleyiniz", "Profil bilginize erişiliyor");

    mAuth = FirebaseAuth.getInstance();
    user = mAuth.getCurrentUser();
    if (user == null) finish();
    database = FirebaseDatabase.getInstance();
    myRef = database.getReference().child("uyeler");
    Query query = myRef.child(user.getUid());

    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        kullanici = dataSnapshot.getValue(Kisi.class);
        txtAd.setText(kullanici.getAd());
        txtSoyad.setText(kullanici.getSoyad());
        txtEmail.setText(kullanici.getEmail());
        txtlat.setText((String.valueOf(kullanici.getLat())));
        txtlng.setText((String.valueOf(kullanici.getLng())));
        if (kullanici.getDogumTarihi() != null) {
          Date dtarihi = new Date(kullanici.getDogumTarihi());
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MM yyyy");
          seciliTarih = dtarihi;
          txtDogumTarihi.setText(simpleDateFormat.format(dtarihi));
        }
        if (kullanici.getFotograf() != null) {
          imgProfil.setImageBitmap(AppTool.stringToBitmap(kullanici.getFotograf()));
        }
        hideProgressDialog();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
    btnGuncelle.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        boolean emailDegistiMi = false;
        Kisi guncellenecekKisi = new Kisi();
        emailDegistiMi = !kullanici.getEmail().equals(txtEmail.getText().toString());
        guncellenecekKisi.setId(user.getUid());
        guncellenecekKisi.setEmail(txtEmail.getText().toString());
        guncellenecekKisi.setAd(txtAd.getText().toString());
        guncellenecekKisi.setSoyad(txtSoyad.getText().toString());
        guncellenecekKisi.setLat(Double.parseDouble(String.valueOf(txtlat.getText())));
        guncellenecekKisi.setLng(Double.parseDouble(String.valueOf(txtlng.getText())));
        if (seciliTarih != null)
          guncellenecekKisi.setDogumTarihi(seciliTarih.toString());
        if (imagePicker != null) {
          guncellenecekKisi.setFotograf(Base64.encodeToString(AppTool.resimToByte(imgProfil), Base64.DEFAULT));
        }
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("uyeler");
        myRef.child(user.getUid()).setValue(guncellenecekKisi);
        if (emailDegistiMi) {
          mAuth = FirebaseAuth.getInstance();
          user = mAuth.getCurrentUser();
          user.updateEmail(txtEmail.getText().toString());
          user.sendEmailVerification();
          mAuth.signOut();
        }
        Toast.makeText(ProfileActivity.this, "Güncelleme işlemi başarılı", Toast.LENGTH_SHORT).show();
        finish();
      }
    });
    imgProfil.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        refreshImagePicker();
        imagePicker.choosePicture(true);
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    imagePicker.handleActivityResult(resultCode, requestCode, data);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    imagePicker.handlePermission(requestCode, grantResults);
  }

  private void refreshImagePicker() {
    imagePicker = new ImagePicker(this, null, new OnImagePickedListener() {
      @Override
      public void onImagePicked(Uri imageUri) {
        imgProfil.setImageURI(imageUri);
      }
    });
    imagePicker.setWithImageCrop(1, 1);
  }
}
