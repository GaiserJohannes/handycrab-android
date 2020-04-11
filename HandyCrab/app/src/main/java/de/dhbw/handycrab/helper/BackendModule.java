package de.dhbw.handycrab.helper;

import dagger.Module;
import dagger.Provides;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import de.dhbw.handycrab.test.MockConnector;

import javax.inject.Singleton;

@Module
public class BackendModule {

    @Provides
    @Singleton
    public IHandyCrabDataHandler provideDataHandler() {
        return new MockConnector();
    }

    @Provides
    @Singleton
    public IDataHolder provideDataHolder() {
        return new DataHolder();
    }
}
