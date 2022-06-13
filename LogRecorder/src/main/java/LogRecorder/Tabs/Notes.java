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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.text.JTextComponent;

import LogRecorder.Application;
import LogRecorder.FileSyncronisedPane;
import LogRecorder.LogFile;
import LogRecorder.SpellCheckedPane;
import LogRecorder.Tabs.End.Time;
import LogRecorder.idleDetectionHandler.IdleTimeDetector;
import lombok.SneakyThrows;

public class Notes {
    static Thread notesTabFileListenerThread;
    static Runnable notesTabFileListenerRunnable;
    static final String TAKING_A_BREAK = "Taking a break";
    static final String BACK_FROM_BREAK = "Back from break";
    
    private Notes() {}
    
    private static void toggleAFK() {
    	switch(btnNewButton_1.getText()) {
    		case TAKING_A_BREAK:
    			setAFK();
    			AutoAFKHandler.manualAFK = true;
    			break;
    		case BACK_FROM_BREAK:
    			setUNAFK();
    			AutoAFKHandler.manualAFK = false;
    			break;
    	}
    }
    private static void setAFK() {
    	LogFile.append(btnNewButton_1.getText());
		topPane.setEnabled(false);
		bottomPane.setEnabled(false);
		lblNewLabel.setEnabled(false);
		btnNewButton.setEnabled(false);
		btnNewButton_1.setText(BACK_FROM_BREAK);
    }
    private static void setUNAFK() {
    	LogFile.append(btnNewButton_1.getText());
		topPane.setEnabled(true);
		bottomPane.setEnabled(true);
		lblNewLabel.setEnabled(true);
		btnNewButton.setEnabled(true);
		btnNewButton_1.setText(TAKING_A_BREAK);
    }
    
    static JPanel panel_2, panel;
    static JButton btnNewButton, btnNewButton_1;
    static JLabel lblNewLabel;
    static JSplitPane splitPane;
    static FileSyncronisedPane topPane;
    static SpellCheckedPane bottomPane;
    
