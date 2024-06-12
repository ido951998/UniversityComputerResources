package bgu.spl.mics;

import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.StudentService;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	final private ConcurrentHashMap<Class,Vector<MicroService>> eventsSubscribers;
	final private ConcurrentHashMap<Class,Vector<MicroService>> broadcastSubscribers;
	final private ConcurrentHashMap<Event,Future> futures;
	final private ConcurrentHashMap<MicroService, Queue<Message>> queues;
	final private Vector<MicroService> microServices;

	private static class MessageBusImplHolder{
		private static final MessageBusImpl messageBus = new MessageBusImpl();
	}


	private MessageBusImpl(){
		eventsSubscribers= new ConcurrentHashMap<>();
		broadcastSubscribers = new ConcurrentHashMap<>();
		futures = new ConcurrentHashMap<>();
		queues = new ConcurrentHashMap<>();
		microServices = new Vector<>();
	}

	@Override
	public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (eventsSubscribers.containsKey(type)) {
			if (!eventsSubscribers.get(type).contains(m))
				eventsSubscribers.get(type).add(m);
		}
		else {
			Vector<MicroService> v = new Vector<>();
			v.add(m);
			eventsSubscribers.put(type, v);
		}
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (broadcastSubscribers.containsKey(type)){
			if (!broadcastSubscribers.get(type).contains(m))
				broadcastSubscribers.get(type).add(m);
		}
		else{
			Vector<MicroService> v = new Vector<>();
			v.add(m);
			broadcastSubscribers.put(type,v);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		futures.get(e).resolve(result);
	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		Vector<MicroService> v = broadcastSubscribers.get(b.getClass());
		if (v == null){
			System.out.println(b.getClass() + " is lost");
			return;
		}
		for (MicroService m : v){
			queues.get(m).add(b);
			notifyAll();
		}
	}

	
	@Override
	public synchronized  <T> Future<T> sendEvent(Event<T> e) {
		Vector<MicroService> v = eventsSubscribers.get(e.getClass());
		if (v.size() == 0){
			return null;
		}
		Future<T> future = new Future<>();
		futures.put(e,future);
		queues.get(v.get(0)).add(e);
		notifyAll();
		MicroService temp = v.remove(0);
		v.add(temp);
		return future;
	}

	@Override
	public synchronized void register(MicroService m) {
		Queue<Message> newQueue = new LinkedList<>();
		queues.put(m, newQueue);
		microServices.add(m);
	}

	@Override
	public synchronized void unregister(MicroService m) {
		queues.remove(m);
		for(Class c : eventsSubscribers.keySet()){
			eventsSubscribers.get(c).remove(m);
		}
		for(Class c : broadcastSubscribers.keySet()){
			broadcastSubscribers.get(c).remove(m);
		}
	}

	@Override
	public synchronized Message awaitMessage(MicroService m) throws InterruptedException {
		if (!queues.containsKey(m)) return null;
		while(queues.get(m).size() == 0) {
			try{
				wait();
			}
			catch(InterruptedException e){
			}
		}
		return queues.get(m).poll();
	}

	/**
	 *
	 * @return returns the instance of the singleton MessageBusImpl
	 */
	public static MessageBus getInstance() {
		return MessageBusImplHolder.messageBus;
	}

	@Override
	public String toString() {
		String ans = "Students:\n";
		for (MicroService m : microServices){
			if (m.getClass() == StudentService.class){
				ans += m.toString();
				ans += '\n';
			}
		}
		ans += "Conferences\n";
		for (MicroService m : microServices){
			if (m.getClass() == ConferenceService.class){
				ans += m.toString();
			}
		}
		return ans;
	}
}
