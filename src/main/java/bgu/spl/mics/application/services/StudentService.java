package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.Vector;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {

    private enum ModelStatus{
        BEGIN, TRAIN, TEST
    }
    private final Student student;
    private ModelStatus modelStatus;
    private int modelsNumber;
    private Future<Model> trainFuture;
    private Future<Model.Results> testFuture;
    private final Vector<Future<Boolean>> publishFutures;

    public StudentService(String name, String department, Student.Degree status, Vector<Model> models) {
        super("Student " + name + " service");
        student = new Student(name, department, status, models);
        modelsNumber = 0;
        trainFuture = null;
        testFuture = null;
        publishFutures = new Vector<>();
        modelStatus = ModelStatus.BEGIN;
    }

    @Override
    protected void initialize() {
        Callback<PublishConferenceBroadcast> conference_callBack = c -> {
            student.increasePapersRead(c.getNumberOfPublications());
        };

        Callback<TickBroadcast> tick_callBack = c -> {
            if (modelStatus == ModelStatus.BEGIN){
                if (modelsNumber < student.getModelSize()) {
                    trainFuture = sendEvent(new TrainModelEvent(student.getModels().get(modelsNumber)));
                    modelsNumber++;
                    modelStatus = ModelStatus.TRAIN;
                }
            }
            else if (modelStatus == ModelStatus.TRAIN){
                if (trainFuture.isDone()){
                    testFuture = sendEvent(new TestModelEvent(trainFuture.get()));
                    modelStatus = ModelStatus.TEST;
                }
            }
            else{
                if (testFuture.isDone()){
                    if (testFuture.get() == Model.Results.Good) {
                        Future<Boolean> new_future = sendEvent(new PublishResultsEvent(trainFuture.get()));
                        if (new_future != null) publishFutures.add(new_future);
                    }
                    modelStatus = ModelStatus.BEGIN;
                }
            }
            for (int i=0; i<publishFutures.size(); i++) {
                Future<Boolean> future = publishFutures.get(i);
                if (future.isDone()) {
                    student.increasePublications();
                    publishFutures.remove(i);
                    i--;
                }
            }
        };

        Callback<TerminateBroadcast> terminate_callBack = c -> {
            terminate();
        };
        subscribeBroadcast(TerminateBroadcast.class, terminate_callBack);
        subscribeBroadcast(PublishConferenceBroadcast.class, conference_callBack);
        subscribeBroadcast(TickBroadcast.class, tick_callBack);
    }

    @Override
    public String toString() {
        return student.toString();
    }
}


