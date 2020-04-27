package de.dhbw.handycrab.helper;

import android.content.Context;
import android.widget.Toast;
import de.dhbw.handycrab.Program;
import de.dhbw.handycrab.R;
import de.dhbw.handycrab.backend.BackendConnectionException;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

public class DataHelper {

    @Inject
    IHandyCrabDataHandler dataHandler;

    @Inject
    IDataCache dataCache;

    @Inject
    public DataHelper(IDataCache dataCache, IHandyCrabDataHandler dataHandler) {
        this.dataCache = dataCache;
        this.dataHandler = dataHandler;
    }

    public String getUsernameFromId(ObjectId userId) {
        String userName;
        if (dataCache.contains(userId.toString())) {
            userName = dataCache.retrieve(userId.toString()).toString();
        }
        else {
            try {
                userName = dataHandler.getUsernameAsync(userId).get();
                dataCache.store(userId.toString(), userName);
            }
            catch (ExecutionException | InterruptedException e) {
                if (e.getCause() instanceof BackendConnectionException) {
                    BackendConnectionException ex = (BackendConnectionException) e.getCause();
                    showError(Program.getAppContext(), ex);
                }
                else {
                    Toast.makeText(Program.getAppContext(), Program.getAppContext().getString(R.string.defaultError), Toast.LENGTH_SHORT).show();
                }
                userName = userId.toHexString();
            }
        }
        return userName;
    }

    public void showError(Context context, BackendConnectionException exception) {
        switch (exception.getErrorCode()) {
            case NO_CONNECTION_TO_SERVER:
                Toast.makeText(context, context.getString(R.string.noConnectionToServerError), Toast.LENGTH_SHORT).show();
                break;
            case INCOMPLETE:
                Toast.makeText(context, context.getString(R.string.incompleteError), Toast.LENGTH_SHORT).show();
                break;
            case UNAUTHORIZED:
                Toast.makeText(context, context.getString(R.string.unauthorizedError), Toast.LENGTH_SHORT).show();
                break;
            case EMAIL_ALREADY_ASSIGNED:
                Toast.makeText(context, context.getString(R.string.emailAlreadyAssignedError), Toast.LENGTH_SHORT).show();
                break;
            case USERNAME_ALREADY_ASSIGNED:
                Toast.makeText(context, context.getString(R.string.usernameAlreadyAssignedError), Toast.LENGTH_SHORT).show();
                break;
            case INVALID_EMAIL:
                Toast.makeText(context, context.getString(R.string.invalidEmailError), Toast.LENGTH_SHORT).show();
                break;
            case INVALID_LOGIN:
                Toast.makeText(context, context.getString(R.string.invalidLoginError), Toast.LENGTH_SHORT).show();
                break;
            case USER_NOT_FOUND:
                Toast.makeText(context, context.getString(R.string.userNotFoundError), Toast.LENGTH_SHORT).show();
                break;
            case INVALID_GEO_LOCATION:
                Toast.makeText(context, context.getString(R.string.invalidLocationError), Toast.LENGTH_SHORT).show();
                break;
            case BARRIER_NOT_FOUND:
                Toast.makeText(context, context.getString(R.string.barrierNotFoundError), Toast.LENGTH_SHORT).show();
                break;
            case INVALID_USER_ID:
                Toast.makeText(context, context.getString(R.string.invalidUserIdError), Toast.LENGTH_SHORT).show();
                break;
            case SOLUTION_NOT_FOUND:
                Toast.makeText(context, context.getString(R.string.solutionNotFoundError), Toast.LENGTH_SHORT).show();
                break;
            case INVALID_USERNAME:
                Toast.makeText(context, context.getString(R.string.invalidUsernameError), Toast.LENGTH_SHORT).show();
                break;
            case INVALID_PASSWORD:
                Toast.makeText(context, context.getString(R.string.invalidPasswordError), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(context, context.getString(R.string.defaultError), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
