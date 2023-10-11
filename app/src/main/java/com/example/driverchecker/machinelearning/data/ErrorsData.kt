package com.example.driverchecker.machinelearning.data

import kotlin.coroutines.cancellation.CancellationException

// ---------------------------------- MACHINE LEARNING ----------------------------------
class ExternalCancellationException : CancellationException ()
class InternalCancellationException : CancellationException ()
class CorrectCancellationException : CancellationException ()