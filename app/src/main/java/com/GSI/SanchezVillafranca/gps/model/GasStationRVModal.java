package com.GSI.SanchezVillafranca.gps.model;

public class GasStationRVModal {

    private int ind;
    private String province;
    private String municipality;
    private String town;
    private String postal_code;
    private String address;
    private String margin;
    private Double longitude;
    private Double latitude;
    private String data;
    private String collection;
    private String gasoline_95;
    private String gasoline_98;
    private String diesel_e;
    private String diesel_10e;
    private String name;
    private String time;

    public GasStationRVModal() {
    }

    public GasStationRVModal(String province, String municipality, String town, String postal_code, String address, String margin, Double longitude, Double latitude, String data, String collection, String gasoline_95, String gasoline_98, String diesel_e, String diesel_10e, String name, String time) {
        this.province = province;
        this.municipality = municipality;
        this.town = town;
        this.postal_code = postal_code;
        this.address = address;
        this.margin = margin;
        this.longitude = longitude;
        this.latitude = latitude;
        this.data = data;
        this.collection = collection;
        this.gasoline_95 = gasoline_95;
        this.gasoline_98 = gasoline_98;
        this.diesel_e = diesel_e;
        this.diesel_10e = diesel_10e;
        this.name = name;
        this.time = time;
    }

    public int getInd() {
        return ind;
    }

    public void setInd(int ind) {
        this.ind = ind;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMargin() {
        return margin;
    }

    public void setMargin(String margin) {
        this.margin = margin;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public String getGasoline_95() {
        return gasoline_95;
    }

    public void setGasoline_95(String gasoline_95) {
        this.gasoline_95 = gasoline_95;
    }

    public String getGasoline_98() {
        return gasoline_98;
    }

    public void setGasoline_98(String gasoline_98) {
        this.gasoline_98 = gasoline_98;
    }

    public String getDiesel_e() {
        return diesel_e;
    }

    public void setDiesel_e(String diesel_e) {
        this.diesel_e = diesel_e;
    }

    public String getDiesel_10e() {
        return diesel_10e;
    }

    public void setDiesel_10e(String diesel_10e) {
        this.diesel_10e = diesel_10e;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "GasStation{" + "province='" + province + '\'' + ", municipality='" + municipality + '\'' + ", town='" + town + '\'' + ", postal_code='" + postal_code + '\'' + ", address='" + address + '\'' + ", margin='" + margin + '\'' + ", longitude='" + longitude + '\'' + ", latitude='" + latitude + '\'' + ", data='" + data + '\'' + ", collection='" + collection + '\'' + ", gasoline_95='" + gasoline_95 + '\'' + ", gasoline_98='" + gasoline_98 + '\'' + ", diesel_e='" + diesel_e + '\'' + ", diesel_10e='" + diesel_10e + '\'' + ", name='" + name + '\'' + ", time='" + time + '\'' + '}';
    }
}
