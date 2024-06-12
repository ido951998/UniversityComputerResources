package bgu.spl.tests;
import static org.junit.Assert.*;

import bgu.spl.mics.*;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;


import java.util.concurrent.TimeUnit;

public class MessageBusTest {


    public static MicroService m1;
    public static Message message;
    public static MessageBus messageBus;

    @Before
    public void setUp() {
        messageBus = MessageBusImpl.getInstance();
        m1 = new MicroService("m1") {
            @Override
            protected void initialize() {

            }
        };
    }

    @Test
    public void test1() {
        messageBus.register(m1);
        ExampleEvent event = new ExampleEvent("ex");
        messageBus.subscribeEvent(event.getClass(), m1);
        Future future = messageBus.sendEvent(event);
        try {
            assertEquals(event, messageBus.awaitMessage(m1));
        } catch (InterruptedException e) {
        }
        assertNull(future.get(0, TimeUnit.SECONDS));
        messageBus.complete(event, "complete");
        assertEquals("complete", future.get(0, TimeUnit.SECONDS));
    }

    @Test
    public void test2() {
        messageBus.register(m1);
        ExampleBroadcast broadcast = new ExampleBroadcast("broad");
        messageBus.subscribeBroadcast(broadcast.getClass(), m1);
        messageBus.sendBroadcast(broadcast);
        try {
            assertEquals(broadcast, messageBus.awaitMessage(m1));
        } catch (InterruptedException e) {
        }
    }

    @Test
    public void test3() {
        messageBus.register(m1);
        messageBus.unregister(m1);
        try {
            assertNull(messageBus.awaitMessage(m1));
        } catch (InterruptedException e) {
        }
    }
}
