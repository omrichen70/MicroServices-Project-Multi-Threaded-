package bgu.spl.mics.application.objects;

import bgu.spl.mics.util.DataType;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {


    private Data data;
    private int start_index;
    private GPU gpu;

    public DataBatch(Data data, int start_index, GPU gpu){
        this.data = data;
        this.start_index = start_index;
        this.gpu = gpu;
    }

    public Data getData() {
        return data;
    }

    public int getStart_index() {
        return start_index;
    }

    public DataType getType(){
        return data.getType();
    }

    public GPU getGpu() {
        return gpu;
    }
}
