package com.example.driverchecker

import java.util.concurrent.Callable

interface MLTaskInterface <Model, Data, Result> : Callable<Result> {
    val mlModel: Model
    abstract fun processAndOEvaluate(input: Data): Result?
}