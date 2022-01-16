package cl.rf.networkhandler.core;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import cl.rf.networkhandler.model.Error;
import cl.rf.networkhandler.model.Resource;

/**
 * Clase genérica intermediaria para manejar las peticiones al api, enmascara la comunicación del
 * {@link Resource} , usa un {@link MediatorLiveData} para canalizar las respuestas y cada vez que se emita
 * un resultado actualizar su valor y notificar a los Observadores. Recibe como parametro el modelo del
 * Response del api
 * @param <ResultType>
 */
public abstract class NetworkBoundResource <ResultType>{
    private final MediatorLiveData<Resource<ResultType>> result;

    public NetworkBoundResource() {
        result = new MediatorLiveData<>();
        result.postValue(new Resource<ResultType>().loading(null));
        result.addSource(createCall(), requestTypeApiResponse -> {
            Resource<ResultType> resource = null;
            if(requestTypeApiResponse instanceof ApiResponse.ApiSuccessResponse){
                resource = new Resource<ResultType>().success(
                        ((ApiResponse.ApiSuccessResponse<ResultType>) requestTypeApiResponse).body(),
                        ((ApiResponse.ApiSuccessResponse<ResultType>) requestTypeApiResponse).getCode()
                );
            }
            if(requestTypeApiResponse instanceof ApiResponse.ApiErrorResponse) {
                resource = new Resource<ResultType>().error(
                        new Error(
                                ((ApiResponse.ApiErrorResponse<ResultType>) requestTypeApiResponse).error(),
                                ((ApiResponse.ApiErrorResponse<ResultType>) requestTypeApiResponse).getCode()
                        ),
                        null
                );
            }
            setValue(resource);
        });
    }

    /**
     * Publica los cambios y notifica a los observadores
     * @param newValue es una instancia de {@link Resource}
     */
    private void setValue(Resource<ResultType> newValue){
        if(result.getValue() != newValue){
            result.postValue(newValue);
        }
    }

    /**
     * Método abstracto donde se implementa la llamada al api, es normalmente instanciado en
     * la clase repositorio que vaya a manejar la petición. La implementación debería invocar alguno
     * de los endpoins declarados en
     * @return retorna la respuesta del api a través de la clase empaquetadora {@link ApiResponse}
     */
    public abstract LiveData<ApiResponse<ResultType>> createCall();

    /**
     * Método para devolver el {@link MediatorLiveData} como un {@link LiveData} para que pueda ser
     * observado
     * @return LiveData {@link Resource}
     */
    public LiveData<Resource<ResultType>> asLiveData(){
        return this.result;
    }

    //TODO Falta impplementar los métodos para persistencia local si es que lo define así el negocio

}
