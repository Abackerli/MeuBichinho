package com.example.meubichinho.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.meubichinho.AddParente;
import com.example.meubichinho.meubichinho;
import com.example.meubichinho.Perfil;
import com.example.meubichinho.db.DatabaseHelper;
import com.example.meubichinho.interfaces.OnFamilySelectListener;
import com.example.meubichinho.model.FamilyMember;
import com.example.meubichinho.ui.view.FamilyTreeView;
import com.example.meubichinho.ui.view.FamilyTreeView2;
import com.example.meubichinho.utils.AssetsUtil;
import com.example.meubichinho.utils.ConvertAnimal;
import com.example.meubichinho.utils.ToastMaster;
import com.example.meubichinho.R;
import com.example.meubichinho.db.FamilyLiteOrm;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.util.List;


public class FamilyTreeActivity extends BaseActivity {

    public static final String HAVE_FOSTER_PARENT = "have_foster_parent";

    private Button btnEnlarge;
    private Button btnShrinkDown;
    private FloatingActionButton fabAddParente, fabAtualizarmeubichinho;
    private FamilyTreeView ftvTree;
    ScaleGestureDetector scaleGestureDetector;

    private FamilyLiteOrm mDatabase;

    private boolean haveFosterParent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_tree);

        initView();

        fabAddParente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FamilyTreeActivity.this, AddParente.class);
                startActivity(intent);
            }
        });

        fabAtualizarmeubichinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar snackbar = Snackbar.make(view, "Árvore atualizada", Snackbar.LENGTH_SHORT);
                snackbar.show();
                setData();
            }
        });

        setData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDatabase != null) {
            mDatabase.closeDB();
        }
    }

    @Override
    public void onPermissionSuccess() {
        setData();
    }

    private void initView() {
  
        fabAddParente = (FloatingActionButton) findViewById(R.id.fabAddParente);
        fabAtualizarmeubichinho = (FloatingActionButton) findViewById(R.id.fabAtualizarmeubichinho);
        ftvTree = (FamilyTreeView) findViewById(R.id.ftv_tree);
 
    }

    private void setData() {
        try{
            haveFosterParent = getIntent().getBooleanExtra(HAVE_FOSTER_PARENT, false);
            if (haveFosterParent) {
                ftvTree.setVisibility(View.GONE);

                btnEnlarge.setVisibility(View.GONE);
                btnShrinkDown.setVisibility(View.GONE);
            } else {
                ftvTree.setVisibility(View.VISIBLE);

            }

            mDatabase = new FamilyLiteOrm(this);

            String novoJson = new ConvertAnimal(this).listarJsonFamilyMembers();
            List<FamilyMember> mList = JSONObject.parseArray(novoJson, FamilyMember.class);


            String MY_ID = "1";
            mDatabase.deleteTable();
            mDatabase.save(mList);

            FamilyMember mFamilyMember = mDatabase.getFamilyTreeById(MY_ID);

            if (mFamilyMember != null) {
                if (haveFosterParent) {
                } else {
                    ftvTree.setFamilyMember(mFamilyMember);
                }
            }

            ftvTree.setOnFamilySelectListener(familySelect);

        }
        catch (Exception ex){
            String msg = ex.getMessage();
        }
    }


    private OnFamilySelectListener familySelect = new OnFamilySelectListener() {
        @Override
        public void onFamilySelect(FamilyMember family) {
            if (family.isSelect()) {
                abrirTelaPerfil(family.getMemberId());
  
                String currentFamilyId = family.getMemberId();
                FamilyMember currentFamily = mDatabase.getFamilyTreeById(currentFamilyId);
                if (currentFamily != null) {
                    if (haveFosterParent) {
                  
                    } else {
                        ftvTree.setFamilyMember(currentFamily);
                    }
                }
            }
        }
    };

   
    public void abrirTelaPerfil(String id){
        Intent intent = new Intent(this, Perfil.class);
        intent.putExtra("id",Integer.parseInt(id));
        startActivity(intent);
    }
}
