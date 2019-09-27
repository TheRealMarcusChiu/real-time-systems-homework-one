import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Job {
    Double releaseTime;
    Double executionTime;
    Double relativeDeadline;
}
