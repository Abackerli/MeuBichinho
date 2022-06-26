package com.example.meubichinho.utils;

import android.content.Context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.meubichinho.db.DatabaseHelper;
import com.example.meubichinho.db.Parentesco;
import com.example.meubichinho.db.Animal;
import com.example.meubichinho.db.TipoParentesco;
import com.example.meubichinho.model.FamilyMember;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONStringer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConvertAnimal {
    private DatabaseHelper db;

    public ConvertAnimal(Context context){
        db = new DatabaseHelper(context);
    }

    private List<FamilyMember> listarFamilyMembers(){
        List<FamilyMember> members = new ArrayList<>();
        List<Animal> Animals = db.getAllAnimals();
        for (Animal Animal:Animals) {
            Animal.popularParentescos(db.getAllParentescosByIdAnimal(Animal.getId()));
            FamilyMember member = new FamilyMember(){{
                setMemberId(String.valueOf(Animal.getId()));
                setMemberName(Animal.getNome());
                setCall(Animal.getTitulo());
                setFatherId(Animal.getPai()!=null?String.valueOf(Animal.getPai().getIdParente()):null);
                setMotherId(Animal.getMae()!=null?String.valueOf(Animal.getMae().getIdParente()):null);
                setSpouseId(Animal.getParceiro()!=null?String.valueOf(Animal.getParceiro().getIdParente()):null);
            }};
            members.add(member);
        }
        return members;
    }

    public String listarJsonFamilyMembers(){
        List<FamilyMember> members = listarFamilyMembers();
        Type familyMemberType = new TypeToken<FamilyMember>(){}.getType();

        Gson gson = new Gson();

        JsonArray array = new JsonArray();

        for (FamilyMember member:
             members) {
            JsonElement element = new Gson().toJsonTree(member,familyMemberType);
            array.add(element);
        }
        String strArray = array.toString();


        return strArray;
    }

}
