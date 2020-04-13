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
        Solution s1 = new Solution(ObjectId.get(), "einfach außen rum gehen! einfach außen rum gehen! einfach außen rum gehen! einfach außen rum gehen! einfach außen rum gehen! einfach außen rum gehen!", ObjectId.get(), 12, 42, Vote.NONE);
        Solution s2 = new Solution(ObjectId.get(), "zweifach außen rum gehen!", ObjectId.get(), 421, 546, Vote.NONE);
        Solution s3 = new Solution(ObjectId.get(), "dreifach außen rum gehen!", ObjectId.get(), 567, 85, Vote.NONE);
        Solution s4 = new Solution(ObjectId.get(), "viewfach außen rum gehen!", ObjectId.get(), 456, 4, Vote.NONE);
        Solution s5 = new Solution(ObjectId.get(), "fünffach außen rum gehen!", ObjectId.get(), 6, 784, Vote.NONE);
        List<Solution> solutions = new ArrayList<>();
        solutions.add(s1);
        solutions.add(s2);
        solutions.add(s3);
        solutions.add(s4);
        solutions.add(s5);
        Barrier b1 = new Barrier(ObjectId.get(), ObjectId.get(), "Treppe", 42.0, 69.0, null, "Das ist eine Beschreibung", null, solutions, 43, 23, Vote.NONE);
        Barrier b2 = new Barrier(ObjectId.get(), ObjectId.get(), "Treppe222", 41.0, 68.0, null, "Das ist eine andere Beschreibung", null, solutions, 42, 56, Vote.NONE);
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
    public CompletableFuture<Barrier> addBarrierAsync(String title, double longitude, double latitude, String picture_base64, String description, String postcode, Solution solution) {
        return null;
    }

    @Override
    public CompletableFuture<Barrier> modifyBarrierAsync(ObjectId id, String title, String picture_base64, String description) {
        return null;
    }

    @Override
    public CompletableFuture<Barrier> addSolutionAsync(ObjectId barrierID, Solution solution) {
        return null;
    }

    @Override
    public CompletableFuture<Void> voteBarrierAsync(ObjectId id, Vote vote) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> voteSolutionAsync(ObjectId id, Vote vote) {
        return CompletableFuture.completedFuture(null);
    }
}
