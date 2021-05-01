package LogRecorder.Tabs;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import LogRecorder.LogFile;
import LogRecorder.SpellCheckedPane;
import lombok.SneakyThrows;

public class ToDo {
    
    private ToDo() {}
    
    @SneakyThrows
    public static void createTab(JTabbedPane tabbedPane) {
        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("To Do", null, panel_2, null);
        panel_2.setLayout(new BorderLayout(0, 0));
        
        JPanel panel = new JPanel();
        panel_2.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));
        
        JButton btnNewButton = new JButton("Add");
        btnNewButton.setHorizontalAlignment(SwingConstants.RIGHT);
        panel.add(btnNewButton, BorderLayout.EAST);
        
        JLabel lblNewLabel = new JLabel("To Do:");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblNewLabel, BorderLayout.WEST);
        

        JTextField textField = new JTextField();
        panel.add(textField, BorderLayout.CENTER);
        textField.setColumns(10);
        
        JList list = new JList();
        panel_2.add(list, BorderLayout.CENTER);
    }
}
