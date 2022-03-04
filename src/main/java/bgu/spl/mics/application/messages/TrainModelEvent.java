package bgu.spl.mics.application.messages;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.util.Status;

public class TrainModelEvent<T> implements Event<T> {
    private Model model;
    private Status status;

    public TrainModelEvent(Model model){
        this.model = model;
        this.status = Status.PreTrained;
    }

    public Model getModel() {
        return model;
    }

    public Status getStatus() {
        return status;
    }
}
