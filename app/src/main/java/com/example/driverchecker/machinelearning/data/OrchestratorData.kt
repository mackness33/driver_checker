package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.general.IMachineLearningModel
import com.example.driverchecker.machinelearning.pytorch.YOLOModel
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass


interface IModelEntity {
    val name: String
}

@Serializable
data class ModelEntity (
    override val name: String,
//    val ciao: KClass<YOLOModel> = YOLOModel::class
) : IModelEntity