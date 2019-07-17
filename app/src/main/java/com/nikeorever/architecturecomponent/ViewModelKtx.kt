package com.nikeorever.architecturecomponent

import android.app.Activity
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import kotlinx.coroutines.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import kotlin.coroutines.CoroutineContext

const val TAG: String = "SecondViewModel"

class SecondViewModel : ViewModel() {
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://my-json-server.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // use viewModelScope.coroutineContext to auto cancel when cancelFrom
    private val postsLiveData: LiveData<List<Post>> = liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {

        try {
            log(TAG, "[CoroutinePostsLiveData] launch")
            val posts = retrofit.create<ApiService>().getPostsWithCall().await()
            log(TAG, "[CoroutinePostsLiveData] result is ok: $posts")
            emit(posts)
        } catch (e: Exception) {
            if (e is CancellationException) {
                log(TAG, "[CoroutinePostsLiveData] cancelled")
            } else {
                log(TAG, "[CoroutinePostsLiveData] $e")
            }
        } finally {
            log(TAG, "[CoroutinePostsLiveData] finally")
        }
    }

    // use viewModelScope.coroutineContext to auto cancel when cancelFrom
    val liveData: LiveData<List<Post>> = liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {

        val disposable = emitSource(liveData<List<Post>> {
            try {
                log(TAG, "[CoroutineProfileLiveData] launch")
                val profile = retrofit.create<ApiService>().getProfile()
                log(TAG, "[CoroutineProfileLiveData] result is ok: $profile")
                emit(emptyList())
            } catch (e: Exception) {
                if (e is CancellationException) {
                    log(TAG, "[CoroutineProfileLiveData] cancelled")
                } else {
                    log(TAG, "[CoroutineProfileLiveData] $e")
                }
            } finally {
                log(TAG, "[CoroutineProfileLiveData] finally")
            }
        })

        // Stop the previous emission to avoid dispatching the updated user
        // as `loading`.
        disposable.dispose()

        try {
            log(TAG, "[CoroutineLiveData] launch")
            val posts = retrofit.create<ApiService>().getPostsWithCall().await()
            log(TAG, "[CoroutineLiveData] result is ok: $posts")
            emit(posts)
        } catch (e: Exception) {
            if (e is CancellationException) {
                log(TAG, "[CoroutineLiveData] cancelled")
            } else {
                log(TAG, "[CoroutineLiveData] $e")
            }
        } finally {
            log(TAG, "[CoroutineLiveData] finally")
        }
    }

    //combine and order run
    val commentsLiveData = postsLiveData.switchMap {
        liveData<List<Comments>>(context = viewModelScope.coroutineContext + Dispatchers.IO) {
            try {
                log(TAG, "[CoroutineCommentsLiveData] launch")
                val comments = retrofit.create<ApiService>().getComments()
                log(TAG, "[CoroutineCommentsLiveData] result is ok: $comments")
                emit(comments)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    log(TAG, "[CoroutineCommentsLiveData] cancelled")
                } else {
                    log(TAG, "[CoroutineCommentsLiveData] $e")
                }
            } finally {
                log(TAG, "[CoroutineCommentsLiveData] finally")
            }
        }
    }

    val postsStrLiveData = postsLiveData.map<List<Post>, String> {
        it.joinToString("/")
    }


    init {
        viewModelScope.launch {
            launch {
                try {
                    log(TAG, "(1) launch")
                    delay(Long.MAX_VALUE)
                } finally {
                    log(TAG, "(1) cancel")
                }
            }

            val secondJob = launch {
                try {
                    log(TAG, "(2) launch")
                    delay(Long.MAX_VALUE)
                } finally {
                    log(TAG, "(2) cancel")
                }
            }
            secondJob.join()

            log(TAG, "root exe")
        }
    }


    interface ApiService {

        @GET("typicode/demo/posts")
        suspend fun getPosts(): List<Post>

        @GET("typicode/demo/posts")
        fun getPostsWithCall(): Call<List<Post>>

        @GET("typicode/demo/comments")
        suspend fun getComments(): List<Comments>

        @GET("typicode/demo/profile")
        suspend fun getProfile(): Profile

        @GET("typicode/demo/profile")
        fun getCallProfile(): Call<Profile>
    }

    data class Post(var id: Int? = null, var title: String? = null)
    data class Comments(var id: Int? = null, var body: String? = null, var postId: Int? = null)
    data class Profile(var name: String? = null)

    companion object {
        fun instance(activity: AppCompatActivity): SecondViewModel {
            val factory = ViewModelProvider.AndroidViewModelFactory.getInstance(activity.application)
            return ViewModelProvider(activity, factory).get<SecondViewModel>()
        }
    }
}