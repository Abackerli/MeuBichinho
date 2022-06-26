package com.example.meubichinho;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meubichinho.db.DatabaseHelper;
import com.example.meubichinho.db.Animal;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

public class Perfil extends AppCompatActivity {

    private Button btnEdit, btnAddParentesco, btnLimparParentescos;
    private MaterialToolbar toolbar;
    private TextView perfilNome, perfilTitulo, perfilGenero, perfilDtNasc;
    private DatabaseHelper db = new DatabaseHelper(this);
    public Integer id;
    Animal Animal = new Animal();

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Objects.requireNonNull(getSupportActionBar()).hide();


        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        iniciarComponentes();

        carregarDadosPerfil();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Perfil.this, Editar.class);
                intent.putExtra("id",id);
                startActivity(intent);
            }
        });

        btnLimparParentescos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Animal.getPai() == null && Animal.getMae() == null && Animal.getParceiro() == null) {
                    Snackbar snackbar = Snackbar.make(view, "Animal não possui parentescos", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else{
                    db.deleteAllParentescos(Animal.getId());
                    Snackbar snackbar = Snackbar.make(view, "Parentescos removidos com sucesso", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });

        btnAddParentesco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animal Animal = db.getAnimal(id);
                Animal.popularParentescos(db.getAllParentescosByIdAnimal(id));
                if(Animal.getPai() == null || Animal.getMae() == null || Animal.getParceiro() == null) {
                    Intent intent = new Intent(Perfil.this, AddParentesco.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
                else{
                    Snackbar snackbar = Snackbar.make(view, "Todos os parentescos da Animal estão preenchidos", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
            }
        });

        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
    }

    public void confirmaExcluir() {
        AlertDialog.Builder msgBox = new AlertDialog.Builder(this);
        msgBox.setTitle("Excluir");
        msgBox.setIcon(android.R.drawable.ic_menu_delete);
        msgBox.setMessage("Você realmente deseja excluir esse registro?");
        msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                excluir();
            }
        });
        msgBox.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        msgBox.show();
    }

    public void excluir(){
        try{
            db.deleteAnimal(id);
            Intent intent = new Intent(Perfil.this, ListaParente.class);
            startActivity(intent);
            finish();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void carregarDadosPerfil(){
        perfilNome.setText(db.getAnimal(id).getNome());
        perfilTitulo.setText(db.getAnimal(id).getTitulo());
        perfilGenero.setText(db.getAnimal(id).getGenero());
        perfilDtNasc.setText(db.getAnimal(id).getDtNasc());
    }

    private void iniciarComponentes(){
        perfilNome = findViewById(R.id.perfilNome);
        perfilTitulo = findViewById(R.id.perfilTitulo);
        perfilGenero = findViewById(R.id.perfilGenero);
        perfilDtNasc = findViewById(R.id.perfilDtNasc);
        btnEdit = findViewById(R.id.btnEdit);
        btnAddParentesco = findViewById(R.id.btnAddParent);
        btnLimparParentescos = findViewById(R.id.btnClearParentescos);
        toolbar = findViewById(R.id.toolbar);
        Animal = db.getAnimal(id);
        Animal.popularParentescos(db.getAllParentescosByIdAnimal(id));
    }
}
