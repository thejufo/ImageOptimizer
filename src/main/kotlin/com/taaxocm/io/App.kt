package com.taaxocm.io

import javafx.application.Application
import javafx.embed.swing.SwingFXUtils
import javafx.event.EventHandler
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import javafx.scene.image.Image
import javafx.stage.Stage
import javafx.stage.WindowEvent
import java.util.function.Consumer
import javax.imageio.ImageIO


class App : Application(), EventHandler<WindowEvent> {

    private lateinit var primaryStage: Stage
    private lateinit var controller: Controller

    override fun start(primaryStage: Stage) {

        val image: Image = SwingFXUtils.toFXImage(
                ImageIO.read(javaClass.getResource("/images/io.png")), null)

        val loader = FXMLLoader(javaClass.getResource("/fxml/main.fxml"))
        val root = loader.load<Parent>()

        this.primaryStage = primaryStage.apply {
            title = "Image Optimizer"
            isResizable = false
            onCloseRequest = this@App
            scene = Scene(root)
            icons.add(image)
        }

        this.primaryStage.show()

        controller = loader.getController()
        controller.setOwner(primaryStage)
        controller.setIcon(image)

    }

    override fun handle(event: WindowEvent) {
        val result = alert {
            alertType = Alert.AlertType.CONFIRMATION
            title = "Confirm"
            contentText = "Exit App?"
            headerText = null
        }.showAndWait()

        var anotherBookFun0 = {  3}
        var anotherBookFun1 = fun() = 4



        alert(Alert.AlertType.CONFIRMATION, {})

        result.ifPresentOrElse({
            if (it == ButtonType.OK) {
                if (controller.setOnWinCloseRequest())
                    primaryStage.close()
            } else
                event.consume()
        }, {
            event.consume()
        })
    }
}

fun main(args: Array<String>) {
    Application.launch(App::class.java, *args)
}
