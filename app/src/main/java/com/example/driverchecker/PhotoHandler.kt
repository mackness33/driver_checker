import android.content.Context
import android.hardware.Camera
import android.hardware.Camera.PictureCallback
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.example.driverchecker.MainActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PhotoHandler(private val context: Context) : PictureCallback {
    override fun onPictureTaken(data: ByteArray, camera: Camera) {
        val pictureFileDir = dir
        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
            Log.d(MainActivity.DEBUG_TAG, "Can't create directory to save image.")
            Toast.makeText(
                context, "Can't create directory to save image.",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val dateFormat = SimpleDateFormat("yyyymmddhhmmss")
        val date = dateFormat.format(Date())
        val photoFile = "Picture_$date.jpg"
        val filename = pictureFileDir.path + File.separator + photoFile
        val pictureFile = File(filename)
        try {
            val fos = FileOutputStream(pictureFile)
            fos.write(data)
            fos.close()
            Toast.makeText(
                context, "New Image saved:$photoFile",
                Toast.LENGTH_LONG
            ).show()
        } catch (error: Exception) {
            Log.d(
                MainActivity.DEBUG_TAG, "File" + filename + "not saved: "
                        + error.message
            )
            Toast.makeText(
                context, "Image could not be saved.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private val dir: File
        private get() {
            val sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            return File(sdDir, "CameraAPIDemo")
        }
}