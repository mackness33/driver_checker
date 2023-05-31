package com.example.driverchecker.machinelearning.data

data class MLResult<R> (val result: R, val metrics: MLMetrics? = null)