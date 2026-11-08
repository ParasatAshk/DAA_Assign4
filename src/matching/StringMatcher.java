package matching;

import java.util.List;
import util.OperationCounter;

public interface StringMatcher {
    String name();

    List<Integer> search(String text, String pattern, OperationCounter counter);
}
