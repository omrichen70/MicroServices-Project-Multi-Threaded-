package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.ConfrenceInformation;

import java.util.ArrayList;

public class PublishConferenceBroadcast implements Broadcast {
    private ConfrenceInformation conf;

    public PublishConferenceBroadcast(ConfrenceInformation conf){
        this.conf = conf;
    }

    public ConfrenceInformation getConf() {
        return conf;
    }
}
