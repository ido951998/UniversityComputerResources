package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.Model;

/**
 * The TrainModelEvent is an implementation of class Event
 * It is used by Students, each Student creates a new TestModelEvent when
 * it begins or when the previous model is published and done.
 * GPUs subscribe to this class in order to receive new models for training.
 *
 * the message holds the model being sent, so the GPU may receive and train it.
 */
public class TrainModelEvent implements Event<Model> {
    /**
     * model being sent from student to cluster for training
     */
    private final Model model;

    public TrainModelEvent(Model model){
        this.model = model;
    }

    /**
     *
     * @return model, model held in this message
     */
    public Model getModel() {
        return model;
    }
}
