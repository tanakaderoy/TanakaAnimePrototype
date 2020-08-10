package com.tanaka.mazivanhanga.tanakaanimeprototype.views

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tanaka.mazivanhanga.tanakaanimeprototype.api.Repository
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.VideoModel
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.VideoRequestBody
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by Tanaka Mazivanhanga on 08/07/2020
 */
class VideoViewModel : ViewModel() {
    private var _videoLiveData: MutableLiveData<VideoModel> = MutableLiveData()
    val videLiveData get() = _videoLiveData
    private val compositeDisposable = CompositeDisposable()

    fun getData(body: VideoRequestBody, context:Context) {
        compositeDisposable.add(
            Repository.getInstance(context).getVideo(body).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _videoLiveData.value = it
                }) {
                    println(it)
                })
    }
}