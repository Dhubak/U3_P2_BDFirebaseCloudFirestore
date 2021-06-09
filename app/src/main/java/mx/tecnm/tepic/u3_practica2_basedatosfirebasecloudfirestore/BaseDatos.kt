package mx.tecnm.tepic.u3_practica2_basedatosfirebasecloudfirestore

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDatos (
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    override fun onCreate(bd: SQLiteDatabase) {
        bd.execSQL("CREATE TABLE PEDIDOS(IDPEDIDO INTEGER PRIMARY KEY AUTOINCREMENT,NOMBRE VARCHAR(200),CELULAR VARCHAR(200),FECHA DATE, DESCRIPCION VARCHAR(200),PRECIO FLOAT,CANTIDAD INT,TOTAL FLOAT,ENTREGADO VARCHAR(10))")
    }

    override fun onUpgrade(bd: SQLiteDatabase, p1: Int, p2: Int) {
    }
}