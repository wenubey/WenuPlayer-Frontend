package org.wenubey.wenuplayerfrontend.presentation

import java.awt.*
import javax.swing.*
import javax.swing.Timer

class DesktopToast(message: String) : JWindow() {
    init {
        // Create a custom JPanel with rounded corners
        val panel = object : JPanel() {
            override fun paintComponent(g: Graphics) {
                val g2 = g as Graphics2D
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                g2.color = Color(0, 0, 0, 180)
                g2.fillRoundRect(0, 0, width, height, 20, 20) // Rounded corners with radius 20
                super.paintComponent(g)
            }
        }
        panel.isOpaque = false
        panel.layout = BorderLayout()
        panel.border = BorderFactory.createEmptyBorder(10, 20, 10, 20)

        val label = JLabel(message)
        label.foreground = Color.WHITE
        label.horizontalAlignment = SwingConstants.CENTER
        panel.add(label)
        contentPane.add(panel)
        pack()
        val mainFrame = Frame.getFrames().firstOrNull()
        if (mainFrame != null) {
            val mainBounds = mainFrame.bounds
            setLocation(
                mainBounds.x + (mainBounds.width - width) / 2,
                mainBounds.y + mainBounds.height - height - 50
            )
        }

        isAlwaysOnTop = true
        background = Color(0, 0, 0, 0)
        isVisible = true

        Timer(3000) { isVisible = false }.apply {
            isRepeats = false
            start()
        }
    }
}
