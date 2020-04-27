package de.dhbw.handycrab.helper;

import dagger.Module;
import dagger.Provides;
import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import de.dhbw.handycrab.test.MockConnector;

import javax.inject.Singleton;

/**
 * Provides implementations for interfaces which are injected
 */
@Module
public class BackendModule {

    /**
     * define implementation for IHandyCrabDataHandler
     * @see IHandyCrabDataHandler
     * @return data handler which will get injected
     */
    @Provides
    @Singleton
    public IHandyCrabDataHandler provideDataHandler() {
        return new MockConnector();
    }

    /**
     * define implementation for IDataHolder
     * @see IDataCache
     * @return data holder which will get injected
     */
    @Provides
    @Singleton
    public IDataCache provideDataHolder() {
        return new InMemoryCache();
    }
}
