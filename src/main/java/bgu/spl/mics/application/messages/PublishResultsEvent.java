package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

/**
 * The PublishResultEvent is an implementation of class Event
 * It is used by Students, each Student creates a new PublishResultEvent when
 * It finished testing a model, and received a GOOD result.
 * Conferences subscribe to this class in order to receive and register new papers submitted to them.
 *
 * the message holds the model being sent, so the conference may add it to it's aggregate.
 */
public class PublishResultsEvent implements Event<Boolean> {
    /**
     * the Tested and GOOD model being sent to the conference from the student
     */
    private final Model model;

    /**
     * Builds a new PublishResultEvent
     * @param model, the model to be sent
     */
    public PublishResultsEvent(Model model){
        this.model = model;
    }

    /**
     * returns the model held by the message, the receiving conference will add this model to it's aggregate
     * @return model, model of type Model being held by the message
     */
    public Model getModel() {
        return model;
    }
}
