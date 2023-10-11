package com.example.driverchecker.machinelearning.data

// ---------------------------------- MACHINE LEARNING ----------------------------------
interface WithConfidence {
    val confidence: Float
}

interface WithWindowData {
    val data: Map<IWindowBasicData, IAdditionalMetrics?>
}


// ---------------------------------- CLASSIFICATION ----------------------------------
interface WithClassification<S> {
    val classification: IClassification<S>
}

interface WithSupergroup<S> {
    val supergroup: S
}

interface WithGroups<S> {
    val groups: Map<S, Set<IClassificationWithMetrics<S>>>
}

interface WithGroupsData<S> : WithWindowData {
    override val data: Map<IWindowBasicData, IGroupMetrics<S>?>
}