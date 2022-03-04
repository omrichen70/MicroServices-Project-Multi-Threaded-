package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) throws IOException {
        Reader reader = new FileReader(args[0]);
        JsonObject jsonObject = (JsonObject) JsonParser.parseReader(reader);

        //Create Students array
        JsonArray studentsJson = (JsonArray) jsonObject.get("Students");
        ArrayList<Student> students = createStudentsArray(studentsJson);

        //Create models and add to Students
        createModels(studentsJson, students);

        //Create GPUs
        JsonArray gpuArray = (JsonArray) jsonObject.get("GPUS");
        ArrayList<GPU> gpus = createGPUS(gpuArray);

        //Create CPUs
        JsonArray cpuArray = (JsonArray) jsonObject.get("CPUS");
        ArrayList<CPU> cpus = createCPUS(cpuArray);

        //Create Conferences
        JsonArray confArray = (JsonArray) jsonObject.get("Conferences");
        ArrayList<ConfrenceInformation> confs = createConferences(confArray);

        //Create Threads list
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<MicroService> microServices = new ArrayList<>();

        createThreads(threads, students, gpus, cpus, confs, microServices);
        for(Thread thread: threads){
            if(thread.getName() != "TimeService"){
                thread.start();
            }
        }
        boolean allInitialized = false;
        while(!allInitialized){
            int done = threads.size();
            for(MicroService m: microServices){
                if(m.hasInitialized()){
                    done--;
                }
            }
            if(done == 0){
                allInitialized = true;
            }
        }

        TimeService ts = new TimeService(jsonObject.get("TickTime").getAsInt(), jsonObject.get("Duration").getAsInt());
        Thread timeService = new Thread(ts);
        threads.add(timeService);
        microServices.add(ts);
        timeService.start();
        try{
            waitForThreadsToFinish(threads);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }


        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        BufferedWriter writer = Files.newBufferedWriter(Paths.get("output.json"));
        writer.write("Students:");
        gson.toJson(students, writer);
        writer.write("Conferences:");
        gson.toJson(confs, writer);
        writer.write("Statistics:");
        gson.toJson(Cluster.getInstance(), writer);
        writer.flush();
        writer.close();

    }


    public static ArrayList<Student> createStudentsArray(JsonArray studs){
        ArrayList<Student> students = new ArrayList<Student>();
        for(int i = 0; i < studs.size(); i++){
            JsonObject s = studs.get(i).getAsJsonObject();
            students.add(new Student(s.get("name").getAsString(), s.get("department").getAsString(), s.get("status").getAsString(), 0, 0));
        }
        return students;
    }

    public static void createModels(JsonArray studs, ArrayList<Student> students){
        for (int i = 0; i < studs.size(); i++){
            JsonObject s = (JsonObject) studs.get(i);
            JsonArray model = (JsonArray) s.get("models");
            for(int j = 0; j < model.size(); j++){
                JsonObject mod = model.get(j).getAsJsonObject();
                Model m = new Model(mod.get("name").getAsString(), mod.get("type").getAsString(), mod.get("size").getAsInt(), students.get(i));
                students.get(i).addModel(m);
            }
        }
    }

    public static ArrayList<GPU> createGPUS(JsonArray gpuArray){
        ArrayList<GPU> gpus = new ArrayList<GPU>();
        for (int i = 0; i < gpuArray.size(); i++){
            GPU gpu = new GPU(gpuArray.get(i).getAsString());
            gpus.add(gpu);
        }
        return gpus;
    }

    private static ArrayList<CPU> createCPUS(JsonArray cpuArray) {
        ArrayList<CPU> cpus = new ArrayList<CPU>();
        for (int i = 0; i < cpuArray.size(); i++){
            CPU cpu = new CPU(cpuArray.get(i).getAsInt());
            cpus.add(cpu);
        }
        return cpus;
    }

    private static ArrayList<ConfrenceInformation> createConferences(JsonArray confArray){
        ArrayList<ConfrenceInformation> confs = new ArrayList<ConfrenceInformation>();
        for (int i = 0; i < confArray.size(); i++){
            JsonObject c = confArray.get(i).getAsJsonObject();
            ConfrenceInformation conf = new ConfrenceInformation(c.get("name").getAsString(), c.get("date").getAsInt());
            confs.add(conf);
        }
        return confs;
    }

    private static void createThreads(ArrayList<Thread> threads, ArrayList<Student> students, ArrayList<GPU> gpus, ArrayList<CPU> cpus, ArrayList<ConfrenceInformation> confs, ArrayList<MicroService> microServices){

        for(GPU g : gpus){
            GPUService gp = new GPUService(g);
            threads.add(new Thread(gp));
            microServices.add(gp);
        }
        for(CPU c: cpus){
            CPUService cp = new CPUService(c);
            threads.add(new Thread(cp));
            microServices.add(cp);
        }
        for(ConfrenceInformation conf : confs){
            ConferenceService cs = new ConferenceService(conf);
            threads.add(new Thread(cs));
            microServices.add(cs);
        }
        for(Student s : students){
            StudentService ss = new StudentService(s);
            threads.add(new Thread(ss));
            microServices.add(ss);
        }
    }

    private static void waitForThreadsToFinish(ArrayList<Thread> threads) throws InterruptedException {
        for(Thread t : threads){
            t.join();
        }
    }
}
