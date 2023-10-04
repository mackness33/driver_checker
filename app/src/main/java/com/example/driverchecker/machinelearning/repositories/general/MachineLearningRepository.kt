package com.example.driverchecker.machinelearning.repositories.general

//open class MachineLearningRepository<I, O : IMachineLearningOutputStats, FR : IOldMachineLearningFinalResult> (importedModel: IMachineLearningModel<I, O>?, repositoryScope: CoroutineScope) :
//    AMachineLearningRepository<I, O, FR> (repositoryScope) {
//    override val window: IMachineLearningWindow<O> = MachineLearningWindow(type = "MachineLearningWindow")
//    override val model: IMachineLearningModel<I, O>? = importedModel
//    override var clientListener: ClientStateListener? = ClientListener()
//    override var modelListener: IGenericListener<Boolean>? = ModelListener()
//
//    init {
//        if (importedModel != null)
//            modelListener = GenericListener(repositoryScope, importedModel.isLoaded)
//    }
//
//    override val collectionOfWindows: MachineLearningWindowsMutableCollection<O>
//        get() = TODO("Not yet implemented")
//}