    @SneakyThrows
    public static void createTab(JTabbedPane tabbedPane) {
    	panel_2 = new JPanel();
        tabbedPane.addTab("Notes", null, panel_2, null);
        panel_2.setLayout(new BorderLayout(0, 0));
        
        panel = new JPanel();
        panel_2.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));
        
        btnNewButton = new JButton("Submit");
        btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(btnNewButton, BorderLayout.EAST);
        
        lblNewLabel = new JLabel("");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblNewLabel, BorderLayout.CENTER);
        
        splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        panel_2.add(splitPane, BorderLayout.CENTER);
        splitPane.setResizeWeight(1);
        splitPane.setDividerLocation(175);
        
        topPane = new FileSyncronisedPane();
        JScrollPane topPaneScroll = new JScrollPane(topPane);
        
        JPanel afkLogPanel = new JPanel();
        afkLogPanel.setLayout(new BorderLayout(0, 0));
        JList<Notes.AutoAFKHandler.TimeDiff> afkLog = new JList<>();
        afkLogPanel.add(new JLabel("AFK Records"), BorderLayout.NORTH);
        afkLogPanel.add(afkLog, BorderLayout.CENTER);
        JPanel temp = new JPanel();
        temp.setLayout(new BorderLayout(0, 0));
        temp.add(new JButton("Merge record down"), BorderLayout.NORTH);
        JButton moveToLogButton = new JButton("Move record to log");
        temp.add(moveToLogButton, BorderLayout.SOUTH);
        moveToLogButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
            	if(afkLog.getSelectedValue() == null) return;
            	String startTimeString = DateTimeFormatter.ofPattern("HH:mm").format(afkLog.getSelectedValue().startTime);
            	String strBreak = startTimeString + " " + TAKING_A_BREAK;
            	String strBack = DateTimeFormatter.ofPattern("HH:mm").format(afkLog.getSelectedValue().endTime) + " " + BACK_FROM_BREAK;
            	if(LogFile.getContents().contains(strBreak) || LogFile.getContents().contains(strBack)) return;
            	
            	Pattern pattern = Pattern.compile("[0-9]{2}:[0-9]{2}", Pattern.CASE_INSENSITIVE);
            	String reconstructedFile = "";
            	Matcher matcher;
            	boolean insertedNewLines = false;
            	for(String line : LogFile.getContents().split(System.lineSeparator())){
            		
            		matcher = pattern.matcher(line);
        			if(matcher.find()) {
        				String time = matcher.group();
        				End.Time thisLineTime = new End.Time(time);
        				End.Time newLogTime = new End.Time(startTimeString);
        				if(thisLineTime.diffTo(newLogTime).toHours() > 12f  && !insertedNewLines) {
        					reconstructedFile += System.lineSeparator() + strBreak;
        					reconstructedFile += System.lineSeparator() + strBack;
        					insertedNewLines = true;
        				}
        			}
            		reconstructedFile += System.lineSeparator() + line;
            	}
            	LogFile.overwrite(reconstructedFile);
            }
        });
        afkLogPanel.add(temp, BorderLayout.SOUTH);
        
        JPanel topSplitPane = new JPanel();
        topSplitPane.setLayout(new BorderLayout(0, 0));
        topSplitPane.add(topPaneScroll, BorderLayout.CENTER);
        topSplitPane.add(afkLogPanel, BorderLayout.EAST);
        
        splitPane.setLeftComponent(topSplitPane);
        
        bottomPane = new SpellCheckedPane();
        splitPane.setRightComponent(bottomPane);
        
        btnNewButton_1 = new JButton("Taking a break");
        panel.add(btnNewButton_1, BorderLayout.WEST);
        btnNewButton_1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
            	toggleAFK();
            }
        });
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        	@Override
        	public void run() {
        		LogFile.overwrite(topPane.getText());
        	}
        }));
        
        AutoAFKHandler.initAFKListener(afkLog);
        
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
        
        btnNewButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
            	LogFile.overwrite(topPane.getText());
            	Application.submitTextboxIntoLogFile(bottomPane);
            }
        });
    }
    
    static class AutoAFKHandler{
    	static LocalDateTime lastAutoAFKTime;
    	static ArrayList<TimeDiff> afkTimes = new ArrayList<TimeDiff>();
        static boolean manualAFK = false;
        static boolean currentlyAutoAFK = false;
        static boolean wasAutoAFK = false;
        private static final long idleTimeAllowanceMS = 60000l;
		public static void initAFKListener(JList<TimeDiff> listElement) {
			new Timer(1000, e -> {
	        	currentlyAutoAFK = IdleTimeDetector.getIdleTimeMS() > idleTimeAllowanceMS;
	        	
	        	if(!wasAutoAFK && currentlyAutoAFK)
	        		lastAutoAFKTime = LocalDateTime.now().minusNanos(idleTimeAllowanceMS * (long)Math.pow(10, 6));
	        		
	        	if(!currentlyAutoAFK && wasAutoAFK) {
	        		afkTimes.add(new TimeDiff(lastAutoAFKTime, LocalDateTime.now()));
	        		listElement.setListData(afkTimes.toArray(new TimeDiff[0]));
	        	}
	        	
	        	wasAutoAFK = currentlyAutoAFK;
	    	}).start(); 
		}
    	
		static class TimeDiff{
			LocalDateTime startTime;
			LocalDateTime endTime;
			TimeDiff(LocalDateTime startTime, LocalDateTime endTime){
				this.startTime = startTime;
				this.endTime = endTime;
			}
			
			public String toString() {
				return DateTimeFormatter.ofPattern("HH:mm").format(startTime) +" - "+ LogRecorder.Tabs.TimeDiff.formatDurationAsText(Duration.between(startTime, endTime));
			}
			public String getTimeDiff() {
				return DateTimeFormatter.ofPattern("HH:mm").format(startTime) +" -> "+ DateTimeFormatter.ofPattern("HH:mm").format(endTime);
			}
		}
    }
}
