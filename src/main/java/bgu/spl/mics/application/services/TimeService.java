package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

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

	private final int tickTime;
	private final int duration;

	public TimeService(int tickTime, int duration) {
		super("Time Service");
		this.tickTime = tickTime;
		this.duration = duration;
	}

	@Override
	protected void initialize() {
		for (int i = 1; i <= duration; i++) {
			sendBroadcast(new TickBroadcast(i));
			try {
				TimeUnit.MILLISECONDS.sleep(tickTime);
			} catch (InterruptedException e) {

			}
		}
		sendBroadcast(new TerminateBroadcast());
		terminate();
	}
}
