package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.objects.Model;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the { PublishConfrenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    private ConfrenceInformation conf;
    private int ticks;
    private int date;

    public ConferenceService(ConfrenceInformation conf) {
        super(conf.getName());
        this.conf = conf;
        ticks = 0;
        date = conf.getDate();
    }

    public int getDate(){
        return date;
    }


    @Override
    protected void initialize() {

        subscribeBroadcast(FinalTickBroadcast.class, (message) -> {
            this.terminate();
        });

        subscribeBroadcast(TickBroadcast.class, (message) -> {
            ticks++;
            if(ticks == conf.getDate()){
                sendBroadcast(new PublishConferenceBroadcast(conf));
                this.terminate();
            }
        });

        subscribeEvent(PublishResultsEvent.class, (message) -> {
            conf.addGoodModel(message.getModel());
        });

    }
}
