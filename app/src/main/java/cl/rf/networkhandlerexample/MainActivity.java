package cl.rf.networkhandlerexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;


public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.data.observe(this, stringResource -> {
            switch (stringResource.status()){
                case ERROR:
                    Log.d("TAG", "Error: "+ stringResource.getMessage());
                    break;
                case LOADING:
                    Log.d("TAG", "Loading: ");
                    break;
                case SUCCESS:
                    Log.d("TAG", "SUCCESS: ");
                    break;
            }
        });
        viewModel.trigger.postValue("a");

         */
    }
}