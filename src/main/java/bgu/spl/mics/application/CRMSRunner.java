package bgu.spl.mics.application;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;


/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        Map<?, ?> map = null;
        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get(args[0]));

            // convert JSON file to map
            map = gson.fromJson(reader, Map.class);

            // close reader
            reader.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        Vector<Thread> threads = new Vector<>();
        Cluster cluster = Cluster.getInstance();
        MessageBus messageBus = MessageBusImpl.getInstance();
        int i = 0;
        for (Map<String,?> studentMap : (ArrayList<Map>)map.get("Students")) {
            String name = (String) studentMap.get("name");
            String department = (String) studentMap.get("department");
            Student.Degree degree;
            if (studentMap.get("status").equals("MSc")) {
                degree = Student.Degree.MSc;
            } else {
                degree = Student.Degree.PhD;
            }
            Vector<Model> models = new Vector<>();
            for (Map<String, ?> modelMap : (ArrayList<Map>) studentMap.get("models")) {
                String modelName = (String) modelMap.get("name");
                Data.Type type;
                if (modelMap.get("type").equals("images")) {
                    type = Data.Type.Images;
                } else if (modelMap.get("type").equals("Text")) {
                    type = Data.Type.Text;
                } else {
                    type = Data.Type.Tabular;
                }
                Double size = (Double) modelMap.get("size");
                models.add(new Model(modelName, size.intValue(), type, degree));
            }
            StudentService studentService = new StudentService(name, department, degree, models);
            threads.add(new Thread(studentService));
        }
        i = 0;
        for (String typeString : ((ArrayList<String>) map.get("GPUS"))){
            GPU.Type type;
            if (typeString.equals("RTX3090")) type = GPU.Type.RTX3090;
            else if (typeString.equals("RTX2080")) type = GPU.Type.RTX2080;
            else type = GPU.Type.GTX1080;
            GPUService gpuService = new GPUService("gpu" + i,type);
            threads.add(new Thread(gpuService));
            i++;
        }
        i=0;
        for (Double cores : ((ArrayList<Double>) map.get("CPUS"))) {
            CPUService cpuService = new CPUService("cpu" + i, cores.intValue());
            threads.add(new Thread(cpuService));
            i++;
        }
        for (Map<String,?> conferencesMap : (ArrayList<Map>)map.get("Conferences")) {
            ConferenceService conferenceService = new ConferenceService((String) conferencesMap.get("name"),((Double)conferencesMap.get("date")).intValue());
            threads.add(new Thread(conferenceService));
        }

        Double tickTime = (Double) map.get("TickTime");
        Double duration = (Double) map.get("Duration");
        TimeService timeService = new TimeService(tickTime.intValue(), duration.intValue());
        threads.add(new Thread(timeService));

        for (int j = 0; j< threads.size()-1; j++){
            threads.get(j).start();
        }

        try{
            TimeUnit.MILLISECONDS.sleep(1);
        } catch(InterruptedException e){

        }

        threads.get(threads.size()-1).start();

        for (Thread t : threads){
            try{
                t.join();
            }
            catch(InterruptedException e){}
        }

        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("./output.txt"));
            writer.write(messageBus.toString() + cluster.toString());
            writer.close();
        } catch (IOException e){

        }
    }
}
