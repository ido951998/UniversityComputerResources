package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * The TerminateBroadcast is an implementation of class Broadcast
 * It is used by TimeService, TimeService will send this event after sending the last tick.
 * all services subscribe to this class in order to know that the last tick was sent out.
 * Once a service receives this message it knows there will be no more ticks, and can Terminate safely.
 */

public class TerminateBroadcast implements Broadcast {

}