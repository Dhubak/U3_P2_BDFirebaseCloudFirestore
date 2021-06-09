package mx.tecnm.tepic.u3_practica2_basedatosfirebasecloudfirestore

import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {

    var baseSQLite = BaseDatos(this, "prueba", null, 1)
    var idSelec = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        var extra = intent.extras
        idSelec = extra!!.getString("idSeleccionado")!!

        actualizar.setOnClickListener {
            actualiza()
        }

        regresar.setOnClickListener {
            finish()
        }

    }

    private fun actualiza() {
        try {
            var transaccion = baseSQLite.writableDatabase
            var selec2= entregado2.getItemAtPosition(entregado2.getSelectedItemPosition()).toString()

            if (selec2.toString()=="") {
                mensaje("FAVOR DE SEECCIONAR LA INFORMACION")
            } else {
                var SQL = "UPDATE PEDIDOS SET ENTREGADO='${selec2.toString()}' WHERE IDPEDIDO=${idSelec}"
                transaccion.execSQL(SQL)
                MainActivity().cargarLista()
                transaccion.close()
                finish()
            }

        } catch (err: SQLiteException) {
            mensaje(err.message!!)
        }

    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){_,_->}
            .show()
    }

}

