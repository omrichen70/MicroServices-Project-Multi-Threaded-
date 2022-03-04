package bgu.spl.mics.application.messages;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;
import com.sun.org.apache.xpath.internal.operations.Mod;

public class PublishResultsEvent<T> implements Event<T> {
    Model model;

    public PublishResultsEvent(Model m){
        this.model = m;
    }

    public Model getModel(){
        return model;
    }
}
