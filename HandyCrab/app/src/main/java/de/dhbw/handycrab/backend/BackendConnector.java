package de.dhbw.handycrab.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.model.Solution;
import de.dhbw.handycrab.model.User;
import de.dhbw.handycrab.model.Vote;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Singleton
public class BackendConnector implements IHandyCrabDataHandler {

    private String connection = "http://handycrab.nico-dreher.de/rest";
    private Client client = ClientBuilder.newClient();
    private WebTarget target = client.target(connection);
    private Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIDDeserializer()).create();

    @Inject
    public BackendConnector() {}

    @Override
    public CompletableFuture<User> registerAsync(final String email, final String username, final String password) {
       return CompletableFuture.supplyAsync(() -> register(email, username, password));
    }

    @Override
    public CompletableFuture<User> loginAsync(String emailOrUsername, String password) {
        return CompletableFuture.supplyAsync(() -> login(emailOrUsername, password));
    }

    @Override
    public CompletableFuture<Void> logoutAsync() {
        return CompletableFuture.runAsync(()->logout());
    }

    @Override
    public CompletableFuture<String> getUsernameAsync(ObjectId id) {
        return CompletableFuture.supplyAsync(() -> getUsername(id));
    }

    @Override
    public CompletableFuture<List<Barrier>> getBarriersAsync(double longitude, double latitude, int radius) {
        return CompletableFuture.supplyAsync(() -> getBarriers(longitude, latitude, radius));
    }

    @Override
    public CompletableFuture<List<Barrier>> getBarriersAsync(String postcode) {
        return CompletableFuture.supplyAsync(() -> getBarriers(postcode));
    }

    @Override
    public CompletableFuture<Barrier> getBarrierAsync(ObjectId id) {
        return CompletableFuture.supplyAsync(() -> getBarrier(id));
    }

    @Override
    public CompletableFuture<Barrier> addBarrierAsync(String title, double longitude, double latitude, String picture_base64, String description, String postcode, Solution solution) {
        return CompletableFuture.supplyAsync(() -> addBarrier(title, longitude, latitude, picture_base64, description, postcode, solution));
    }

    @Override
    public CompletableFuture<Barrier> modifyBarrierAsync(ObjectId id, String title, String picture_base64, String description) {
        return CompletableFuture.supplyAsync(() -> modifyBarrier(id, title, picture_base64, description));
    }

    @Override
    public CompletableFuture<Barrier> addSolutionAsync(ObjectId barrierID, Solution solution) {
        return CompletableFuture.supplyAsync(() -> addSolution(barrierID, solution));
    }

    @Override
    public CompletableFuture<Void> voteBarrierAsync(ObjectId id, Vote vote) {
        return CompletableFuture.runAsync(() -> voteBarrier(id, vote));
    }

    @Override
    public CompletableFuture<Void> voteSolutionAsync(ObjectId id, Vote vote) {
        return CompletableFuture.runAsync(() -> voteSolution(id, vote));
    }

    //helpermethods
    private Response DoPost(String path, String json){
        return target.path(path).request(MediaType.APPLICATION_JSON).post(Entity.entity(json, MediaType.APPLICATION_JSON));
    }

    private BackendConnectionException getException(Response response){
        return gson.fromJson(response.readEntity(String.class), BackendConnectionException.class);
    }

    //synchron Restcall
    private User register(String email, String username, String password) {
        String path = "users/register";
        JsonObject object = new JsonObject();
        object.addProperty("email", email);
        object.addProperty("username", username);
        object.addProperty("password", password);
        String erg = object.toString();
        Response response = DoPost(path, erg);
        if(response.getStatus() == 200){
            return gson.fromJson(response.readEntity(String.class), User.class);
        }
        else{
            throw getException(response);
        }
    }

    private User login(String emailOrUsername, String password) {
        String path = "users/login";
        JsonObject object = new JsonObject();
        object.addProperty("email", emailOrUsername);
        object.addProperty("username", emailOrUsername);
        object.addProperty("password", password);
        Response response = DoPost(path, object.toString());
        if(response.getStatus() == 200){
            return gson.fromJson(response.readEntity(String.class), User.class);
        }
        else{
            throw getException(response);
        }
    }

    private void logout() {

    }

    private String getUsername(ObjectId id) {
        return null;
    }

    private List<Barrier> getBarriers(double longitude, double latitude, int radius) {
        return null;
    }

    private List<Barrier> getBarriers(String postcode) {
        return null;
    }

    private Barrier getBarrier(ObjectId id) {
        return null;
    }

    private Barrier addBarrier(String title, double longitude, double latitude, String picture_base64, String description, String postcode, Solution solution) {
        return null;
    }

    private Barrier modifyBarrier(ObjectId id, String title, String picture_base64, String description) {
        return null;
    }

    private void voteBarrier(ObjectId id, Vote vote) {

    }

    private Barrier addSolution(ObjectId barrierID, Solution solution) {
        return null;
    }

    private void voteSolution(ObjectId id, Vote vote) {

    }
}
