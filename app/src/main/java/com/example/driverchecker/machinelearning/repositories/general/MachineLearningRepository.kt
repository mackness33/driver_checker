package com.example.driverchecker.machinelearning.repositories.general

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.ClientStateListener
import com.example.driverchecker.machinelearning.helpers.listeners.GenericListener
import com.example.driverchecker.machinelearning.helpers.listeners.IGenericListener
import com.example.driverchecker.machinelearning.models.IMachineLearningModel
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow
import com.example.driverchecker.machinelearning.helpers.windows.MachineLearningWindow

open class MachineLearningRepository<I, O : WithConfidence, FR : WithConfidence> (importedModel: IMachineLearningModel<I, O>?) :
    AMachineLearningRepository<I, O, FR> () {
    override val window: IMachineLearningWindow<O> = MachineLearningWindow()
    override val model: IMachineLearningModel<I, O>? = importedModel
    override var clientListener: ClientStateListener? = ClientListener()
    override var modelListener: IGenericListener<Boolean>? = ModelListener()

    init {
        if (importedModel != null)
            modelListener = GenericListener(repositoryScope, importedModel.isLoaded)
    }
}