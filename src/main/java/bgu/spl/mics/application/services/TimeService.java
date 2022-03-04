package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{

	private int speed;
	private int duration;
	private int tickCounter;
	private int time;
	private Timer timer;
	private TimerTask task;


	public TimeService(int speed, int duration) {
		super("TimeService");
		this.speed = speed;
		this.duration = duration;
		this.time = 1;
		this.tickCounter = 0;
		this.timer = new Timer();
		this.task = new TimerTask() {
			@Override
			public void run() {
				sendBroadcast(new TickBroadcast());
				time++;
				if(time >= duration){
					sendBroadcast(new FinalTickBroadcast());
					timer.cancel();
				}

			}
		};

	}

	@Override
	protected void initialize() {
		subscribeBroadcast(FinalTickBroadcast.class, (message) -> {
			this.terminate();
		});
		timer.scheduleAtFixedRate(task, 0, speed);
	}
	}




