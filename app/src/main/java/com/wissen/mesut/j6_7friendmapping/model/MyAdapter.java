package com.wissen.mesut.j6_7friendmapping.model;


import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wissen.mesut.j6_7friendmapping.R;
import com.wissen.mesut.j6_7friendmapping.tools.AppTool;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by TurgutSonmez on 10.09.2017.
 */

public class MyAdapter extends ArrayAdapter<Kisi> {

  private final Context context;
  private final ArrayList<Kisi> values;
  private FirebaseDatabase database;
  private DatabaseReference myRef;

  public MyAdapter(Context context, ArrayList<Kisi> values) {
    super(context, R.layout.todolist_item, values);
    this.context = context;
    this.values = values;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.todolist_item, parent, false);

    final CircleImageView imgProfil = rowView.findViewById(R.id.lst_imgprofil);
    TextView txtKullaniciAdi = rowView.findViewById(R.id.lst_txtKullaniciAdi);




    kullaniciBilgisiniGetir(values.get(position).getId(),imgProfil,txtKullaniciAdi);
    return rowView;
  }

  private void kullaniciBilgisiniGetir(String ekleyenID, final CircleImageView imageView, final TextView textView) {
    database = FirebaseDatabase.getInstance();
    myRef = database.getReference().child("uyeler");
    final Query kullaniciQuery=myRef.child(ekleyenID);
    kullaniciQuery.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Kisi gelenKisi=dataSnapshot.getValue(Kisi.class);
        if (gelenKisi.getAd() !=null && gelenKisi.getSoyad() !=null)
          textView.setText(gelenKisi.getAd()+ " "+gelenKisi.getSoyad());
        if (gelenKisi.getFotograf()!=null)
          imageView.setImageBitmap(AppTool.stringToBitmap(gelenKisi.getFotograf()));
        kullaniciQuery.removeEventListener(this);
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }
}
