package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.messages.*;

import java.util.Vector;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    private final ConfrenceInformation confrenceInformation;
    private final Vector<PublishResultsEvent> publishResultsEvents;

    public ConferenceService(String name, int date) {
        super("Conference " + name + " service");
        confrenceInformation = new ConfrenceInformation(name, date);
        publishResultsEvents = new Vector<>();
    }

    @Override
    protected void initialize() {
        /**
         * TickBroadcast callback, for each tick the conference checks if it is time to publish
         * if the time has come, the conference will broadcast all it's models and then update the MessageBus.
         * the MessageBus updates the student who sent the model, that it was published successfully.
         * after that the conference terminates
         */
        Callback<TickBroadcast> tick_callBack = c -> {
            if (confrenceInformation.isDone(c.getTime())) {
                sendBroadcast(new PublishConferenceBroadcast(confrenceInformation.getAggregated()));
                for (PublishResultsEvent publishResultsEvent : publishResultsEvents) {
                    complete(publishResultsEvent, true);
                }
                terminate();
            }
        };
        /**
         * PublishResultEvent callback, adds the Model to the list of models in the conference
         */
        Callback<PublishResultsEvent> publishResult_callBack = c -> {
            publishResultsEvents.add(c);
            confrenceInformation.addModel(c.getModel());
        };
        /**
         * TerminateBroadcast callback, terminates the service
         */
        Callback<TerminateBroadcast> terminate_callBack = c -> {
            terminate();
        };
        subscribeBroadcast(TerminateBroadcast.class, terminate_callBack);
        subscribeBroadcast(TickBroadcast.class, tick_callBack);
        subscribeEvent(PublishResultsEvent.class, publishResult_callBack);
    }

    @Override
    public String toString() {
        return confrenceInformation.toString();
    }
}
