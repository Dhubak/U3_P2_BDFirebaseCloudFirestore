package mx.tecnm.tepic.u3_practica2_basedatosfirebasecloudfirestore

import android.app.AlertDialog
import android.content.Intent
import android.database.sqlite.SQLiteException
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.get
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    var baseSQLite=BaseDatos(this,"prueba", null,1)
    var listaID=ArrayList<String>()
    var baseRemota= FirebaseFirestore.getInstance()
    var datalista=ArrayList<String>()
    var listaID2=ArrayList<String>()
    var cadena= ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inserta.setOnClickListener{
            insertar()
        }

        subir.setOnClickListener{
          sincronizar()

        }

        salir.setOnClickListener{
            finish()

        }

        sale.setOnClickListener {
            calcular()
        }

        cargarLista()


    }

    private fun sincronizar() {

        datalista.clear()
        baseRemota.collection("pedidos")
            .addSnapshotListener {querySnapshot, firebaseFirestoreException ->

                if(firebaseFirestoreException !=null) {
                    mensaje("NO SE PUDO REALIZAR CONEXION CON LA NUBE")
                    return@addSnapshotListener
                }

                for(registro in querySnapshot!!)
                {
                    cadena=registro.id
                    datalista.add(cadena)
                }

                try {
                    var c = baseSQLite.readableDatabase
                    var res = c.query("PEDIDOS", arrayOf("*"),null,null,null,null,null)

                    if(res.moveToFirst())
                    {
                        do {
                            if(datalista.contains(res.getString(0))) {
                                baseRemota.collection("pedidos")
                                    .document(res.getString(0))
                                    .update( "NOMBRE",res.getString(1), "CELULAR",res.getString(2), "FECHA", res.getString(3), "PEDIDO.DESCRIPCION", res.getString(4), "PEDIDO.PRECIO", res.getString(5), "PEDIDO.CANTIDAD", res.getString(6), "TOTAL", res.getString(7), "ENTREGADO", res.getString(8) )
                                    //aqui va
                            } else {

                                var datosInsertar = hashMapOf(
                                    "IDPEDIDO" to res.getString(0),
                                    "NOMBRE" to res.getString(1),
                                    "CELULAR" to res.getString(2),
                                    "FECHA" to res.getString(3),
                                    "ENTREGADO" to res.getString(8),
                                    "PEDIDO" to hashMapOf(
                                        "DESCRIPCION" to res.getString(4),
                                        "PRECIO" to res.getString(5),
                                        "CANTIDAD" to res.getString(6)),
                                    "TOTAL" to res.getString(7)
                                )

                                baseRemota.collection("pedidos").document("${res.getString(0)}")
                                    .set(datosInsertar as Any)
                                    .addOnFailureListener{
                                        mensaje("NO SE PUDO SUBIR A LA NUBE\n${it.message!!}")
                                    }

                            }

                        }while(res.moveToNext())
                    } else {
                        datalista.add("NO SE HAN ENCONTRADO CAMBIOS QUE SUBIR")
                    }
                    c.close()
                } catch (e: SQLiteException) {
                    mensaje("ALGO SALIÓ MAL: " + e.message!!)
                }
            }

        alerta("LA SINCRONIZACION SE LLEVÓ A CABO SATISFACTORIAMENTE. TU INFOMACION YA SE ENCUENTRA EN LA NUBE")
        startActivity(Intent(this,MainActivity3::class.java))
    }



    private fun calcular(){
        if (desc.getItemAtPosition(desc.getSelectedItemPosition()).toString()=="Hamburguesa simple"){
            precio.setText("PRECIO: 20")
            var cambio=(cantidad.getText().toString()).toFloat()
            var cal=(cambio*20).toString()
            total.setText("TOTAL: "+cal)
        }else if (desc.getItemAtPosition(desc.getSelectedItemPosition()).toString()=="Hamburguesa doble"){
            precio.setText("PRECIO: 25")
            var cambio=(cantidad.getText().toString()).toFloat()
            var cal=(cambio*25).toString()
            total.setText("TOTAL: "+cal)
        }else if (desc.getItemAtPosition(desc.getSelectedItemPosition()).toString()=="Hamburguesa especial"){
            precio.setText("PRECIO: 30")
            var cambio=(cantidad.getText().toString()).toFloat()
            var cal=(cambio*30).toString()
            total.setText("TOTAL: "+cal)
        }else if (desc.getItemAtPosition(desc.getSelectedItemPosition()).toString()=="CocaCola"){
            precio.setText("PRECIO: 12")
            var cambio=(cantidad.getText().toString()).toFloat()
            var cal=(cambio*12).toString()
            total.setText("TOTAL: "+cal)
        }else if (desc.getItemAtPosition(desc.getSelectedItemPosition()).toString()=="Refresco de manzana"){
            precio.setText("PRECIO: 19")
            var cambio=(cantidad.getText().toString()).toFloat()
            var cal=(cambio*19).toString()
            total.setText("TOTAL: "+cal)
        }else if (desc.getItemAtPosition(desc.getSelectedItemPosition()).toString()=="Jugo Jumex"){
            precio.setText("PRECIO: 21")
            var cambio=(cantidad.getText().toString()).toFloat()
            var cal=(cambio*21).toString()
            total.setText("TOTAL: "+cal)
        }
    }

    private fun insertar() {

        try{
            var inser = baseSQLite.writableDatabase
            var selec= desc.getItemAtPosition(desc.getSelectedItemPosition()).toString()
            var selec2= entregado.getItemAtPosition(entregado.getSelectedItemPosition()).toString()

            if(nombre.getText().toString()=="" || celular.getText().toString()=="" || fecha.toString()=="" || selec.toString()=="" || precio.toString()==""|| total.toString()==""|| cantidad.toString()==""|| selec2.toString()==""){
                mensaje("FAVOR DE INGRESAR INFORMACION")
            }else{

                var SQL="INSERT INTO PEDIDOS VALUES(NULL, '${nombre.text.toString()}', '${celular.text.toString()}', '${fecha.text.toString()}','${selec}', '${precio.text.toString()}', '${cantidad.text.toString()}', '${total.text.toString()}','${selec2}')"

                inser.execSQL(SQL)
                mensaje("INFORMACION INSERTADA CORRECTAMENTE")
                cargarLista()
                limpiarCampos()
            }
            inser.close()

        }catch(err: SQLiteException){
            mensaje(err.message!!)
        }
    }

    fun cargarLista() {
        try{
            var select = baseSQLite.readableDatabase
            var clientes = ArrayList<String>()

            var SQL="SELECT * FROM PEDIDOS"
            var cursor = select.rawQuery(SQL,null)
            listaID.clear()

            if(cursor.moveToFirst()){

                do{
                    var data = "PEDIDO: "+cursor.getString(0)+" - NOMBRE: "+cursor.getString(1)+" - ORDEN: "+ cursor.getString(4)+" - TOTAL: "+ cursor.getString(7)+" - ENTREGADO: "+ cursor.getString(8)
                    clientes.add(data)
                    listaID.add(cursor.getInt(0).toString())
                }while(cursor.moveToNext())

            }else{
                clientes.add("AUN NO HAY INFORMACION")
            }
            select.close()
            todos.adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,clientes)
            todos.setOnItemClickListener { _, _, pos, _ ->

                var idSelec = listaID.get(pos)

                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("ATENCION")
                    .setMessage("QUE DESEAS HACER CON LA INFORMACION DEL ID: ${idSelec}")
                    .setNegativeButton("CANCELAR") { _, _ -> }
                    .setPositiveButton("ELIMINAR") { _, _ ->
                        eliminar(idSelec)
                    }

                    .setNeutralButton("ACTUALIZAR"){_,_->
                        var intent= Intent(this,MainActivity2::class.java)
                        intent.putExtra("idSeleccionado", idSelec)
                        startActivity(intent)
                    }
                    .show()
            }

        }catch(err:SQLiteException){
            mensaje(err.message!!)
        }
    }

    private fun eliminar(idBorrar: String) {
        try{
            var elimin = baseSQLite.writableDatabase
            var SQL="DELETE FROM PEDIDOS WHERE IDPEDIDO = ${idBorrar}"

            elimin.execSQL(SQL)
            cargarLista()
            elimin.close()

        }catch(err:SQLiteException){
            mensaje(err.message!!)
        }

    }


    private fun alerta(s: String) {
        Toast.makeText(this,s, Toast.LENGTH_LONG).show()
    }

    fun limpiarCampos(){
        nombre.setText("")
        celular.setText("")
        fecha.setText("")
        desc.setSelection(0)
        cantidad.setText("")
        entregado.setSelection(0)
        precio.setText("")
        total.setText("")
    }

    fun mensaje(m:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(m)
            .setPositiveButton("OK") {_, _ -> }
            .show()
    }

}
