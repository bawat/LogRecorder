package LogRecorder;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

import com.inet.jortho.FileUserDictionary;
import com.inet.jortho.SpellChecker;

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
        
        JButton btnNewButton = new JButton("Start Work");
        btnNewButton.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(btnNewButton, BorderLayout.EAST);
        
        JLabel lblNewLabel = new JLabel("What will you be working on today?");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblNewLabel, BorderLayout.CENTER);
        
        JSplitPane splitPane = new JSplitPane();
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        panel_2.add(splitPane, BorderLayout.CENTER);
        splitPane.setResizeWeight(1);
        splitPane.setDividerLocation(175);
        
        JTextPane topPane = new JTextPane();
        SpellChecker.register(topPane);
        splitPane.setRightComponent(topPane);
        
        JTextPane bottomPane = new JTextPane();
        SpellChecker.register(bottomPane);
        splitPane.setLeftComponent(bottomPane);
    }
    
    private void endTab(JTabbedPane tabbedPane)
    {
        JPanel panel_2 = new JPanel();
        tabbedPane.addTab("Finished", null, panel_2, null);
        panel_2.setLayout(new BorderLayout(0, 0));
        
        JTextPane textPane = new JTextPane();
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
        
        JTextPane topPane = new JTextPane();
        splitPane.setLeftComponent(topPane);
        
        new Thread(new Runnable() {

            @Override
            public void run()
            {
                Path dir = LogFile.TodaysURI.provide();
                try {
                    WatchKey key = dir.register(watcher, ENTRY_MODIFY);
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
                        key = watcher.take();
                    } catch (InterruptedException x) {
                        return;
                    }

                    for (WatchEvent<?> event: key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        // This key is registered only
                        // for ENTRY_CREATE events,
                        // but an OVERFLOW event can
                        // occur regardless if events
                        // are lost or discarded.
                        if (kind == OVERFLOW) {
                            continue;
                        }

                        // The filename is the
                        // context of the event.
                        WatchEvent<Path> ev = (WatchEvent<Path>)event;
                        Path filename = ev.context();

                        // Verify that the new
                        //  file is a text file.
                        try {
                            // Resolve the filename against the directory.
                            // If the filename is "test" and the directory is "foo",
                            // the resolved name is "test/foo".
                            Path child = dir.resolve(filename);
                            if (!Files.probeContentType(child).equals("text/plain")) {
                                System.err.format("New file '%s'" +
                                    " is not a plain text file.%n", filename);
                                continue;
                            }
                        } catch (IOException x) {
                            System.err.println(x);
                            continue;
                        }

                        // Email the file to the
                        //  specified email alias.
                        System.out.format("Emailing file %s%n", filename);
                        //Details left to reader....
                    }
                    keyIsValid = key.reset();
                }
            }
            
        }).start();
        
        JTextPane bottomPane = new JTextPane();
        splitPane.setRightComponent(bottomPane);
        
        btnNewButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                String note = bottomPane.getText();
                if(note.isEmpty()) return;
                LogFile.append(note + System.lineSeparator());
                bottomPane.setText("");
                topPane.setText(LogFile.getContents());
            }
        });
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
