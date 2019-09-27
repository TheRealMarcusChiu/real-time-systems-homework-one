import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IterativeNetworkFlow {

    public static final Double SCALE = 10000.0d;

    public static void start(ArrayList<Task> tasks) {
        List<Integer> frameSizes = TaskSchedulerInfo.getPossibleFrameSizes(tasks, false)
                .stream()
                .sorted((f1, f2) -> Long.compare(f2, f1))
                .collect(Collectors.toList());

        double totalExecutionTime = TaskSchedulerInfo.getTotalExecutionTimePerHyperPeriod(tasks);
        int hyperPeriod = TaskSchedulerInfo.getHyperPeriod(tasks).intValue();
        int numJobs = TaskSchedulerInfo.getTotalNumJobsPerHyperPeriod(tasks);
        // iterate from largest to smallest frame size
        for (Integer frameSize : frameSizes) {
            double totalFlow = maxFlow(tasks, numJobs, frameSize, hyperPeriod);
            if (totalFlow >= totalExecutionTime) {
                residuleGraph(tasks, numJobs, frameSize, hyperPeriod);
                return;
            }
        }
        System.out.println("INF - FAILED TO FIND");
    }

    private static double maxFlow(ArrayList<Task> tasks, Integer numJobs, Integer frameSize, Integer hyperPeriod) {
        int numFrames = hyperPeriod / frameSize;
        int numNodes = numFrames + numJobs + 2; // numFrames + numJobs + source and sink vertices

        int[][] graph = generateGraph(tasks, frameSize, numFrames, numNodes);

        int totalFlow = new FordFulkerson(numNodes).fordFulkerson(graph, numNodes-1, numNodes);
        return ((double) totalFlow) / IterativeNetworkFlow.SCALE;
    }

    private static void residuleGraph(ArrayList<Task> tasks, Integer numJobs, Integer frameSize, Integer hyperPeriod) {
        int numFrames = hyperPeriod / frameSize;
        int numNodes = numFrames + numJobs + 2; // numFrames + numJobs + source and sink vertices

        int[][] graph = generateGraph(tasks, frameSize, numFrames, numNodes);

        // NODES
        // graph[0] = not used
        // graph[1 to numFrames] = frame nodes
        // graph[(numFrames + 1) to (numNodes - 2)] = job nodes
        // graph[numNodes-1] = source
        // graph[numNodes] = sink
        int[][] residuleGraph = new FordFulkerson(numNodes).fordFulkersonRG(graph, numNodes-1, numNodes);
        double[][] rg = int2Double(residuleGraph, numNodes);

        String[] nodeStrings = generateNodeString(tasks, numFrames, numJobs, frameSize, hyperPeriod);

        for (int i = 1; i <= numFrames; i++) {
            System.out.println("\nFRAME " + i);
            for (int j = (numFrames + 1); j <= (numNodes - 2); j++) {
                if (rg[i][j] > 0.0) {
                    System.out.println(nodeStrings[j] + " - execution-time: " + rg[i][j]);
                }
            }
        }
    }

    private static String[] generateNodeString(ArrayList<Task> tasks, Integer numFrames, Integer numJobs, Integer frameSize, Integer hyperPeriod) {
        // NODES
        // graph[0] = not used
        // graph[1 to numFrames] = frame nodes
        // graph[(numFrames + 1) to (numNodes - 2)] = job nodes
        // graph[numNodes-1] = source
        // graph[numNodes] = sink
        String[] nodeString = new String[numFrames + numJobs + 2];

        // set job node strings
        int jobIndex = numFrames + 1;
        for (Task task : tasks) {
            Integer numJobsOfTask = TaskSchedulerInfo.getNumJobsPerHyperPeriod(task, (double)hyperPeriod);
            for (int i = 0; i < numJobsOfTask; i++) {
                nodeString[jobIndex] = "TASK: " + task.getTaskID() + " - JOB: " + i;
                jobIndex++;
            }
        }

        return nodeString;
    }

    private static int[][] generateGraph(ArrayList<Task> tasks, Integer frameSize, int numFrames, int numNodes) {
        // NODES
        // graph[0] = not used
        // graph[1 to numFrames] = frame nodes
        // graph[(numFrames + 1) to (numNodes - 2)] = job nodes
        // graph[numNodes-1] = source
        // graph[numNodes] = sink

        // CAPACITY
        // graph[x][y] = contains max-capacity from node x to y (one-way)
        double[][] graph = new double[numNodes + 1][numNodes + 1];

        // source-node to job-nodes
        // job-nodes   to frame-nodes
        List<Job> jobs = generateJobs(tasks);
        int nextTaskIndex = numFrames + 1;
        for (Job j : jobs) {
            // source-node to job-nodes
            graph[numNodes-1][nextTaskIndex] = j.getExecutionTime();

            // job-nodes   to frame-nodes
            PossibleFrames possibleFrames = getPossibleRelativeFrameIndex(j, new Double(frameSize));
            for (int i = 0; i <= possibleFrames.getRelativeEndFrameIndex(); i++) {
                int frameIndex = 1 + possibleFrames.getStartFrameIndex() + i;
                if (frameIndex <= numFrames) {
                    graph[nextTaskIndex][frameIndex] = frameSize;
                } else {
                    break;
                }
            }

            nextTaskIndex++;
        }

        // frame-nodes to sink-node
        for (int i = 1; i <= numFrames; i++) {
            graph[i][numNodes] = frameSize;
        }

        return double2Int(graph, numNodes);
    }

    private static double[][] int2Double(int[][] graph, Integer numNodes) {
        double[][] newGraph = new double[numNodes + 1][numNodes + 1];

        for (int i = 0; i <= numNodes; i++) {
            for (int j = 0; j <= numNodes; j++) {
                newGraph[i][j] = (double)graph[i][j] / IterativeNetworkFlow.SCALE;
            }
        }

        return newGraph;
    }

    private static int[][] double2Int(double[][] graph, Integer numNodes) {
        int[][] newGraph = new int[numNodes + 1][numNodes + 1];

        for (int i = 0; i <= numNodes; i++) {
            for (int j = 0; j <= numNodes; j++) {
                newGraph[i][j] = (int)(IterativeNetworkFlow.SCALE * graph[i][j]);
            }
        }

        return newGraph;
    }

    /**
     * first frame indexed at 0
     * @param job
     * @param frameSize
     * @return
     */
    private static PossibleFrames getPossibleRelativeFrameIndex(Job job, Double frameSize) {
        double startFrame = Math.ceil(job.getReleaseTime() / frameSize);
        double relativeEndFrame = Math.floor(job.getRelativeDeadline() / frameSize) - 1.0;

        return new PossibleFrames(
                (int) startFrame,
                (int) relativeEndFrame
        );
    }

    private static List<Job> generateJobs(ArrayList<Task> tasks) {
        List<Job> jobs = new ArrayList<>();
        Double hyperPeriod = TaskSchedulerInfo.getHyperPeriod(tasks);
        for (Task task : tasks) {
            Integer numJobsOfTask = TaskSchedulerInfo.getNumJobsPerHyperPeriod(task, hyperPeriod);
            Double releaseTime = task.getPhaseMS();
            for (int i = 0; i < numJobsOfTask; i++) {
                jobs.add(new Job(
                        releaseTime,
                        task.getExecutionMS(),
                        task.getRelativeDeadlineMS()
                ));
                releaseTime += task.getPeriodMS();
            }
        }
        return jobs;
    }
}
