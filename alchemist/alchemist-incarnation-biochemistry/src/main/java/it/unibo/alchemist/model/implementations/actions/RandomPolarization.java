package it.unibo.alchemist.model.implementations.actions;

import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.alchemist.model.implementations.positions.Continuous2DEuclidean;
import it.unibo.alchemist.model.interfaces.Action;
import it.unibo.alchemist.model.interfaces.CellNode;
import it.unibo.alchemist.model.interfaces.Context;
import it.unibo.alchemist.model.interfaces.Node;
import it.unibo.alchemist.model.interfaces.Position;
import it.unibo.alchemist.model.interfaces.Reaction;

/**
 * 
 */
public class RandomPolarization extends AbstractAction<Double> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @SuppressFBWarnings(value = "SE_BAD_FIELD", justification = "All provided RandomGenerator implementations are actually Serializable")
    private final RandomGenerator rand;


    /**
     * 
     * @param node 
     * @param random 
     */
    public RandomPolarization(final Node<Double> node, final RandomGenerator random) {
        super(node);
        if (node instanceof CellNode) {
            rand = random;
        } else {
            throw  new UnsupportedOperationException("Polarization can happen only in cells.");
        }
    }

    /**
     * 
     */
    @Override
    public void execute() {
        final double x = rand.nextFloat() - 0.5;
        final double y = rand.nextFloat() - 0.5;
        Position randomVersor = new Continuous2DEuclidean(x, y);
        if (x == 0) {
            randomVersor = new Continuous2DEuclidean(0, 1);
        } else if (y == 0) {
            randomVersor = new Continuous2DEuclidean(1, 0);
        } else {
            final double module = FastMath.sqrt(FastMath.pow(x, 2) + FastMath.pow(y, 2));
            if (module == 0) {
                randomVersor = new Continuous2DEuclidean(0, 0);
            } else {
                randomVersor = new Continuous2DEuclidean(x / module, y / module);
            }
        }
        getNode().addPolarization(randomVersor);
    }

    /**
     * 
     */
    @Override
    public Context getContext() {
        return Context.LOCAL;
    }

    @Override
    public Action<Double> cloneOnNewNode(final Node<Double> n, final Reaction<Double> r) {
        return new RandomPolarization(n, rand);
    }

    @Override 
    public CellNode getNode()  {
        return (CellNode) super.getNode();
    }

}
