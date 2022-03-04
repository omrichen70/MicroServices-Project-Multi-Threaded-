package bgu.spl.mics.application.objects;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    @Expose private String name;
    @Expose private int date;
    @Expose private ArrayList<Model> publications;

    public ConfrenceInformation(String name, int date){
        this.name = name;
        this.date = date;
        publications = new ArrayList<Model>();
    }

    public String getName() {
        return name;
    }

    public int getDate() {
        return date;
    }

    public ArrayList<Model> getGoodModels(){
        return publications;
    }

    public void addGoodModel(Model m){
        publications.add(m);
    }
}
