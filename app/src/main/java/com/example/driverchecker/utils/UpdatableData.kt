package com.example.driverchecker.utils

open class UpdatableData<T> (initialData: T) : MutableData<T> {
    override var value: T = initialData
        protected set

    override fun tryApply(next: T) : Boolean {
        value = next
        return true
    }

    override suspend fun apply(next: T) {
        tryApply(next)
    }
}