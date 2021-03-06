package com.example.meubichinho;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.meubichinho.db.DatabaseHelper;
import com.example.meubichinho.db.Animal;
import com.example.meubichinho.ui.FamilyTreeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class meubichinho extends AppCompatActivity {

    private TextView nomeUsuario, emailUsuario;
    private Button btnmeubichinho, btnAdd, btnListar, btnDeslogar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String usuarioId, titulo = "Eu", img="", genero = "", dtNasc = "";
    DatabaseHelper helper = new DatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meubichinho);

        Objects.requireNonNull(getSupportActionBar()).hide();
        iniciarComponentes();

        btnDeslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(meubichinho.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(meubichinho.this, AddParente.class);
                startActivity(intent);
            }
        });

        btnmeubichinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(meubichinho.this, FamilyTreeActivity.class);
                startActivity(intent);
            }
        });

        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(meubichinho.this, ListaParente.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        usuarioId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("usuarios").document(usuarioId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                nomeUsuario.setText(documentSnapshot.getString("nome"));
                emailUsuario.setText(email);
                List<Animal> Animals = helper.getAllAnimals();
                if(Animals.isEmpty())
                    helper.insertAnimal(nomeUsuario.getText().toString(), "Eu", "", "", "");
            }
        });
    }

    private void iniciarComponentes(){
        nomeUsuario = findViewById(R.id.txtNomeUsuario);
        emailUsuario = findViewById(R.id.txtEmailUsuario);
        btnDeslogar = findViewById(R.id.btnDeslogar);
        btnmeubichinho = findViewById(R.id.btnmeubichinho);
        btnListar = findViewById(R.id.btnListar);
        btnAdd = findViewById(R.id.btnAdicionar);
        List<Animal> Animals = helper.getAllAnimals();
        if(Animals.isEmpty())
            helper.recreate();

    }
}