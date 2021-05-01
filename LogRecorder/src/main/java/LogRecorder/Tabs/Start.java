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
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import LogRecorder.Application;
import LogRecorder.LogFile;
import LogRecorder.SpellCheckedPane;
import lombok.SneakyThrows;

public class Start {
    
    private Start() {}
    
    @SneakyThrows
    public static void createTab(JTabbedPane tabbedPane) {
    	JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Start", null, panel_2, null);
        panel_2.setLayout(new BorderLayout(0, 0));
        
        JPanel panel = new JPanel();
        panel_2.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));
        
        JLabel lblNewLabel = new JLabel("What will you be working on today?");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblNewLabel, BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        panel_2.add(splitPane, BorderLayout.CENTER);
        splitPane.setResizeWeight(1);
        splitPane.setDividerLocation(175);
        
        SpellCheckedPane topPane = new SpellCheckedPane();
        topPane.setEditable(false);
        JScrollPane topPaneScroll = new JScrollPane(topPane);
        splitPane.setLeftComponent(topPaneScroll);
        
        Optional<String> fileContents = LogFile.maybeProvidePreviousFile();
        topPane.setText("If you had a log for a previous day, you would see it here to review.");
        if(fileContents.isPresent()) {
        	topPane.setText(fileContents.get());
        }
        
        SpellCheckedPane bottomPane = new SpellCheckedPane();
        splitPane.setRightComponent(bottomPane);
        
        JButton btnNewButton = new JButton("Start Work");
        btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(btnNewButton, BorderLayout.EAST);
        btnNewButton.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		Application.submitTextboxIntoLogFile(bottomPane);
        		tabbedPane.setSelectedIndex(tabbedPane.getSelectedIndex() + 1);
        	}
        });
    }
}
