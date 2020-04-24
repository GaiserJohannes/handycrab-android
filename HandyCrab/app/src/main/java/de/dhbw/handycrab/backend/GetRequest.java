package de.dhbw.handycrab.backend;

import java.net.URI;

import cz.msebera.android.httpclient.client.methods.HttpEntityEnclosingRequestBase;

public class GetRequest extends HttpEntityEnclosingRequestBase {

    public final static String METHOD_NAME = "GET";

    public GetRequest() {
        super();
    }

    public GetRequest(final URI uri) {
        super();
        setURI(uri);
    }

    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public GetRequest(final String uri) {
        super();
        setURI(URI.create(uri));
    }

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

}
