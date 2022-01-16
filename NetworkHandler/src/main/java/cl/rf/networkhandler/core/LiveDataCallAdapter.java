package cl.rf.networkhandler.core;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Clase CallAdapter personalizado para manejar todas las peticiones al api a través de
 * retrofit usando {@link LiveData}
 * @param <R>
 */
public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<ApiResponse<R>>> {
    private final Type responseType;

    public LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @NonNull
    @Override
    public Type responseType() {
        return this.responseType;
    }

    @NonNull
    @Override
    public LiveData<ApiResponse<R>> adapt(@NonNull Call<R> call) {
        return new LiveData<>() {
            private Boolean started = false;

            @Override
            protected void onActive() {
                super.onActive();
                if (!started) {
                    started = true;
                    call.enqueue(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<R> call, @NonNull Response<R> response) {
                            postValue(new ApiResponse<R>().create(response));
                        }

                        @Override
                        public void onFailure(@NonNull Call<R> call, @NonNull Throwable t) {
                            postValue(new ApiResponse<R>().create(t));
                        }
                    });
                }
            }
        };
    }
}