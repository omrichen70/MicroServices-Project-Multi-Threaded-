package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.util.DataType;

/**
 * CPU service is responsible for handling the { DataPreProcessEvent}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    private CPU cpu;
    private int ticks;


    public CPUService(CPU cpu) {
        super("CPU");
        this.cpu = cpu;
        ticks = 0;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(FinalTickBroadcast.class, (message) -> {
            this.terminate();
        });
        subscribeBroadcast(TickBroadcast.class, (message) -> {
            if(cpu.getData() == null){
                cpu.getCluster().sendDataToCPU(cpu);
            }
            if(cpu.getData() != null){
                ticks++;
                if(cpu.getData().getType() == DataType.Images){
                    if(ticks >= (32 / cpu.getCores()) * 4){
                        cpu.getCluster().sendBackToGPU(cpu.getData());
                        cpu.getCluster().incCPUTicks(ticks);
                        ticks = 0;
                        cpu.addData(null);
                    }
                } else if(cpu.getData().getType() == DataType.Text){
                    if(ticks >= (32 / cpu.getCores()) * 2){
                        cpu.getCluster().sendBackToGPU(cpu.getData());
                        ticks = 0;
                        cpu.getCluster().incCPUTicks(ticks);
                        cpu.addData(null);
                    }
                } else {
                    if(ticks >= (32 / cpu.getCores())){
                        cpu.getCluster().sendBackToGPU(cpu.getData());
                        ticks = 0;
                        cpu.getCluster().incCPUTicks(ticks);
                        cpu.addData(null);
                    }
                }
            }
        });

    }

}
