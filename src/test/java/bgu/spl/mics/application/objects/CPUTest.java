package bgu.spl.mics.application.objects;

import bgu.spl.mics.util.DataType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CPUTest {

    private CPU cpu;
    private int cores;
    private Cluster cluster;

    @Before
    public void setUp() throws Exception {
        cores = 32;
        cpu = new CPU(cores);
        cluster = Cluster.getInstance();
    }

    @After
    public void tearDown() throws Exception {
        cpu = null;
        cores = 0;
    }

    @Test
    public void getCores() {
        assertEquals(32, cpu.getCores());
    }



    @Test
    public void addData() {
        DataBatch d = new DataBatch(new Data("Images", 1000), 0, null);
        assertEquals(null, cpu.getData());
        cpu.addData(d);
        assertEquals(d, cpu.getData());
    }

    @Test
    public void getCluster() {
        assertEquals(Cluster.getInstance(), cpu.getCluster());
    }
}