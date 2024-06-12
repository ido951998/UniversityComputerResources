package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.GPU;

import java.util.Vector;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent}, {@link TestModelEvent} and {@link TickBroadcast}
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private final GPU gpu;
    private final Vector<Event> modelsInQueue;

    public GPUService(String name, GPU.Type type) {
        super("GPU " + name + " service");
        gpu = new GPU(type);
        this.modelsInQueue = new Vector<>();
    }

    @Override
    protected void initialize() {
        gpu.registerToCluster();
        /**
         * TickBroadcast callback, in each tick the GPUService decides what should the gpu do in this tick
         * if the gpu is not busy and the service has a model waiting for training, the GPU begins training that model
         * if the GPU is already training a model, then the callback will trigger a processing cycle
         */
        Callback<TickBroadcast> tick_callBack = c -> {
            if (!modelsInQueue.isEmpty()) {
                if (modelsInQueue.get(0).getClass() == TrainModelEvent.class) {
                    if (!gpu.isBusy() && !modelsInQueue.isEmpty()) {
                        gpu.setModel(((TrainModelEvent) modelsInQueue.get(0)).getModel());
                    }
                    if (gpu.trainModelSingleCycle()) {
                        complete(modelsInQueue.get(0), ((TrainModelEvent) modelsInQueue.get(0)).getModel());
                        modelsInQueue.remove(0);
                    }
                } else {
                    gpu.setModel(((TestModelEvent) (modelsInQueue.get(0))).getModel());
                    gpu.testModel();
                    complete(modelsInQueue.get(0), ((TestModelEvent) (modelsInQueue.get(0))).getResult());
                    modelsInQueue.remove(0);
                }
            }
        };
        /**
         * TrainModelEvent callback, adds the new event to Queue of models
         */
        Callback<TrainModelEvent> train_callBack = c -> {
            modelsInQueue.add(c);
        };
        /**
         * TestModelEvent callback, adds the new model to Queue of models
         */
        Callback<TestModelEvent> test_callBack = c -> {
            modelsInQueue.add(c);
        };
        /**
         * TerminateBroadcast callback, the GPU sends its statistics to the cluster and then the service terminates
         */
        Callback<TerminateBroadcast> terminate_callBack = c -> {
            gpu.updateTimeAtCluster();
            terminate();
        };
        subscribeBroadcast(TerminateBroadcast.class,terminate_callBack);
        subscribeBroadcast(TickBroadcast.class, tick_callBack);
        subscribeEvent(TrainModelEvent.class, train_callBack);
        subscribeEvent(TestModelEvent.class, test_callBack);
    }

}
