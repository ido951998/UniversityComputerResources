package bgu.spl.mics.application.objects;

import java.util.*;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private final int cores;
    private final Collection<DataBatch> data;
    private final Cluster cluster;
    private int curr_tick;
    private DataBatch curr_dataBatch;
    private long timeUsed;
    private long processedBatches;

    public CPU(int cores) {
        this.cores = cores;
        this.cluster = Cluster.getInstance();
        data = new Vector<>();
        curr_dataBatch = null;
        curr_tick = 0;
        timeUsed = 0;
        processedBatches = 0;
    }

    /**
     * called by CPUService at initialization, registers the cpu to the cluster
     */
    public void registerToCluster() {
        cluster.registerCPU(this);
    }

    /**
     * called by CPUService at termination, sends number of processed time used to the cluster
     */
    public void updateTimeAtCluster(){
        cluster.increaseCpuTime(timeUsed);
    }
    /**
     * called by CPUService at termination, sends number of processed batches to the cluster
     */
    public void updateBatchesAtCluster(){
        cluster.increaseBatchProcessed(processedBatches);
    }

    /**
     * process a batch of data from the collection and send the result back through the cluster
     * this method is a single tick
     * @return return the processed data
     */
    public void processBatchSingleCycle() {
        if (data.isEmpty()){
            DataBatch next = cluster.getUnprocessedDataFromCluster();
            if (next != null) data.add(next);
        }
        if (curr_tick == 0) {
            if (!data.isEmpty()) {
                curr_dataBatch = data.iterator().next();
                if (curr_dataBatch.getDataType() == Data.Type.Images) {
                    curr_tick = (32 / cores) * 4;
                } else if (curr_dataBatch.getDataType() == Data.Type.Text) {
                    curr_tick = (32 / cores) * 2;
                } else {
                    curr_tick = 32 / cores;
                }
            }
        }
        if (curr_dataBatch != null) {
            timeUsed++;
            curr_tick--;
            if (curr_tick == 0) {
                cluster.sendProcessedData(curr_dataBatch);
                curr_dataBatch.increaseProcessed();
                processedBatches++;
                data.remove(curr_dataBatch);
                curr_dataBatch = null;
            }
        }
    }

    /**
     * used for testing
     * @return  the data in the cpu
     */
    public Collection<DataBatch> getData(){
        return data;
    }

    public String toString(){
        return Integer.valueOf(cores).toString();
    }
}
