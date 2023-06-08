package com.example.driverchecker.media

import android.graphics.Bitmap
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.util.Log
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.ByteBuffer


/**
 * Created by Duc Ky Ngo on 9/15/2021.
 * duckyngo1705@gmail.com
 */
class FrameExtractor(private val listener: FrameExtractorInterface) {
    private var isTerminated = false

    private val size = 1
    private val frameCount = Int.MAX_VALUE
    private val SDK_VERSION_INT = android.os.Build.VERSION.SDK_INT

    private val verbose = false
    private var MAX_FRAMES = 0

    var isPortrait = false
    var savedFrameWidth = 0
    var savedFrameHeight = 0
    var buffer: ByteBuffer? = null

    /**
     * Terminate the process
     */
    fun terminate() {
        isTerminated = true
    }

    @Throws(IOException::class)
    fun extractFrames(inputFilePath: String) {
        var decoder: MediaCodec? = null
//        var outputSurface: CodecOutputSurface? = null
        var extractor: MediaExtractor? = null
        var width: Int
        var height: Int
        try {
            val inputFile: File = File(inputFilePath)

            // Check whether the input file exist or not
            if (!inputFile.canRead()) {
                throw FileNotFoundException("Unable to read $inputFile")
            }

            extractor = MediaExtractor()
            extractor.setDataSource(inputFile.toString())
            val trackIndex = selectTrack(extractor)
            if (trackIndex < 0) {
                throw IOException("No video track found in $inputFile")
            }
            extractor.selectTrack(trackIndex)

            // Checking orientation by degree
            val format = extractor.getTrackFormat(trackIndex)
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(inputFile.absolutePath)
            val orientation: Int = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?.toInt() ?: 90
            Log.d(TAG, "Orientation: $orientation")

            // Checking duration by milliseconds
            val duration = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            Log.d(TAG, "Video duration: $duration")

            // We must set MAX_FRAMES for decode
            val FPS = 60
            if (frameCount == Int.MAX_VALUE) {
                MAX_FRAMES = FPS * duration!!.toInt() / 1000
            } else {
                MAX_FRAMES = frameCount
            }
            height = format.getInteger(MediaFormat.KEY_HEIGHT)
            width = format.getInteger(MediaFormat.KEY_WIDTH)
            if (height > MAX_RESOLUTION || width > MAX_RESOLUTION) {
                val ratio = height.toFloat() / width
                if (height > width) {
                    height = MAX_RESOLUTION
                    width = (height / ratio).toInt()
                } else {
                    width = MAX_RESOLUTION
                    height = (ratio * width).toInt()
                }
            }
            Log.d(
                TAG,
                "$height  h : w  $width"
            )


            // Checking video orientation is portrait or landscape
            isPortrait = orientation == 90 || orientation == 270
            Log.d(TAG, "isPortrait:  $isPortrait")
            if (SDK_VERSION_INT >= 21) {
                if (isPortrait) {
                    savedFrameHeight = (if (width > height) width else height) / size
                    savedFrameWidth = (if (height < width) height else width) / size
                } else {
                    savedFrameHeight = height / size
                    savedFrameWidth = width / size
                }
            } else {
                savedFrameHeight = height / size
                savedFrameWidth = width / size
            }
            if (verbose) {
                Log.d(TAG, "Video size: " + format.getInteger(MediaFormat.KEY_WIDTH) + "x" + format.getInteger(MediaFormat.KEY_HEIGHT))
            }

            // Could use width/height from the MediaFormat to get full-size frames.
//            outputSurface = CodecOutputSurface(savedFrameWidth, savedFrameHeight, isPortrait)
            buffer = ByteBuffer.allocate(width * height * 4)

            // Create a MediaCodec decoder, and configure it with the MediaFormat from the
            // extractor.  It's very important to use the format from the extractor because
            // it contains a copy of the CSD-0/CSD-1 codec-specific data chunks.
            val mime = format.getString(MediaFormat.KEY_MIME)
            decoder = MediaCodec.createDecoderByType(mime!!)
            Log.d(TAG, "Mime :  $mime")
//            decoder.configure(format, outputSurface.getSurface(), null, 0)
            decoder.configure(format, null, null, 0)
            decoder.start()
//            doExtract(extractor, trackIndex, decoder, outputSurface)
            doExtract(extractor, trackIndex, decoder)
        } finally {
            // release everything we grabbed
//            if (outputSurface != null) {
//                outputSurface.release()
//                outputSurface = null
//            }
            if (decoder != null) {
                decoder.stop()
                decoder.release()
                decoder = null
            }
            if (extractor != null) {
                extractor.release()
                extractor = null
            }
        }
    }


