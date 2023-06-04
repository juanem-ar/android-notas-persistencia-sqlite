package com.ispc.persistencia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText listadoTareas;
    TextView codigo, tarea;
    Button agregarBoton, modificarBoton, eliminarBoton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        codigo = findViewById(R.id.codigo);
        tarea = findViewById(R.id.tarea);
        agregarBoton = findViewById(R.id.agregar);
        modificarBoton = findViewById(R.id.modificar);
        eliminarBoton = findViewById(R.id.eliminar);
        listadoTareas = findViewById(R.id.listaTareas);

        NotasSQLiteHelper notasDb = new NotasSQLiteHelper(MainActivity.this,"Notas",null,1);
        SQLiteDatabase db = notasDb.getWritableDatabase();
        if (db==null){
            throw new RuntimeException("La db no existe");
        }
        actualizarListado(db);
        db.close();

        tarea.requestFocus();
        codigo.setText(String.valueOf(numeroCodigo(notasDb)));

        agregarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!codigo.getText().toString().isEmpty() && !tarea.getText().toString().isEmpty()){
                    if(Integer.parseInt(codigo.getText().toString()) != 0){
                        if(Integer.parseInt(codigo.getText().toString()) > numeroCodigo(notasDb)-1){
                            addNota(notasDb,"Notas","codigo","tarea", Integer.parseInt(codigo.getText().toString()),tarea.getText().toString());
                            resetearCampos(notasDb,false);
                        }else{
                            Toast.makeText(MainActivity.this, "Para modificar este registro debes presionar el botón Modificar", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "El código debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Debes introducir un codigo y una tarea", Toast.LENGTH_SHORT).show();
                }
            }
        });
        modificarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!codigo.getText().toString().isEmpty() && !tarea.getText().toString().isEmpty()){
                    if(Integer.parseInt(codigo.getText().toString()) < numeroCodigo(notasDb) && Integer.parseInt(codigo.getText().toString()) != 0){
                        updateNota(notasDb,"Notas",codigo.getText().toString(),tarea.getText().toString());
                        resetearCampos(notasDb,false);
                    }else{
                        Toast.makeText(MainActivity.this, "Para modificar un registro debes ingresar un codigo del listado", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Debes introducir un codigo y una tarea", Toast.LENGTH_SHORT).show();
                }
            }
        });
        eliminarBoton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!codigo.getText().toString().isEmpty()){
                    if(Integer.parseInt(codigo.getText().toString()) < numeroCodigo(notasDb) && Integer.parseInt(codigo.getText().toString()) != 0){
                        deleteNota(notasDb,"Notas",codigo.getText().toString());
                        resetearCampos(notasDb,true);
                    }else{
                        Toast.makeText(MainActivity.this, "Para eliminar un registro debes ingresar un codigo del listado", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Debes introducir un codigo", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
    private void resetearCampos(NotasSQLiteHelper db, boolean seElimino){
        if(seElimino){
            codigo.setText(String.valueOf(numeroCodigo(db)+1));
        }else{
            codigo.setText(String.valueOf(numeroCodigo(db)));
        }
        tarea.setText("");
    }

    // INSERT
    private void addNota(NotasSQLiteHelper notaSql, String tableName, String col1, String col2, int val1, String val2){
        SQLiteDatabase db = notaSql.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(col1, val1);
        cv.put(col2, val2);
        db.insert(tableName,null ,cv);

        actualizarListado(db);
        db.close();
    }
    //UPDATE
    private void updateNota(NotasSQLiteHelper notaSql, String tableName, String codigo, String nuevoValor){
        SQLiteDatabase db = notaSql.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("tarea", nuevoValor);
        db.update(tableName,cv ,"codigo="+codigo,null);
        actualizarListado(db);
        db.close();
    }
    //DELETE
    private void deleteNota(NotasSQLiteHelper notaSql, String tableName, String codigo){
        SQLiteDatabase db = notaSql.getWritableDatabase();
        db.delete(tableName,"codigo="+codigo,null);
        actualizarListado(db);
        db.close();
    }
    private void actualizarListado(SQLiteDatabase db){
        String results = selectAllTable(db);
        listadoTareas.setText(results);
    }

    private String selectAllTable(SQLiteDatabase db){

        StringBuilder stringBuilder = new StringBuilder();

        Cursor c = db.rawQuery(" SELECT * FROM Notas;", null);

        if (c.moveToFirst()) {
            do {
                int codigo = c.getInt(0);
                String tarea = c.getString(1);
                stringBuilder.append(codigo + " - ").append(tarea).append("\n");
            } while(c.moveToNext());
        }
        return stringBuilder.toString();
    }

    private int numeroCodigo(NotasSQLiteHelper notasSQLiteHelper){
        SQLiteDatabase db = notasSQLiteHelper.getWritableDatabase();
        Cursor c = db.rawQuery(" SELECT * FROM Notas;", null);
        int numero = c.getCount() + 1;
        db.close();
        return numero;
    }
}