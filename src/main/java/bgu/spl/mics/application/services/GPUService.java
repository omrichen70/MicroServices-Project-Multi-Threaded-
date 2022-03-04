package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.DataBatch;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.util.Degree;
import bgu.spl.mics.util.Result;
import bgu.spl.mics.util.Status;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    GPU gpu;
    int ticks;
    boolean full;
    Event e;
    Queue<Message> waitingEvents;

    public GPUService(GPU gpu) {
        super(gpu.getName());
        this.gpu = gpu;
        ticks = 0;
        full = false;
        e = null;
        waitingEvents = new LinkedList<>();
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(FinalTickBroadcast.class, (message) -> {
            this.terminate();
        });
        subscribeBroadcast(TickBroadcast.class, (message) -> {
            ticks++;
            gpu.setTicksCounter(ticks);
            if(isBusy()){
                gpu.trainModel();
            }
            if(isBusy() && !gpu.isCurrentlyTraining()){
                complete(e, gpu.getModel());
                setIfBusy(false);
                e = getNewEvent();
            }
        });

        subscribeEvent(TrainModelEvent.class, (message) -> {
            if(!isBusy()){
                setTrainModel(message);
            } else {
                waitingEvents.add(message);
            }
        });

        subscribeEvent(TestModelEvent.class, (message) -> {
            if(!isBusy()){
                this.testModel(message);
            }
           else{
                waitingEvents.add(message);
            }
        });

    }

    public Event getNewEvent(){
        Event e = null;
        if(!waitingEvents.isEmpty()){
            e = (Event) waitingEvents.remove();
            if(e.getClass() == TrainModelEvent.class){
                this.setTrainModel((TrainModelEvent) e);
            }
            else{
                testModel((TestModelEvent) e);
            }
        }
        return e;
    }

    public void setTrainModel(TrainModelEvent message){
        setIfBusy(true);
        gpu.setModel(message.getModel());
        gpu.setCurrentlyTraining(true);
        gpu.sendData();
        e = message;
    }

    public void testModel(TestModelEvent message){
        setIfBusy(true);
        Student student = message.getModel().getStudent();
        Result res = Result.Good;
        double rand = Math.random();
        if(student.getStatus() == Degree.MSc){
            if(rand>= 0.6){
                res = Result.Bad;
            }
        } else {
            if(rand >= 0.8){
                res = Result.Bad;
            }
        }
        message.getModel().setResult(res);
        complete(message, message.getModel());
        setIfBusy(false);
        e = null;
    }
}
