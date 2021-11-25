package geneticalgorithm;

import java.util.Comparator;

/**
 * @author Arefeva Ilona
 */
public class IndividualComparator implements Comparator<Individual> {

    @Override
    public int compare(Individual ind1, Individual ind2) {
        if (ind1.getWeightTree() == ind2.getWeightTree()) {
            if (ind1.getDiameter() == ind2.getDiameter()) {
                return ind1.getCountLeaves() - ind2.getCountLeaves();
            } else {
                return ind1.getWeightTree() - ind2.getWeightTree();
            }
        }
        if (ind1.getWeightTree() > ind2.getWeightTree()) {
            return 1;
        } else {
            return -1;
        }
    }
}