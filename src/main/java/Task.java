import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

@Data
@AllArgsConstructor
public class Task {
    Integer taskID;
    Double phaseMS;
    Double periodMS;
    Double executionMS;
    Double relativeDeadlineMS;

    public static ArrayList<Task> getTasks(File file) throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();

        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader)) {

            String line;
            Integer taskID = 0;
            while ((line = br.readLine()) != null) {
                taskID++;
                String[] strArray = line.replace('(', ' ').replace(')', ' ').split(",");
                Task task = new Task(
                        taskID,
                        new Double(strArray[0]),
                        new Double(strArray[1]),
                        new Double(strArray[2]),
                        new Double(strArray[3])
                );
                tasks.add(task);
            }
        }

        return tasks;
    }
}
