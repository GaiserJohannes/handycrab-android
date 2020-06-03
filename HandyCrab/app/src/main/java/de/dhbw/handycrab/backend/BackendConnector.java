package de.dhbw.handycrab.backend;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.client.methods.HttpPut;
import cz.msebera.android.httpclient.client.methods.HttpUriRequest;
import cz.msebera.android.httpclient.cookie.Cookie;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.BasicCookieStore;
import cz.msebera.android.httpclient.impl.client.CloseableHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.model.ErrorCode;
import de.dhbw.handycrab.model.User;
import de.dhbw.handycrab.model.Vote;
import io.gsonfire.GsonFireBuilder;
import io.gsonfire.PostProcessor;

import org.bson.types.ObjectId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class BackendConnector implements IHandyCrabDataHandler {

    public static int TIMEOUT_MILLIS = 1000;

    private static String TOKEN = "TOKEN";

    private CookieStore cookieStore = new BasicCookieStore();
    private String connection = "https://handycrab.nico-dreher.de/rest/";
    private CloseableHttpClient client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    private Gson gson;

    public BackendConnector() {
        gson = new GsonFireBuilder()
                .registerPostProcessor(Barrier.class, new PostProcessor<Barrier>() {
                    @Override
                    public void postDeserialize(Barrier result, JsonElement src, Gson gson) {
                        result.downloadImage();
                    }

                    @Override
                    public void postSerialize(JsonElement result, Barrier src, Gson gson) {
                    }
                })
                .createGsonBuilder()
                .registerTypeAdapter(ObjectId.class, (JsonDeserializer<ObjectId>) (json, typeOfT, context) -> new ObjectId(json.getAsString()))
                .create();
    }

    @Override
    public CompletableFuture<User> registerAsync(final String email, final String username, final String password, boolean createToken) {
        return CompletableFuture.supplyAsync(() -> register(email, username, password, createToken));
    }

    @Override
    public CompletableFuture<User> loginAsync(String emailOrUsername, String password, boolean createToken) {
        return CompletableFuture.supplyAsync(() -> login(emailOrUsername, password, createToken));
    }

    @Override
    public CompletableFuture<User> currenUserAsync() {
        return CompletableFuture.supplyAsync(() -> currentUser());
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
    public CompletableFuture<List<Barrier>> getBarriersAsync() {
        return CompletableFuture.supplyAsync(() -> getBarriers());
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
    public CompletableFuture<Void> deleteBarrierAsync(ObjectId id) {
        return CompletableFuture.runAsync(() -> deleteBarrier(id));
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

    @Override
    public void loadToken(String token, String domain) {
        BasicClientCookie cookie = new BasicClientCookie(TOKEN, token);
        cookie.setDomain(domain);
        cookie.setPath("/");
        cookie.setVersion(1);
        cookieStore.addCookie(cookie);
        client = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }

    @Override
    public void saveToken(BiConsumer<String, String> function) {
        boolean tokenAvailable = cookieStore.getCookies().stream().anyMatch(c -> c.getName().equals(TOKEN));
        if(tokenAvailable){
            Cookie cookie = cookieStore.getCookies().stream().filter(c -> c.getName().equals(TOKEN)).findFirst().get();
            String tokenValue = cookie.getValue();
            String domain = cookie.getDomain();
            function.accept(tokenValue, domain);
        }
    }

    //synchron Restcalls
    private User register(String email, String username, String password, boolean createToken) {
        String path = "users/register";
        JsonObject object = new JsonObject();
        object.addProperty("email", email);
        object.addProperty("username", username);
        object.addProperty("password", password);
        object.addProperty("createToken", Boolean.toString(createToken));
        HttpResponse response = post(path, object.toString());
        return getUserOfResponse(response);
    }

    private User login(String emailOrUsername, String password, boolean createToken) {
        String path = "users/login";
        JsonObject object = new JsonObject();
        object.addProperty("login", emailOrUsername);
        object.addProperty("password", password);
        object.addProperty("createToken", Boolean.toString(createToken));
        HttpResponse response = post(path, object.toString());
        return getUserOfResponse(response);
    }

    private User currentUser() {
        String path = "users/currentuser";
        HttpResponse response = get(path);
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

    private List<Barrier> getBarriers() {
        String path = "barriers/get";
        HttpResponse response = get(path, new JsonObject().toString());
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
        if(picture_base64 != null && !picture_base64.isEmpty()){
            object.addProperty("picture", picture_base64);
        }
        object.addProperty("description", description);
        object.addProperty("postcode", postcode);
        if(solution != null && !solution.isEmpty()) {
            object.addProperty("solution", solution);
        }
        HttpResponse response = post(path, object.toString());
        return getBarrierOfResponse(response);
    }

    private Barrier modifyBarrier(ObjectId id, String title, String picture_base64, String description) {
        String path = "barriers/modify";
        JsonObject object = new JsonObject();
        object.addProperty("_id", id.toString());
        object.addProperty("title", title);
        if(picture_base64 != null && !picture_base64.isEmpty()){
            object.addProperty("picture", picture_base64);
        }
        object.addProperty("description", description);
        HttpResponse response = put(path, object.toString());
        return getBarrierOfResponse(response);
    }

    private void deleteBarrier(ObjectId id) {
        String path = "barriers/delete";
        JsonObject object = new JsonObject();
        object.addProperty("_id", id.toString());
        HttpResponse response = delete(path, object.toString());
        checkSuccessResponse(response);
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
        CustomRequest getRequest = new CustomRequest(connection + path);
        getRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        return execute(getRequest);
    }

    private HttpResponse get(String path) {
        CustomRequest getRequest = new CustomRequest(connection + path);
        return execute(getRequest);
    }

    private HttpResponse post(String path, String json) {
        HttpPost postRequest = new HttpPost(connection + path);
        postRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        return execute(postRequest);
    }

    private HttpResponse put(String path, String json) {
        HttpPut putRequest = new HttpPut(connection + path);
        putRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        return execute(putRequest);
    }

    private HttpResponse delete(String path, String json) {
        CustomRequest deleteRequest = new CustomRequest(connection + path, "DELETE");
        deleteRequest.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        return execute(deleteRequest);
    }

    private HttpResponse execute(HttpUriRequest request){
        try {
            return client.execute(request);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //other helpermethods
    private String getJsonBody(HttpResponse response) {
        try {
            String s = new BufferedReader(new InputStreamReader(response.getEntity().getContent())).readLine();
            return s;
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
