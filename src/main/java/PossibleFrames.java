import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PossibleFrames {
    Integer startFrameIndex;
    Integer relativeEndFrameIndex; // 0 means same as startFrameIndex
}
