package bgu.spl.mics.application.objects;

import com.google.gson.annotations.Expose;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    @Expose private int cores;
    private DataBatch data;
    private Cluster cluster;

    public CPU(int cores){
        this.cores = cores;
        data = null;
        cluster = Cluster.getInstance();
        cluster.registerCPU(this);
    }

    public int getCores() {
        return cores;
    }

    public DataBatch getData(){
        return data;
    }

    public void addData(DataBatch dataBatch){
        this.data = dataBatch;
    }

    public Cluster getCluster() {
        return cluster;
    }
}
