package com.taaxocm.io

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.embed.swing.SwingFXUtils
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Slider
import javafx.scene.image.Image
import javafx.stage.FileChooser
import javafx.stage.Window
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.text.DecimalFormat
import java.util.*
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam
import javax.imageio.ImageWriter

class Controller : Initializable {

    @FXML
    private lateinit var browseBtn: Button

    @FXML
    private lateinit var compressBtn: Button

    @FXML
    private lateinit var qualitySlider: Slider
    private var quality = 0f

    private var originalFile: File? = null
    private var compressedFile: File? = null
    private var originalBufferedImage: BufferedImage? = null
    private var originalImageStage: Viewer? = null
    private var compressedImageStage: Viewer? = null
    private var owner: Window? = null
    private var icon: Image? = null

    // Properties
    private val imagePathText = SimpleStringProperty("")
    private val qualityLevelText = SimpleStringProperty("")
    private val qualityLevelLabelDisable = SimpleBooleanProperty(true)
    private val qualityLevelSliderDisable = SimpleBooleanProperty(true)

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        browseBtn.onAction = EventHandler {
            val file: File? = grapFile()
            if (file != null) {
                originalFile = file
                compressedFile = extractFileFromFile(originalFile!!)
                setImagePathText(file.absolutePath)
                try {
                    originalBufferedImage = ImageIO.read(file)
                    val image: Image = SwingFXUtils.toFXImage(originalBufferedImage, null)

                    // Enable compression functionality
                    compressBtn.isDisable = false
                    setQualityLevelSliderDisable(false)
                    if (originalImageStage == null) {
                        originalImageStage = Viewer()
                        originalImageStage!!.icons.add(icon)
                        originalImageStage!!.isResizable = false
                    }
                    val title = "Original Image " + originalFile!!.length() / 1024 + " KB"
                    originalImageStage!!.title = title
                    originalImageStage!!.setImage(image)
                    originalImageStage!!.show()
                } catch (e: IOException) {
                    showErrorAlert()
                } catch (e: NullPointerException) {
                    showErrorAlert()
                }
            }
        }

        compressBtn.isDisable = true
        compressBtn.onAction = EventHandler {
            val writers = ImageIO.getImageWritersByFormatName("jpg")
            check(writers.hasNext()) { "No writers exception" }
            val writer = writers.next()
            val compressedImage = performCompression(writer)
            if (compressedImageStage == null) {
                compressedImageStage = Viewer()
                compressedImageStage!!.isResizable = false
                compressedImageStage!!.icons.add(icon)
            }
            val title = "Compressed Image " + compressedFile!!.length() / 1024 + " KB"
            compressedImageStage!!.title = title
            compressedImageStage!!.setImage(compressedImage)
            compressedImageStage!!.show()
        }
        setupQuality()
    }

    private fun grapFile(): File {
        val chooser = FileChooser()
        chooser.title = "Pick an image"
        chooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("JPG files", "*.jpg"),
                FileChooser.ExtensionFilter("PNG files", "*.png"))
        return chooser.showOpenDialog(owner)
    }

    private fun extractFileFromFile(original: File): File {
        val fileDirectories = original.absolutePath
                .substring(0, original.absolutePath.lastIndexOf(File.separator))
        val fileName = original.name.substring(0, original.name.lastIndexOf('.'))
        var fileExt = ""
        val i = original.name.lastIndexOf('.')
        if (i > 0) fileExt = original.name.substring(i + 1)
        val suffix = 1
        val extractedFileName = fileDirectories + File.separator + fileName + suffix + "." + fileExt
        return File(extractedFileName)
    }

    private fun performCompression(writer: ImageWriter): Image? {
        try {
            FileOutputStream(compressedFile).use { out ->
                ImageIO.createImageOutputStream(out).use { ios ->
                    writer.output = ios
                    val param = writer.defaultWriteParam

                    // compress to a given quality
                    param.compressionMode = ImageWriteParam.MODE_EXPLICIT
                    println(quality)
                    param.compressionQuality = quality

                    // appends a complete image stream containing a single image and
                    //associated stream and image metadata and thumbnails to the output
                    writer.write(null, IIOImage(originalBufferedImage, null, null), param)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            writer.dispose()
        }
        var compressesImageBuffered: BufferedImage? = null
        try {
            compressesImageBuffered = ImageIO.read(compressedFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val image: Image
        if (compressesImageBuffered != null) {
            image = SwingFXUtils.toFXImage(compressesImageBuffered, null)
            return image
        }
        return null
    }

    private fun setupQuality() {
        val df = DecimalFormat("0.0")
        qualitySlider.isSnapToTicks = true
        qualitySlider.minorTickCount = 0
        qualitySlider.valueProperty().addListener { _, _, newValue: Number? ->
            quality = df.format(newValue).toFloat()
            setQualityLevelText(quality)
        }
        quality = df.format(qualitySlider.value).toFloat()
        setQualityLevelText(quality)
    }

    private fun showErrorAlert() {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = "Error"
        alert.headerText = null
        alert.contentText = "Unknown error occurred\nPlease try again"
        alert.showAndWait()
    }

    fun setOwner(owner: Window?) {
        this.owner = owner
    }

    fun setIcon(icon: Image?) {
        this.icon = icon
    }

    fun setOnWinCloseRequest(): Boolean {
        if (originalImageStage != null) originalImageStage!!.close()
        if (compressedImageStage != null) compressedImageStage!!.close()
        return true
    }

    fun getImagePathText(): String {
        return imagePathText.get()
    }

    fun setImagePathText(imagePathText: String) {
        this.imagePathText.set(imagePathText)
    }

    fun getQualityLevelText(): String {
        return qualityLevelText.get()
    }

    fun setQualityLevelText(qualityLevelText: String) {
        this.qualityLevelText.set(qualityLevelText)
    }

    fun setQualityLevelText(quality: Float) {
        val qualityText = "Quality : $quality"
        setQualityLevelText(qualityText)
    }

    fun isQualityLevelLabelDisable(): Boolean {
        return qualityLevelLabelDisable.get()
    }

    fun setQualityLevelLabelDisable(qualityLevelLabelDisable: Boolean) {
        this.qualityLevelLabelDisable.set(qualityLevelLabelDisable)
    }

    fun isQualityLevelSliderDisable(): Boolean {
        return qualityLevelSliderDisable.get()
    }

    fun setQualityLevelSliderDisable(qualityLevelSliderDisable: Boolean) {
        this.qualityLevelSliderDisable.set(qualityLevelSliderDisable)
    }

    init {
        qualityLevelSliderDisable.value = false
        setQualityLevelLabelDisable(true)
    }
}