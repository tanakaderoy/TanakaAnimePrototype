package com.tanaka.mazivanhanga.tanakaanimeprototype.views

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tanaka.mazivanhanga.tanakaanimeprototype.api.Repository
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.Episode
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.LatestShow
import com.tanaka.mazivanhanga.tanakaanimeprototype.models.SearchResult
import com.tanaka.mazivanhanga.tanakaanimeprototype.room.LatestShowEpisodeCacheMapper
import com.tanaka.mazivanhanga.tanakaanimeprototype.room.RoomModule
import com.tanaka.mazivanhanga.tanakaanimeprototype.util.DataState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * Created by Tanaka Mazivanhanga on 08/07/2020
 */
class LatestEpisodeViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private var _dataState: MutableLiveData<DataState<List<LatestShow>>> = MutableLiveData()
    var searchDataState:MutableLiveData<DataState<List<SearchResult>>> = MutableLiveData()
    var episodeDataState:MutableLiveData<DataState<List<Episode>>> = MutableLiveData()

    val dataState get() = _dataState

    fun getData(context: Context) {
        _dataState.value = DataState.Loading
        compositeDisposable.add(
            Repository.getInstance(context).getLatestShows().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _dataState.value = DataState.Success<List<LatestShow>>(it)
                }) { thro ->
                    println(thro)
                    RoomModule.getAnimeDao(RoomModule.getAnimeDb(context)).getLatestShows()
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe({

                            _dataState.value = DataState.Success(
                                LatestShowEpisodeCacheMapper().mapFromEntitiesList(it)
                            )
                        }) {
                            Log.e("ViewModeel", it.message, it)
                            _dataState.value = DataState.Error(it as Exception)

                        }
                })
    }
    fun searchForShow(query:String, context: Context){
        searchDataState.value = DataState.Loading
        compositeDisposable.add(
            Repository.getInstance(context).getSearchResults(query).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    searchDataState.value = DataState.Success(it)
                }){
                    searchDataState.value = DataState.Error(it as Exception)
                }
        )
    }

    fun deleteCacheEpisodes(context: Context){
        RoomModule.getAnimeDao(RoomModule.getAnimeDb(context)).deleteShows()
    }

    fun getEpisodes(show:String,context: Context){
        episodeDataState.value = DataState.Loading
        compositeDisposable.add(
            Repository.getInstance(context).getEpisodes(show).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    episodeDataState.value = DataState.Success(it)
                }){
                    episodeDataState.value = DataState.Error(it as Exception)
                }
        )
    }

    fun clearDisposable() {
        compositeDisposable.clear()
    }


}

