package de.dhbw.handycrab;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import de.dhbw.handycrab.backend.BackendConnectionException;
import de.dhbw.handycrab.backend.BackendConnector;
import de.dhbw.handycrab.model.User;

public class BackendCommunicationTest {

    private BackendConnector connector = new BackendConnector();

    @Test
    public void registerTest() {
        CompletableFuture<User> cuser = connector.registerAsync("abc@test.com", "usern4me123", "abc123DEF!");
        User user = null;
        try {
            user = cuser.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(user);
    }

    @Test
    public void loginTest() {
        CompletableFuture<User> cuser = connector.loginAsync("abc@test.com", "abc123DEF!");
        User user = null;
        try {
            user = cuser.get();
        } catch (ExecutionException e) {
            System.out.println(((BackendConnectionException)e.getCause()).getErrorCode());
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(user);
    }
}
