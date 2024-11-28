package ua.orlov.springcoregym.model;

import org.apache.http.client.methods.HttpPost;

public class HttpRequest extends HttpPost {

    private final String method;

    public HttpRequest(String url, String method) {
        super(url);
        this.method = method;
    }

    @Override
    public String getMethod() {
        return method;
    }
}