    /**
     * Select video tracks
     * Return -1 if no track found
     */
    private fun selectTrack(extractor: MediaExtractor): Int {
        // Select the first video track we find, ignore the rest.
        val numTracks = extractor.trackCount
        for (idx in 0 until numTracks) {
            val format = extractor.getTrackFormat(idx)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime!!.startsWith("video/")) {
                if (verbose) {
                    Log.d(TAG, "Extractor selected track $idx ($mime): $format")
                }
                return idx
            }
        }
        return -1
    }

    /**
     * Work loop.
     */
    @Throws(IOException::class)
    fun doExtract(extractor: MediaExtractor, trackIndex: Int, decoder: MediaCodec) { //, outputSurface: CodecOutputSurface) {
        val TIMEOUT_USEC = 10000
        val decoderInputBuffers = decoder.inputBuffers
        val info = MediaCodec.BufferInfo()
        var inputChunk = 0
        var decodeCount = 0
        var totalSavingTimeNs: Long = 0
        var outputDone = false
        var inputDone = false
        var presentationTimeUs: Long = 0

        if (verbose) Log.d(TAG, "Start extract loop...")
        while (!outputDone && !isTerminated) {

            // Feed more data to the decoder.
            if (!inputDone) {
                val inputBufIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC.toLong())
                if (inputBufIndex >= 0) {
                    val inputBuf = decoderInputBuffers[inputBufIndex]
                    // Read the sample data into the ByteBuffer.  This neither respects nor
                    // updates inputBuf's position, limit, etc.
                    val chunkSize = extractor.readSampleData(inputBuf, 0)
                    if (chunkSize < 0) {
                        // End of stream -- send empty frame with EOS flag set.
                        decoder.queueInputBuffer(
                            inputBufIndex, 0, 0, 0L,
                            MediaCodec.BUFFER_FLAG_END_OF_STREAM
                        )
                        inputDone = true
                        if (verbose) Log.d(TAG, "sent input EOS")
                    } else {
                        if (extractor.sampleTrackIndex != trackIndex) {
                            Log.w(TAG, "WEIRD: got sample from track " + extractor.sampleTrackIndex + ", expected " + trackIndex)
                        }
                        presentationTimeUs = extractor.sampleTime
                        decoder.queueInputBuffer(
                            inputBufIndex, 0, chunkSize,
                            presentationTimeUs, 0 /*flags*/
                        )
                        if (verbose) {
                            Log.d(TAG, ("submitted frame $inputChunk to dec, size=$chunkSize at $presentationTimeUs"))
                        }
                        inputChunk++
                        extractor.advance()
                    }
                } else {
                    if (verbose) Log.d(TAG, "input buffer not available")
                }
            }
            if (!outputDone) {
                val decoderStatus = decoder.dequeueOutputBuffer(info, TIMEOUT_USEC.toLong())
                if (decoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                    // no output available yet
                    if (verbose) Log.d(TAG, "no output from decoder available")
                } else if (decoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                    // not important for us, since we're using Surface
                    if (verbose) Log.d(TAG, "decoder output buffers changed")
                } else if (decoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    val newFormat = decoder.outputFormat
                    if (verbose) Log.d(TAG, "decoder output format changed: $newFormat")
                } else if (decoderStatus < 0) {
//                    fail("unexpected result from decoder.dequeueOutputBuffer: " + decoderStatus);
                    Log.d(TAG, "doExtract: unexpected result from decoder.dequeueOutputBuffer: $decoderStatus")
                } else { // decoderStatus >= 0
                    if (verbose) Log.d(
                        TAG,
                        ("surface decoder given buffer " + decoderStatus +
                                " (size=" + info.size + ")")
                    )
                    if ((info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        if (verbose) Log.d(
                            TAG,
                            "output EOS"
                        )
                        outputDone = true
                    }
                    val doRender = (info.size != 0)

                    // As soon as we call releaseOutputBuffer, the buffer will be forwarded
                    // to SurfaceTexture to convert to a texture.  The API doesn't guarantee
                    // that the texture will be available before the call returns, so we
                    // need to wait for the onFrameAvailable callback to fire.
                    decoder.releaseOutputBuffer(decoderStatus, doRender)
                    if (doRender) {
                        if (verbose) Log.d(TAG, "Awaiting decode of frame $decodeCount")
//                        outputSurface.awaitNewImage()
//                        if (isPortrait) {
//                            outputSurface.drawImage(false)
//                        } else {
//                            outputSurface.drawImage(true)
//                        }
                        if (decodeCount < MAX_FRAMES) {
                            val startWhen = System.nanoTime()
                            val currentFrame = retrieveFrame(decodeCount, presentationTimeUs)
                            listener.onCurrentFrameExtracted(currentFrame)
                            totalSavingTimeNs += System.nanoTime() - startWhen
                            if(verbose) Log.d(TAG, "$decodeCount / Max: $MAX_FRAMES")
                        }
                        decodeCount++
                    }
                }
            }
        }
        val totalSavedFrames = if ((MAX_FRAMES < decodeCount)) MAX_FRAMES else decodeCount

        if (verbose) Log.d(
            TAG, ("Total saved frames: $totalSavedFrames  " +
                "| Total time: ${totalSavingTimeNs / 1000000} ms  " +
                "| Each frame took: ${(totalSavingTimeNs / totalSavedFrames / 1000)} us "))

        listener.onAllFrameExtracted(totalSavedFrames, totalSavingTimeNs / 1000000)
    }

    /**
     * Get the current frame by glReadPixels
     */
    @Throws(IOException::class)
    fun retrieveFrame(framePos: Int, timestamp: Long): Frame {
        // glReadPixels gives us a ByteBuffer filled with what is essentially big-endian RGBA
        // data (i.e. a byte of red, followed by a byte of green...).  To use the Bitmap
        // constructor that takes an int[] array with pixel data, we need an int[] filled
        // with little-endian ARGB data.
        //
        // If we implement this as a series of buf.get() calls, we can spend 2.5 seconds just
        // copying data around for a 720p frame.  It's better to do a bulk get() and then
        // rearrange the data in memory.  (For comparison, the PNG compress takes about 500ms
        // for a trivial frame.)
        //
        // So... we set the ByteBuffer to little-endian, which should turn the bulk IntBuffer
        // get() into a straight memcpy on most Android devices.  Our ints will hold ABGR data.
        // Swapping B and R gives us ARGB.  We need about 30ms for the bulk get(), and another
        // 270ms for the color swap.
        //
        // We can avoid the costly B/R swap here if we do it in the fragment shader (see
        // http://stackoverflow.com/questions/21634450/ ).
        //
        // Having said all that... it turns out that the Bitmap#copyPixelsFromBuffer()
        // method wants RGBA pixels, not ARGB, so if we create an empty bitmap and then
        // copy pixel data in we can avoid the swap issue entirely, and just copy straight
        // into the Bitmap from the ByteBuffer.
        //
        // Making this even more interesting is the upside-down nature of GL, which means
        // our output will look upside-down relative to what appears on screen if the
        // typical GL conventions are used.  (For ExtractMpegFrameTest, we avoid the issue
        // by inverting the frame when we render it.)
        //
        // Allocating large buffers is expensive, so we really want mPixelBuf to be
        // allocated ahead of time if possible.  We still get some allocations from the
        // Bitmap / PNG creation.
        buffer?.rewind()
        var rotation = 0
        var isFlipX = false
        if (isPortrait){
            rotation = 180
            isFlipX = true
        }

        val bitmap = Bitmap.createBitmap(savedFrameWidth, savedFrameHeight, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        return Frame(buffer!!, savedFrameWidth, savedFrameHeight, framePos, timestamp, rotation, isFlipX, false)
    }

    companion object {
        const val TAG = "FrameExtractor"
        const val MAX_RESOLUTION = 2000
    }
}