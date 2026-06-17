package ma.oprojet.ln0wp.Services;

import ma.oprojet.ln0wp.Models.Media;
import ma.oprojet.ln0wp.Models.Post;
import ma.oprojet.ln0wp.Models.User;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface FtiarService {

    String WEBSITE = "https://oprojet.ma/ln0wp";
    String ENDPOINT = WEBSITE + "/wp-json/wp/v2";
    String BLOG = "/blog";

    @GET("/posts")
    void getPostsAsync( Callback<List<Post>> callback);

    @GET("/posts/{post_id}")
    void getPostAsync(@Path("post_id") int post_id, Callback<Post> callback); // Async

    @GET("/media/{media_id}")
    void getMediaAsync(@Path("media_id") int media_id, Callback<Media> callback); // Async

    @GET("/users/{user_id}")
    void getUserAsync(@Path("user_id") int user_id, Callback<User> callback); // Async




    @GET("/posts")
    void getLatestPosts(
            @Query("per_page") int perPage,
            @Query("orderby") String orderBy,
            @Query("_embed") boolean embed,
            Callback<List<Post>> callback
    );

    @GET("/posts")
    void getPostsAfterDate(
            @Query("after") String date,
            @Query("per_page") int perPage,
            @Query("_embed") boolean embed,
            Callback<List<Post>> callback
    );

}