package bgu.spl.mics.application.objects;


import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private final Collection<GPU> GPUS;
	private final Collection<CPU> CPUS;
	private final ConcurrentHashMap<DataBatch, GPU> map;
	private final Vector<DataBatch> unprocessed;
	private final AtomicLong gpuTimeUsed;
	private final AtomicLong cpuTimeUsed;
	private final AtomicLong batchesProcessed;

	private static class ClusterHolder {
		private static final Cluster cluster = new Cluster();
	}

	private Cluster() {
		GPUS = new Vector<>();
		CPUS = new Vector<>();
		map = new ConcurrentHashMap<>();
		this.unprocessed = new Vector<>();
		gpuTimeUsed = new AtomicLong(0);
		cpuTimeUsed = new AtomicLong(0);
		batchesProcessed = new AtomicLong(0);
	}

	/**
	 * used by cpus to send their work time to the cluster.
	 * @param time	sent from cpu to cluster to aggregate all cpu time used.
	 */
	public void increaseCpuTime(long time) {
		long val;
		do {
			val = cpuTimeUsed.get();
		} while (!cpuTimeUsed.compareAndSet(val, val + time));
	}

	/**
	 * used by gpus to send their work time to the cluster.
	 * @param time	sent from gpu to cluster to aggregate all gpu time used.
	 */
	public void increaseGpuTime(long time) {
		long val;
		do {
			val = gpuTimeUsed.get();
		} while (!gpuTimeUsed.compareAndSet(val, val + time));
	}

	/**
	 * used by cpus to send the amount of batches they have processed to the cluster.
	 * @param batches	number of batches the cpu processed, sent to cluster to aggregate.
	 */
	public void increaseBatchProcessed(long batches) {
		long val;
		do {
			val = batchesProcessed.get();
		} while (!batchesProcessed.compareAndSet(val, val + batches));
	}

	/**
	 * registers gpu to cluster
	 * @param gpu	gpu to be registered
	 */
	public void registerGPU(GPU gpu) {
		GPUS.add(gpu);
	}

	/**
	 * registers cpu to cluster
	 * @param cpu	cpu to be registered
	 */
	public void registerCPU(CPU cpu) {
		CPUS.add(cpu);
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Cluster getInstance() {
		return ClusterHolder.cluster;
	}

	/**
	 * called by a gpu that needs data to be processed
	 * the cluster will add this new batch to it's list and will assign it to a cpu when one is available
	 * the cluster also map between the databatch and the gpu, so it knows who to return the processed data to
	 * @param dataBatch	databacth to be processed
	 * @param gpu	gpu that sent the databatch
	 */
	public synchronized void sendUnprocessedData(DataBatch dataBatch, GPU gpu) {
		unprocessed.add(dataBatch);
		map.put(dataBatch, gpu);
	}

	/**
	 * called by a free cpu to get a new databatch to process
	 * @return	returns the next dataBatch to be processed, null if there is no unprocessed data
	 */
	public synchronized DataBatch getUnprocessedDataFromCluster() {
		if (!unprocessed.isEmpty()) return unprocessed.remove(0);
		return null;
	}

	/**
	 * called by cpu when it is finished processing data.
	 * the cluster will match the processed data with a gpu and send it forward.
	 * @param dataBatch processed dataBatch
	 */
	public synchronized void sendProcessedData(DataBatch dataBatch) {
		map.get(dataBatch).getDataFromCluster(dataBatch);
		map.remove(dataBatch);
	}

	/**
	 * used for building output file
	 * @return returns String in required format
	 */
	@Override
	public String toString() {
		return "cpuTimeUsed=" + cpuTimeUsed + '\n' +
				"gpuTimeUsed=" + gpuTimeUsed + '\n' +
				"batchesProcessed=" + batchesProcessed
				;
	}
}
