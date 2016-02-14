package com.nadhif.onbeng;

/**
 * Created by nadhif on 21/01/2016.
 */
public class DataRecycler {
    String id, logoBengkel, nameBengkel, dateOrder, statusOrder, damageOrder, detail_bengkel, detail_order;

    public DataRecycler(String id, String logoBengkel, String nameBengkel, String dateOrder, String statusOrder, String damageOrder, String detail_bengkel, String detail_order) {
        this.id = id;
        this.logoBengkel = logoBengkel;
        this.nameBengkel = nameBengkel;
        this.dateOrder = dateOrder;
        this.statusOrder = statusOrder;
        this.damageOrder = damageOrder;
        this.detail_bengkel = detail_bengkel;
        this.detail_order = detail_order;
    }

    public String getId() {
        return id;
    }

    public String getLogoBengkel() {
        return logoBengkel;
    }

    public String getNameBengkel() {
        return nameBengkel;
    }

    public String getDateOrder() {
        return dateOrder;
    }

    public String getStatusOrder() {
        return statusOrder;
    }

    public String getDamageOrder() {
        return damageOrder;
    }

    public String getDetail_bengkel() {
        return detail_bengkel;
    }

    public String getDetail_order() {
        return detail_order;
    }
}
