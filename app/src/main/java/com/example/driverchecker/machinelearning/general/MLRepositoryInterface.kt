package com.example.driverchecker.machinelearning.general

interface MLRepositoryInterface<Data, Result> {
    suspend fun instantClassification (input: Data): Result?
    suspend fun continuousClassification (input: List<Data>): Result?
//    fun liveClassification (input: Flow<Data>): Result?
}