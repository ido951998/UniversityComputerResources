package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    private final Data data;
    private final int start_index;

    public DataBatch(Data data, int start_index){
        this.data = data;
        this.start_index = start_index;
    }

    public Data.Type getDataType(){
        return data.getType();
    }

    public void increaseProcessed(){
        data.Processed1000();
    }
}
