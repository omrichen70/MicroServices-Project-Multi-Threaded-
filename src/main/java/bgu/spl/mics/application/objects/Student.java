package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.util.Degree;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */



    @Expose private String name;
    @Expose private String department;
    @Expose private Degree status;

    @Expose private int publications;
    @Expose private int papersRead;

    private ArrayList<Model> models;
    @Expose private ArrayList<Model> modelsTrained;
    private HashMap<Event, Future> futures;

    public Student(String name, String department, String status, int publications, int papersRead){
        this.name = name;
        this.department = department;
        this.status = Degree.valueOf(status);
        this.publications = publications;
        this.papersRead = papersRead;
        models = new ArrayList<Model>();
        futures = new HashMap<>();
        modelsTrained = new ArrayList<Model>();
    }

    public String getName() {
        return name;
    }

    public String getDepartment(){
        return department;
    }

    public Degree getStatus() {
        return status;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public void addPublications() {
        this.publications++;
    }

    public void addPapersRead() {
        this.papersRead++;
    }

    public ArrayList<Model> getModels() {
        return models;
    }

    public void addModel(Model m){
        models.add(m);
    }

    public Model getModel(String name){
        for(Model model : models){
            if(model.getName() == name){
                return model;
            }
        }
        return null;
    }

    public void addFuture(Event e,Future f){
        futures.put(e, f);
    }

    public HashMap<Event, Future> getFutures(){
        return futures;
    }

    public void addTrainedModel(Model m){
        modelsTrained.add(m);
    }

    public ArrayList<Model> getModelsTrained(){
        return modelsTrained;
    }
}
