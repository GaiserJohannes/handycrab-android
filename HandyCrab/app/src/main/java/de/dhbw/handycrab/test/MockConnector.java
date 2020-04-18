package de.dhbw.handycrab.test;

import de.dhbw.handycrab.backend.BackendConnectionException;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.model.Solution;
import de.dhbw.handycrab.model.User;
import de.dhbw.handycrab.model.Vote;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MockConnector implements IHandyCrabDataHandler {

    @Override
    public CompletableFuture<User> registerAsync(String email, String username, String password) throws BackendConnectionException {
        User user = new User(new ObjectId(), username, email);
        return CompletableFuture.completedFuture(user);
    }

    @Override
    public CompletableFuture<User> loginAsync(String emailOrUsername, String password) {
        User user = new User(new ObjectId(), emailOrUsername, emailOrUsername);
        return CompletableFuture.completedFuture(user);
    }

    @Override
    public CompletableFuture<Void> logoutAsync() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<String> getUsernameAsync(ObjectId id) {
        return CompletableFuture.completedFuture("mocked Username");
    }

    @Override
    public CompletableFuture<List<Barrier>> getBarriersAsync(double longitude, double latitude, int radius) {
        Barrier b1 = new Barrier(new ObjectId(), new ObjectId(), "Treppe", 42.0, 69.0, null, "Das ist eine Beschreibung", null, 0, 0, Vote.NONE);
        Barrier b2 = new Barrier(new ObjectId(), new ObjectId(), "Treppe222", 41.0, 68.0, null, "Das ist eine andere Beschreibung", null, 0, 0, Vote.NONE);
        List<Barrier> list = new ArrayList<>();
        list.add(b1);
        list.add(b2);
        return CompletableFuture.completedFuture(list);
    }

    @Override
    public CompletableFuture<List<Barrier>> getBarriersAsync(String postcode) {
        return null;
    }

    @Override
    public CompletableFuture<Barrier> getBarrierAsync(ObjectId id) {
        return null;
    }

    @Override
    public CompletableFuture<Barrier> addBarrierAsync(String title, double longitude, double latitude, String picture_base64, String description, String postcode, String solution) {
        return null;
    }

    @Override
    public CompletableFuture<Barrier> modifyBarrierAsync(ObjectId id, String title, String picture_base64, String description) {
        return null;
    }

    @Override
    public CompletableFuture<Barrier> addSolutionAsync(ObjectId barrierID, String solution) {
        return null;
    }

    @Override
    public CompletableFuture<Void> voteBarrierAsync(ObjectId id, Vote vote) {
        return null;
    }

    @Override
    public CompletableFuture<Void> voteSolutionAsync(ObjectId id, Vote vote) {
        return null;
    }

}
