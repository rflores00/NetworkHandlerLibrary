package cl.rf.networkhandler.core;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cl.rf.networkhandler.model.Resource;

public abstract class GenericRepository<ResultType> {

    //protected final ApiServices apiServices;
    //protected final AccessCredentials accessCredentials;
    protected final List<Object> params;

    /*
    public GenericRepository(@RetrofitModule.CommonApiServices ApiServices apiServices, AccessCredentials accessCredentials) {
        this.apiServices = apiServices;
        this.accessCredentials = accessCredentials;
        this.params = new ArrayList<>();
    }*/

    public GenericRepository() {
        this.params = new ArrayList<>();
    }

    /**
     * Este para escuchar {@link LiveData} con el resultado del request
     * @return {@link LiveData} con el {@link Resource} según el VO
     */
    public LiveData<Resource<ResultType>> request(Object... params){
        this.params.clear();
        this.params.addAll(Arrays.asList(params));
        return new NetworkBoundResource<ResultType>() {
            @Override
            public LiveData<ApiResponse<ResultType>> createCall() {
                return attachedEndpoint();
            }
        }.asLiveData();
    }

    /**
     * Se debe usar este método para indicar cual es el recurso al que se accede
     * @return un {@link LiveData} con el response del api
     */
    protected abstract @NonNull
    LiveData<ApiResponse<ResultType>> attachedEndpoint();

}