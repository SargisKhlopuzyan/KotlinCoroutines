package com.example.kotlincoroutines


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.kotlincoroutines.model.Tutorial
import com.example.kotlincoroutines.utils.SnowFilter
import kotlinx.android.synthetic.main.fragment_tutorial.*
import kotlinx.coroutines.*
import java.net.URL

/**
 * A simple [Fragment] subclass.
 */
class TutorialFragment : Fragment() {

    // await - սպասել
    // suspend - հետաձգել
    // Deferred - Հետաձգված
    // Dispatchers - Դիսպետչերներ, առաքիչներ

    companion object {

        const val TUTORIAL_KEY = "TUTORIAL"

        fun newInstance(tutorial: Tutorial): TutorialFragment {
            val fragmentHome = TutorialFragment()
            val args = Bundle()
            args.putParcelable(TUTORIAL_KEY, tutorial)
            fragmentHome.arguments = args
            return fragmentHome
        }
    }

    private val coroutineExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            coroutineScope.launch(Dispatchers.Main) {
                errorMessage.visibility = View.VISIBLE
                errorMessage.text = getString(R.string.error_message)
            }
            GlobalScope.launch {
                println("Caught $throwable")
            }
        }

    private val parentJob = Job()
    private val coroutineScope =
        CoroutineScope(Dispatchers.Main + parentJob + coroutineExceptionHandler)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val tutorial = arguments?.getParcelable<Tutorial>(
            TUTORIAL_KEY
        )
        val view = inflater.inflate(R.layout.fragment_tutorial, container, false)
        view.findViewById<TextView>(R.id.tutorialName).text = tutorial?.name
        view.findViewById<TextView>(R.id.tutorialDesc).text = tutorial?.description
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tutorial = arguments?.getParcelable<Tutorial>(
            TUTORIAL_KEY
        )

        coroutineScope.launch(Dispatchers.Main) {
            val originalBitmap = getOriginalBitmapAsync(tutorial!!).await()
            val snowFilterBitmap = loadSnowFilterAsync(originalBitmap).await()
            loadImage(snowFilterBitmap)
        }
    }

    private fun loadImage(snowFilterBitmap: Bitmap) {
        progressBar?.visibility = View.GONE
        snowFilterImage?.setImageBitmap(snowFilterBitmap)
    }

    override fun onDestroy() {
        super.onDestroy()
        parentJob.cancel()
    }

    /**
    1) Creates a regular function, getOriginalBitmapAsync(), which returns a Deferred Bitmap value.
    This emphasizes that the result may not be immediately available.

    2) Use the async() to create a coroutine in an input-output optimized Dispatcher.
    This will offload work from the main thread, to avoid freezing the UI.

    3) Opens a stream from the image’s URL and uses it to create a Bitmap, finally returning it.
     */
    // 1
    private fun getOriginalBitmapAsync(tutorial: Tutorial): Deferred<Bitmap> =
        // 2
        coroutineScope.async(Dispatchers.IO) {
            // 3
            Log.e("LOG_TAG", "coroutineScope.async(Dispatchers.IO)")
            URL(tutorial.url).openStream().use {
                return@async BitmapFactory.decodeStream(it)
            }
        }

    private fun loadSnowFilterAsync(originalBitmap: Bitmap): Deferred<Bitmap> =
        coroutineScope.async(Dispatchers.Default) {
            SnowFilter.applySnowEffect(originalBitmap)
        }


    /**
     * - NOTE -
     * Right now, we are returning Deferreds. But we want the results when they become available.
     * We’ll have to use await(), a suspending function, on the Deferreds, which will give us the result when it’s available.
     * In our case – a Bitmap.
     * */
}
