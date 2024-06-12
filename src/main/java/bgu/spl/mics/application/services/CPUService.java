package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;

/**
 * CPU service is responsible for handling time updates from {@link TickBroadcast}
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {

    private final CPU cpu;

    public CPUService(String name, int cores) {
        super("CPU " + name + " service");
        cpu = new CPU(cores);
    }

    @Override
    protected void initialize() {
        cpu.registerToCluster();
        /**
         * TickBroadcast callback, in each tick the cpu service will alert the cpu to run a single cycle of processing
         */
        Callback<TickBroadcast> tick_callBack = c -> cpu.processBatchSingleCycle();
        /**
         * TerminateBroadcast callback, the CPU sends its statistics to the cluster and then the service terminates
         */
        Callback<TerminateBroadcast> terminate_callBack = c -> {
            cpu.updateTimeAtCluster();
            cpu.updateBatchesAtCluster();
            terminate();
        };
        subscribeBroadcast(TerminateBroadcast.class,terminate_callBack);
        subscribeBroadcast(TickBroadcast.class,tick_callBack);
    }
}
