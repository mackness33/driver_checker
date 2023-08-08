package com.example.driverchecker.machinelearning.repositories.general

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.models.IMachineLearningModel
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow
import com.example.driverchecker.machinelearning.helpers.windows.MachineLearningWindow

open class MachineLearningRepository<I, O : WithConfidence, FR : WithConfidence> (importedModel: IMachineLearningModel<I, O>?) :
    AMachineLearningRepository<I, O, FR> () {
    override val window: IMachineLearningWindow<O> = MachineLearningWindow()
    override val model: IMachineLearningModel<I, O>? = importedModel
}