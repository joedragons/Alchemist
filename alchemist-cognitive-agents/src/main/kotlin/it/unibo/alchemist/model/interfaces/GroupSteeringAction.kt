package it.unibo.alchemist.model.interfaces

import it.unibo.alchemist.model.interfaces.geometry.Vector

/**
 * A [SteeringAction] related to a group of pedestrians.
 */
interface GroupSteeringAction<T, P> : SteeringAction<T, P> where P : Position<P>, P : Vector<P> {

    /**
     * The list of pedestrians influencing this action.
     */
    fun group(): List<Node<T>>
}
