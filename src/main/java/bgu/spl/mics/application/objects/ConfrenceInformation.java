package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class ConfrenceInformation {

    private final String name;
    private final int date;
    private final Vector<Model> goodModels;

    public ConfrenceInformation(String name, int date){
        this.date = date;
        this.name = name;
        this.goodModels = new Vector<>();
    }

    /**
     * used by the conference service to check whether the conference time is now
     * @param time  current time in the system
     * @return  true if the current time is the conference time, false otherwise
     */
    public boolean isDone(int time) {
        return date == time;
    }

    /**
     * used by the conference service to add a new model to the conference
     * @param model model to be added
     */
    public void addModel(Model model){
        goodModels.add(model);
    }

    /**
     * used by the conference service, when publishing the conference
     * the method will return all the models held in the conference
     * @return  the methods in the conference
     */
    public Vector<Model> getAggregated(){
        return goodModels;
    }

    /**
     * used for building output file
     * @return returns String in required format
     */
    @Override
    public String toString() {
        String modelsStr = "";
        for (Model m : goodModels){
            modelsStr += m.toString();
            modelsStr += '\n';
        }

        return  "\tname='" + name + '\'' + '\n' +
                "\tdate=" + date + '\n' +
                "\tpublications=\n" + modelsStr + '\n'
                ;
    }
}
