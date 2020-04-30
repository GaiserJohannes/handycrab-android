package de.dhbw.handycrab;

import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import de.dhbw.handycrab.backend.BackendConnectionException;
import de.dhbw.handycrab.backend.BackendConnector;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.model.User;
import de.dhbw.handycrab.model.Vote;

public class BackendCommunicationTest {

    private BackendConnector connector = new BackendConnector();

    @Before
    public void login(){
        CompletableFuture<User> cuser = connector.loginAsync("abc@test.com", "abc123DEF!");
        User user = null;
        try {
            user = cuser.get();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode() + " - Http-Code: " + ((BackendConnectionException) e.getCause()).getHttpStatusCode());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void registerTest() {
        CompletableFuture<User> cuser = connector.registerAsync("aasdaddc@test.com", "uGn4me123", "abdc123DEF!");
        User user = null;
        try {
            user = cuser.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode() + " - Http-Code: " + ((BackendConnectionException) e.getCause()).getHttpStatusCode());
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
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode() + " - Http-Code: " + ((BackendConnectionException) e.getCause()).getHttpStatusCode());
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
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode() + " - Http-Code: " + ((BackendConnectionException) e.getCause()).getHttpStatusCode());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Assert.assertNotNull(name);
    }

    @Test
    public void getBarrierTest(){
        CompletableFuture<Barrier> cbarrier = connector.getBarrierAsync(new ObjectId("5ea9478cbcfd422f07270d1c"));
        Barrier barrier = null;
        try {
            barrier = cbarrier.get();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode() + " - Http-Code: " + ((BackendConnectionException) e.getCause()).getHttpStatusCode());
            }
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertNotNull(barrier);
    }

    @Test
    public void getBarriersPostcodeTest(){
        CompletableFuture<List<Barrier>> cbarriers = connector.getBarriersAsync("72166");
        List<Barrier> barriers = null;
        try {
            barriers = cbarriers.get();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode() + " - Http-Code: " + ((BackendConnectionException) e.getCause()).getHttpStatusCode());
            }
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertNotNull(barriers);
    }

    @Test
    public void getBarriersGeolocationTest(){
        CompletableFuture<List<Barrier>> cbarriers = connector.getBarriersAsync(48.5, 8.5, 500);
        List<Barrier> barriers = null;
        try {
            barriers = cbarriers.get();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode() + " - Http-Code: " + ((BackendConnectionException) e.getCause()).getHttpStatusCode());
            }
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertNotNull(barriers);
    }

    @Test
    public void addBarrierTest(){
        CompletableFuture<Barrier> cbarrier = connector.addBarrierAsync("Test barrier", 48.5, 8.5, "", "Dies ist eine böse Barriere", "72166", "So kann man diese barriere umgehen");
        Barrier barrier = null;
        try {
            barrier = cbarrier.get();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode() + " - Http-Code: " + ((BackendConnectionException) e.getCause()).getHttpStatusCode());
            }
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertNotNull(barrier);
    }

    @Test
    public void modifyBarrierTest(){
        CompletableFuture<Barrier> cbarrier = connector.modifyBarrierAsync(new ObjectId("5ea9478cbcfd422f07270d1c"),"Test barrier", "", "Dies ist eine böse Barriere");
        Barrier barrier = null;
        try {
            barrier = cbarrier.get();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode() + " - Http-Code: " + ((BackendConnectionException) e.getCause()).getHttpStatusCode());
            }
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertNotNull(barrier);
    }

    @Test
    public void addSolutionTest(){
        //zu einer barrierID machen
        CompletableFuture<Barrier> cbarrier = connector.addSolutionAsync(new ObjectId("5ea9478cbcfd422f07270d1c"), "Dies ist eine böse Barriere");
        Barrier barrier = null;
        try {
            barrier = cbarrier.get();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode() + " - Http-Code: " + ((BackendConnectionException) e.getCause()).getHttpStatusCode());
            }
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertNotNull(barrier);
    }

    @Test
    public void voteBarrierTest(){
        try {
            //zu einer BarrierID machen
            connector.voteBarrierAsync(new ObjectId("5ea9478cbcfd422f07270d1c"), Vote.UP).get();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode() + " - Http-Code: " + ((BackendConnectionException) e.getCause()).getHttpStatusCode());
            }
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertTrue(true);
    }

    @Test
    public void voteSolutionTest(){
        try {
            //zu einer SolutionID machen
            connector.voteSolutionAsync(new ObjectId("5ea94c84bcfd422f07270d47"), Vote.UP).get();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode() + " - Http-Code: " + ((BackendConnectionException) e.getCause()).getHttpStatusCode());
            }
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertTrue(true);
    }

    @Test
    public void logoutTest(){
        try {
            connector.logoutAsync().get();
        } catch (ExecutionException e) {
            if(e.getCause() instanceof BackendConnectionException){
                System.out.println(((BackendConnectionException) e.getCause()).getErrorCode() + " - Http-Code: " + ((BackendConnectionException) e.getCause()).getHttpStatusCode());
            }
            Assert.fail();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertTrue(true);
    }


}
