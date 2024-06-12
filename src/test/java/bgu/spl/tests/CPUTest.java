package bgu.spl.tests;
import static org.junit.Assert.*;

import bgu.spl.mics.application.objects.*;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class CPUTest {

    public static CPU cpu16;
    public static CPU cpu32;
    public  static  Cluster cluster;

    @Before
    public void setUp() {
        cpu16 = new CPU(16);
        cpu32 = new CPU(32);
        cluster = Cluster.getInstance();
        Data data = new Data(Data.Type.Images,1000);
        DataBatch dataBatch = new DataBatch(data,0);
        GPU gpu = new GPU(GPU.Type.RTX3090);
        cluster.sendUnprocessedData(dataBatch,gpu);
    }

    @Test

    public void testCPU1() {
        assertTrue(cpu16.getData().isEmpty());
        assertTrue(cpu32.getData().isEmpty());
        cpu16.processBatchSingleCycle();
        cpu32.processBatchSingleCycle();
        assertTrue(cpu32.getData().isEmpty());
        for(int i = 0; i<7; i++){
            assertFalse(cpu16.getData().isEmpty());
            cpu16.processBatchSingleCycle();
        }
        assertTrue(cpu16.getData().isEmpty());
    }

}
