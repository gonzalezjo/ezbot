package com.github.gonzalezjo.ezbot.typeracer;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class JSessionTest {
    @Test
    public void testAnonymousJSession() {
        try {
            final String id = new JSession().toString();
            Assert.assertEquals(id.length(), 32);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            Assert.fail("Could not detect jsessionid.");
        }
    }
}