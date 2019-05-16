package com.maxciv.infer.plugin.process

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.util.net.HttpConfigurable
import com.maxciv.infer.plugin.InferProjectComponent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.apache.commons.io.FileUtils
import org.kohsuke.github.GHAsset
import org.kohsuke.github.GitHub
import org.kohsuke.github.GitHubBuilder
import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.ArchiverFactory
import org.rauschig.jarchivelib.CompressionType
import java.io.File
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URL
import java.util.concurrent.atomic.AtomicInteger


/**
 * @author maxim.oleynik
 * @since 16.05.2019
 */
class InferDownloader(private val project: Project) {

    private val github: GitHub
    private var isDownloading = AtomicInteger(0)

    init {
        val httpConfig = HttpConfigurable.getInstance()
        github = if (httpConfig.USE_HTTP_PROXY)
            GitHubBuilder().withProxy(
                Proxy(
                    Proxy.Type.HTTP,
                    InetSocketAddress(httpConfig.PROXY_HOST, httpConfig.PROXY_PORT)
                )
            ).build()
        else GitHub.connectAnonymously()
    }

    fun getVersionList(): List<String> {
        return github.getRepository("facebook/infer")!!.releases.map { it.tagName }
    }

    fun downloadAndInstall(tagVersion: String, operationSystem: OperationSystems) {
        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Infer Running...") {
            override fun run(indicator: ProgressIndicator) {
                if (isDownloading.getAndIncrement() != 0) return
                indicator.text = "Infer: Searching..."
                indicator.isIndeterminate = true
                val downloadAsset = getDownloadAsset(tagVersion, operationSystem)

                indicator.text = "Infer: Downloading..."
                val inferDir = File(System.getProperty("user.home") + "/.infer-bin")
                if (!inferDir.exists()) inferDir.mkdir()

                val inferArchiveFile = download(indicator, inferDir, downloadAsset)

                indicator.text = "Infer: Unzipping..."
                indicator.isIndeterminate = true
                val unpackedDir = File(inferDir, inferArchiveFile.name.dropLast(7))
                val inferBin = File(unpackedDir.canonicalPath + "/lib/infer/infer/bin/infer")

                if (!inferBin.exists()) {
                    val archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.XZ)
                    archiver.extract(inferArchiveFile, inferDir)
                }

                project.getComponent(InferProjectComponent::class.java)
                    .settingsTab.updateInferBinPath(inferBin.canonicalPath)
            }

            override fun onFinished() {
                isDownloading.getAndDecrement()
            }
        })
    }

    private fun getDownloadAsset(tagVersion: String, operationSystem: OperationSystems): GHAsset {
        return github.getRepository("facebook/infer")!!.releases
            .find { it.tagName == tagVersion }!!
            .assets
            .find { it.name.contains(operationSystem.key) }!!
    }

    private fun download(indicator: ProgressIndicator, inferDir: File, downloadAsset: GHAsset): File {
        val inferArchiveFile = File(inferDir, downloadAsset.name)
        if (!inferArchiveFile.exists()) {
            runBlocking {
                val job = GlobalScope.launch {
                    FileUtils.copyURLToFile(URL(downloadAsset.browserDownloadUrl), inferArchiveFile)
                }
                while (job.isActive) {
                    delay(500L)
                    updateProgress(indicator, inferArchiveFile, downloadAsset)
                }
            }
        }
        return inferArchiveFile
    }

    private fun updateProgress(indicator: ProgressIndicator, inferArchiveFile: File, downloadAsset: GHAsset) {
        indicator.text = "Infer: Downloading ${(inferArchiveFile.length() / 1_000_000.0).toInt()}" +
                "/${(downloadAsset.size / 1_000_000.0).toInt()} MB..."
        indicator.isIndeterminate = false
        val progress = inferArchiveFile.length() / downloadAsset.size.toDouble()
        if (progress in 0.0..1.0) indicator.fraction = inferArchiveFile.length() / downloadAsset.size.toDouble()
    }
}
