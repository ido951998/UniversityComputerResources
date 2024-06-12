package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

/**
 * The TestModelEvent is an implementation of class Event
 * It is used by Students, each Student creates a new TestModelEvent when
 * it finished training a model and wants to test it.
 * GPUs subscribe to this class in order to receive new models for testing.
 *
 * the message holds the model being sent, so the GPU may receive and test it.
 */
public class TestModelEvent implements Event<Model.Results> {
    /**
     * model being sent from student to cluster for testing
     */
    private final Model model;

    /**
     * builds a new TestModelEvent with the model
     * @param model sent from student for testing
     */
    public TestModelEvent(Model model){
        this.model = model;
    }

    /**
     * used by GPU to receive the model for testing
     * @return model held in message
     */
    public Model getModel(){
        return model;
    }

    /**
     * used by the gpu service to resolve the result back in the MessageBus
     * @return enum Result, the result of testing the model
     */
    public Model.Results getResult() {
        return model.getResults();
    }
}
