package cl.rf.networkhandler.auth;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class BearerAuth implements Interceptor {

    public static String accessToken;

    @NonNull
    @Override
    public Response intercept(@NonNull Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder reqBuilder = request.newBuilder();

        if(accessToken != null && !accessToken.isEmpty())
            reqBuilder.addHeader("Authorization", "Bearer "+ accessToken);

        return chain.proceed(reqBuilder.build());
    }
}