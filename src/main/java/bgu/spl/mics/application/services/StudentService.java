package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.util.Result;
import bgu.spl.mics.util.Status;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    private Student student;
    private Future future;
    private int modelCounter;
    private int ticks;
    private Model m; /*CHANGED*/

    public StudentService(Student student) {
        super(student.getName());
        this.student = student;
        modelCounter =0;
        future = null;
        m = null;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(FinalTickBroadcast.class, (message) -> {
            this.terminate();
        });
        subscribeBroadcast(PublishConferenceBroadcast.class, (message) -> {
            for (Model model : message.getConf().getGoodModels()) {
                if (student.getModels().contains(model)) {
                    student.addPublications();
                } else {
                    student.addPapersRead();
                }
            }
        });

        subscribeBroadcast(TickBroadcast.class, (message) -> {
            ArrayList<Model> models = student.getModels();
            if (modelCounter < models.size()) {
                if(m == null){
                    m = models.get(modelCounter);
                }
                if (future == null) {
                    future = sendEvent(new TrainModelEvent<>(m));
                } else {
                    if(m.getStatus() == Status.PreTrained && future.isDone()){
                        m = (Model) future.get();
                        m.setStatus(Status.Trained);
                        student.addTrainedModel(m);
                        future = sendEvent(new TestModelEvent<>(m));

                    }

                    if (m.getStatus() == Status.Trained && future.isDone()) {
                        m = (Model) future.get();
                        m.setStatus(Status.Tested);

                        if (m.getResult() == Result.Good) {
                            sendEvent(new PublishResultsEvent(m));
                        }
                        m = null;
                        future = null;
                        modelCounter++;
                    }
                }
            }
        });
    }


}
