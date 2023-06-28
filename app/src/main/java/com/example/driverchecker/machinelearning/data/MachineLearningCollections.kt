package com.example.driverchecker.machinelearning.data

class MachineLearningCollections {
}

interface WithConfidence {
    val confidence: Float
}

interface MachineLearningResultList<Result> : List<Result>,  WithConfidence

open class MachineLearningResultArrayList<Result : WithConfidence> : ArrayList<Result>, MachineLearningResultList<Result> {
    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor(collection: Collection<Result>) : super(collection)
    constructor() : super(10)

    override var confidence: Float = this.fold(0.0f) { acc, next -> acc + next.confidence } / this.size
        protected set
}

//abstract class MachineLearningArray<Result> : Array<IMachineLearningResult<Result>> {
//
//}1