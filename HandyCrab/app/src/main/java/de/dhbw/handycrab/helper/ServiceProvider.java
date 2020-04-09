package de.dhbw.handycrab.helper;

import de.dhbw.handycrab.backend.IHandyCrabDataHandler;
import de.dhbw.handycrab.test.MockConnector;

public class ServiceProvider {

    public static IHandyCrabDataHandler DataHandler = new MockConnector();

    public static IDataHolder DataHolder = new DataHolder();

}
