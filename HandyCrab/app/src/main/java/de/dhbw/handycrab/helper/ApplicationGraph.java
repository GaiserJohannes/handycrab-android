package de.dhbw.handycrab.helper;

import de.dhbw.handycrab.SearchActivity;

import javax.inject.Singleton;

@Singleton
public interface ApplicationGraph {
    void inject(SearchActivity searchActivity);
}
