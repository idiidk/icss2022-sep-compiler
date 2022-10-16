package nl.han.ica.datastructures;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HANLinkedListTest {
    HANLinkedList<Object> sut;

    @BeforeEach
    void setup() {
        sut = new HANLinkedList<>();
    }

    @Test
    void testAddFirst() {
        sut.addFirst(5);
        sut.addFirst(1);

        Assertions.assertEquals(1, sut.first.getValue());
        Assertions.assertEquals(5, sut.first.getNext().getValue());
    }

    @Test
    void testGetSize() {
        Assertions.assertEquals(0, sut.getSize());

        sut.addFirst(5);
        Assertions.assertEquals(1, sut.getSize());

        sut.addFirst(1);
        Assertions.assertEquals(2, sut.getSize());

        sut.addFirst(1);
        Assertions.assertEquals(3, sut.getSize());
    }

    @Test
    void testClear() {
        sut.addFirst(5);
        sut.addFirst(1);

        sut.clear();

        Assertions.assertNull(sut.first);
    }

    @Test
    void testInsert() {
        sut.addFirst(1);
        sut.addFirst(2);
        sut.addFirst(3);
        sut.addFirst(4);

        sut.insert(1, 1337);

        Assertions.assertEquals(1337, sut.first.getNext().getValue());
    }

    @Test
    void testGet() {
        sut.addFirst(1);
        sut.addFirst(2);
        sut.addFirst(1337);
        sut.addFirst(4);

        Assertions.assertEquals(1337, sut.get(1));
    }

    @Test
    void testDelete() {
        sut.addFirst(1);
        sut.delete(0);
        Assertions.assertEquals(0, sut.getSize());

        sut.addFirst(2);
        sut.addFirst(1337);
        sut.addFirst(4);

        sut.delete(0);
        Assertions.assertEquals(2, sut.getSize());
        Assertions.assertEquals(1337, sut.get(0));
    }
}
