package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    public enum Type {
        Images, Text, Tabular
    }

    private final Type type;
    private int processed;
    private final int size;

    public Data(Type type, int size){
        this.type = type;
        this.processed = 0;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public int getProcessed() {
        return processed;
    }

    public Type getType() {
        return type;
    }

    public synchronized void Processed1000(){
        processed += 1000;
    }

    @Override
    public String toString() {
        return  "\t\t\ttype=" + type + '\n' +
                "\t\t\tsize=" + size
                ;
    }
}
