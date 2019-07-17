package com.nikeorever.architecturecomponent


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

/**
 * A simple [Fragment] subclass.
 *
 */
private const val TAG_BLANK = "BlankFragment"
class BlankFragment : Fragment(), MainCoroutineScope {

    init {
        bindMainScopeTo(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        withLaunchedMainScope {
            try {
                log(TAG_BLANK, "[AndroidMainScope] launched")
                delay(Long.MAX_VALUE)
            } catch (e: CancellationException) {
                log(TAG_BLANK, "[AndroidMainScope] cancelled")
            } finally {
                log(TAG_BLANK, "[AndroidMainScope] finally")
            }
        }
    }


}
