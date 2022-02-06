package LogRecorder;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import com.inet.jortho.FileUserDictionary;
import com.inet.jortho.SpellChecker;

import LogRecorder.Tabs.End;
import LogRecorder.Tabs.Notes;
import LogRecorder.Tabs.Setup;
import LogRecorder.Tabs.Start;
import LogRecorder.Tabs.ToDo;
import LogRecorder.Tabs.TimeDiff;

public class Application
{

    private JFrame frame;

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
        
        Setup.createTab(tabbedPane, frame);
        Start.createTab(tabbedPane);
        Notes.createTab(tabbedPane);
        ToDo.createTab(tabbedPane);
        End.createTab(tabbedPane);
        TimeDiff.createTab(tabbedPane);
        disableAllTabsExceptSetupUntilFolderSelected(tabbedPane);
    }
    
    public static void disableAllTabsExceptSetupUntilFolderSelected(JTabbedPane tabbedPane)
    {
    	tabbedPane.setEnabled(!YamlFile.logFolderIsDefaultValue());
    }
    
    public static void submitTextboxIntoLogFile(SpellCheckedPane submitBox) {
    	String note = submitBox.getText();
        if(note.isEmpty()) return;
        LogFile.append(note);
        submitBox.setText("");
    }
}
