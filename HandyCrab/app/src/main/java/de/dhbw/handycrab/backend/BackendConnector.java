package de.dhbw.handycrab.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.bson.types.ObjectId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.model.Solution;
import de.dhbw.handycrab.model.User;
import de.dhbw.handycrab.model.Vote;

public class BackendConnector implements IHandyCrabDataHandler {

    private String connection = "http://handycrab.nico-dreher.de/rest/";
    private HttpClient client = HttpClientBuilder.create().build();
    private Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIDDeserializer()).create();

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
    private HttpResponse get(String path, String json){
        HttpEntityEnclosingRequestBase httpEntity = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return "GET";
            }
        };
        httpEntity.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.toString());
        httpEntity.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            httpEntity.setURI(new URI(connection + path));
            httpEntity.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            return client.execute(httpEntity);
        } catch (URISyntaxException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpResponse post(String path, String json){
        HttpPost postRequest = new HttpPost( connection + path);
        postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        try {
           return client.execute(postRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getJsonBody(HttpResponse response){
        try {
            return new BufferedReader(new InputStreamReader(response.getEntity().getContent())).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{errorCode:0}";
    }

    private BackendConnectionException getException(HttpResponse response){
        JsonObject object = gson.fromJson(getJsonBody(response), JsonObject.class);
        return new BackendConnectionException(object.get("errorCode").getAsInt(), response.getStatusLine().getStatusCode());
    }

    //synchron Restcalls

    private User register(String email, String username, String password) {
        String path = "users/register";
        JsonObject object = new JsonObject();
        object.addProperty("email", email);
        object.addProperty("username", username);
        object.addProperty("password", password);
        HttpResponse response = post(path, object.toString());
        if(response.getStatusLine().getStatusCode() == 200){
            return gson.fromJson(getJsonBody(response), User.class);
        }
        else{
            throw getException(response);
        }
    }

    private User login(String emailOrUsername, String password) {
        String path = "users/login";
        JsonObject object = new JsonObject();
        object.addProperty("login", emailOrUsername);
        object.addProperty("password", password);
        HttpResponse response = post(path, object.toString());
        if(response.getStatusLine().getStatusCode() == 200){
            return gson.fromJson(getJsonBody(response), User.class);
        }
        else{
            throw getException(response);
        }
    }

    private void logout() {
        String path = "users/logout";
        HttpResponse response = post(path, "");
        if(response.getStatusLine().getStatusCode() < 300){
            return;
        }
        else{
            throw getException(response);
        }
    }

    private String getUsername(ObjectId id) {
        String path = "users/name";
        JsonObject object = new JsonObject();
        object.addProperty("_id", id.toString());
        HttpResponse response = get(path, object.toString());
        if(response.getStatusLine().getStatusCode() == 200){
            return gson.fromJson(getJsonBody(response), JsonObject.class).get("result").getAsString();
        }
        else{
            throw getException(response);
        }
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
