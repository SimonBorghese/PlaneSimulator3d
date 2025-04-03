package Utils;


import Graphics.GLObject;
import Utils.Stack.GraphicsNode;

import java.security.InvalidParameterException;

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
     * The node BEFORE the top node
     * This is here to keep the first pop() O(1), the next O(n), and then back to O(1),
     * If the length is less than 2, this will be null
     */
    private GraphicsNode before_top;

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
        this.before_top = null;
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
        // If we have no stack
        if (root == null){
            this.root = next;
            this.top = null;
            this.before_top = null;
        }
        // If the stack has no top but a before_top
        else if (top == null && before_top != null){
            before_top.setNext(next);
            this.top = next;
            top.setNext(next);
            top = next;
        }
        // If there is a top, we don't care about the before_top
        else if (top != null){
            // Set before_top to top which still should point to the old top
            before_top = top;
            top.setNext(next);
            top = next;
        }
        // If we're not in any of these states, the stack is one element long, set before_top to the current top
        else{
            rebuildStack();
            // BEWARE A RECURSION LOOP HERE
            push(next);
        }
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
        if (top == null && root.hasNext()){
            // REBUILD THE STACK, this is slow and may be suboptimal
            rebuildStack();
        }

        // At this point, if top is still null, then the top must be root

        // Get the top of the stack
        GraphicsNode top;

        if (this.top != null){
            top = this.top;
        } else{
            top = root;
            root = null;
        }
        // Set the top of the stack to the one before this, if it is null, this is ok, next time it'll rebuild to root

        this.top = before_top;

        if (this.top != null) {
            // Set the top next to null
            this.top.setNext(null);
        }

        if (top != null){
            // Destroy whatever was in top if it has it
            if (top.hasElement()){
                top.getElement().destroy();
            }
        }

        // Return what was on top
        return top;
    }

    /**
     * Rebuild the stack, this is public as it may be more desirable to rebuild at a point where it won't affect
     * performance rather than waiting for the next pop()
     * This will NOT throw an exception if root is null, rather it'll just do nothing
     * Whatever is in top and before_top *WILL* be lost
     */
    public void rebuildStack(){
        if (root != null){
            GraphicsNode current = root;
            GraphicsNode previous_node = null;
            while (current.next() != null){
                previous_node = current;
                current = current.next();
            }
            before_top = previous_node;
            if (current == root){
                top = null;
            } else {
                top = current;
            }
        }
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

    /**
     * Check whether this stack needs to be rebuilt
     * @return If both top and before_top are null but root isn't
     */
    public boolean needsRebuild(){
        return (top == null && before_top == null) && root != null;
    }
}
