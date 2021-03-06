package com.example.meubichinho;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meubichinho.db.DatabaseHelper;
import com.example.meubichinho.db.Animal;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListaParente extends AppCompatActivity {

    public ListView listViewDados;
    public Button botao;
    private MaterialToolbar toolbar;
    public ArrayList<Integer> arrayIds;
    public Integer idSelecionado;
    private DatabaseHelper db = new DatabaseHelper(this);
    private List<Animal> parentes = new ArrayList<Animal>();
    public  ArrayList<String> nomesParentes = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        Objects.requireNonNull(getSupportActionBar()).hide();

        iniciarComponentes();

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaCadastro();
            }
        });

        listViewDados.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                idSelecionado = parentes.get(i).getId();
                if(idSelecionado != 1) {
                    confirmaExcluir();
                }
                else{
                    Snackbar snackbar = Snackbar.make(view, "Não é possível apagar o próprio usuário", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }

                return true;
            }
        });

        listViewDados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                idSelecionado = parentes.get(i).getId();
                abrirTelaPerfil();
            }
        });

        listarDados();

        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        listarDados();
    }

    public void listarDados(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nomesParentes);
        listViewDados.setAdapter(arrayAdapter);
    }

    private ArrayList<String> preencherDados(List<Animal> parentes) {
        ArrayList<String> dados = new ArrayList<String>();
        for (Animal Animal:
                parentes) {
            dados.add(Animal.getNome());
        }
        return dados;}

    public void abrirTelaCadastro(){
        Intent intent = new Intent(this,AddParente.class);
        startActivity(intent);
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
                iniciarComponentes();
                listarDados();
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
            db.deleteAnimal(idSelecionado);
            listarDados();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void abrirTelaPerfil(){
        Intent intent = new Intent(this, Perfil.class);
        intent.putExtra("id",idSelecionado);
        startActivity(intent);
    }

    private void iniciarComponentes(){
        botao = (Button) findViewById(R.id.buttonAlterar);
        listViewDados = (ListView) findViewById(R.id.listViewDados);
        parentes = db.getAllAnimals();
        nomesParentes = preencherDados(parentes);
        toolbar = findViewById(R.id.toolbar);
    }
}
