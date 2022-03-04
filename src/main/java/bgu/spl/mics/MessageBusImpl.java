package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.services.ConferenceService;
import bgu.spl.mics.application.services.TimeService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private ConcurrentHashMap<MicroService, BlockingQueue> microServices;
	private ConcurrentHashMap<Class<? extends Event>, BlockingQueue> events;
	private ConcurrentHashMap<Class<? extends Broadcast>, BlockingQueue> broadcasts;
	private ConcurrentHashMap<Event, Future> futures;


	private static class SingletonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private MessageBusImpl(){
		microServices = new ConcurrentHashMap<>();

		events = new ConcurrentHashMap<>();
		events.put(TrainModelEvent.class, new LinkedBlockingQueue<MicroService>());
		events.put(TestModelEvent.class, new LinkedBlockingQueue<MicroService>());
		events.put(PublishResultsEvent.class, new LinkedBlockingQueue<MicroService>());
		broadcasts = new ConcurrentHashMap<>();
		broadcasts.put(TickBroadcast.class, new LinkedBlockingQueue<MicroService>());
		broadcasts.put(PublishConferenceBroadcast.class, new LinkedBlockingQueue<MicroService>());
		broadcasts.put(FinalTickBroadcast.class, new LinkedBlockingQueue<MicroService>());
		futures = new ConcurrentHashMap<Event, Future>();
	}

	public static MessageBusImpl getInstance(){ //MessageBus is a singleton and as one we don't want its constructor to be available
		return SingletonHolder.instance;
	}

	@Override
	public  <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		synchronized (type){
			events.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (type){
			broadcasts.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		futures.get(e).resolve(result);
		futures.remove(e);
	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		Queue queue = broadcasts.get(b.getClass());
		Iterator it = queue.iterator();
		while(it.hasNext()){
			microServices.get(it.next()).add(b);
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (e.getClass()){
			BlockingQueue<MicroService> queue = events.get(e.getClass());
			if(queue.isEmpty()){
				return null;
			} else {
				if(!futures.containsKey(e)){
					Future<T> future = new Future<>();
					futures.put(e, future);
				}
				if(e.getClass() != PublishResultsEvent.class){
					MicroService m = queue.poll();
					microServices.get(m).add(e);
					queue.add(m);
				} else {
					Iterator it = queue.iterator();
					ConferenceService min = null;
					int date = Integer.MAX_VALUE;
					while (it.hasNext()){
						ConferenceService curr = (ConferenceService) it.next();
						if(curr.getDate() < date){
							date= curr.getDate();
							min = curr;
						}
					}
					if(min != null){
						microServices.get(min).add(e);
					}
				}
				return futures.get(e);
			}
		}
	}

	@Override
	public void register(MicroService m) {
		BlockingQueue queue = new LinkedBlockingQueue<Message>();
		microServices.put(m, queue);
	}

	@Override
	public synchronized void unregister(MicroService m) {
		if(microServices.containsKey(m)){
			microServices.remove(m);
		}

		for(Class<? extends Event> e : events.keySet()){
			events.get(e).remove(m);
		}
		for(Class<? extends Broadcast> e : broadcasts.keySet()){
			broadcasts.get(e).remove(m);
		}

		/** Should check how to delete the microservice queue**/
	}


	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		return (Message) microServices.get(m).take();

	}

	

}
