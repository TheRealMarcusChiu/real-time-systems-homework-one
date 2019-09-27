
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Application {

    public static void main(String[] args) throws IOException {
        // read file from /src/main/resources folder
        File file = new File(Application.class.getClassLoader().getResource(args[0]).getFile());
//         File file = new File("absolute path");
        ArrayList<Task> tasks = Task.getTasks(file);

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println("Task Utilization " + (i + 1) + ": " + TaskSchedulerInfo.getTaskUtilization(task));
        }
        System.out.println("System Utilization: " + TaskSchedulerInfo.getSystemUtilization(tasks));
        System.out.println("Hyper Period: " + TaskSchedulerInfo.getHyperPeriod(tasks));

        List<Integer> possibleFrameSizes = TaskSchedulerInfo.getPossibleFrameSizes(tasks, true);
        System.out.println("Possible Frame-Sizes (All Constraints): " + possibleFrameSizes.toString());

        possibleFrameSizes = TaskSchedulerInfo.getPossibleFrameSizes(tasks, false);
        System.out.println("Possible Frame-Sizes (W/O Constraint 1): " + possibleFrameSizes.toString());

//        ArrayList<Task> tasks = new ArrayList<>();
//        tasks.add(new Task(1,0.0,4.0,1.0,4.0));
//        tasks.add(new Task(2,0.0,5.0,2.0,7.0));
//        tasks.add(new Task(3,0.0,20.0,5.0,20.0));

        System.out.println("\nINF START");
        IterativeNetworkFlow.start(tasks);
    }
}
