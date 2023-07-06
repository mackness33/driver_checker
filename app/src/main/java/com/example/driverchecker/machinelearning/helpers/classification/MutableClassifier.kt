package com.example.driverchecker.machinelearning.helpers.classification

import android.util.Log
import com.example.driverchecker.machinelearning.data.*

open class MutableClassifier<Superclass : Comparable<Superclass>> : IMutableClassifier<Superclass> {
    protected val _superclasses: MutableMap<Superclass, MutableSet<IClassification<Superclass>>>

    constructor (newDataset: ClassificationSuperclassMap<Superclass>?) {
        _superclasses = mutableMapOf()

        initClassifier(newDataset)
    }

    constructor (newClassifier: IClassifier<Superclass>) {
        _superclasses = mutableMapOf()

        initClassifier(newClassifier.superclasses)
    }

    private fun initClassifier (newDataset: ClassificationSuperclassMap<Superclass>?) = load(newDataset)
    private fun initClassifier (importedJson: ImportClassifier<Superclass>?) = load(importedJson)

    override val superclasses : ClassificationSuperclassMap<Superclass>
        get() = _superclasses


    override fun load(newDataset: ClassificationSuperclassMap<Superclass>?) : Boolean {
        if (newDataset != null) {
            for ((group, set) in newDataset.entries)
                _superclasses[group] = set.toMutableSet()

            return true
        }

        return false
    }

    override fun load(importedJson: ImportClassifier<Superclass>?) : Boolean {
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

    override fun add(name: String, group: Superclass) {
        if (!exist(name)) {
            _superclasses.putIfAbsent(group, mutableSetOf())
            _superclasses[group]!!.add(Classification(name, size(), group))
        }
    }

    override fun append(name: String, group: Superclass) {
        if (!exist(name) && _superclasses.contains(group)) {
            _superclasses[group]!!.add(Classification(name, size(), group))
        }
    }

    override fun put(newSuperclass: ClassificationSet<Superclass>, group: Superclass) {
        _superclasses[group] = newSuperclass.toMutableSet()
    }

    override fun putIfAbsent(newSuperclass: ClassificationSet<Superclass>, group: Superclass) {
        _superclasses.putIfAbsent(group, newSuperclass.toMutableSet())
    }

    override fun asList(outerComparator: Comparator<ClassificationSet<Superclass>>?, innerComparator: Comparator<IClassification<Superclass>>?): ClassificationList<Superclass> {
        // assign default comparator if null passed
        val outer = outerComparator ?: Comparator { _, _ -> 0}
        val inner = innerComparator ?: Comparator { _, _ -> 0}

        val output = mutableListOf<IClassification<Superclass>>()

        for (outerSet in _superclasses.values.toList().sortedWith(outer)) {
            output.plusAssign(outerSet.sortedWith(inner))
        }

        return output
    }

    override fun asSortedList(listComparator: Comparator<IClassification<Superclass>>?): ClassificationList<Superclass> {
        // default sort is by index
        val comparator = listComparator ?: Comparator { o1, o2 -> o2.index.compareTo(o1.index)}
        val output = mutableListOf<IClassification<Superclass>>()

        _superclasses.values.toList().forEach { outerSet -> output.plusAssign(outerSet) }

        return output.sortedWith(comparator)
    }

    override fun asUnsortedList(): ClassificationList<Superclass> {
        val output = mutableListOf<IClassification<Superclass>>()

        _superclasses.values.toList().forEach { outerSet -> output.plusAssign(outerSet) }

        return output
    }

    override fun clear(group: Superclass?) {
        if (group == null) {
            _superclasses.clear()
        } else {
            _superclasses.remove(group)
        }
    }

    override fun remove(name: String) : Boolean {
        for ((group, set) in _superclasses.entries)
            if (set.removeIf { element -> element.name == name } && _superclasses[group].isNullOrEmpty())
                return true

        return false
    }

    override fun remove(index: Int)  : Boolean {
        for ((group, set) in _superclasses.entries)
            if (set.removeIf { element -> element.index == index })
                return true


        return false
    }

    override fun delete(name: String) : Boolean {
        for ((group, set) in _superclasses.entries) {
            if (set.removeIf { element -> element.name == name }) {
                if (_superclasses[group].isNullOrEmpty()) {
                    _superclasses.remove(group)
                }

                return true
            }
        }

        return false
    }

    override fun delete(index: Int) : Boolean {
        for ((group, set) in _superclasses.entries) {
            if (set.removeIf { element -> element.index == index }) {
                if (_superclasses[group].isNullOrEmpty()) {
                    _superclasses.remove(group)
                }

                return true
            }
        }

        return false
    }

    override fun get(name: String): IClassification<Superclass>? {
        for (set in _superclasses.values) {
            val partial = set.find { classification -> classification.name == name }

            if (partial != null)
                return partial
        }

        return null
    }

    override fun get(index: Int): IClassification<Superclass>? {
        for (set in _superclasses.values) {
            val partial = set.find { classification -> classification.index == index }

            if (partial != null)
                return partial
        }

        return null
    }

    override fun get(classification: IClassification<Superclass>): IClassification<Superclass>? {
        for (set in _superclasses.values) {
            val partial = set.find { element -> element == classification }

            if (partial != null)
                return partial
        }

        return null
    }

    override fun exist(name: String): Boolean {
        return _superclasses.values.find { set -> set.any { classification -> classification.name == name } } != null
    }

    override fun exist(index: Int): Boolean {
        return _superclasses.values.find { set -> set.any { classification -> classification.index == index } } != null
    }

    override fun exist(classification: IClassification<Superclass>): Boolean {
        return _superclasses.values.find { supergroup -> supergroup.contains(classification) } != null
    }

    override fun getSuperclass(group: Superclass): ClassificationSet<Superclass>? {
        return _superclasses[group]
    }

    override fun size(): Int {
        return _superclasses.values.fold(0) { size, set -> size + set.size }
    }

    override fun sizeSuperClass(): Int {
        return _superclasses.size
    }

    override fun maxClassesInGroup(): Int = _superclasses.values.maxOf { set -> set.size }
}