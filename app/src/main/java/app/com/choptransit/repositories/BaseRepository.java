package app.com.choptransit.repositories;

import android.util.Log;

import androidx.annotation.NonNull;

import app.com.choptransit.network.GetDataService;
import app.com.choptransit.network.RetrofitClientInstance;
import app.com.choptransit.utilities.Commons;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class BaseRepository {

    private static final String BASE_URL = "http://chopchop.curlbrackets.com/api/";

    protected GetDataService apiService = RetrofitClientInstance.getRetService(BASE_URL);


    protected <t> void registerApiRequest(Call<t> call, ResponseCallback callback) {
        call.enqueue(new Callback<t>() {

            @Override
            public void onResponse(@NonNull Call<t> call, @NonNull Response<t> response) {
                if (response.code() == 200) {
                    callback.onResponseCallback(response.body());
                } else {
                    Log.d("Response", response.message());
                    Commons.hideProgress();
                }
            }

            @Override
            public void onFailure(@NonNull Call<t> call, @NonNull Throwable t) {
                Log.d("Response", t.getLocalizedMessage());
                Commons.hideProgress();
            }
        });
    }

    public interface ResponseCallback {
        void onResponseCallback(Object body);
    }

}
