package com.example.shuttlesmgmt.DAOImplements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.shuttlesmgmt.DAO.DAO;
import com.example.shuttlesmgmt.db.ShuttlesSchema;
import com.example.shuttlesmgmt.entity.Order;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Michel on 2017/10/31.
 */

public class OrderDAOImpl extends DAO<Order> {

    public OrderDAOImpl(Context c) {
        super(c);
    }

    @Override
    public SQLiteDatabase openRead() {
        return super.openRead();
    }

    @Override
    public SQLiteDatabase openWrite() {
        return super.openWrite();
    }

    @Override
    public SQLiteDatabase getDBRead() {
        return super.getDBRead();
    }

    @Override
    public SQLiteDatabase getDBWrite() {
        return super.getDBWrite();
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    public SQLiteDatabase getDB() {
        return super.getDB();
    }

    @Override
    public boolean addAll(List<Order> obj) {
        return super.addAll(obj);
    }

    @Override
    public boolean isExist(Order obj) {
        Cursor c = getDBRead().rawQuery(
                "SELECT " +
                    ShuttlesSchema.Order.ORDER_ID +
                    " FROM " +
                    ShuttlesSchema.Order.ORDER_TABLE_NAME +
                    " WHERE " +
                    ShuttlesSchema.Order.ORDER_DATE + " = ? and " +
                    ShuttlesSchema.Order.ORDER_ISPAID + " = ? and " +
                    ShuttlesSchema.Order.ORDER_QUANTITY + " = ? and " +
                    ShuttlesSchema.Order.ORDER_TOTAL_PRICE + " = ? and " +
                    ShuttlesSchema.Order.ORDER_CUSTOMER_ID + " = ? and " +
                    ShuttlesSchema.Order.ORDER_PRODUCT_ID + " = ? ",
                obj.getOrder()
        );
        if(c != null){
            while(c.moveToNext()){
                if(c.getLong(0)==obj.getId()){
                    c.close();
                    return true;
                }
            }
        }
        c.close();
        return false;
    }

    @Override
    public boolean create(Order obj) {
        if(isExist(obj) == false){
            ContentValues values = new ContentValues();
            values.put(ShuttlesSchema.Order.ORDER_DATE, new SimpleDateFormat("dd/MM/yyyy").format(obj.getDate()));
            values.put(ShuttlesSchema.Order.ORDER_ISPAID, obj.getIsPaid());
            values.put(ShuttlesSchema.Order.ORDER_QUANTITY, obj.getQuantity());
            values.put(ShuttlesSchema.Order.ORDER_TOTAL_PRICE, obj.getPrice());
            values.put(ShuttlesSchema.Order.ORDER_CUSTOMER_ID, obj.getIdCustomer());
            values.put(ShuttlesSchema.Order.ORDER_PRODUCT_ID, obj.getIdProduct());
            getDBWrite().insert(ShuttlesSchema.Order.ORDER_TABLE_NAME, null, values);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public Order fetchById(long id){
        //don't implement it
        return null;
    }

    @Override
    public boolean isEmpty() {
        Cursor c = getDBRead().rawQuery(
                "SELECT COUNT ( " +
                        ShuttlesSchema.Order.ORDER_ID +
                        " ) FROM " +
                        ShuttlesSchema.Order.ORDER_TABLE_NAME,
                null);
        if(c != null){
            if(c.moveToFirst()){
                return c.getLong(0) == 0;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public Order fetchById2(long id) throws ParseException{
        Cursor c = getDBRead().rawQuery(
                "SELECT * FROM " +
                        ShuttlesSchema.Order.ORDER_TABLE_NAME +
                        " WHERE " + ShuttlesSchema.Order.ORDER_ID +" = ? ",
                new String[]{Long.toString(id)});

        if(c != null){
            c.moveToFirst();
            Order order = new Order();
            order.setId(c.getLong(0));
            order.setDate(StringToDate(c.getString(3)));//3
            order.setIdCustomer(c.getLong(4));
            order.setIdProduct(c.getLong(1));
            order.setIsPaid(Boolean.valueOf(c.getString(7)));
            order.setPrice(c.getDouble(6));
            order.setQuantity(c.getInt(5));
            c.close();
            return order;
        }else{
            return null;
        }
    }

    @Override
    public List<Order> fetchAll(){
        //don't implement it
        return null;
    }

    public List<Order> fetchAll2()throws ParseException{
        Cursor c = getDBRead().rawQuery(
                "SELECT * FROM " +
                        ShuttlesSchema.Order.ORDER_TABLE_NAME,
                null);
        Order order;
        List<Order> listOrder = new ArrayList<>();
        if(c != null){
            while(c.moveToNext()){
                order = new Order(c.getLong(0),
                        StringToDate(c.getString(2)),
                        Boolean.valueOf(c.getString(6)),
                        c.getInt(4),
                        c.getDouble(5),
                        c.getLong(3),
                        c.getLong(1)
                );
                listOrder.add(order);
            }
            c.close();
            return listOrder;
        }else{
            return null;
        }
    }

    @Override
    public void readData(int datafile, Context c) {
        if(deleteAll()==true){
            InputStream inputstream = c.getResources().openRawResource(datafile);
            List<Order> listOrder = new ArrayList<>();
            Order order;
            String[] splits;
            String lines;

            if(inputstream != null){
                //Log.i("AppInfo", "J'ai trouve le fichier");
                InputStreamReader inputreader = new InputStreamReader(inputstream);
                BufferedReader buffreader = new BufferedReader(inputreader);

                try{
                    while((lines = buffreader.readLine()) != null){
                        Log.i("AppInfo", "Line :" + lines);
                        splits = lines.split(" - ");
                        order = new Order();
                        order.setIdProduct(Long.valueOf(splits[0]));
                        order.setIdCustomer(Long.valueOf(splits[1]));
                        order.setDate(StringToDate(splits[3]));
                        order.setQuantity(Integer.valueOf(splits[2]));
                        order.setPrice(Double.valueOf(splits[4]));
                        order.setIsPaid(Boolean.valueOf(splits[5]));
                        listOrder.add(order);
                    }
                    buffreader.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                addAll(listOrder);
            }else{
                Log.i("AppInfo", "Jai pas trouve le fichier");
            }
        }
    }

    @Override
    public boolean update(Order obj) {
        if(isExist(obj) == false){
            ContentValues values = new ContentValues();
            values.put(ShuttlesSchema.Order.ORDER_PRODUCT_ID, obj.getIdProduct());
            values.put(ShuttlesSchema.Order.ORDER_DATE, String.valueOf(obj.getDate()));
            values.put(ShuttlesSchema.Order.ORDER_CUSTOMER_ID, obj.getIdCustomer());
            values.put(ShuttlesSchema.Order.ORDER_QUANTITY, obj.getQuantity());
            values.put(ShuttlesSchema.Order.ORDER_TOTAL_PRICE, obj.getPrice());
            values.put(ShuttlesSchema.Order.ORDER_ISPAID, obj.getIsPaid());
            getDBWrite().update(ShuttlesSchema.Order.ORDER_TABLE_NAME, values, ShuttlesSchema.Order.ORDER_ID + " = ? ", new String[]{Long.toString(obj.getId())});
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean deleteAll() {
        return getDBWrite().delete(ShuttlesSchema.Order.ORDER_TABLE_NAME, null, null)>0;
    }

    @Override
    public boolean delete(long id) {
        return getDBWrite().delete(ShuttlesSchema.Order.ORDER_TABLE_NAME, ShuttlesSchema.Order.ORDER_ID + " = ? ", new String[] {Long.toString(id)})>0;
    }

    public Date StringToDate(String s) throws ParseException{
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = format.parse(s);
        return date;
    }
}