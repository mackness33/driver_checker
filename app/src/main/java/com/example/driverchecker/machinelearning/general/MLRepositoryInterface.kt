package com.example.driverchecker.machinelearning.general

import kotlinx.coroutines.flow.Flow

interface MLRepositoryInterface<in Data, out Result> {
    suspend fun instantClassification (input: Data): Result?
    suspend fun continuousClassification (input: List<Data>): Result?
    suspend fun continuousClassification (input: Flow<Data>): Result?

//    fun liveClassification (input: Flow<Data>): Result?
}