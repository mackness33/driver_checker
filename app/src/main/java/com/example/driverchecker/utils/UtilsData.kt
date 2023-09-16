package com.example.driverchecker.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.ColumnInfo
import kotlinx.coroutines.*

data class Settings (
    @ColumnInfo(name = "window_frames") override val windowFrames: Int,
    @ColumnInfo(name = "window_threshold") override val windowThreshold: Float,
    @ColumnInfo(name = "model_threshold") override val modelThreshold: Float
) : ISettings {
    constructor(copy: ISettings?) : this (
        copy?.windowFrames ?: 0,
        copy?.windowThreshold ?: 0.0f,
        copy?.modelThreshold ?: 0.0f
    )

    constructor() : this (0, 0.0f, 0.0f)
}

interface ISettings {
    val windowFrames: Int
    val windowThreshold: Float
    val modelThreshold: Float
}

class SettingsException(override val message: String?, override val cause: Throwable?) : Throwable(message, cause)

sealed interface IPage

sealed class Page : IPage {
    object Camera : Page()
    object Result : Page()
}

/* DATA */
interface Data<T> {
    val value: T
}

interface ObservableData<T> : Data<T> {
    val liveData: LiveData<T>

    fun observe(owner: LifecycleOwner, block: (T) -> Unit): Observer<T>
    fun observeForever(block: (T) -> Unit): Observer<T>
}

interface MutableData<T> : Data<T> {
    fun tryApply (next: T) : Boolean
    suspend fun apply (next: T)
}

interface MutableObservableData<T> : MutableData<T>, ObservableData<T> {
    fun postValue(next: T)
}

interface AtomicData<T> : MutableData<T> {
    fun tryUpdate (next: T)
    suspend fun update (next: T)
}

interface AtomicObservableData<T> : AtomicData<T>, MutableObservableData<T>

interface CompletableData<T> : ObservableData<T> {
    suspend fun await ()

    fun deferredAwait ()

    fun isCompleted () : Boolean

    fun cancel (cause: CancellationException)

    fun complete (next: T) : Boolean
}

interface MutableCompletableData<T> : CompletableData<T> {
    fun reset ()
}