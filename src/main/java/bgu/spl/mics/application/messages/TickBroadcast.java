package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * The TickBroadcast is an implementation of class Broadcast
 * It is used by TimeService, TimeService sends this out after each Sleep cycle.
 * all services subscribe to this class in order to perceive the passage of time.
 *
 * it holds the current time, so conferences know when to publish.
 */
public class TickBroadcast implements Broadcast {
    /**
     * current time from TimeService
     */
    private final int time;

    public TickBroadcast(int time){
        this.time = time;
    }

    /**
     *
     * @return time, current time from TimeService
     */
    public int getTime() {
        return time;
    }
}
