package org.envirocar.qad;

import okhttp3.ResponseBody;
import org.envirocar.qad.model.FeatureCollection;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.io.IOException;

public interface EnviroCarApi {
    @GET("tracks/{id}")
    @Headers({"Accept: application/json"})
    Call<FeatureCollection> getTrack(@Path("id") String id);

    default FeatureCollection fetchTrack(String id) throws IOException {
        Response<FeatureCollection> execute = getTrack(id).execute();
        if (!execute.isSuccessful()) {
            ResponseBody errorBody = execute.errorBody();
            if (errorBody != null) {
                throw new IOException(errorBody.string());
            } else {
                throw new IOException(execute.message());
            }
        }

        return execute.body();
    }
}
