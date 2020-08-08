package com.tanaka.mazivanhanga.tanakaanimeprototype.views

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tanaka.mazivanhanga.tanakaanimeprototype.api.Repository
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.LatestShow
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by Tanaka Mazivanhanga on 08/07/2020
 */
class LatestEpisodeViewModel : ViewModel() {
    private var _latestepisodesLiveData: MutableLiveData<List<LatestShow>> = MutableLiveData()
    val latestEpisodesLiveData get() = _latestepisodesLiveData
    private val compositeDisposable = CompositeDisposable()

    fun getData() {
        compositeDisposable.add(
            Repository.getLatestShows().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _latestepisodesLiveData.value = it
                }) {
                    println(it)
                })
    }

    fun clearDisposable() {
        compositeDisposable.clear()
    }


}

