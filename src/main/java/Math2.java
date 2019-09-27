import java.util.ArrayList;
import java.util.Collections;

public class Math2 {

    /**
     * Least-Common-Multiple of array of elements
     * @param element_array
     * @return least-common-multiple of element_array
     */
    public static Double lcm_of_array_elements(Double[] element_array)
    {
        double lcm_of_array_elements = 1.0d;
        int divisor = 2;

        while (true) {
            int counter = 0;
            boolean divisible = false;

            for (int i = 0; i < element_array.length; i++) {

                // lcm_of_array_elements (n1, n2, ... 0) = 0.
                // For negative number we convert into
                // positive and calculate lcm_of_array_elements.

                if (element_array[i] == 0) {
                    return 0.0d;
                }
                else if (element_array[i] < 0) {
                    element_array[i] = element_array[i] * (-1);
                }
                if (element_array[i] == 1) {
                    counter++;
                }

                // Divide element_array by devisor if complete
                // division i.e. without remainder then replace
                // number with quotient; used for find next factor
                if (element_array[i] % divisor == 0) {
                    divisible = true;
                    element_array[i] = element_array[i] / divisor;
                }
            }

            // If divisor able to completely divide any number
            // from array multiply with lcm_of_array_elements
            // and store into lcm_of_array_elements and continue
            // to same divisor for next factor finding.
            // else increment divisor
            if (divisible) {
                lcm_of_array_elements = lcm_of_array_elements * divisor;
            }
            else {
                divisor++;
            }

            // Check if all element_array is 1 indicate
            // we found all factors and terminate while loop.
            if (counter == element_array.length) {
                return lcm_of_array_elements;
            }
        }
    }

    /**
     * Greatest Common Divisor
     * @param a
     * @param b
     * @return
     */
    public static double gcd(double a, double b)
    {
        // Everything divides 0
        if (a == 0)
            return b;
        if (b == 0)
            return a;

        // base case
        if (a == b)
            return a;

        // a is greater
        if (a > b)
            return gcd(a-b, b);
        return gcd(a, b-a);
    }

    /**
     * Factors of a number
     * @param a
     * @return
     */
    public static ArrayList<Integer> allFactors(int a) {
        int upperLimit = (int)(java.lang.Math.sqrt(a));
        ArrayList<Integer> factors = new ArrayList<>();
        for(int i=1;i <= upperLimit; i+= 1){
            if(a%i == 0){
                factors.add(i);
                if(i != a/i){
                    factors.add(a/i);
                }
            }
        }
        Collections.sort(factors);
        return factors;
    }
}
