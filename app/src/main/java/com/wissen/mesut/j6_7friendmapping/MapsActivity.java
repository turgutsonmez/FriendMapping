package com.wissen.mesut.j6_7friendmapping;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.wissen.mesut.j6_7friendmapping.model.Kisi;

import java.util.Date;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  private LocationManager locationManager;
  private LocationListener locationListener;
  private LatLng konum;
  ProgressDialog mProgressDialog;
  private Marker isaretci;
  FirebaseUser user;
  FirebaseDatabase database;
  DatabaseReference myRef;
  FirebaseAuth mAuth;
  Kisi kullanici;
  Date seciliTarih;
  ImagePicker imagePicker;
  ImageView imgProfil;
  Button btnBul;


  String id, email, ad, soyad, dt, foto, lat, lng;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
      .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    btnBul=findViewById(R.id.btn_bul);

    final int konumIzin = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
    if (konumIzin != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);


    }
    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    locationListener = new LocationListener() {
      @Override
      public void onLocationChanged(final Location location) {
        konum = new LatLng(location.getLatitude(), location.getLongitude());
        if (isaretci != null)
          isaretci.remove();
        //isaretci = mMap.addMarker(new MarkerOptions().position(konum).title("Burdasın"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(konum, 13));

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
            ad = kullanici.getAd();
            soyad = kullanici.getSoyad();
            email = kullanici.getEmail();
            id = kullanici.getId();
            lat = String.valueOf(location.getLatitude());
            lng = String.valueOf(location.getLongitude());
            dt = kullanici.getDogumTarihi();
            foto = kullanici.getFotograf();


            //LatLng konum = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            //mMap.addMarker(new MarkerOptions().position(konum).title("asdasd"));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(konum));

          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
        });
      }

      @Override
      public void onStatusChanged(String s, int i, Bundle bundle) {

      }

      @Override
      public void onProviderEnabled(String s) {

      }

      @Override
      public void onProviderDisabled(String s) {

      }
    };
    btnBul.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Kisi guncellenecekKisi=new Kisi();
        guncellenecekKisi.setId(id);
        guncellenecekKisi.setAd(ad);
        guncellenecekKisi.setSoyad(soyad);
        guncellenecekKisi.setEmail(email);
        guncellenecekKisi.setDogumTarihi(dt);
        guncellenecekKisi.setFotograf(foto);
        guncellenecekKisi.setLat(Double.parseDouble(lat));
        guncellenecekKisi.setLng(Double.parseDouble(lng));
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("uyeler");
        myRef.child(user.getUid()).setValue(guncellenecekKisi);

      }
    });
  }


  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == 200) {
      boolean izinVerildiMi = grantResults[0] == PackageManager.PERMISSION_GRANTED;
      if (!izinVerildiMi)
        Toast.makeText(this, "İzin verirmisin rica etsem", Toast.LENGTH_SHORT).show();
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;
    mMap.setTrafficEnabled(true);
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
      konumlariGetir();
    }

  }

  public void konumlariGetir() {
    database = FirebaseDatabase.getInstance();
    myRef = database.getReference();
    final Query uyelerQuery = myRef.child("uyeler");
    uyelerQuery.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        mMap.clear();
        for (DataSnapshot gelen: dataSnapshot.getChildren()){
          Kisi digerUyeler=gelen.getValue(Kisi.class);
          String digerUyeAdlari=digerUyeler.getAd();
          double digerUyeLat=digerUyeler.getLat();
          double digerUyeLng=digerUyeler.getLng();
          LatLng gelenUye=new LatLng(digerUyeLat,digerUyeLng);

          mMap.addMarker(new MarkerOptions().position(gelenUye).title(digerUyeAdlari));
          mMap.moveCamera(CameraUpdateFactory.newLatLng(gelenUye));
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }
}
