package bgu.spl.mics.application.objects;

import bgu.spl.mics.util.Result;
import bgu.spl.mics.util.Status;
import com.google.gson.annotations.Expose;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {

    @Expose private String name;
    private Data data;
    private Student student;
    @Expose private Status status;
    @Expose private Result result;

    public Model(String name, String dataType, int dataSize, Student student){
        this.name = name;
        this.data = new Data(dataType, dataSize);
        this.student = student;
        status = Status.PreTrained;
        result = Result.None;
    }

    public String getName() {
        return name;
    }

    public Data getData() {
        return data;
    }

    public Student getStudent() {
        return student;
    }

    public Status getStatus() {
        return status;
    }

    public Result getResult() {
        return result;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void updateData(int n){
        data.updateProcessed(n);
    }
}
