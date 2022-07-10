package gini.ohadsa.day35.ui.main


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class PageViewModel : ViewModel() {

    private val _index = MutableSharedFlow<Int>(replay = 1)
    val text: Flow<String> =_index.map {
        "hello world from tub $it"
    }

    fun setIndex(index: Int) {
        _index.tryEmit(index)
    }
}