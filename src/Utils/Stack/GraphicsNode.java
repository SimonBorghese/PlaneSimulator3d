package Utils.Stack;

import Graphics.GLObject;

import java.security.InvalidParameterException;
import java.util.Iterator;

/**
 * This is the node object which will be used for our linked list implementation, though this one is special because
 * it only takes GLObject
 */
public class GraphicsNode implements Iterator<GraphicsNode> {
    /**
     * The GLObject this node points to
     */
    private GLObject element;

    /**
     * The next GLObject this node points to, may be null
     */
    private GraphicsNode next;

    /**
     * Construct this object with the provided element and next, both of which may be null
     * @param element The element to assign to this node
     * @param next The next node after this one
     */
    public GraphicsNode(GLObject element, GraphicsNode next) {
        this.element = element;
        this.next = next;
    }

    /**
     * Construct this object with the provided element but also have next as null
     */
    public GraphicsNode(GLObject element) {
        this(element, null);
    }

    /**
     * Construct an empty node, both element and next default to null
     */
    public GraphicsNode(){
        this(null, null);
    }

    /**
     * Find whether the next element is null
     * @return Whether or not next is null, true if it isn't
     */
    public boolean hasNext(){
        return next != null;
    }

    /**
     * Find whether the element is null
     * @return If element is null, true if it isn't
     */
    public boolean hasElement(){
        return element != null;
    }

    /**
     * Return the next object, may be null
     * @return The "next" element this node points to, may be null
     */
    public GraphicsNode next(){
        return next;
    }

    /**
     * Get the element from this node, will return an exception if it is null
     * @return The element
     * @throws java.security.InvalidParameterException If element is null, shouldn't happen at this point
     */
    public GLObject getElement(){
        if (element == null){
            throw new InvalidParameterException("Requested Element is null. Throwing to prevent failure!");
        }
        return element;
    }

    /**
     * Set the next pointer of this node, if the current next pointer is not saved it will be lost and leaked
     * @param next The next node to point to, may be null
     */
    public void setNext(GraphicsNode next){
        this.next = next;
    }

    /**
     * Set the element of this object, this should be done BEFORE any getElement calls are done, or it'll [getElement]
     * will throw an exception. It may be null but that'll cause getElement to throw an exception
     */
    public void setElement(GLObject element){
        this.element = element;
    }


}
