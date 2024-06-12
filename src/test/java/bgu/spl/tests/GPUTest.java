package bgu.spl.tests;
import static org.junit.Assert.*;

import bgu.spl.mics.application.objects.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class GPUTest {
    private static GPU gpu;
    private static Model model;
    private static Cluster cluster;
    private static CPU cpu;

    @Before
    public void setUp() {
        gpu = new GPU(GPU.Type.GTX1080);
        model = new Model("model1",1000, Data.Type.Images, Student.Degree.PhD);
        cluster = Cluster.getInstance();
        cpu = new CPU(32);
    }

    @Test
    public void testGPUSendDataToCluster(){
        assertNull(cluster.getUnprocessedDataFromCluster());
        gpu.setModel(model);
        gpu.trainModelSingleCycle();
        assertNotNull(cluster.getUnprocessedDataFromCluster());
        assertNull(cluster.getUnprocessedDataFromCluster());
    }

    @Test
    public void testGPUTrain(){
        gpu.setModel(model);
        gpu.trainModelSingleCycle();
        for(int i = 0; i<4; i++)
            cpu.processBatchSingleCycle();
        for(int i = 0;i<4;i++) {
            assertEquals(Model.Status.Training, model.getStatus());
            gpu.trainModelSingleCycle();
        }
        assertEquals(Model.Status.Trained, model.getStatus());
    }

    @Test
    public void testGPUTest(){
        model.setStatus(Model.Status.Trained);
        gpu.setModel(model);
        gpu.testModel();
        assertEquals(Model.Status.Tested,model.getStatus());
    }
}
