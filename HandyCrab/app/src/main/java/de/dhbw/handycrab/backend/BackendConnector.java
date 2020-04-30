package de.dhbw.handycrab.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.model.ErrorCode;
import de.dhbw.handycrab.model.User;
import de.dhbw.handycrab.model.Vote;
import org.bson.types.ObjectId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/*
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
 */

public class BackendConnector implements IHandyCrabDataHandler {

    private String connection = "https://handycrab.nico-dreher.de/rest/";
    private HttpClient client = HttpClientBuilder.create().build();
    private Gson gson = new GsonBuilder().registerTypeAdapter(ObjectId.class, new ObjectIDDeserializer()).create();

    public BackendConnector() {
    }

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
        return CompletableFuture.runAsync(this::logout);
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
    public CompletableFuture<Barrier> addBarrierAsync(String title, double longitude, double latitude, String picture_base64, String description, String postcode, String solution) {
        return CompletableFuture.supplyAsync(() -> addBarrier(title, longitude, latitude, picture_base64, description, postcode, solution));
    }

    @Override
    public CompletableFuture<Barrier> modifyBarrierAsync(ObjectId id, String title, String picture_base64, String description) {
        return CompletableFuture.supplyAsync(() -> modifyBarrier(id, title, picture_base64, description));
    }

    @Override
    public CompletableFuture<Barrier> addSolutionAsync(ObjectId barrierID, String solution) {
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

    //synchron Restcalls
    private User register(String email, String username, String password) {
        String path = "users/register";
        JsonObject object = new JsonObject();
        object.addProperty("email", email);
        object.addProperty("username", username);
        object.addProperty("password", password);
        HttpResponse response = post(path, object.toString());
        return getUserOfResponse(response);
    }

    private User login(String emailOrUsername, String password) {
        String path = "users/login";
        JsonObject object = new JsonObject();
        object.addProperty("login", emailOrUsername);
        object.addProperty("password", password);
        HttpResponse response = post(path, object.toString());
        return getUserOfResponse(response);
    }

    private void logout() {
        String path = "users/logout";
        HttpResponse response = post(path, "");
        checkSuccessResponse(response);
    }

    private String getUsername(ObjectId id) {
        String path = "users/name";
        JsonObject object = new JsonObject();
        object.addProperty("_id", id.toString());
        HttpResponse response = get(path, object.toString());
        if (response != null && response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(getJsonBody(response), JsonObject.class).get("result").getAsString();
        }
        else {
            throw getException(response);
        }
    }

    private List<Barrier> getBarriers(double longitude, double latitude, int radius) {
        String path = "barriers/get";
        JsonObject object = new JsonObject();
        object.addProperty("longitude", longitude);
        object.addProperty("latitude", latitude);
        object.addProperty("radius", radius);
        HttpResponse response = get(path, object.toString());
        return getBarriersOfResponse(response);
    }

    private List<Barrier> getBarriers(String postcode) {
        String path = "barriers/get";
        JsonObject object = new JsonObject();
        object.addProperty("postcode", postcode);
        HttpResponse response = get(path, object.toString());
        return getBarriersOfResponse(response);
    }

    private Barrier getBarrier(ObjectId id) {
        String path = "barriers/get";
        JsonObject object = new JsonObject();
        object.addProperty("_id", id.toString());
        HttpResponse response = get(path, object.toString());
        return getBarrierOfResponse(response);
    }

    private Barrier addBarrier(String title, double longitude, double latitude, String picture_base64, String description, String postcode, String solution) {
        String path = "barriers/add";
        JsonObject object = new JsonObject();
        object.addProperty("title", title);
        object.addProperty("longitude", longitude);
        object.addProperty("latitude", latitude);
        object.addProperty("picture", picture_base64);
        object.addProperty("description", description);
        object.addProperty("postcode", postcode);
        object.addProperty("solution", solution);
        HttpResponse response = post(path, object.toString());
        return getBarrierOfResponse(response);
    }

    private Barrier modifyBarrier(ObjectId id, String title, String picture_base64, String description) {
        String path = "barriers/modify";
        JsonObject object = new JsonObject();
        object.addProperty("_id", id.toString());
        object.addProperty("title", title);
        object.addProperty("picture", picture_base64);
        object.addProperty("description", description);
        HttpResponse response = put(path, object.toString());
        return getBarrierOfResponse(response);
    }

    private void voteBarrier(ObjectId id, Vote vote) {
        String path = "barriers/vote";
        JsonObject object = new JsonObject();
        object.addProperty("_id", id.toString());
        object.addProperty("vote", vote.toString());
        HttpResponse response = put(path, object.toString());
        checkSuccessResponse(response);
    }

    private Barrier addSolution(ObjectId barrierID, String solution) {
        String path = "barriers/solution";
        JsonObject object = new JsonObject();
        object.addProperty("_id", barrierID.toString());
        object.addProperty("solution", solution);
        HttpResponse response = post(path, object.toString());
        return getBarrierOfResponse(response);
    }

    private void voteSolution(ObjectId id, Vote vote) {
        String path = "barriers/solutions/vote";
        JsonObject object = new JsonObject();
        object.addProperty("_id", id.toString());
        object.addProperty("vote", vote.toString());
        HttpResponse response = put(path, object.toString());
        checkSuccessResponse(response);
    }

    //helper get objects of Response
    private Barrier getBarrierOfResponse(HttpResponse response) {
        if (response != null && response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(getJsonBody(response), Barrier.class);
        }
        else {
            throw getException(response);
        }
    }

    private List<Barrier> getBarriersOfResponse(HttpResponse response) {
        if (response != null && response.getStatusLine().getStatusCode() == 200) {
            Type listOfBarrierType = new TypeToken<ArrayList<Barrier>>() {
            }.getType();
            return gson.fromJson(getJsonBody(response), listOfBarrierType);
        }
        else {
            throw getException(response);
        }
    }

    private User getUserOfResponse(HttpResponse response) {
        if (response != null && response.getStatusLine().getStatusCode() == 200) {
            return gson.fromJson(getJsonBody(response), User.class);
        }
        else {
            throw getException(response);
        }
    }

    private void checkSuccessResponse(HttpResponse response) {
        if (response != null && response.getStatusLine().getStatusCode() < 300) {
            return;
        }
        throw getException(response);
    }

    //http methods
    private HttpResponse get(String path, String json) {
        GetRequest postRequest = new GetRequest(connection + path);
        postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        try {
            return client.execute(postRequest);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpResponse post(String path, String json) {
        HttpPost postRequest = new HttpPost(connection + path);
        postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        try {
            return client.execute(postRequest);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpResponse put(String path, String json) {
        HttpPut putRequest = new HttpPut(connection + path);
        putRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        try {
            return client.execute(putRequest);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //other helpermethods
    private String getJsonBody(HttpResponse response) {
        try {
            return new BufferedReader(new InputStreamReader(response.getEntity().getContent())).readLine();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return "{errorCode:0}";
    }

    private BackendConnectionException getException(HttpResponse response) {
        if (response == null) {
            return new BackendConnectionException(ErrorCode.NO_CONNECTION_TO_SERVER, -1);
        }
        JsonObject object = gson.fromJson(getJsonBody(response), JsonObject.class);
        ErrorCode err = ErrorCode.values()[object.get("errorCode").getAsInt()];
        return new BackendConnectionException(err, response.getStatusLine().getStatusCode());
    }
}
