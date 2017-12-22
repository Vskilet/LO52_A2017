package utbm.fr.ffbad.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utbm.fr.ffbad.R;
import utbm.fr.ffbad.entity.Purchase;
import utbm.fr.ffbad.entity.PurchaseLine;
import utbm.fr.ffbad.entity.StockLine;
import utbm.fr.ffbad.entity.Tube;
import utbm.fr.ffbad.entity.Volant;

/**
 * Created by Clément on 22/09/2017.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SHUTTLECOCK";

    private SQLiteDatabase db;
    private Context context;

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
        this.db = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Cursor cu = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if(cu.getCount()<=1)
        {
            DbUtil.executeScript(db,this.context,R.raw.shuttlecock);
            DbUtil.executeScript(db,this.context,R.raw.purchases);
        }
    }

    public List<StockLine> getStock(){
        List<StockLine> stockLinesResult = new ArrayList<>();
        String query_str = "SELECT * FROM TUBE";
        Cursor c = this.db.rawQuery(query_str ,null);
        if(c.moveToFirst()){
            do{
                String ref = c.getString(0);
                Integer cost = Integer.parseInt(c.getString(2));
                Integer nbStock = Integer.parseInt(c.getString(3));
                String image = c.getString(4);

                //Retrieve the brand
                String brandQuery_str = "SELECT MARQUE FROM VOLANT WHERE REF = '" + ref +"'";
                Cursor brandC = this.db.rawQuery(brandQuery_str ,null);
                String brand = "default";
                if(brandC.moveToNext()){
                   brand = brandC.getString(0);
                }
                StockLine line = new StockLine(new Volant(ref,brand),new Tube(nbStock,cost,ref,image));
                stockLinesResult.add(line);
            }while(c.moveToNext());
            c.close();
        }
        return stockLinesResult;
    }

    public List<Purchase> getPurchases(){

        HashMap<String, Purchase> purchaseMap = new HashMap<String, Purchase>();

        String query_str =
                "SELECT COMMANDE.ESTPAYE, COMMANDE.NOM, COMMANDE.REF_DE_COMMANDE, LIGNE_COMMANDE.TUBE_REF, LIGNE_COMMANDE.NOMBRE, " +
                "LIGNE_COMMANDE.PRIX_TOTAL  " +
                "FROM COMMANDE " +
                "INNER JOIN LIGNE_COMMANDE " +
                "ON COMMANDE.REF_DE_COMMANDE = LIGNE_COMMANDE.REF_DE_COMMANDE";

        Cursor c = this.db.rawQuery(query_str ,null);

        if(c.moveToFirst()){
            do{

                Boolean isPaid = c.getInt(0) != 0;
                String buyerName = c.getString(1);
                String refCmd = c.getString(2);
                String refTube = c.getString(3);
                int quantity = c.getInt(4);
                Double linePrice = c.getDouble(5);
                Purchase purchase;
                PurchaseLine purchaseLine = new PurchaseLine(linePrice, quantity, refTube, refCmd);

                if(purchaseMap.containsKey(refCmd)){
                    purchase = purchaseMap.get(refCmd);
                }else{
                    purchase = new Purchase(isPaid, buyerName,refCmd);
                }

                purchase.addLine(purchaseLine);
                purchaseMap.put(refCmd, purchase);
            }while(c.moveToNext());
            c.close();
        }
        return new ArrayList<Purchase>(purchaseMap.values());
    }

    public void updatePaid(boolean isPaid, String cmdRef){
        SQLiteStatement stmt = db.compileStatement(
                "UPDATE COMMANDE " +
                "SET ESTPAYE = ?" +
                "WHERE REF_DE_COMMANDE = ?"
        );
        stmt.bindLong(1, isPaid?1:0);
        stmt.bindString(2, cmdRef);
        stmt.execute();
    }

    public void registerPurchase(Purchase purchase){

        SQLiteStatement stmt = db.compileStatement(
                "INSERT INTO COMMANDE (NOM, REF_DE_COMMANDE, ESTPAYE)" +
                "VALUES (?,?,?)"
        );
        stmt.bindString(1, purchase.getBuyer());
        stmt.bindString(2,purchase.getCmdRef());
        stmt.bindLong(3,purchase.isPaid()?1:0);
        stmt.execute();

        int i = 1;
        for(PurchaseLine purchaseLine : purchase.getPurchaseLines()) {
            SQLiteStatement stmt2 = db.compileStatement(
                    "INSERT INTO LIGNE_COMMANDE (REF_DE_COMMANDE,TUBE_REF,NUMERO,PRIX_TOTAL,NOMBRE)" +
                            "VALUES (?,?,?,?,?)"
            );
            stmt2.bindString(1, purchase.getCmdRef());
            stmt2.bindString(2, purchaseLine.getRef());
            stmt2.bindLong(3,i);
            stmt2.bindDouble(4,purchase.getTotalPrice());
            stmt2.bindLong(5,purchaseLine.getQuantity());
            stmt2.execute();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onOpen(SQLiteDatabase db){

    }
}