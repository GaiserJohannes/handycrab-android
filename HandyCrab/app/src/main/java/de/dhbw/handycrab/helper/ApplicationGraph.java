package de.dhbw.handycrab.helper;

import dagger.Component;
import de.dhbw.handycrab.BarrierListActivity;
import de.dhbw.handycrab.DetailActivity;
import de.dhbw.handycrab.LoginActivity;
import de.dhbw.handycrab.SearchActivity;

import javax.inject.Singleton;

/**
 * Provides methods for classes that rely on dependency injection
 */
@Singleton
@Component(modules = BackendModule.class)
public interface ApplicationGraph {
    void inject(SearchActivity searchActivity);

    void inject(BarrierListActivity barrierListActivity);

    void inject(DetailActivity detailActivity);

    void inject(LoginActivity loginActivity);
}
