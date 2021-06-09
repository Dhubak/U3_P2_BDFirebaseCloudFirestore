package mx.tecnm.tepic.u3_practica2_basedatosfirebasecloudfirestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main3.*

class MainActivity3 : AppCompatActivity() {

    var baseRemota= FirebaseFirestore.getInstance()
    var listaID= ArrayList<String>()
    var datalista = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        cargarLista2()

        regresar2.setOnClickListener {
            finish()
        }
    }

    private fun cargarLista2() {
        baseRemota.collection("pedidos")
            .addSnapshotListener { querySnapShot, error ->
                if(error != null){
                    mensaje(error.message!!)
                    return@addSnapshotListener
                }
                datalista.clear()
                listaID.clear()

                for(document in querySnapShot!!){
                    var cadena = "PEDIDO: ${document.getString("IDPEDIDO")} - NOMBRE: ${document.getString("NOMBRE")} - ORDEN: ${document.get("PEDIDO.DESCRIPCION")} - TOTAL: ${document.get("TOTAL")} -ENTREGADO: ${document.get("ENTREGADO")}"
                    datalista.add(cadena)

                    listaID.add(document.id.toString())
                }
                todos2.adapter= ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1,datalista)
                todos2.setOnItemClickListener{_,_,i,_->
                    eliminaS(i)
                }
            }

    }

    private fun eliminaS(i: Int) {
        var idElegido = listaID.get(i)
        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage("¿DESEAS ELIMINAR LA INFORMACION: \n${datalista.get(i)}?")
            .setPositiveButton("ELIMINAR"){_,_->
                elimina(idElegido)
            }
            .setNegativeButton("CANCELAR"){_,_->}
            .show()

    }

    private fun elimina(idElegido: String) {
        baseRemota.collection("pedidos")
            .document(idElegido)
            .delete()

            .addOnSuccessListener {
                alerta("SE ELIMINO LA INFORMACION CON EXITO")
            }

            .addOnFailureListener {
                mensaje("error: ${it.message!!}")
            }
    }

    private fun alerta(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show()
    }

    private fun mensaje(s: String) {
        AlertDialog.Builder(this).setTitle("ATENCION")
            .setMessage(s)
            .setPositiveButton("OK"){_,_->}
            .show()
    }
}