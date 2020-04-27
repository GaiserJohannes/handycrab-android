package de.dhbw.handycrab.helper;

import dagger.Component;
import de.dhbw.handycrab.*;

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

    void inject(EditorActivity editorActivity);
}
