package cl.rf.networkhandler.core;

import java.io.IOException;

import retrofit2.Response;

/**
 * Clase genérica para menejar las peticiones al servidor, se usa para crear el Adaptador
 * personalizado de Retrofit
 * @param <T> modelo con el tipo de respuesta que retorna el api
 */
public class ApiResponse<T> {

    public ApiErrorResponse<T> create(Throwable error){
        return new ApiErrorResponse<>(error != null ? error.getMessage() : "unknown error", -1);
    }

    public ApiResponse<T> create(Response<T> response){
        T body = response.body();
        try {
            if (response.isSuccessful()) {
                if (body == null || response.code() >= 300) {
                    return new ApiErrorResponse<>(response.errorBody() != null ? response.errorBody().string() : "unknown error", response.code());
                } else {
                    return new ApiSuccessResponse<>(response.body(), response.code());
                }
            } else {
                return new ApiErrorResponse<>(response.errorBody() != null ? response.message() : "unknown error", response.code());
            }
        }catch (IOException e) {
            return new ApiErrorResponse<>(e.getMessage(), -1);
        }
    }

    /**
     * Para manejar respuestas correcctas y devolver dolo el body que ademas será del tipo <T>
     * @param <T>
     */
    public static class ApiSuccessResponse<T> extends ApiResponse<T>{
        private final T body;
        private final int code;

        public ApiSuccessResponse(T body, int code) {
            this.body = body;
            this.code = code;
        }

        public T body() {
            return body;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * Para manejar respuestas con errores, cualquier response con de código mayor a 300
     * @param <T>
     */
    public static class ApiErrorResponse<T> extends ApiResponse<T>{
        private final String error;
        private final int code;

        public ApiErrorResponse(String error, int code) {
            this.error = error;
            this.code = code;
        }

        public String error() {
            return error;
        }

        public int getCode() {
            return code;
        }
    }
}
