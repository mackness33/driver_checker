package com.example.driverchecker.machinelearning.repositories.general

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.models.IClassificationModel
import com.example.driverchecker.machinelearning.models.pytorch.YOLOModel
import com.example.driverchecker.machinelearning.repositories.IMachineLearningFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

abstract class ClassificationFactoryRepository<D, R : WithConfidence, S>
    : MachineLearningFactoryRepository<D, R> {

    constructor() : super()

    constructor(modelName: String, modelInit: Map<String, Any?>) : super(modelName, modelInit)

    override fun jobClassification (input: Flow<D>, scope: CoroutineScope): Job {
        return repositoryScope.launch(Dispatchers.Default) {
            // check if the repo is ready to make evaluations
            if (_externalProgressState.replayCache.last() == LiveEvaluationState.Ready(true)) {
                _externalProgressState.emit(LiveClassificationState.Start((model as IClassificationModel<*, *, *>).classifier.maxClassesInGroup()))

                flowClassification(input, ::cancel)?.collect()
            } else {
                _externalProgressState.emit(LiveEvaluationState.End(Throwable("The stream is not ready yet"), null))
                _externalProgressState.emit(LiveEvaluationState.Ready(model?.isLoaded?.value ?: false))
            }
        }
    }
}