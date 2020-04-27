package de.dhbw.handycrab.backend;

import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.model.Solution;
import de.dhbw.handycrab.model.User;
import de.dhbw.handycrab.model.Vote;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface IHandyCrabDataHandler {
    CompletableFuture<User> registerAsync(String email, String username, String password) throws BackendConnectionException;

    CompletableFuture<User> loginAsync(String emailOrUsername, String password);

    CompletableFuture<Void> logoutAsync();

    CompletableFuture<String> getUsernameAsync(ObjectId id);

    CompletableFuture<List<Barrier>> getBarriersAsync(double longitude, double latitude, int radius);

    CompletableFuture<List<Barrier>> getBarriersAsync(String postcode);

    CompletableFuture<Barrier> getBarrierAsync(ObjectId id);

    CompletableFuture<Barrier> addBarrierAsync(String title, double longitude, double latitude, String picture_base64, String description, String postcode, String solution);

    CompletableFuture<Barrier> modifyBarrierAsync(ObjectId id, String title, String picture_base64, String description);

    CompletableFuture<Barrier> addSolutionAsync(ObjectId barrierID, String solution);

    CompletableFuture<Void> voteBarrierAsync(ObjectId id, Vote vote);

    CompletableFuture<Void> voteSolutionAsync(ObjectId id, Vote vote);
}
