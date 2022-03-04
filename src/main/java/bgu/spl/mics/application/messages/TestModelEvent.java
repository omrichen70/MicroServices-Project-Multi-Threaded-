package bgu.spl.mics.application.messages;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

public class TestModelEvent<T> implements Event<T> {
    private Model model;

    public TestModelEvent(Model model){
        this.model = model;
    }

    public Model getModel() {
        return model;
    }
}
