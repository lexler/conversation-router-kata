package com.lexler.kata.hello;

import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

class HelloWorldTest {

    @Test
    void greetsTheWorld() {
        Approvals.verify(greet("World"));
    }

    private String greet(String name) {
        return "Hello, " + name + "!";
    }
}
