package cl.rf.networkhandlerexample;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import cl.rf.networkhandler.autogenerate.InteRepo;
import cl.rf.networkhandler.model.Resource;

public class MainViewModel extends ViewModel {

    public MutableLiveData<String> trigger = new MutableLiveData<>();

    public LiveData<Resource<SimpleResponse>> data = Transformations.switchMap(trigger, input -> InteRepo.getText2("bella"));

}
