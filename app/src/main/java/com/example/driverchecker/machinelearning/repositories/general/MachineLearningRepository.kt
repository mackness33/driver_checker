package com.example.driverchecker.machinelearning.repositories.general

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.ClientStateListener
import com.example.driverchecker.machinelearning.helpers.listeners.GenericListener
import com.example.driverchecker.machinelearning.helpers.listeners.IGenericListener
import com.example.driverchecker.machinelearning.models.IMachineLearningModel
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow
import com.example.driverchecker.machinelearning.helpers.windows.MachineLearningWindow
import kotlinx.coroutines.CoroutineScope

open class MachineLearningRepository<I, O : IMachineLearningOutputStats, FR : IMachineLearningFinalResult> (importedModel: IMachineLearningModel<I, O>?, repositoryScope: CoroutineScope) :
    AMachineLearningRepository<I, O, FR> (repositoryScope) {
    override val window: IMachineLearningWindow<O> = MachineLearningWindow(type = "MachineLearningWindow")
    override val model: IMachineLearningModel<I, O>? = importedModel
    override var clientListener: ClientStateListener? = ClientListener()
    override var modelListener: IGenericListener<Boolean>? = ModelListener()

    init {
        if (importedModel != null)
            modelListener = GenericListener(repositoryScope, importedModel.isLoaded)
    }
}