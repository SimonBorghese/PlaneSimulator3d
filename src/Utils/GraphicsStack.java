package Utils;


import Graphics.GLObject;
import Utils.Stack.GraphicsNode;
import org.jetbrains.annotations.NotNull;

import java.security.InvalidParameterException;
import java.util.Iterator;

/**
 * In this program, we use unsafe OpenGL bindings. Java doesn't do garbage collection for the OpenGL objects but rather
 * it is up to the programmer to implement allocation, creation, safe usage, and then destruction of objects.
 * To assist in this process, the following class implements a stack, similar to that of used in programming languages
 * to manage memory. This class holds a stack of GLObject's which we push onto the stack upon creation and then pop
 * when we are done, when something is popped from this stack, destroy() is automatically called. Following this
 * formula, it should be easier to debug which objects have been created, which ones are destroyed and to prevent
 * leaks of objects.
 * This is implemented using the GraphicsNode object which are LinkedNodes
 * To keep O(1) operation, alternate between push and pop, otherwise top will need to be rebuilt
 */
public class GraphicsStack {
    /**
     * This is the first node of our stack, may be null
     */
    private GraphicsNode root;

    /**
     * This is the read of our stack, may be equal to root or null
     */
    private GraphicsNode top;

    /**
     * Construct a stack with a provided root node and top node, if there is no link between these objects, an exception
     * will be thrown eventually.
     * The stack is NOT BUILT HERE! So before_top will always be null.
     * @param root The root node, should eventually point to top
     * @param top The top of the stack node, root should have a path to this
     */
    public GraphicsStack(GraphicsNode root, GraphicsNode top) {
        this.root = root;
        this.top = top;
    }

    /**
     * Construct this stack with only a root node, in this case top will also point to root
     * The stack is NOT BUILT HERE! So before_top will always be null.
     * @param root The node to construct this stack from
     */
    public GraphicsStack(GraphicsNode root) {
        this(root, root);
    }

    /**
     * Construct this stack as an empty stack with all elements pointing to null
     * This is safe as long as we add something at some point
     */
    public GraphicsStack(){
        this(null, null);
    }

    /**
     * Push something to the top of the stack, this *shouldn't* be null
     * THIS MAY RESULT IN A RECURSION LOOP IN AN INVALID STACK
     * @param next The next node to push, should NOT be null
     * @throws java.security.InvalidParameterException If next is null
     */
    public void push(GraphicsNode next){
        if (next == null){
            throw new InvalidParameterException("The provided node is null!");
        }
        // If we have a top node, then set the next node of top to our new node then set the new node to top
        if (top != null){
            top.setNext(next);
        } else if (root != null){
            // If we don't have a top node, but we do have a root, add it to that
            root.setNext(next);
        } else{
            // If we don't have a root or top, we're empty
            root = next;
        }
        top = next;
    }

    /**
     * Push something to the top of the stack, this *shouldn't* be null
     * THIS MAY RESULT IN A RECURSION LOOP IN AN INVALID STACK
     * @param glObject The next node to push, as a GLObject, should NOT be null
     * @throws java.security.InvalidParameterException If next is null
     */
    public void push(GLObject glObject){
        if (glObject == null){
            throw new InvalidParameterException("The provided node is null!");
        }

        GraphicsNode next = new GraphicsNode(glObject);
        push(next);
    }

    /**
     * Pop the element off the top of the stack, this WILL call destroy on it if it isn't null, it'll still return
     * but the return shouldn't really be used.
     * top is set to null after this. If top is null but root isn't, the top will be reconstructed (which is O(n))
     * @return The element which was destroyed
     * @throws IllegalStateException If root is null
     */
    public GraphicsNode pop(){
        if (root == null){
            throw new InvalidParameterException("Root is null!");
        }

        // We store our current node and the node before it
        GraphicsNode node = root;
        GraphicsNode prev = null;
        while (node.hasNext()){
            // If we have another node, set the previous to the current and then iterate
            prev = node;
            node = node.next();
        }

        // If we have a previous node then we have more than one node and therefore set the previous node as the top
        if (prev != null){
            prev.setNext(null);
            top = prev;
        } else{
            // If we dont have a previous node then the root is the only node, we pop it off here.
            root = null;
        }

        // Return what was on top
        return node;
    }

    /**
     * Return the root node, may be null
     * @return Root node, may be null
     */
    public GraphicsNode getRoot(){
        return root;
    }

    /**
     * Check whether this stack has elements
     * @return If root node is null, true if it isn't
     */
    public boolean hasElements(){
        return root != null;
    }
}
