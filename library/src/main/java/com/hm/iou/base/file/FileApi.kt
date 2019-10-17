@file:JvmName("FileApi")

package com.hm.iou.base.file


import com.hm.iou.network.HttpReqManager
import com.hm.iou.network.interceptor.file.ProgressListener
import com.hm.iou.sharedata.model.BaseResponse
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by hjy on 2018/5/24.
 */

object FileApi {

    /**
     * 上传文件
     *
     * @param file
     * @param bizType 文件所属业务类型
     * @return
     */
    fun uploadFile(file: File, bizType: FileBizType): Flowable<BaseResponse<FileUploadResult>> {
        val map = mutableMapOf<String, Int>()
        map["bizType"] = bizType.getType()
        map["fileType"] = 0
        return upload(file, map)
    }

    /**
     * 上传文件/图片
     *
     * @param file
     * @param map
     * @return
     */
    fun upload(file: File, map: Map<String, Int>): Flowable<BaseResponse<FileUploadResult>> {
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val partFile = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return HttpReqManager.getInstance().getService(FileService::class.java)
                .upload(partFile, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 上传图片
     *
     * @param file
     * @param bizType 图片所属业务类型
     * @return
     */
    fun uploadImage(file: File, bizType: FileBizType): Flowable<BaseResponse<FileUploadResult>> {
        val map = mutableMapOf<String, Int>()
        map["bizType"] = bizType.getType()
        map["fileType"] = 1
        return upload(file, map)
    }

    //通过协程来上传
    suspend fun uploadByCoroutine(file: File, map: Map<String, Int>): BaseResponse<FileUploadResult> {
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val partFile = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return HttpReqManager.getInstance().getService(FileService::class.java).uploadFile(partFile, map)
    }

    //通过协程来上传图片
    suspend fun uploadImageByCoroutine(file: File, bizType: FileBizType): BaseResponse<FileUploadResult> {
        val map = mutableMapOf<String, Int>()
        map["bizType"] = bizType.getType()
        map["fileType"] = 1
        return uploadByCoroutine(file, map)
    }

    //通过协程来上传普通文件
    suspend fun uploadFileByCoroutine(file: File, bizType: FileBizType): BaseResponse<FileUploadResult> {
        val map = mutableMapOf<String, Int>()
        map["bizType"] = bizType.getType()
        map["fileType"] = 0
        return uploadByCoroutine(file, map)
    }

    /**
     * 下载文件
     *
     * @param url              文件地址
     * @param destDir          目标文件夹
     * @param fileName         文件名称
     * @param progressListener 下载进度监听对象
     * @return
     */
    fun downLoadFile(url: String, destDir: String, fileName: String, progressListener: ProgressListener): Flowable<File> {
        return HttpReqManager.getInstance().getService(FileService::class.java, progressListener)
                .downLoadFile(url)
                .subscribeOn(Schedulers.io())//subscribeOn和ObserOn必须在io线程，如果在主线程会出错
                .observeOn(Schedulers.io())
                .observeOn(Schedulers.computation())//需要
                .map { responseBody -> saveFile(responseBody, destDir, fileName) }
                .observeOn(AndroidSchedulers.mainThread())
    }

    /**
     * 保存File
     *
     * @param response
     * @param destFileDir
     * @param destFileName
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    fun saveFile(response: ResponseBody, destFileDir: String, destFileName: String): File {
        var inputStream: InputStream? = null
        val buf = ByteArray(2048)
        var fos: FileOutputStream? = null

        try {
            inputStream = response.byteStream()
            var currentSize = 0L
            val dir = File(destFileDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            val file = File(dir, destFileName)
            fos = FileOutputStream(file)

            var len1: Int = inputStream.read(buf)
            while (len1 != -1) {
                currentSize += len1.toLong()
                fos.write(buf, 0, len1)
                fos.flush()
                len1 = inputStream.read(buf)
            }
            return file
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}