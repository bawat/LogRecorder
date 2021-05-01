package LogRecorder;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Optional;
import java.util.function.BiConsumer;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
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
import javax.swing.JTextPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.inet.jortho.FileUserDictionary;
import com.inet.jortho.SpellChecker;

import LogRecorder.Tabs.End;
import LogRecorder.Tabs.Notes;
import LogRecorder.Tabs.Setup;
import LogRecorder.Tabs.Start;
import LogRecorder.Tabs.ToDo;
import lombok.SneakyThrows;

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
