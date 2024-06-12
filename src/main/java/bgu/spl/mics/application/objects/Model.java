package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Model {


    public enum Status {PreTrained, Training, Trained, Tested}

    public enum Results {None, Good, Bad}

    private Status status;
    private Results results;
    private final Data data;
    private final String name;
    private final Student.Degree studentDegree;

    public Model(String name, int size, Data.Type type, Student.Degree studentDegree) {
        status = Status.PreTrained;
        results = Results.None;
        this.data = new Data(type, size);
        this.name = name;
        this.studentDegree = studentDegree;
    }

    public Data getData() {
        return data;
    }

    public Status getStatus() {
        return status;
    }

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Student.Degree getStudentDegree() {
        return studentDegree;
    }

    @Override
    public String toString() {
        return  "\t\tname='" + name + '\'' + '\n' +
                "\t\tdata=\n" + data +  '\n' +
                "\t\tstatus=" + status + '\n' +
                "\t\tresults=" + results
                ;
    }
}