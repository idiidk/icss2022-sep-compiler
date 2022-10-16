package nl.han.ica.datastructures;

public class HANLinkedList<T> implements IHANLinkedList<T> {
    LinkedListNode<T> first = null;

    // Just to stop me being confused,
    // this function adds
    @Override
    public void addFirst(T value) {
        // If the first node doesn't exist yet create it
        if (first == null) {
            first = new LinkedListNode<>(value);
            return;
        }

        // Else make a new node and switch the first and new node
        LinkedListNode<T> node = new LinkedListNode<>(value);
        node.setNext(first);
        first = node;
    }

    @Override
    public void clear() {
        // Just remove the first node and everything cascades and dies
        // all hail the garbage collector
        first = null;
    }

    @Override
    public void insert(int index, T value) {
        LinkedListNode<T> newNode = new LinkedListNode<>(value);
        LinkedListNode<T> current = first;

        if (index > getSize()) {
            throw new IndexOutOfBoundsException(index);
        }

        if(first == null) {
            first = newNode;
            return;
        }

        if (index == 0) {
            addFirst(value);
            return;
        }

        for (int i = 0; i < index - 1; i++) {
            current = current.getNext();
        }

        newNode.setNext(current.getNext());
        current.setNext(newNode);
    }

    @Override
    public void delete(int pos) {
        // If removing first node it's easy
        if (pos == 0) {
            removeFirst();
            return;
        }

        // Index OOB
        if (pos > getSize()) {
            throw new IndexOutOfBoundsException(pos);
        }

        int temp = 0;
        LinkedListNode<T> current = first;

        while (temp < pos) {
            current = current.getNext();
            temp++;
        }

        // Set the next of the node before the one to remove
        // to one node after the node to be removed
        // effectively jumping over it. Let garbage collection handle the rest :D
        current.setNext(current.getNext());
    }

    @Override
    public T get(int pos) {
        LinkedListNode<T> temp = first;

        for (int i = 0; i < pos; i++) {
            temp = temp.getNext();
        }

        if (temp == null) {
            throw new IndexOutOfBoundsException(pos);
        }

        return temp.getValue();
    }

    @Override
    public void removeFirst() {
        first = first.getNext();
    }

    @Override
    public T getFirst() {
        return first.getValue();
    }

    @Override
    public int getSize() {
        if (first == null) {
            return 0;
        }

        int count = 1;
        LinkedListNode<T> currentNode = first;

        while (currentNode.getNext() != null) {
            currentNode = currentNode.getNext();
            count++;
        }

        return count;
    }
}
