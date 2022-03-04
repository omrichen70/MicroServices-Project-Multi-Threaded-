package bgu.spl.mics.application.objects;

import bgu.spl.mics.util.DataType;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */


    private DataType type;



    private int processed;
    private int size;
    private int index;

    public Data(String type, int size){
        this.type = DataType.valueOf(type);
        this.processed = 0;
        this.size = size;
        this.index = 0;
    }

    public int getProcessed(){
        return processed;
    }

    public int getSize(){
        return size;
    }

    public DataType getType() {
        return type;
    }

    public void updateProcessed(int processed) {
        this.processed = this.processed + processed;
    }

    public int getIndex() {
        return index;
    }

    public void incIndex() {
        this.index = index + 1000;
    }
}
