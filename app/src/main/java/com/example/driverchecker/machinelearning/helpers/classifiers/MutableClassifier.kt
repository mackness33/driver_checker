package com.example.driverchecker.machinelearning.helpers.classifiers

import android.util.Log
import com.example.driverchecker.machinelearning.data.*

open class MutableClassifier<S : Comparable<S>> : IMutableClassifier<S> {
    protected val mSupergroups: MutableMap<S, MutableSet<IClassification<S>>>

    constructor (newDataset: Map<S, Set<IClassification<S>>>?) {
        mSupergroups = mutableMapOf()

        initClassifier(newDataset)
    }

    constructor (newClassifier: IClassifier<S>) {
        mSupergroups = mutableMapOf()

        initClassifier(newClassifier.supergroups)
    }

    private fun initClassifier (newDataset: Map<S, Set<IClassification<S>>>?) = load(newDataset)
    private fun initClassifier (importedJson: ImportClassifier<S>?) = load(importedJson)

    override val supergroups : Map<S, Set<IClassification<S>>>
        get() = mSupergroups


    override fun load(newDataset: Map<S, Set<IClassification<S>>>?) : Boolean {
        if (newDataset != null) {
            for ((group, set) in newDataset.entries)
                mSupergroups[group] = set.toMutableSet()

            return true
        }

        return false
    }

    override fun load(importedJson: ImportClassifier<S>?) : Boolean {
        if (importedJson != null) {
            try {
                for ((group, set) in importedJson.value.entries)
                    for (name in set)
                        add(name, group)
            } catch (e: Throwable) {
                Log.e("ImportJsonToClassifier", e.message.toString())

                return false
            }


            return true
        }

        return false
    }

    override fun add(name: String, group: S) {
        if (!exist(name)) {
            mSupergroups.putIfAbsent(group, mutableSetOf())
            mSupergroups[group]!!.add(Classification(name, size(), mSupergroups[group]!!.size, group))
        }
    }

    override fun append(name: String, group: S) {
        if (!exist(name) && mSupergroups.contains(group)) {
            mSupergroups[group]!!.add(Classification(name, size(), mSupergroups[group]!!.size, group))
        }
    }

    override fun put(newSuperclass: Set<IClassification<S>>, group: S) {
        mSupergroups[group] = newSuperclass.toMutableSet()
    }

    override fun putIfAbsent(newSuperclass: Set<IClassification<S>>, group: S) {
        mSupergroups.putIfAbsent(group, newSuperclass.toMutableSet())
    }

    override fun asList(outerComparator: Comparator<Set<IClassification<S>>>?, innerComparator: Comparator<IClassification<S>>?): List<IClassification<S>> {
        // assign default comparator if null passed
        val outer = outerComparator ?: Comparator { _, _ -> 0}
        val inner = innerComparator ?: Comparator { _, _ -> 0}

        val output = mutableListOf<IClassification<S>>()

        for (outerSet in mSupergroups.values.toList().sortedWith(outer)) {
            output.plusAssign(outerSet.sortedWith(inner))
        }

        return output
    }

    override fun asSortedList(listComparator: Comparator<IClassification<S>>?): List<IClassification<S>> {
        // default sort is by index
        val comparator = listComparator ?: Comparator { o1, o2 -> o2.externalIndex.compareTo(o1.externalIndex)}
        val output = mutableListOf<IClassification<S>>()

        mSupergroups.values.toList().forEach { outerSet -> output.plusAssign(outerSet) }

        return output.sortedWith(comparator)
    }

    override fun asUnsortedList(): List<IClassification<S>> {
        val output = mutableListOf<IClassification<S>>()

        mSupergroups.values.toList().forEach { outerSet -> output.plusAssign(outerSet) }

        return output
    }

    override fun clear(group: S?) {
        if (group == null) {
            mSupergroups.clear()
        } else {
            mSupergroups.remove(group)
        }
    }

    override fun remove(name: String) : Boolean {
        for ((group, set) in mSupergroups.entries)
            if (set.removeIf { element -> element.name == name } && mSupergroups[group].isNullOrEmpty())
                return true

        return false
    }

    override fun remove(index: Int)  : Boolean {
        for ((group, set) in mSupergroups.entries)
            if (set.removeIf { element -> element.externalIndex == index })
                return true


        return false
    }

    override fun delete(name: String) : Boolean {
        for ((group, set) in mSupergroups.entries) {
            if (set.removeIf { element -> element.name == name }) {
                if (mSupergroups[group].isNullOrEmpty()) {
                    mSupergroups.remove(group)
                }

                return true
            }
        }

        return false
    }

    override fun delete(index: Int) : Boolean {
        for ((group, set) in mSupergroups.entries) {
            if (set.removeIf { element -> element.externalIndex == index }) {
                if (mSupergroups[group].isNullOrEmpty()) {
                    mSupergroups.remove(group)
                }

                return true
            }
        }

        return false
    }

    override fun get(name: String): IClassification<S>? {
        for (set in mSupergroups.values) {
            val partial = set.find { classification -> classification.name == name }

            if (partial != null)
                return partial
        }

        return null
    }

    override fun get(index: Int): IClassification<S>? {
        for (set in mSupergroups.values) {
            val partial = set.find { classification -> classification.externalIndex == index }

            if (partial != null)
                return partial
        }

        return null
    }

    override fun get(index: Int, supergroup: S): IClassification<S>? {
        return mSupergroups[supergroup]?.toList()?.get(index)
    }

    override fun get(classification: IClassification<S>): IClassification<S>? {
        for (set in mSupergroups.values) {
            val partial = set.find { element -> element == classification }

            if (partial != null)
                return partial
        }

        return null
    }

    override fun exist(name: String): Boolean {
        return mSupergroups.values.find { set -> set.any { classification -> classification.name == name } } != null
    }

    override fun exist(index: Int): Boolean {
        return mSupergroups.values.find { set -> set.any { classification -> classification.externalIndex == index } } != null
    }

    override fun exists(index: Int, supergroup: S): Boolean {
        return mSupergroups[supergroup]?.toList()?.get(index) != null
    }

    override fun exist(classification: IClassification<S>): Boolean {
        return mSupergroups.values.find { supergroup -> supergroup.contains(classification) } != null
    }

    override fun getSuperclass(group: S): Set<IClassification<S>>? {
        return mSupergroups[group]
    }

    override fun size(): Int {
        return mSupergroups.values.fold(0) { size, set -> size + set.size }
    }

    override fun sizeSuperClass(): Int {
        return mSupergroups.size
    }

    override fun maxClassesInGroup(): Int = mSupergroups.values.maxOf { set -> set.size }
}