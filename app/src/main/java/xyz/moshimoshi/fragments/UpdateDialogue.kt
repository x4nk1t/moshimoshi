package xyz.moshimoshi.fragments

import android.app.Dialog
import android.app.DownloadManager
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import xyz.moshimoshi.utils.updater.ReleaseModel

class UpdateDialogue(private val releaseData: ReleaseModel): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)

            builder.setMessage("New update found! Update now?")
                .setPositiveButton("Download",
                    DialogInterface.OnClickListener { _, _ ->
                        val downloadUrl = releaseData.apkUrl
                        val apkName = "${releaseData.apkName}"

                        val downloadManager: DownloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                        downloadManager.enqueue(
                            DownloadManager.Request(Uri.parse(downloadUrl))
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkName)
                                .setTitle(apkName)
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        )
                        Toast.makeText(requireContext(),"Install the apk from the notification or download folder!", Toast.LENGTH_SHORT).show()
                    })
                .setNegativeButton("Later",
                    DialogInterface.OnClickListener { _, _ ->
                        return@OnClickListener
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}