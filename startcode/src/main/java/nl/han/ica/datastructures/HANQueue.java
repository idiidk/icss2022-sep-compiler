package nl.han.ica.datastructures;

public class HANQueue<T> implements IHANQueue<T> {
    HANLinkedList<T> buffer = new HANLinkedList<>();

    @Override
    public void clear() {
        buffer.clear();
    }

    @Override
    public boolean isEmpty() {
        return buffer.getSize() == 0;
    }

    @Override
    public void enqueue(T value) {
        buffer.insert(buffer.getSize(), value);
    }

    @Override
    public T dequeue() {
        T result = buffer.getFirst();
        buffer.removeFirst();

        return result;
    }

    @Override
    public T peek() {
        return buffer.getFirst();
    }

    @Override
    public int getSize() {
        return buffer.getSize();
    }
}
