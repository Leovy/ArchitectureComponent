package com.nikeorever.architecturecomponent

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.*
import kotlin.concurrent.thread
import kotlin.random.Random

private const val TAG_WM = "WorkManagerTag"

private fun upImageSync(): Boolean {
    return try {
        log(TAG_WM, "start upload image")
        Thread.sleep(5000)

        Random.nextBoolean().also {
            if (it) {
                log(TAG_WM, "upload image successfully")
            } else {
                throw Exception("unKnow")
            }
        }
    } catch (e: Exception) {
        log(TAG_WM, "upload image failure, cause by $e")
        false
    }
}

private fun upImageAsync(parent: Job?): Deferred<Boolean> {
    val deferred = CompletableDeferred<Boolean>(parent)
    val thread = thread {
        val result = upImageSync()
        if (result) {
            deferred.complete(true)
        } else {
            deferred.completeExceptionally(Exception("uploadError"))
        }
    }
    deferred.invokeOnCompletion {
        if (deferred.isCancelled) {
            thread.interrupt()
        }
    }

    return deferred
}

fun testCompletableDeferred() {
    GlobalScope.launch {
        val job = Job()
        CoroutineScope(job).launch {
            val deferred = upImageAsync(job)
            try {
                log(TAG_WM, "start upload")
                val result = deferred.await()
                log(TAG_WM, "upload success, result is $result")
            } catch (e: Exception) {
                log(TAG_WM, "upload failure, cause by $e")
            } finally {
                log(TAG_WM, "upload finally")
            }
        }.let {
            log(TAG_WM, "same job?: ${it == job}")
//            it.join()
//            log(TAG_WM, "job join complete")
            delay(2000)
            job.cancel()
        }
    }
}


/**
 * Create a background task
 */
private class UploadWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    /**
     * run synchronously on a background thread provided by WorkManager.
     */
    override fun doWork(): Result {
        /*
            1.finished successfully via Result.success()
            2.failed via Result.failure()
            3.needs to be retried at a later time via Result.retry()
         */
        return upImageSync().resultValue()
    }

    private fun Boolean.resultValue(): Result {
        return if (this) Result.success() else Result.failure()
    }
}

/**
 * Threading in CoroutineWorker
 */
private class CoroutineUploadWorker(context: Context, workerParams: WorkerParameters)
    : CoroutineWorker(context, workerParams) {

    override val coroutineContext: CoroutineDispatcher
        get() = Dispatchers.IO

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val jobs = (0 until 10).map {
                async {
                    if (!upImageSync()) {
                        throw IllegalStateException("upload Failure")
                    }
                }
            }

            // awaitAll will throw an exception if a download fails, which CoroutineWorker will treat as a failure
            jobs.awaitAll()
            Result.success()
        }
    }

}

private object MyWorkerFactory : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            UploadWorker::class.qualifiedName -> {
                UploadWorker(appContext, workerParameters)
            }
            CoroutineUploadWorker::class.qualifiedName -> {
                CoroutineUploadWorker(appContext, workerParameters)
            }
            else -> {
                null
            }
        }
    }

}

var isWorkManagerInitialized = false

fun runWork(context: Context) {

    //This method throws an exception if it is called multiple times.
    if (!isWorkManagerInitialized) {
        isWorkManagerInitialized = true
        WorkManager.initialize(
            context, Configuration.Builder()
                .setWorkerFactory(MyWorkerFactory)
                .build()
        )
    }


    //a WorkRequest defines how and when work should be run.
    val uploadWorkRequest = OneTimeWorkRequestBuilder<CoroutineUploadWorker>()
        .setConstraints(run {  //Work constraints
            //You can add Constraints to your work to indicate when it can run.
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.METERED)
                .build()
        })
        .build()

    //Hand off your task to the system
    WorkManager.getInstance(context).enqueue(uploadWorkRequest)

}