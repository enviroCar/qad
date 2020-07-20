package org.envirocar.qad;

import okhttp3.ResponseBody;
import org.envirocar.qad.model.FeatureCollection;
import org.envirocar.qad.model.Link;
import org.envirocar.qad.model.TrackList;
import org.envirocar.qad.model.TrackSummary;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface EnviroCarApi {

    String Q_LIMIT = "limit";
    String Q_PAGE = "page";
    String H_LINK = "Link";

    @GET("tracks/{id}")
    @Headers({"Accept: application/json"})
    Call<FeatureCollection> getTrack(@Path("id") String id);

    @GET("tracks")
    @Headers({"Accept: application/json"})
    Call<TrackList> getTracks(@QueryMap Map<String, String> query);

    default Stream<FeatureCollection> fetchTracks(Map<String, String> query) {
        return fetchTrackSummaries(query).getTracks().stream().map(TrackSummary::getId).map(this::fetchTrack);
    }

    default TrackList fetchTrackSummaries(Map<String, String> query) {
        try {
            if (query.containsKey(Q_LIMIT) || query.containsKey(Q_PAGE)) {
                return execute(getTracks(query)).body();
            }

            Map<String, String> q = new HashMap<>(query);
            q.put(Q_LIMIT, String.valueOf(100));
            int page = 1;
            List<TrackSummary> tracks = new LinkedList<>();
            Response<TrackList> response;
            q.put(Q_PAGE, String.valueOf(page++));
            response = execute(getTracks(q));
            Optional.ofNullable(response.body())
                    .map(TrackList::getTracks)
                    .ifPresent(tracks::addAll);

            while (response.headers().values(H_LINK).stream().map(Link::parse)
                           .anyMatch(link -> "last".equalsIgnoreCase(link.getRel()))) {
                q.put(Q_PAGE, String.valueOf(page++));
                response = execute(getTracks(q));
                Optional.ofNullable(response.body())
                        .map(TrackList::getTracks)
                        .ifPresent(tracks::addAll);
            }
            TrackList trackList = new TrackList();
            trackList.setTracks(tracks);
            return trackList;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    default FeatureCollection fetchTrack(String id) {
        try {
            return execute(getTrack(id)).body();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Nonnull
    default <T> Response<T> execute(Call<T> call) throws IOException {
        Response<T> response = call.execute();
        if (!response.isSuccessful()) {
            ResponseBody errorBody = response.errorBody();
            if (errorBody != null) {
                throw new IOException(errorBody.string());
            } else {
                throw new IOException(response.message());
            }
        }
        return response;
    }

}
