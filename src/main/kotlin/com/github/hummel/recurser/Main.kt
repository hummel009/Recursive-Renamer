package com.github.hummel.recurser

import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTGitHubDarkIJTheme
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.GridLayout
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder

fun main() {
	FlatLightLaf.setup()
	EventQueue.invokeLater {
		try {
			UIManager.setLookAndFeel(FlatMTGitHubDarkIJTheme())
			val frame = GUI()
			frame.isVisible = true
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}

class GUI : JFrame() {
	private fun selectPath(pathField: JTextField, dir: Boolean) {
		val fileChooser = JFileChooser()
		if (dir) {
			fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
		}
		val result = fileChooser.showOpenDialog(this)
		if (result == JFileChooser.APPROVE_OPTION) {
			pathField.text = fileChooser.selectedFile.absolutePath
		}
	}

	private fun process(
		pathField: JTextField, fromField: JTextField, toField: JTextField, ignoreCase: Boolean, renameFolders: Boolean
	) {
		if (pathField.text.isEmpty() || fromField.text.isEmpty() || toField.text.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Fill the fields", "Error", JOptionPane.ERROR_MESSAGE)
			return
		}

		rename(pathField.text, fromField.text, toField.text, ignoreCase, renameFolders)

		JOptionPane.showMessageDialog(
			this, "Rename complete", "Message", JOptionPane.INFORMATION_MESSAGE
		)
	}

	private fun rename(path: String, from: String, to: String, ignoreCase: Boolean, renameFolders: Boolean) {
		val inputDirectory = File(path)
		inputDirectory.listFiles()?.forEach {
			if (it.isDirectory) {
				rename(it.path, from, to, ignoreCase, renameFolders)
				if (renameFolders) {
					val folderName = it.name
					if (folderName.contains(from, ignoreCase)) {
						val newFolderName = folderName.replace(from, to, ignoreCase)
						val newFolder = File(it.parent, newFolderName)
						it.renameTo(newFolder)
					}
				}
			} else {
				val fileName = it.name
				if (fileName.contains(from, ignoreCase)) {
					val newFileName = fileName.replace(from, to)
					val newFile = File(it.parent, newFileName)
					it.renameTo(newFile)
				}
			}
		}
	}

	init {
		title = "Hummel009's Recursive Renamer"
		defaultCloseOperation = EXIT_ON_CLOSE
		setBounds(100, 100, 600, 270)

		val contentPanel = JPanel()
		contentPanel.border = EmptyBorder(5, 5, 5, 5)
		contentPanel.layout = BorderLayout(0, 0)
		contentPanel.layout = GridLayout(0, 1, 0, 0)
		contentPane = contentPanel

		val inputPanel = JPanel()
		val inputLabel = JLabel("Folder path:")
		inputLabel.preferredSize = Dimension(90, inputLabel.preferredSize.height)
		val inputField = JTextField(24)
		val inputButton = JButton("Select path")
		inputButton.addActionListener { selectPath(inputField, true) }
		inputPanel.add(inputLabel)
		inputPanel.add(inputField)
		inputPanel.add(inputButton)

		val fromPanel = JPanel()
		val fromLabel = JLabel("Replace this:")
		fromLabel.preferredSize = Dimension(90, fromLabel.preferredSize.height)
		val fromField = JTextField(24)
		fromPanel.add(fromLabel)
		fromPanel.add(fromField)

		val toPanel = JPanel()
		val toLabel = JLabel("With this:")
		toLabel.preferredSize = Dimension(90, toLabel.preferredSize.height)
		val toField = JTextField(24)
		toPanel.add(toLabel)
		toPanel.add(toField)

		val checkboxPanel = JPanel()
		val checkbox1 = JCheckBox("Ignore Case")
		checkbox1.isSelected = false
		checkboxPanel.add(checkbox1)
		val checkbox2 = JCheckBox("Rename folders too")
		checkbox2.isSelected = false
		checkboxPanel.add(checkbox2)

		val processPanel = JPanel()
		val processButton = JButton("Process")
		processButton.addActionListener {
			process(inputField, fromField, toField, checkbox1.isSelected, checkbox2.isSelected)
		}
		processPanel.add(processButton)

		contentPanel.add(inputPanel)
		contentPanel.add(fromPanel)
		contentPanel.add(toPanel)
		contentPanel.add(checkboxPanel)
		contentPanel.add(processPanel)

		setLocationRelativeTo(null)
	}
}
