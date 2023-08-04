package com.example.driverchecker.machinelearning.repositories.general

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.models.IMachineLearningModel
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow
import com.example.driverchecker.machinelearning.helpers.windows.MachineLearningWindow

open class MachineLearningRepository<D, R : WithConfidence> (importedModel: IMachineLearningModel<D, R>?) :
    AMachineLearningRepository<D, R> () {
    override val window: IMachineLearningWindow<R> = MachineLearningWindow()
    override val model: IMachineLearningModel<D, R>? = importedModel
}