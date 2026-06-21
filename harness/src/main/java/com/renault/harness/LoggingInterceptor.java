package com.renault.harness;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

import java.io.IOException;

/// OkHttp interceptor that captures the most recent HTTP exchange for display.
public class LoggingInterceptor implements Interceptor {

    public record Exchange(String method, String url, int statusCode, String responseBody) {
        @Override public String toString() {
            return "%s %s  →  %d".formatted(method, url, statusCode);
        }
    }

    private volatile Exchange last;

    public Exchange last() { return last; }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        Response res = chain.proceed(req);

        ResponseBody body = res.body();
        String bodyStr = body != null ? body.string() : "";

        // Re-wrap so downstream can still read the body
        Response rebuilt = res.newBuilder()
            .body(ResponseBody.create(bodyStr, body != null ? body.contentType() : null))
            .build();

        last = new Exchange(req.method(), req.url().toString(), res.code(), bodyStr);
        return rebuilt;
    }
}
