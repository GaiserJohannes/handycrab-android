package de.dhbw.handycrab;

import de.dhbw.handycrab.helper.DataHelper;
import de.dhbw.handycrab.helper.IDataCache;
import de.dhbw.handycrab.helper.InMemoryCache;
import de.dhbw.handycrab.model.Barrier;
import de.dhbw.handycrab.model.Solution;
import de.dhbw.handycrab.model.Vote;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class DataHelperTest {

    @Test
    public void replaceBarrierInListTest() {
        // Arrange
        IDataCache dataCache = Mockito.mock(InMemoryCache.class);

        Barrier oldBarrier = new Barrier(ObjectId.get(), ObjectId.get(), "test", 12.0, 42.0, null, "desc", "", new ArrayList<Solution>(), 0, 0, Vote.NONE);
        Barrier newBarrier = new Barrier(oldBarrier.getId(), ObjectId.get(), "test2", 21.0, 24.0, null, "csed", "", new ArrayList<Solution>(), 1, 1, Vote.NONE);
        List<Barrier> list = new ArrayList<>();
        list.add(oldBarrier);
        Mockito.when(dataCache.retrieve(Mockito.anyString())).thenReturn(list);

        DataHelper dataHelper = new DataHelper(dataCache, null);

        // Act
        dataHelper.replaceBarrierInList(newBarrier);

        // Assert
        Assert.assertEquals(1, list.size());
        Assert.assertNotEquals(oldBarrier, list.get(0));
        Assert.assertEquals(newBarrier, list.get(0));
    }

    @Test
    public void deleteBarrierInListTest() {
        // Arrange
        IDataCache dataCache = Mockito.mock(InMemoryCache.class);

        Barrier b1 = new Barrier(ObjectId.get(), ObjectId.get(), "test", 12.0, 42.0, null, "desc", "", new ArrayList<Solution>(), 0, 0, Vote.NONE);
        Barrier b2 = new Barrier(ObjectId.get(), ObjectId.get(), "test2", 21.0, 24.0, null, "csed", "", new ArrayList<Solution>(), 1, 1, Vote.NONE);
        List<Barrier> list = new ArrayList<>();
        list.add(b1);
        list.add(b2);
        Mockito.when(dataCache.retrieve(Mockito.anyString())).thenReturn(list);

        DataHelper dataHelper = new DataHelper(dataCache, null);

        // Act
        dataHelper.deleteBarrierInList(b2);

        // Assert
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(b1, list.get(0));
    }

}
