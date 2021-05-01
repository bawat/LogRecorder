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

public class End {
    
    private End() {}
    
    @SneakyThrows
    public static void createTab(JTabbedPane tabbedPane) {
    	JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Finished", null, panel_2, null);
        panel_2.setLayout(new BorderLayout(0, 0));
        
        SpellCheckedPane textPane = new SpellCheckedPane();
        panel_2.add(textPane, BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        panel_2.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));
        
        JButton btnNewButton = new JButton("End Work");
        btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(btnNewButton, BorderLayout.EAST);
        
        JLabel lblNewLabel = new JLabel("What will you be working on tomorrow?");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblNewLabel, BorderLayout.CENTER);
    }
}
