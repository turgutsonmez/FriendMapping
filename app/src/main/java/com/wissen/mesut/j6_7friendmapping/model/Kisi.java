package com.wissen.mesut.j6_7friendmapping.model;

import java.util.UUID;

/**
 * Created by TurgutSonmez on 8.09.2017.
 */

public class Kisi {
  private String id, ad, soyad, email, dogumTarihi, fotograf;
  private double lat,lng;

  public double getLat() {
    return lat;
  }

  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getLng() {
    return lng;
  }

  public void setLng(double lng) {
    this.lng = lng;
  }

  public String getFotograf() {
    return fotograf;
  }

  public void setFotograf(String fotograf) {
    this.fotograf = fotograf;
  }

  public String getDogumTarihi() {
    return dogumTarihi;
  }

  public void setDogumTarihi(String dogumTarihi) {
    this.dogumTarihi = dogumTarihi;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAd() {
    return ad;
  }

  public void setAd(String ad) {
    this.ad = ad;
  }

  public String getSoyad() {
    return soyad;
  }

  public void setSoyad(String soyad) {
    this.soyad = soyad;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String toString() {
    return ad;
  }
}
