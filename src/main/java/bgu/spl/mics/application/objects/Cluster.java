package bgu.spl.mics.application.objects;


import bgu.spl.mics.Event;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import com.google.gson.annotations.Expose;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	/**
     * Retrieves the single instance of this class.
     */
	private static class ClusterHolder{
		private static Cluster instance = new Cluster();
	}

	private Cluster(){
		GPUS = new ConcurrentHashMap<>();
		CPUData = new ConcurrentHashMap<>();
		unprocessedData = new LinkedBlockingDeque<>();
		modelsTrained = new LinkedList<>();
		batchesProcessed = new AtomicInteger(0);
		cpuTimeUsed = new AtomicInteger(0);
		gpuTimeUsed = new AtomicInteger(0);
	}

	public static Cluster getInstance() {
		return ClusterHolder.instance;
	}

	private ConcurrentHashMap<GPU, BlockingQueue> GPUS; //keeps queues for each GPU to hold processed data
	private ConcurrentHashMap<CPU, BlockingQueue> CPUData; //For each CPU it holds a queue with unprocessed data
	private BlockingQueue<DataBatch> unprocessedData; //The cluster holds this data and gives it to the CPUS
	private LinkedList<Model> modelsTrained;

	@Expose private AtomicInteger batchesProcessed;
	@Expose private AtomicInteger cpuTimeUsed;
	@Expose private AtomicInteger gpuTimeUsed;

	public void trainData(int n, GPU gpu){
		Queue q = GPUS.get(gpu);
		q.clear();
	}

	public synchronized void sendDataToCPU(CPU cpu){
		if(!unprocessedData.isEmpty()){
			cpu.addData(unprocessedData.remove());
		}
	}

	public void addToCluster(DataBatch d){
		unprocessedData.add(d);
	}

	public void sendBackToGPU(DataBatch data){
		batchesProcessed.getAndIncrement();
		data.getGpu().addProcessedData(data);
	}

	public void registerCPU(CPU cpu) {
		BlockingQueue queue = new LinkedBlockingQueue<DataBatch>();
		CPUData.put(cpu, queue);
	}

	public void registerGPU(GPU gpu) {
		BlockingQueue queue = new LinkedBlockingQueue<DataBatch>();
		GPUS.put(gpu, queue);
	}

	public void addModelTrained(Model m){
		modelsTrained.add(m);
	}

	public void incCPUTicks(int t){
		cpuTimeUsed.addAndGet(t);
	}

	public void incGPUTicks(int t){
		gpuTimeUsed.addAndGet(t);
	}

}
