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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import LogRecorder.Application;
import LogRecorder.FileSyncronisedPane;
import LogRecorder.LogFile;
import LogRecorder.SpellCheckedPane;
import lombok.SneakyThrows;

public class Notes {
    static Thread notesTabFileListenerThread;
    static Runnable notesTabFileListenerRunnable;
    
    private Notes() {}
    
    @SneakyThrows
    public static void createTab(JTabbedPane tabbedPane) {
    	JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Notes", null, panel_2, null);
        panel_2.setLayout(new BorderLayout(0, 0));
        
        JPanel panel = new JPanel();
        panel_2.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));
        
        JButton btnNewButton = new JButton("Submit");
        btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(btnNewButton, BorderLayout.EAST);
        
        JLabel lblNewLabel = new JLabel("");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblNewLabel, BorderLayout.CENTER);
        
        JButton btnNewButton_1 = new JButton("Taking a break");
        panel.add(btnNewButton_1, BorderLayout.WEST);
        
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        panel_2.add(splitPane, BorderLayout.CENTER);
        splitPane.setResizeWeight(1);
        splitPane.setDividerLocation(175);
        
        FileSyncronisedPane topPane = new FileSyncronisedPane();
        JScrollPane topPaneScroll = new JScrollPane(topPane);
        splitPane.setLeftComponent(topPaneScroll);
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        	@Override
        	public void run() {
        		LogFile.overwrite(topPane.getText());
        	}
        }));
        
        
        topPane.setText(LogFile.getContents());
        
        notesTabFileListenerRunnable = new Runnable() {
        	
        	WatchService watchService = FileSystems.getDefault().newWatchService();

            @Override
            public void run()
            {
                Path dir = LogFile.TodaysURI.provide().getParent();
                WatchKey toCancel = null;
                try {
                	toCancel = dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                } catch (IOException x) {
                    System.err.println(x);
                }
                
                // Reset the key -- this step is critical if you want to
                // receive further watch events.  If the key is no longer valid,
                // the directory is inaccessible so exit the loop.
                boolean keyIsValid = true;
                while(keyIsValid) {
                    // wait for key to be signaled
                    WatchKey key = null;
                    try {
                        key = watchService.take();
                    } catch (InterruptedException x) {
                    	toCancel.cancel();
                        return;
                    }

                    for (WatchEvent<?> event: key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind != StandardWatchEventKinds.ENTRY_MODIFY) continue;

                        System.out.println("Loaded from file after detecting change");
                        SwingUtilities.invokeLater(() -> {
                        	int caretPosition = topPane.getCaretPosition();
                        	topPane.setText(LogFile.getContents());
                        	topPane.setCaretPosition(caretPosition);
                        });
                    }
                    keyIsValid = key.reset();
                }
            }
            
        };
        notesTabFileListenerThread = new Thread(notesTabFileListenerRunnable);
        notesTabFileListenerThread.start();
        
        SpellCheckedPane bottomPane = new SpellCheckedPane();
        splitPane.setRightComponent(bottomPane);
        
        btnNewButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
            	LogFile.overwrite(topPane.getText());
            	Application.submitTextboxIntoLogFile(bottomPane);
            }
        });
    }
}
