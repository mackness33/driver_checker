package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.collections.ClassificationItemMutableList


// ---------------------------------- CLASSES ----------------------------------

// with classification
interface WithClassification<S> {
    val classification: IClassification<S>
}

// with supergroup
interface WithSupergroup<S> {
    val supergroup: S
}

// with supergroup
interface WithGroups<S> {
    val groups: Map<S, Set<IClassificationWithMetrics<S>>>
}



// ---------------------------------- BASIC OUTPUT ----------------------------------
