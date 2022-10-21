package nl.han.ica.datastructures;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HANQueueTest {
    HANQueue<Object> sut;

    @BeforeEach
    public void setup() {
        sut = new HANQueue();
    }

    @Test
    public void testIsEmpty() {
        sut.buffer.addFirst(1);
        Assertions.assertEquals(false, sut.isEmpty());

        sut.buffer.clear();
        Assertions.assertEquals(true, sut.isEmpty());
    }

    @Test
    public void testEnqueue() {
        sut.enqueue(1);
        sut.enqueue(2);

        Assertions.assertEquals(2, sut.buffer.get(1));
    }

    @Test
    public void testDequeue() {
        sut.enqueue(1);
        sut.enqueue(2);
        sut.enqueue(3);
        sut.enqueue(4);

        Assertions.assertEquals(1, sut.dequeue());
        Assertions.assertEquals(2, sut.dequeue());
        Assertions.assertEquals(3, sut.dequeue());
        Assertions.assertEquals(4, sut.dequeue());
    }

    @Test
    public void testPeek() {
        sut.enqueue(1);
        sut.enqueue(2);
        sut.enqueue(3);
        sut.enqueue(4);

        Assertions.assertEquals(1, sut.peek());
    }

    @Test
    public void testGetSize() {
        sut.enqueue(1);
        sut.enqueue(2);
        sut.enqueue(3);
        sut.enqueue(4);

        Assertions.assertEquals(4, sut.getSize());
    }
}
