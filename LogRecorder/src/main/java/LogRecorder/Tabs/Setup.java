package LogRecorder.Tabs;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import LogRecorder.Application;
import LogRecorder.LogFile;
import LogRecorder.SpellCheckedPane;
import LogRecorder.YamlFile;
import lombok.SneakyThrows;

public class Setup {
    
    private Setup() {}
    
    @SneakyThrows
    public static void createTab(JTabbedPane tabbedPane, JFrame frame) {
    	JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Setup", null, panel_2, null);
        
        YamlFile yamlFile = YamlFile.load();
        
        JTextField txtLogLocation = new JTextField(yamlFile.getLogFolder());
        txtLogLocation.setColumns(10);
        txtLogLocation.setEditable(false);
        
        JTextField txtLogBackupLocation = new JTextField(yamlFile.getLogFolderBackup());
        txtLogBackupLocation.setColumns(10);
        txtLogBackupLocation.setEditable(false);
        
        Runnable updateTextFields = () -> {
        	txtLogLocation.setText(yamlFile.logFolder);
        	txtLogBackupLocation.setText(yamlFile.logFolderBackup);
        };
        
        JButton btnLogLocation = new JButton("Choose Log Location");
        btnLogLocation.addActionListener(new ActionListener() {
            @Override @SneakyThrows
            public void actionPerformed(ActionEvent e) {
               JFileChooser fileChooser = new JFileChooser();
               fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
               int option = fileChooser.showOpenDialog(frame);
               if(option == JFileChooser.APPROVE_OPTION){
                  File file = fileChooser.getSelectedFile();
                  yamlFile.logFolder = file.getAbsolutePath();
                  yamlFile.save();
                  updateTextFields.run();
                  Application.disableAllTabsExceptSetupUntilFolderSelected(tabbedPane);
                  Notes.notesTabFileListenerThread.interrupt();
                  Notes.notesTabFileListenerThread.join();
                  Notes.notesTabFileListenerThread = new Thread(Notes.notesTabFileListenerRunnable);
                  Notes.notesTabFileListenerThread.start();
               }
            }
         });
        
        JButton btnLogBackupLocation = new JButton("Choose Log Backup Location");
        btnLogBackupLocation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               JFileChooser fileChooser = new JFileChooser();
               fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
               int option = fileChooser.showOpenDialog(frame);
               if(option == JFileChooser.APPROVE_OPTION){
                  File file = fileChooser.getSelectedFile();
                  yamlFile.logFolderBackup = file.getAbsolutePath();
                  yamlFile.save();
                  updateTextFields.run();
               }
            }
         });
        
        GroupLayout gl_panel_2 = new GroupLayout(panel_2);
        gl_panel_2.setHorizontalGroup(
            gl_panel_2.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_2.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_panel_2.createSequentialGroup()
                            .addComponent(btnLogLocation)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(txtLogLocation, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE))
                        .addGroup(gl_panel_2.createSequentialGroup()
                            .addComponent(btnLogBackupLocation)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(txtLogBackupLocation, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        gl_panel_2.setVerticalGroup(
            gl_panel_2.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_panel_2.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnLogLocation)
                        .addComponent(txtLogLocation, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnLogBackupLocation)
                        .addComponent(txtLogBackupLocation, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                    .addGap(170))
        );
        panel_2.setLayout(gl_panel_2);
    }
}
