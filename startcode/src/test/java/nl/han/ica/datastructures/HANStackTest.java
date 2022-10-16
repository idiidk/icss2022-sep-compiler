package nl.han.ica.datastructures;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HANStackTest {
    HANStack<Object> sut;

    @BeforeEach
    void setup() {
        sut = new HANStack<>();
    }

    @Test
    void testPush() {
        sut.push(1);
        Assertions.assertEquals(1, sut.stack.get(0));

        sut.push(2);
        Assertions.assertEquals(2, sut.stack.get(1));

        sut.push(3);
        sut.push(4);
        Assertions.assertEquals(4, sut.stack.get(3));
    }

    @Test
    void testPop() {
        sut.push(1);
        sut.push(2);
        sut.push(3);

        Assertions.assertEquals(3, sut.pop());
        Assertions.assertEquals(2, sut.pop());

        sut.push(3);
        Assertions.assertEquals(3, sut.pop());
    }

    @Test
    void testPeek() {
        sut.push(1);
        sut.push(2);
        sut.push(3);

        Assertions.assertEquals(3, sut.peek());
    }
}
