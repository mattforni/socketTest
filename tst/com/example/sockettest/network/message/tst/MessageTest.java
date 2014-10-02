package com.example.sockettest.network.message.tst;

import java.util.List;

import android.test.AndroidTestCase;

import com.example.sockettest.network.message.Message;

public class MessageTest extends AndroidTestCase {
    public void testSeparateWithNullData() {
        assertTrue("Messages should be empty", Message.separate(null).isEmpty());
    }

    public void testSeparateWithEmptyData() {
        assertTrue("Messages should be empty", Message.separate("").isEmpty());
    }

    public void testSeparateWithManyMessages() {
        final String message1 = "{This is a message}";
        final String message2 = "{This is a second message}";
        List<String> messages = Message.separate(message1+message2);
        assertFalse("Messages should not be empty", messages.isEmpty());
        assertEquals(2, messages.size());
        assertEquals(message1, messages.get(0));
        assertEquals(message2, messages.get(1));
    }

    public void testSeparateWithNestedObjects() {
        final String message = "{This:{Is nested}}";
        List<String> messages = Message.separate(message);
        assertFalse("Messages should not be empty", messages.isEmpty());
        assertEquals(1, messages.size());
        assertEquals(message, messages.get(0));
    }

    public void testSeparateWithOneMessage() {
        final String message = "{This is a message}";
        List<String> messages = Message.separate(message);
        assertFalse("Messages should not be empty", messages.isEmpty());
        assertEquals(1, messages.size());
        assertEquals(message, messages.get(0));
    }
}
