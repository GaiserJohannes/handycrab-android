package de.dhbw.handycrab.helper;

import android.widget.Toast;
import de.dhbw.handycrab.Program;
import de.dhbw.handycrab.R;
import de.dhbw.handycrab.SearchActivity;
import de.dhbw.handycrab.backend.BackendConnectionException;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import de.dhbw.handycrab.model.Barrier;
import org.bson.types.ObjectId;

import javax.inject.Inject;
import java.util.List;
import java.util.ListIterator;
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
        if(userId == null){
            return "404 Username not found!";
        }
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
                    Toast.makeText(Program.getAppContext(), ex.getDetailedMessage(Program.getAppContext()), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(Program.getAppContext(), Program.getAppContext().getString(R.string.unknownError), Toast.LENGTH_SHORT).show();
                }
                userName = userId.toHexString();
            }
        }
        return userName;
    }

    public void replaceBarrierInList(Barrier barrier) {
        List<Barrier> list = (List<Barrier>) dataCache.retrieve(SearchActivity.BARRIER_LIST);

        ListIterator<Barrier> iterator = list.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(barrier.getId())) {
                iterator.set(barrier);
                break;
            }
        }
    }

    public void deleteBarrierInList(Barrier barrier) {
        List<Barrier> list = (List<Barrier>) dataCache.retrieve(SearchActivity.BARRIER_LIST);

        ListIterator<Barrier> iterator = list.listIterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(barrier.getId())) {
                iterator.remove();
                break;
            }
        }
    }

}
