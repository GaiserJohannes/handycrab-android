package de.dhbw.handycrab;

import org.bson.types.ObjectId;
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
        CompletableFuture<User> cuser = connector.registerAsync("aasdaddc@test.com", "uGn4me123", "abdc123DEF!");
        User user = null;
        try {
            user = cuser.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode());
            }
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
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(user);
    }

    @Test
    public void userNameTest(){
        String name = null;
        CompletableFuture<String> username = connector.getUsernameAsync(new ObjectId("5e85a97841e46f5d00cb3a5d"));
        try {
            name = username.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(name);
    }

    @Test
    public void logoutTest(){
        try {
           connector.logoutAsync().get();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode());
            }
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }


}
