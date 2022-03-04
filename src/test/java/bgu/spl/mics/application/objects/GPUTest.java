package bgu.spl.mics.application.objects;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GPUTest {

    private GPU gpu;
    private Cluster cluster;
    private DataBatch dataBatch;
    private Model m;

    @Before
    public void setUp() throws Exception {
        gpu = new GPU("RTX3090");
        cluster = Cluster.getInstance();
        dataBatch = new DataBatch(new Data("Images", 1000), 0, gpu);
        m = new Model("Model", "Images", 1000, null );
    }

    @After
    public void tearDown() throws Exception {
        gpu = null;
        cluster = null;
        dataBatch = null;
        m = null;
    }

    @Test
    public void getType() {
        assertEquals(GPU.Type.RTX3090, gpu.getType());
    }

    @Test
    public void getModel() {
        assertEquals(null, gpu.getModel());
        gpu.setModel(m);
        assertEquals(m, gpu.getModel());
    }

    @Test
    public void getNumOfTicks() {
        assertEquals(1, gpu.getNumOfTicks());
    }

    @Test
    public void getMemory() {
        assertEquals(32, gpu.getMemory());
    }

    @Test
    public void getName() {
        assertEquals("RTX3090", gpu.getName());
    }

    @Test
    public void setModel() {
        assertEquals(null, gpu.getModel());
        gpu.setModel(m);
        assertEquals(m, gpu.getModel());
    }

    @Test
    public void getCluster() {
        assertEquals(Cluster.getInstance(), gpu.getCluster());
    }

    @Test
    public void getTicksCounter() {
        assertEquals(0, gpu.getTicksCounter());
    }

    @Test
    public void setTicksCounter() {
        assertEquals(0, gpu.getTicksCounter());
        gpu.setTicksCounter(10);
        assertEquals(10, gpu.getTicksCounter());
    }

    @Test
    public void incTickCounter() {
        assertEquals(0, gpu.getTicksCounter());
        gpu.incTickCounter();
        assertEquals(1, gpu.getTicksCounter());
    }

    @Test
    public void getStartingTime() {
        assertEquals(-1, gpu.getStartingTime());
    }

    @Test
    public void setStartingTime() {
        assertEquals(-1, gpu.getStartingTime());
        gpu.setStartingTime(10);
        assertEquals(10, gpu.getStartingTime());
    }

    @Test
    public void isCurrentlyTraining() {
        assertEquals(false, gpu.isCurrentlyTraining());
        gpu.setCurrentlyTraining(true);
        assertEquals(true, gpu.isCurrentlyTraining());
    }

    @Test
    public void setCurrentlyTraining() {
        assertEquals(false, gpu.isCurrentlyTraining());
        gpu.setCurrentlyTraining(true);
        assertEquals(true, gpu.isCurrentlyTraining());
    }



}