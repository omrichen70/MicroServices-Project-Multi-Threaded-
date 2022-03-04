package bgu.spl.mics.application.objects;

import bgu.spl.mics.Event;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.util.Status;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    enum Type {RTX3090, RTX2080, GTX1080}

    @Expose private Type type;
    @Expose private Model model;
    private Cluster cluster;
    private ArrayList<DataBatch> processedData;
    private ArrayList<DataBatch> unprocessedData;
    private int ticksCounter;
    private int startingTime;
    private boolean currentlyTraining;
    private int freeSpace;
    private boolean hasUnprocessedData;

    public GPU(String t){
        type = Type.valueOf(t);
        model = null;
        cluster = Cluster.getInstance();
        processedData = new ArrayList<DataBatch>();
        cluster.registerGPU(this);
        ticksCounter = 0;
        startingTime = -1;
        currentlyTraining = false;
        freeSpace = getMemory();
        hasUnprocessedData = false;
        unprocessedData = new ArrayList<>();
    }

    public Type getType() {
        return type;
    }

    public Model getModel() {
        return model;
    }

    public int getNumOfTicks(){
        if(type == Type.GTX1080){
            return 4;
        } else if(type == Type.RTX2080){
            return 2;
        } else {
            return 1;
        }
    }

    public int getMemory(){
        if(type == Type.GTX1080){
            return 8;
        } else if(type == Type.RTX2080){
            return 16;
        } else {
            return 32;
        }
    }

    public String getName(){
        if(type == Type.GTX1080){
            return "GTX1080";
        } else if(type == Type.RTX2080){
            return "RTX2080";
        } else {
            return "RTX3090";
        }
    }


    public void setModel(Model m){
        this.model = m;
        for(int i = m.getData().getSize(); i >= 0; i=i-1000){
            DataBatch dataBatch = new DataBatch(m.getData(), i, this);
            unprocessedData.add(dataBatch);
        }
    }


    public Cluster getCluster(){
        return cluster;
    }

    public int getTicksCounter() {
        return ticksCounter;
    }

    public void setTicksCounter(int ticksCounter) {
        this.ticksCounter = ticksCounter;
    }

    public void incTickCounter(){ticksCounter++;}

    public int getStartingTime() {
        return startingTime;
    }

    public void setStartingTime(int startingTime) {
        this.startingTime = startingTime;
    }

    public boolean isCurrentlyTraining() {
        return currentlyTraining;
    }

    public void setCurrentlyTraining(boolean currentlyTraining) {
        this.currentlyTraining = currentlyTraining;
    }

    public void trainModel(){
        synchronized (this){
            if(!processedData.isEmpty()){
                if(startingTime == -1){
                    startingTime = ticksCounter;
                } else if(ticksCounter >= startingTime + getNumOfTicks()){
                    processedData.remove(0);
                    model.getData().updateProcessed(1000);
                    this.getCluster().incGPUTicks(getNumOfTicks());
                    startingTime = -1;
                    freeSpace++;
                    sendData();
                }

            }
            if (model.getData().getSize() <= model.getData().getProcessed()){
                hasUnprocessedData = false;
                currentlyTraining = false;
            }
        }
    }

    public void sendData(){
        Data data = model.getData();
        for(int i = 0; i < freeSpace && !unprocessedData.isEmpty(); i++){
            cluster.addToCluster(unprocessedData.remove(0));
        }
        freeSpace = 0;
    }

    public void addProcessedData(DataBatch d){
        processedData.add(d);
    }
}
