package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }

    private final String name;
    private final String department;
    private final Degree status;
    private int publications;
    private int papersRead;
    private final Vector<Model> models;

    public Student(String name, String department, Degree status, Vector<Model> models){
        this.department = department;
        this.name = name;
        this.status = status;
        this.publications = 0;
        this.papersRead = 0;
        this.models = models;
    }

    public void increasePublications(){
        publications++;
    }

    public void increasePapersRead(int numOfPapers){
        papersRead += numOfPapers;
    }

    public int getModelSize(){
        return models.size();
    }

    public Vector<Model> getModels(){
        return models;
    }

    @Override
    public String toString() {
        String modelsStr = "";
        for (Model m : models){
            if (m.getStatus() == Model.Status.Trained) {
                modelsStr += m;
                modelsStr += '\n';
            }
        }

        return  "\tname='" + name + '\'' + '\n' +
                "\tdepartment='" + department + '\'' + '\n' +
                "\tstatus=" + status + '\n' +
                "\tpublications=" + publications + '\n' +
                "\tpapersRead=" + papersRead + '\n' +
                "\tTrained models=\n" + modelsStr
                ;
    }
}
