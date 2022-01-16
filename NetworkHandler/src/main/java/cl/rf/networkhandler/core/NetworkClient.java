package cl.rf.networkhandler.core;


import cl.rf.networkhandler.auth.BearerAuth;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private final String baseURL;
    private final HttpLoggingInterceptor.Level logLevel;

    public NetworkClient(Builder builder){
        this.baseURL    = builder.baseUrl;
        this.logLevel   = builder.logLevel;
    }

    public static Builder builder(){
        return new Builder();
    }

    public <T> T create(Class<T> service){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.level(logLevel);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(new BearerAuth())
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseURL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(service);
    }

    public static final class Builder{
        protected String baseUrl;
        protected HttpLoggingInterceptor.Level logLevel = HttpLoggingInterceptor.Level.NONE;

        public Builder baseUrl(String baseUrl){
            this.baseUrl = baseUrl;
            return this;
        }

        public Builder showLogs(Boolean showLogs){
            if(showLogs)
                logLevel = HttpLoggingInterceptor.Level.BODY;
            return this;
        }

        public NetworkClient build(){
            return new NetworkClient(this);
        }
    }
}
