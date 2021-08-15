package LogRecorder.Tabs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.io.File;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import LogRecorder.Application;
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
        
        JTextField txtInvoiceDestinationFolder = new JTextField(yamlFile.getInvoiceDestinationFolder());
        txtInvoiceDestinationFolder.setColumns(10);
        txtInvoiceDestinationFolder.setEditable(false);
        
        JLabel lblInvoiceDestinationEmail = new JLabel("Invoice Monthly Destination Email: ");
        
        JTextField txtInvoiceTemplateFile = new JTextField(yamlFile.getInvoiceTemplateFile());
        txtInvoiceTemplateFile.setColumns(10);
        txtInvoiceTemplateFile.setEditable(false);
        
        JTextField txtInvoiceDestinationEmail = new JTextField(yamlFile.getInvoiceDestinationEmail());
        txtInvoiceDestinationEmail.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				yamlFile.invoiceDestinationEmail = txtInvoiceDestinationEmail.getText();
                yamlFile.save();
			}
        });
        
        Runnable updateTextFields = () -> {
        	txtLogLocation.setText(yamlFile.getLogFolder());
        	txtLogBackupLocation.setText(yamlFile.getLogFolderBackup());
        	txtInvoiceDestinationFolder.setText(yamlFile.getInvoiceDestinationFolder());
        	txtInvoiceDestinationEmail.setText(yamlFile.getInvoiceDestinationEmail());
        	txtInvoiceTemplateFile.setText(yamlFile.getInvoiceTemplateFile());
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
        
        JButton btnInvoiceDestination = new JButton("Choose Invoice Destination");
        btnInvoiceDestination.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               JFileChooser fileChooser = new JFileChooser();
               fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
               int option = fileChooser.showOpenDialog(frame);
               if(option == JFileChooser.APPROVE_OPTION){
                  File file = fileChooser.getSelectedFile();
                  yamlFile.invoiceDestinationFolder = file.getAbsolutePath();
                  yamlFile.save();
                  updateTextFields.run();
               }
            }
         });
        
        JButton btnInvoiceTemplateFile = new JButton("Choose Invoice Template");
        btnInvoiceTemplateFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               JFileChooser fileChooser = new JFileChooser();
               fileChooser.setFileFilter(new FileNameExtensionFilter("JasperSoft JRXML file", "jrxml"));
               fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
               int option = fileChooser.showOpenDialog(frame);
               if(option == JFileChooser.APPROVE_OPTION){
                  File file = fileChooser.getSelectedFile();
                  yamlFile.invoiceTemplateFile = file.getAbsolutePath();
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
                            .addComponent(txtLogBackupLocation, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE))
                        .addGroup(gl_panel_2.createSequentialGroup()
                            .addComponent(btnInvoiceDestination)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(txtInvoiceDestinationFolder, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE))
                        .addGroup(gl_panel_2.createSequentialGroup()
                            .addComponent(lblInvoiceDestinationEmail)
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(txtInvoiceDestinationEmail, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE))
		                .addGroup(gl_panel_2.createSequentialGroup()
	                        .addComponent(btnInvoiceTemplateFile)
	                        .addPreferredGap(ComponentPlacement.UNRELATED)
	                        .addComponent(txtInvoiceTemplateFile, GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)))
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
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnInvoiceDestination)
                        .addComponent(txtInvoiceDestinationFolder, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
                        .addComponent(lblInvoiceDestinationEmail)
                        .addComponent(txtInvoiceDestinationEmail, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
                        .addComponent(btnInvoiceTemplateFile)
                        .addComponent(txtInvoiceTemplateFile, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGap(170))
        );
        panel_2.setLayout(gl_panel_2);
    }
}
