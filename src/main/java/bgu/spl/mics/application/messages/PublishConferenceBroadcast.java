package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.Vector;

/**
 * The PublishConferenceBroadcast is an implementation of class Broadcast
 * It is used by Conferences, each Conference creates a new PublishConferenceBroadcast when
 * the time of the conference arrived.
 * Students subscribe to this class in order to increase the number of papers they've read.
 *
 * the message holds all the models held by the conference, so the students may receive the publications.
 */
public class PublishConferenceBroadcast implements Broadcast {
    /**
     * aggregated good tested models from all students sent to the conference being published
     */
    private final Vector<Model> aggregated;

    /**
     * Builds new publish broadcast with the models supplied from the conference
     * @param aggregated models from the conference
     */
    public PublishConferenceBroadcast(Vector<Model> aggregated){
        this.aggregated = aggregated;
    }

    /**
     * used by students to increase the number of papers they've read.
     * @return number of papers in the conference being published.
     */
    public int getNumberOfPublications(){
        return aggregated.size();
    }
}
