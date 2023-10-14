package com.example.driverchecker.machinelearning.windows.helpers

class NonExistentWindowFactoryException(override val message: String?, override val cause: Throwable?) : Throwable(message, cause)
class FailedParameterCastFactoryException(override val message: String?, override val cause: Throwable?) : Throwable(message, cause)