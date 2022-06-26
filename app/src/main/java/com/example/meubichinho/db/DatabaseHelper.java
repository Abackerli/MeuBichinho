package com.example.meubichinho.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.example.meubichinho.db.Parentesco;
import com.example.meubichinho.db.Animal;
import com.example.meubichinho.db.TipoParentesco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class DatabaseHelper extends SQLiteOpenHelper {


    private String usuarioId;
    FirebaseFirestore fireDB = FirebaseFirestore.getInstance();


    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "meubichinho_";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(Animal.CREATE_TABLE);
        db.execSQL(Parentesco.CREATE_TABLE);
        db.execSQL(TipoParentesco.CREATE_TABLE);
        db.execSQL(TipoParentesco.INSERT_VALORES);

 
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Animal.NOME_TABELA);
        db.execSQL("DROP TABLE IF EXISTS " + Parentesco.NOME_TABELA);
        db.execSQL("DROP TABLE IF EXISTS " + TipoParentesco.NOME_TABELA);

        onCreate(db);
    }

    public void recreate() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + Animal.NOME_TABELA);
        db.execSQL("DROP TABLE IF EXISTS " + Parentesco.NOME_TABELA);
        db.execSQL("DROP TABLE IF EXISTS " + TipoParentesco.NOME_TABELA);

        onCreate(db);
    }

    public long insertAnimal(String nome, String titulo, String imagem, String genero, String dtNasc) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Animal.NOME, nome);
        values.put(Animal.TITULO, titulo);
        values.put(Animal.IMAGEM, imagem);
        values.put(Animal.GENERO, genero);
        values.put(Animal.DT_NASC, dtNasc);

        long id = db.insert(Animal.NOME_TABELA, null, values);

        db.close();

        return id;
    }

    private long insertParentesco(int idAnimal, int idParente, int idTipoParentesco) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Parentesco.ID_Animal, idAnimal);
        values.put(Parentesco.ID_PARENTE, idParente);
        values.put(Parentesco.ID_TIPO_PARENTESCO, idTipoParentesco);

        long id = db.insert(Parentesco.NOME_TABELA, null, values);

        db.close();

        return id;
    }

    public long insertTipoParentesco(String nomeTipoParentesco) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(TipoParentesco.NOME_TIPO_PARENTESCO, nomeTipoParentesco);

        long id = db.insert(TipoParentesco.NOME_TIPO_PARENTESCO, null, values);

        db.close();

        return id;
    }

    public long insertParentescoMae(int idAnimal, int idParente) {
        return insertParentesco(idAnimal, idParente, TipoParentesco.mae);
    }

    public long insertParentescoPai(int idAnimal, int idParente) {
        return insertParentesco(idAnimal, idParente, TipoParentesco.pai);
    }

    public void insertParentescoparceiro(int idAnimal, int idParente) {
        insertParentesco(idAnimal, idParente, TipoParentesco.parceiro);
        insertParentesco(idParente, idAnimal, TipoParentesco.parceiro);
    }

    public Animal getAnimal(long id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Animal.NOME_TABELA,
                new String[]{Animal.ID_Animal, Animal.NOME, Animal.TITULO, Animal.IMAGEM, Animal.GENERO, Animal.DT_NASC},
                Animal.ID_Animal + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Animal Animal = new Animal(
                cursor.getInt(cursor.getColumnIndex(Animal.ID_Animal)),
                cursor.getString(cursor.getColumnIndex(Animal.NOME)),
                cursor.getString(cursor.getColumnIndex(Animal.TITULO)),
                cursor.getString(cursor.getColumnIndex(Animal.IMAGEM)),
                cursor.getString(cursor.getColumnIndex(Animal.GENERO)),
                cursor.getString(cursor.getColumnIndex(Animal.DT_NASC))
        );



        cursor.close();

        return Animal;
    }

    public Parentesco getParentesco(long id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Parentesco.NOME_TABELA,
                new String[]{Parentesco.ID_PARENTESCO, Parentesco.ID_Animal, Parentesco.ID_PARENTE, Parentesco.ID_TIPO_PARENTESCO},
                Parentesco.ID_PARENTESCO + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Parentesco parentesco = new Parentesco(
                cursor.getInt(cursor.getColumnIndex(Parentesco.ID_PARENTESCO)),
                cursor.getInt(cursor.getColumnIndex(Parentesco.ID_Animal)),
                cursor.getInt(cursor.getColumnIndex(Parentesco.ID_PARENTE)),
                cursor.getInt(cursor.getColumnIndex(Parentesco.ID_TIPO_PARENTESCO))
        );


        cursor.close();

        return parentesco;
    }

    public Parentesco getParentesco(int idAnimal, int idTipoParentesco) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(Parentesco.NOME_TABELA,
                new String[]{Parentesco.ID_PARENTESCO, Parentesco.ID_Animal, Parentesco.ID_PARENTE, Parentesco.ID_TIPO_PARENTESCO},
                Parentesco.ID_Animal + "=? and " + Parentesco.ID_TIPO_PARENTESCO + "=?",
                new String[]{String.valueOf(idAnimal),String.valueOf(idTipoParentesco)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Parentesco parentesco = new Parentesco(
                cursor.getInt(cursor.getColumnIndex(Parentesco.ID_PARENTESCO)),
                cursor.getInt(cursor.getColumnIndex(Parentesco.ID_Animal)),
                cursor.getInt(cursor.getColumnIndex(Parentesco.ID_PARENTE)),
                cursor.getInt(cursor.getColumnIndex(Parentesco.ID_TIPO_PARENTESCO))
        );

        cursor.close();

        return parentesco;
    }

    public List<Animal> getAllAnimals() {
        List<Animal> Animals = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Animal.NOME_TABELA + " ORDER BY " +
                Animal.NOME + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Animal Animal = new Animal();
                Animal.setId(cursor.getInt(cursor.getColumnIndex(Animal.ID_Animal)));
                Animal.setNome(cursor.getString(cursor.getColumnIndex(Animal.NOME)));
                Animal.setTitulo(cursor.getString(cursor.getColumnIndex(Animal.TITULO)));
                Animal.setImagem(cursor.getString(cursor.getColumnIndex(Animal.IMAGEM)));
                Animal.setGenero(cursor.getString(cursor.getColumnIndex(Animal.GENERO)));
                Animal.setDtNasc(cursor.getString(cursor.getColumnIndex(Animal.DT_NASC)));

                Animals.add(Animal);
            } while (cursor.moveToNext());
        }

        db.close();

        for (Animal Animal:
             Animals) {
            List<Parentesco> parentescos = getAllParentescosByIdAnimal(Animal.getId());
            Animal.popularParentescos(parentescos);
        }


        return Animals;
    }

    public List<Parentesco> getAllParentescosByIdAnimal(int idAnimal) {
        List<Parentesco> parentescos = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Parentesco.NOME_TABELA + " WHERE " + Parentesco.ID_Animal + " = " +
                idAnimal;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Parentesco parentesco = new Parentesco();
                parentesco.setId(cursor.getInt(cursor.getColumnIndex(Parentesco.ID_PARENTESCO)));
                parentesco.setIdAnimal(cursor.getInt(cursor.getColumnIndex(Parentesco.ID_Animal)));
                parentesco.setIdParente(cursor.getInt(cursor.getColumnIndex(Parentesco.ID_PARENTE)));
                parentesco.setIdTipoParentesco(cursor.getInt(cursor.getColumnIndex(Parentesco.ID_TIPO_PARENTESCO)));

                parentescos.add(parentesco);
            } while (cursor.moveToNext());
        }

        db.close();

        return parentescos;
    }

    public List<Parentesco> getAllParentescosByIdParente(int idParente) {
        List<Parentesco> parentescos = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + Parentesco.NOME_TABELA + " WHERE " + Parentesco.ID_PARENTE + " = " +
                idParente;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Parentesco parentesco = new Parentesco();
                parentesco.setId(cursor.getInt(cursor.getColumnIndex(Parentesco.ID_PARENTESCO)));
                parentesco.setIdAnimal(cursor.getInt(cursor.getColumnIndex(Parentesco.ID_Animal)));
                parentesco.setIdParente(cursor.getInt(cursor.getColumnIndex(Parentesco.ID_PARENTE)));
                parentesco.setIdTipoParentesco(cursor.getInt(cursor.getColumnIndex(Parentesco.ID_TIPO_PARENTESCO)));

                parentescos.add(parentesco);
            } while (cursor.moveToNext());
        }

        db.close();

        return parentescos;
    }

    public List<TipoParentesco> getAllTiposParentesco() {
        List<TipoParentesco> tiposParentesco = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TipoParentesco.NOME_TABELA;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TipoParentesco tipoParentesco = new TipoParentesco();
                tipoParentesco.setId(cursor.getInt(cursor.getColumnIndex(TipoParentesco.ID_TIPO_PARENTESCO)));
                tipoParentesco.setNomeTipoParentesco(cursor.getString(cursor.getColumnIndex(TipoParentesco.NOME_TIPO_PARENTESCO)));

                tiposParentesco.add(tipoParentesco);
            } while (cursor.moveToNext());
        }


        db.close();


        return tiposParentesco;
    }

    public int getAnimalsCount() {
        String countQuery = "SELECT  * FROM " + Animal.NOME_TABELA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        return count;
    }

    public int updateAnimal(Animal Animal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Animal.NOME, Animal.getNome());
        values.put(Animal.TITULO, Animal.getTitulo());
        values.put(Animal.IMAGEM, Animal.getImagem());
        values.put(Animal.GENERO, Animal.getGenero());
        values.put(Animal.DT_NASC, Animal.getDtNasc());

        return db.update(Animal.NOME_TABELA, values, Animal.ID_Animal + " = ?",
                new String[]{String.valueOf(Animal.getId())});
    }

    public void deleteAnimal(int idAnimal) {
        deleteAllParentescos(idAnimal);

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(Animal.NOME_TABELA, Animal.ID_Animal + " = ?",
                new String[]{String.valueOf(idAnimal)});
        db.close();
    }

    public void deleteAllParentescos(int idAnimal){
        ArrayList<Parentesco> parentescos = new ArrayList<Parentesco>();
        parentescos.addAll(getAllParentescosByIdAnimal(idAnimal));
        parentescos.addAll(getAllParentescosByIdParente(idAnimal));

        for (Parentesco parentesco : parentescos) {
            deleteParentesco(parentesco.getId());
        }
    }

    public void deleteParentescoparceiro(int idParentesco) {
        Parentesco parentesco = this.getParentesco(idParentesco);
        Parentesco parentesco2 = this.getParentesco(parentesco.getIdParente(),TipoParentesco.parceiro);

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Parentesco.NOME_TABELA, Parentesco.ID_PARENTESCO + " = ?",
                new String[]{String.valueOf(idParentesco)});
        db.delete(Parentesco.NOME_TABELA, Parentesco.ID_PARENTESCO + " = ?",
                new String[]{String.valueOf(parentesco2.getId())});
        db.close();
    }

    public void deleteParentescoMae(int idParentesco) {
        deleteParentesco(idParentesco);
    }

    public void deleteParentescoPai(int idParentesco) {
        deleteParentesco(idParentesco);
    }

    private void deleteParentesco(int idParentesco) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Parentesco.NOME_TABELA, Parentesco.ID_PARENTESCO + " = ?",
                new String[]{String.valueOf(idParentesco)});
        db.close();
    }

    public void insertValoresTeste(){
        int eu = (int) insertAnimal("Breno","Eu","","Masculino","");
        int mae = (int)insertAnimal("Marileide","Mãe","","Feminino","");
        int pai = (int)insertAnimal("Carlos","Pai","","Masculino","");
        int vovo = (int)insertAnimal("José","Avô","","Masculino","");
        int vo = (int)insertAnimal("Marília","Avó","","Feminino","");
        int esp = (int)insertAnimal("Giselle","parceiro","","Feminino","");
        int filha = (int)insertAnimal("Larissa","Filha","","Feminino","");
        int neto = (int)insertAnimal("Jorge","Neto","","Masculino","");

        insertParentescoPai(eu,pai);
        insertParentescoPai(mae,vovo);
        insertParentescoPai(filha,eu);

        insertParentescoMae(eu, mae);
        insertParentescoMae(mae, vo);
        insertParentescoMae(filha, esp);
        insertParentescoMae(neto, filha);

        insertParentescoparceiro(eu,esp);
        insertParentescoparceiro(mae,pai);
        insertParentescoparceiro(vovo,vo);
    }
}
