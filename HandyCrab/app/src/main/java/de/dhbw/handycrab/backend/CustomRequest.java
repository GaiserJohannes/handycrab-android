package de.dhbw.handycrab.backend;

import cz.msebera.android.httpclient.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

public class CustomRequest extends HttpEntityEnclosingRequestBase {

    private String method;

    public CustomRequest(final String uri, String method) {
        this(uri);
        this.method = method;
    }

    /**
     * @throws IllegalArgumentException if the uri is invalid.
     */
    public CustomRequest(final String uri) {
        super();
        setURI(URI.create(uri));
        method = "GET";
    }

    @Override
    public String getMethod() {
        return method;
    }
}
