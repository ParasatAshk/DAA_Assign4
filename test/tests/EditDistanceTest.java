package tests;

import dp.EditDistance;
import util.OperationCounter;

public class EditDistanceTest {

    public static void run() {
        TestSupport.checkEquals("Edit distance identical strings", 0, EditDistance.distance("abcd", "abcd"));
        TestSupport.checkEquals("Edit distance source empty", 3, EditDistance.distance("", "abc"));
        TestSupport.checkEquals("Edit distance target empty", 4, EditDistance.distance("abcd", ""));
        TestSupport.checkEquals("Edit distance both empty", 0, EditDistance.distance("", ""));
        TestSupport.checkEquals("Edit distance substitution only", 1, EditDistance.distance("cat", "cut"));
        TestSupport.checkEquals("Edit distance insertion only", 1, EditDistance.distance("cat", "cart"));
        TestSupport.checkEquals("Edit distance deletion only", 1, EditDistance.distance("cart", "cat"));
        TestSupport.checkEquals("Edit distance mixed operations", 3, EditDistance.distance("kitten", "sitting"));
        TestSupport.checkEquals("Edit distance is symmetric", EditDistance.distance("sunday", "saturday"), EditDistance.distance("saturday", "sunday"));

        OperationCounter counter = new OperationCounter();
        EditDistance.distance("abcd", "abc", counter);
        TestSupport.checkTrue("Edit distance fills n times m cells", counter.cellUpdates == 12);
    }
}
