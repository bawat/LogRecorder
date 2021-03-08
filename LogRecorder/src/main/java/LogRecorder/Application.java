package LogRecorder;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import com.inet.jortho.FileUserDictionary;
import com.inet.jortho.SpellChecker;

import lombok.SneakyThrows;

public class Application
{

    private JFrame frame;
    private JTextField textField;

    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable() {
            public void run()
            {
                try
                {
                    Application window = new Application();
                    window.frame.setVisible(true);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public Application()
    {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        
        
        SpellChecker.setUserDictionaryProvider(new FileUserDictionary());
        SpellChecker.registerDictionaries(Thread.currentThread().getContextClassLoader().getResource("/resources"), "en");
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        frame.getContentPane().add(tabbedPane);
        
        startTab(tabbedPane);
        notesTab(tabbedPane);
        toDoList(tabbedPane);
        endTab(tabbedPane);
        
    }

    private void startTab(JTabbedPane tabbedPane)
    {
        
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
        		appendLineIntoLog(bottomPane);
        		tabbedPane.setSelectedIndex(1);
        	}
        });
    }
    
    private void endTab(JTabbedPane tabbedPane)
    {
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

    @SneakyThrows
    private void notesTab(JTabbedPane tabbedPane)
    {
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
        
        SpellCheckedPane topPane = new SpellCheckedPane();
        JScrollPane topPaneScroll = new JScrollPane(topPane);
        splitPane.setLeftComponent(topPaneScroll);
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
        	@Override
        	public void run() {
        		LogFile.overwrite(topPane.getText());
        	}
        }));
        
        
        topPane.setText(LogFile.getContents());
        new Thread(new Runnable() {
        	
        	WatchService watchService = FileSystems.getDefault().newWatchService();

            @Override
            public void run()
            {
                Path dir = LogFile.TodaysURI.provide().getParent();
                try {
                    dir.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                } catch (IOException x) {
                    System.err.println(x);
                }
                
                // Reset the key -- this step is critical if you want to
                // receive further watch events.  If the key is no longer valid,
                // the directory is inaccessible so exit the loop.
                boolean keyIsValid = true;
                while(keyIsValid) {
                    // wait for key to be signaled
                    WatchKey key;
                    try {
                        key = watchService.take();
                    } catch (InterruptedException x) {
                        return;
                    }

                    for (WatchEvent<?> event: key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind != StandardWatchEventKinds.ENTRY_MODIFY) continue;

                        System.out.println("Loaded from file after detecting change");
                        topPane.setText(LogFile.getContents());
                    }
                    keyIsValid = key.reset();
                }
            }
            
        }).start();
        
        SpellCheckedPane bottomPane = new SpellCheckedPane();
        splitPane.setRightComponent(bottomPane);
        
        btnNewButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
            	LogFile.overwrite(topPane.getText());
            	appendLineIntoLog(bottomPane);
            }
        });
    }
    
    private void appendLineIntoLog(SpellCheckedPane submitBox) {
    	String note = submitBox.getText();
        if(note.isEmpty()) return;
        LogFile.append(note);
        submitBox.setText("");
    }
    
    private void toDoList(JTabbedPane tabbedPane)
    {
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
        
        textField = new JTextField();
        panel.add(textField, BorderLayout.CENTER);
        textField.setColumns(10);
        
        JList list = new JList();
        panel_2.add(list, BorderLayout.CENTER);
    }

}
