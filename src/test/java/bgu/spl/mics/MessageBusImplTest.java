package bgu.spl.mics;

import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import bgu.spl.mics.application.services.GPUService;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class MessageBusImplTest {
    private MessageBusImpl mb;
    private MicroService micro;
    private Event e;
    private GPU gpu;
    private Model model;
    private Student student;


    @Before
    public void setUp() throws Exception {
        mb = MessageBusImpl.getInstance();
        gpu = new GPU("RTX3090");
        micro = new GPUService(gpu);
        student = new Student("Gadi", "Computer", "MSc", 0 , 0);
        model = new Model("Test", "Images", 1000, student);
        e = new TrainModelEvent(model);
    }

    @After
    public void tearDown() throws Exception {
        mb = null;
        micro = null;
        gpu = null;
        model = null;
    }

    @Test
    public void getInstance() {
        assertEquals(MessageBusImpl.getInstance(), mb);
    }


    @Test
    public void subscribeBroadcast() throws InterruptedException {
        mb.register(micro);
        mb.subscribeBroadcast(new TickBroadcast().getClass(), micro);
        TickBroadcast tickBroadcast = new TickBroadcast();
        mb.sendBroadcast(tickBroadcast);
        assertEquals(tickBroadcast, mb.awaitMessage(micro));
    }


    @Test
    public void sendBroadcast() throws InterruptedException {
        mb.register(micro);
        mb.subscribeBroadcast(TickBroadcast.class, micro);
        TickBroadcast tickBroadcast = new TickBroadcast();
        mb.sendBroadcast(tickBroadcast);
        assertEquals(tickBroadcast, mb.awaitMessage(micro));
    }


    @Test
    public void register() throws InterruptedException {
        assertThrows(NullPointerException.class, () -> {
            mb.awaitMessage(micro);
        });
        mb.register(micro);
        mb.subscribeBroadcast(TickBroadcast.class, micro);
        TickBroadcast tickBroadcast = new TickBroadcast();
        mb.sendBroadcast(tickBroadcast);
        assertEquals(tickBroadcast, mb.awaitMessage(micro));
    }

    @Test
    public void unregister() throws InterruptedException {
        mb.register(micro);
        mb.subscribeBroadcast(TickBroadcast.class, micro);
        TickBroadcast tickBroadcast = new TickBroadcast();
        mb.sendBroadcast(tickBroadcast);
        mb.unregister(micro);
        assertThrows(NullPointerException.class, () -> {
            mb.awaitMessage(micro);
        });
    }

}