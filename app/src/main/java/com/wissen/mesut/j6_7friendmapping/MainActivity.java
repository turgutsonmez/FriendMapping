package com.wissen.mesut.j6_7friendmapping;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import com.wissen.mesut.j6_7friendmapping.model.Kisi;
import com.wissen.mesut.j6_7friendmapping.model.MyAdapter;
import com.wissen.mesut.j6_7friendmapping.tools.AppTool;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity
  implements NavigationView.OnNavigationItemSelectedListener {

  static int sayac = 0;
  ImageView nav_userimg;
  TextView nav_txtAdSoyad, nav_txtEmail;
  FirebaseUser user;
  FirebaseDatabase database;
  DatabaseReference myRef;
  Kisi kullanici;
  ListView listView;
  SwipeRefreshLayout swipeRefresh;

  ArrayList<Kisi> kisiList;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    listView = (ListView) findViewById(R.id.main_listView);
    swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.main_swiperrefresh);
    swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        listeyiDoldur();
      }
    });

    /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
          .setAction("Action", null).show();

      }


    });*/

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
      this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    View header = navigationView.getHeaderView(0);
    mAuth = FirebaseAuth.getInstance();
    user = mAuth.getCurrentUser();
    if (user == null) finish();

    nav_userimg = header.findViewById(R.id.nav_userimg);
    nav_txtAdSoyad = header.findViewById(R.id.nav_txtAdSoyad);
    nav_txtEmail = header.findViewById(R.id.nav_txtEmail);

    nav_txtEmail.setText(user.getEmail());
    database = FirebaseDatabase.getInstance();
    myRef = database.getReference().child("uyeler");
    Query query = myRef.child(user.getUid());
    query.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        kullanici = dataSnapshot.getValue(Kisi.class);
        nav_txtAdSoyad.setText(String.format("%s %s", kullanici.getAd() == null ? "ad" : kullanici.getAd(), kullanici.getSoyad() == null ? "soyad" : kullanici.getSoyad()));
        if (kullanici.getFotograf() != null)
          nav_userimg.setImageBitmap(AppTool.stringToBitmap(kullanici.getFotograf()));
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }

  private void listeyiDoldur() {
    showProgressDialog("Lütfen bekleyin", "Veritabanına bağlantı kuruluyor");
    database = FirebaseDatabase.getInstance();
    myRef = database.getReference();
    final Query yapilacaklarQuery = myRef.child("uyeler");
    yapilacaklarQuery.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        kisiList=new ArrayList<>();
        for (DataSnapshot gelen : dataSnapshot.getChildren()){
          Kisi yeniKisi=gelen.getValue(Kisi.class);
          kisiList.add(yeniKisi);
        }
        MyAdapter adapter=new MyAdapter(MainActivity.this,kisiList);
        listView.setAdapter(adapter);
        hideProgressDialog();
        yapilacaklarQuery.removeEventListener(this);
        swipeRefresh.setRefreshing(false);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();
    super.onStart();
    mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    if (user == null) {
      Toast.makeText(this, "Lütfen giriş yapın", Toast.LENGTH_SHORT).show();
      finish();
    }

    listeyiDoldur();
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      sayac++;
      if (sayac == 2) {
        mAuth.signOut();
        finish();
        super.onBackPressed();
      }
    }
  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    //if (id == R.id.action_settings) {
      //return true;
    //}

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_profil) {
      // Handle the camera action
      startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
    } else if (id == R.id.nav_cikis_yap) {
      mAuth = FirebaseAuth.getInstance();
      Toast.makeText(this, "Güle Güle", Toast.LENGTH_SHORT).show();
      mAuth.signOut();
      finish();
    }else if (id==R.id.nav_harita){
      startActivity(new Intent(getApplicationContext(),MapsActivity.class));
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }


}
