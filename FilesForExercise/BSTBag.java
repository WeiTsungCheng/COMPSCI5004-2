
// Student ID: 3118197
// Name: WEI-TSUNG CHENG

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;


public class BSTBag<E extends Comparable<E>> implements Bag<E> {

    private static class Node<E extends Comparable<E>> {
        private CountedElement<E> element;
        private Node<E> left;
        private Node<E> right;

        Node(CountedElement<E> e) {
            this.element = e;
            this.left = null;
            this.right = null;
        }
    }

    private Node<E> root;
    private int size;

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void add(E element) {
        if (element == null) {
            return;
        }

        CountedElement<E> newElement = new CountedElement<>(element);
        if (root == null) {
            root = new Node<>(newElement);
            size += 1;
            return;
        }

        Node<E> current = root;
        Node<E> parent = null;
        int compare = 0;

        while (current != null) {
            parent = current;
            compare = newElement.compareTo(current.element);
            if (compare == 0) {
                current.element.setCount(current.element.getCount() + 1);
                size += 1;
                return;
            } else if (compare > 0) {
                current = current.right;
            } else {
                current = current.left;
            }
        }

        Node<E> newNode = new Node<>(newElement);
        if (compare > 0) {
            parent.right = newNode;
        } else {
            parent.left = newNode;
        }
        size += 1;
    }

    @Override
    public void remove(E element) {
        if (element == null || root == null)  {
            return;
        }
        CountedElement<E> target = new CountedElement<>(element);
        root = recursiveRemove(root, target);
    }

    // Recursive Remove Target
    private Node<E> recursiveRemove(Node<E> node, CountedElement<E> target) {
        if (node == null) {
            return null;
        }

        int compare = target.compareTo(node.element);

        if (compare == 0) {
            int count = node.element.getCount();

            // only reduce count if count > 1
            if (count > 1) {
                 node.element.setCount(count - 1);
                 size -= 1;
                 return node;

            // if count = 1, reduce count and remove the node
            } else {
                 size -= 1;
                 // if node has no children, return null
                 if (node.left == null && node.right == null) {
                     return null;
                 }
                 // if node has only one child, replace with child
                 if (node.left == null) {
                     return node.right;
                 }

                 if (node.right == null) {
                     return node.left;
                 }
                 // if node has two children
                 // Find the smallest node in the right subtree (in-order successor).

                 // get the smallest node in the right subtree
                 Node<E> successor = findMin(node.right);
                 // Copy the data from the successor node into the node to be deleted.
                 node.element = new CountedElement<>(
                         successor.element.getElement(),
                         successor.element.getCount()
                 );
                 node.right = deleteMin(node.right);
            }

        } else if (compare > 0) {
            node.right = recursiveRemove(node.right, target);
        } else {
            node.left = recursiveRemove(node.left, target);
        }

        return node;
    }

    // Find the smallest node in the right subtree (in-order successor).
    private Node<E> findMin(Node<E> node) {
        if (node == null) {
            return null;
        }
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Delete the smallest node in the right subtree (in-order successor).
    private Node<E> deleteMin(Node<E> node) {
        if (node == null) {
            return null;
        }
        if (node.left == null) {
            return node.right;
        }
        node.left = deleteMin(node.left);
        return  node;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;

    }

    @Override
    public boolean contains(E element) {
        if (root == null) {
            return false;
        }
        Node<E> current = root;
        CountedElement<E> target = new CountedElement<>(element);

        while (current != null) {
            int compare = target.compareTo(current.element);
            if (compare == 0) {
               return true;
            } else if (compare > 0) {
                current = current.right;
            } else {
                current = current.left;
            }
        }
        return false;
    }

    @Override
    public boolean equals(Bag<E> that) {
        if (that == null) {
            return false;
        }
        if (this.size() != that.size()) {
            return false;
        }

        Map<E, Integer> thisMap = new HashMap<>();
        for (E e: this) {
            thisMap.put(e, thisMap.getOrDefault(e, 0) + 1);
        }

        for (E e: that) {
            Integer count = thisMap.get(e);
            if (count == null || count == 0) {
                return false;
            }
            thisMap.put(e, count - 1);
        }

        for (Integer count: thisMap.values()) {
            if (count != 0) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Iterator<E> iterator() {
        return new BSTIterator();
    }

    // BSTIterator
    private class BSTIterator implements Iterator<E> {

        private final Stack<Node<E>> stack = new LinkedStack<>();
        private int repeatCount = 0;
        private CountedElement<E> currentElement = null;

        BSTIterator() {
            pushLeft(root);
        }

        private void pushLeft(Node<E> node) {
            while (node != null) {
                stack.push(node);
                node = node.left;
            }
        }

        @Override
        public boolean hasNext() {
            return repeatCount > 0 || !stack.empty();
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            if (repeatCount > 0) {
                repeatCount -= 1;
                return currentElement.getElement();
            }

            Node<E> node = stack.pop();
            currentElement = node.element;
            repeatCount = currentElement.getCount() - 1;

            if (node.right != null) {
                pushLeft(node.right);
            }

            return currentElement.getElement();
        }
    }

}
