import java.util.*;
import java.util.stream.Collectors;

public class TaskSchedulerInfo {

    public static List<Integer> getPossibleFrameSizes(ArrayList<Task> tasks, Boolean constraint1) {
        // constraint 2
        Set<Integer> possibleFrameSizes = new HashSet<>();
        Set<Double> setOfPeriods = getSetOfPeriods(tasks);
        for (Double periodsMS : setOfPeriods) {
            possibleFrameSizes.addAll(Math2.allFactors(periodsMS.intValue()));
        }

        // constraint 3
        List<Integer> frameSizesPassed2And3 = new ArrayList<>();
        List<Integer> sortedFrameSizes = new ArrayList<>(possibleFrameSizes);
        Collections.sort(sortedFrameSizes);
        for (Integer frameSize : sortedFrameSizes) {
            if (passConstraint3(tasks, frameSize)) {
                frameSizesPassed2And3.add(frameSize);
            }
        }

        // constraint 1
        if (constraint1) {
            double max = 0;
            for (Task task : tasks) {
                max = Math.max(
                        Math.ceil(task.getExecutionMS()),
                        max);
            }
            Double finalMax = max;
            frameSizesPassed2And3 = frameSizesPassed2And3.stream().filter(fs -> fs > finalMax).collect(Collectors.toList());
        }

        return frameSizesPassed2And3.stream().sorted().collect(Collectors.toList());
    }

    public static Double getTaskUtilization(Task task) {
        return task.getExecutionMS() / task.getPeriodMS();
    }

//    public static Double getTotalExecutionTimeMSPerHyperPeriod(ArrayList<Task> tasks) {
//        int totalExecutionTimeMS = 0;
//        int hyperPeriod = getHyperPeriod(tasks).intValue();
//        for (Task task : tasks) {
//            int jobs = hyperPeriod / task.getPeriodMS().intValue();
//            totalExecutionTimeMS += (jobs * task.getExecutionMS());
//        }
//        return totalExecutionTimeMS;
//    }

    public static Double getSystemUtilization(ArrayList<Task> tasks) {
        double util = 0.0d;
        for (Task task : tasks) {
            util += TaskSchedulerInfo.getTaskUtilization(task);
        }
        return util;
    }

    public static Double getHyperPeriod(ArrayList<Task> tasks) {
        Set<Double> setOfPeriods = getSetOfPeriods(tasks);
        return Math2.lcm_of_array_elements(setOfPeriods.toArray(new Double[setOfPeriods.size()]));
    }

//    public static double getTotalExecutionTime(ArrayList<Task> tasks) {
//        Double total = 0.0;
//        for (Task task : tasks) {
//            total += task.getExecutionMS();
//        }
//        return total;
//    }

    public static Integer getTotalNumJobsPerHyperPeriod(ArrayList<Task> tasks) {
        Integer numJobs = 0;
        Double hyperPeriod = getHyperPeriod(tasks);
        for (Task task : tasks) {
            numJobs += getNumJobsPerHyperPeriod(task, hyperPeriod);
        }
        return numJobs;
    }

    public static Double getTotalExecutionTimePerHyperPeriod(ArrayList<Task> tasks) {
        int totalExecutionTimeMS = 0;
        double hyperPeriod = getHyperPeriod(tasks);
        for (Task task : tasks) {
            int numJobs = getNumJobsPerHyperPeriod(task, hyperPeriod);
            int jobExecutionCost = (int)(task.getExecutionMS() * IterativeNetworkFlow.SCALE);
            totalExecutionTimeMS += (numJobs * jobExecutionCost);
        }
        return totalExecutionTimeMS / IterativeNetworkFlow.SCALE;
    }

    public static Integer getNumJobsPerHyperPeriod(Task task, Double hyperPeriod) {
        double numJobs = Math.floor((hyperPeriod - task.getPhaseMS()) / task.getPeriodMS());
        return (int) numJobs;
    }

    private static Boolean passConstraint3(ArrayList<Task> tasks, Integer frameSize) {
        // to pass constraint 3:
        // 2f − gcd(pi,f) ≤ Di
        for (Task task : tasks) {
            if ((2 * frameSize) - Math2.gcd(task.getPeriodMS().intValue(), frameSize) > task.getRelativeDeadlineMS()) {
                return false;
            }
        }

        return true;
    }

    private static Set<Double> getSetOfPeriods(ArrayList<Task> tasks) {
        Set<Double> setOfPeriods = new HashSet<>();
        for (Task task : tasks) {
            setOfPeriods.add(task.getPeriodMS());
        }
        return setOfPeriods;
    }
}
