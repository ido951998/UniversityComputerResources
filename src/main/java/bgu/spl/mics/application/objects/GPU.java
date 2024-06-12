package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    /**
     * Enum representing the type of the GPU.
     */
    public enum Type {RTX3090, RTX2080, GTX1080}

    final private int VRAM;
    private int batchesInCluster;
    final private Type type;
    private Model model;
    final private Cluster cluster;
    final private Vector<DataBatch> unprocessed;
    final private Vector<DataBatch> processed;
    private int curr_tick;
    private DataBatch curr_dataBatch;
    private int numOfProcesses;
    private long timeUsed;

    public GPU(Type type) {
        this.type = type;
        if (type == Type.RTX2080) {
            VRAM = 16;
        } else if (type == Type.RTX3090) {
            VRAM = 32;
        } else {
            VRAM = 8;
        }
        this.cluster = Cluster.getInstance();
        this.model = null;
        this.unprocessed = new Vector<>();
        this.processed = new Vector<>();
        this.batchesInCluster = 0;
        this.curr_dataBatch = null;
        this.curr_tick = 0;
        this.numOfProcesses = 0;
        this.timeUsed = 0;
    }

    public String toString(){
        if (type == Type.RTX2080) return "RTX2080";
        else if (type == Type.RTX3090) return "RTX3090";
        else return "GTX1080";
    }

    /**
     * called by GPUService at initialization, registers the gpu to the cluster
     */
    public void registerToCluster() {
        cluster.registerGPU(this);
    }

    /**
     * called by GPUService to set a new model for the gpu
     * if not trained, calls split data
     * sets the model for training or testing
     * @param model from the service
     */
    public void setModel(Model model) {
        this.model = model;
        if (model.getStatus() == Model.Status.PreTrained)
            splitData();
    }

    /**
     * called when a new untrained model enters the GPU
     * splits the model data and stores it in the unprocessed list
     */
    private void splitData() {
        for (int i = 0; i < model.getData().getSize() / 1000; i++) {
            DataBatch temp = new DataBatch(model.getData(), (i * 1000));
            unprocessed.add(temp);
        }
    }

    /**
     * sends an unprocessed dataBatch to the cluster
     */
    private void sendDataToCluster() {
        if (unprocessed.size() == 0) return;
        DataBatch dataBatch = unprocessed.iterator().next();
        batchesInCluster++;
        cluster.sendUnprocessedData(dataBatch, this);
        unprocessed.remove(unprocessed.iterator().next());
    }


    /**
     * called by cluster, when the unprocessed databatch that was sent to cluster is finished being processed
     * @param dataBatch processed batch from the cluster
     */
    public void getDataFromCluster(DataBatch dataBatch) {
        batchesInCluster--;
        processed.add(dataBatch);
    }

    /**
     * called by GPUService at termination, sends number of processed time used to the cluster
     */
    public void updateTimeAtCluster(){
        cluster.increaseGpuTime(timeUsed);
    }

    /**
     * called by GPUService to check if the gpu is currently busy
     * @return true, if the gpu is currently processing a model, false otherwise
     */
    public boolean isBusy() {
        return model != null;
    }

    /**
     * processes the model for each tick the GPUService receives
     * @return true, if the model is finished training, false otherwise
     */
    public boolean trainModelSingleCycle() {
        if (model == null) return false;
        if (model.getStatus() == Model.Status.PreTrained) {
            model.setStatus(Model.Status.Training);
        }
        if (!unprocessed.isEmpty() && processed.size() + batchesInCluster < VRAM) sendDataToCluster();
        if (curr_tick == 0 && !processed.isEmpty()) {
            curr_dataBatch = processed.get(0);
            if (type == Type.GTX1080) curr_tick = 4;
            else if (type == Type.RTX2080) curr_tick = 2;
            else curr_tick = 1;
        }
        if (curr_dataBatch != null) {
            timeUsed++;
            curr_tick--;
            if (curr_tick == 0) {
                numOfProcesses++;
                processed.remove(curr_dataBatch);
                curr_dataBatch = null;
            }
        }
        if (numOfProcesses == model.getData().getSize() / 1000) {
            model.setStatus(Model.Status.Trained);
            model = null;
            numOfProcesses = 0;
            return true;
        }
        return false;
    }

    /**
     * tests the trained model
     */
    public void testModel() {
        if (model.getStatus() == Model.Status.Trained) {
            timeUsed++;
            if (model.getStudentDegree() == Student.Degree.PhD) {
                if (Math.random() > 0.8) {
                    model.setResults(Model.Results.Bad);
                } else {
                    model.setResults(Model.Results.Good);
                }
            } else {
                if (Math.random() > 0.6) {
                    model.setResults(Model.Results.Bad);
                } else {
                    model.setResults(Model.Results.Good);
                }
            }
            model.setStatus(Model.Status.Tested);
        }
        model = null;
    }

}

