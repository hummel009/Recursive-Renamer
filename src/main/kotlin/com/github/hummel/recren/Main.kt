package com.github.hummel.recren

import com.formdev.flatlaf.FlatLightLaf
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTGitHubDarkIJTheme
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.EventQueue
import java.awt.GridLayout
import java.io.File
import javax.swing.*
import javax.swing.border.EmptyBorder
import kotlin.concurrent.thread

fun main() {
	FlatLightLaf.setup()
	EventQueue.invokeLater {
		try {
			UIManager.setLookAndFeel(FlatMTGitHubDarkIJTheme())
			val frame = RecursiveRenamer()
			frame.isVisible = true
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
}

class RecursiveRenamer : JFrame() {
	private var folderPathField: JTextField
	private var fromField: JTextField
	private var toField: JTextField
	private var ignoreCaseCheckbox: JCheckBox
	private var renameFoldersCheckbox: JCheckBox
	private var processButton: JButton

	init {
		title = "Hummel009's Recursive Renamer"
		defaultCloseOperation = EXIT_ON_CLOSE
		setBounds(100, 100, 600, 270)

		val contentPanel = JPanel().apply {
			border = EmptyBorder(10, 10, 10, 10)
			layout = GridLayout(0, 1, 5, 10)
		}

		val folderPanel = JPanel(BorderLayout(5, 0)).apply {
			add(JLabel("Folder path:").apply {
				preferredSize = Dimension(100, preferredSize.height)
			}, BorderLayout.WEST)

			folderPathField = JTextField()
			add(folderPathField, BorderLayout.CENTER)

			val browseButton = JButton("Browse").apply {
				preferredSize = Dimension(100, preferredSize.height)
				addActionListener {
					selectPath()
				}
			}
			add(browseButton, BorderLayout.EAST)
		}

		val fromPanel = JPanel(BorderLayout(5, 0)).apply {
			add(JLabel("Replace this:").apply {
				preferredSize = Dimension(100, preferredSize.height)
			}, BorderLayout.WEST)

			fromField = JTextField()
			add(fromField, BorderLayout.CENTER)
		}

		val toPanel = JPanel(BorderLayout(5, 0)).apply {
			add(JLabel("With this:").apply {
				preferredSize = Dimension(100, preferredSize.height)
			}, BorderLayout.WEST)

			toField = JTextField()
			add(toField, BorderLayout.CENTER)
		}

		val checkboxesPanel = JPanel(GridLayout(1, 2, 5, 0)).apply {
			ignoreCaseCheckbox = JCheckBox("Ignore case")
			renameFoldersCheckbox = JCheckBox("Rename folders too")
			add(ignoreCaseCheckbox)
			add(renameFoldersCheckbox)
		}

		processButton = JButton("Rename").apply {
			preferredSize = Dimension(100, preferredSize.height)
			addActionListener {
				process()
			}
		}

		contentPanel.add(folderPanel)
		contentPanel.add(fromPanel)
		contentPanel.add(toPanel)
		contentPanel.add(checkboxesPanel)
		contentPanel.add(processButton)

		contentPane = contentPanel
		setLocationRelativeTo(null)
	}

	private fun process() {
		val folderPath = folderPathField.text
		val fromText = fromField.text
		val toText = toField.text

		if (folderPath.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Fill all fields", "Error", JOptionPane.ERROR_MESSAGE)
			return
		}

		listOf(folderPathField, fromField, toField, processButton, ignoreCaseCheckbox, renameFoldersCheckbox).forEach {
			it.isEnabled = false
		}

		thread {
			try {
				rename(
					folderPath,
					fromText,
					toText,
					ignoreCaseCheckbox.isSelected,
					renameFoldersCheckbox.isSelected
				)

				SwingUtilities.invokeLater {
					JOptionPane.showMessageDialog(
						this, "Rename completed!", "Success", JOptionPane.INFORMATION_MESSAGE
					)
				}
			} catch (e: Exception) {
				e.printStackTrace()
				SwingUtilities.invokeLater {
					JOptionPane.showMessageDialog(
						this,
						"Error during renaming: ${e.message}",
						"Error",
						JOptionPane.ERROR_MESSAGE
					)
				}
			} finally {
				SwingUtilities.invokeLater {
					listOf(
						folderPathField,
						fromField,
						toField,
						processButton,
						ignoreCaseCheckbox,
						renameFoldersCheckbox
					).forEach {
						it.isEnabled = true
					}
				}
			}
		}
	}

	private fun selectPath() {
		val chooser = JFileChooser().apply {
			fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
		}
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			folderPathField.text = chooser.selectedFile.absolutePath
		}
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
					val newFileName = fileName.replace(from, to, ignoreCase)
					val newFile = File(it.parent, newFileName)
					it.renameTo(newFile)
				}
			}
		}
	}
}