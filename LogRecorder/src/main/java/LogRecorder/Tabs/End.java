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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    
    private final static String tabName = "Finished";
    
    @SneakyThrows
    public static void createTab(JTabbedPane tabbedPane) {
    	JPanel panel_2 = new JPanel();
        tabbedPane.addTab(tabName, null, panel_2, null);
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
        
        calculateTimeWorked(tabbedPane, textPane);
    }

	private static void calculateTimeWorked(JTabbedPane tabbedPane, SpellCheckedPane textPane) {
		Pattern pattern = Pattern.compile("[0-9]{2}:[0-9]{2}", Pattern.CASE_INSENSITIVE);
        tabbedPane.addChangeListener(e -> {
        	if(tabName.equalsIgnoreCase(tabbedPane.getTitleAt(tabbedPane.getSelectedIndex()))) {
        		//Calculate time worked
        		String fileContents = LogFile.getContents();
        		String[] lines = fileContents.split("\\r?\\n");
        		
        		String firstTime = "";
        		String finalTime = "";
        	    Matcher matcher;
        		for(String line : lines){
        		    matcher = pattern.matcher(line);
        			if(matcher.find()) {
        				finalTime = matcher.group();
        				if(firstTime.isEmpty())
            		    	firstTime = finalTime;
        			}
        		}
        		
        		textPane.setText("Time worked: " + firstTime + " -> " + finalTime + System.lineSeparator() +
        		"Duration: " + new Time(firstTime).diffTo(new Time(finalTime)) + System.lineSeparator());
        	}
        });
	}
	
	static class TimeDiff extends Time{
		TimeDiff(String t) {super(t);}
		TimeDiff(int hour, int min){
			super(new DecimalFormat("00").format(hour) + ":" + new DecimalFormat("00").format(min));
		}
		
		public String toString() {
			return hour + " hours " + min + " minutes";
		}
	}
	static class Time{
		final int hour, min;
		Time(String t) {
			String[] components = t.split(":");
			hour = Integer.parseInt(components[0]);
			min = Integer.parseInt(components[1]);
		}
		
		int toMinutes(){
			return hour * 60 + min;
		}
		
		TimeDiff diffTo(Time end){
			SimpleDateFormat format = new SimpleDateFormat("HH:mm");
			
			Date date1 = new Date(2000, 1, 1);
			date1.setHours(hour);
			date1.setMinutes(min);
			
			Date date2 = new Date(2000, 1, 1);
			date2.setHours(end.hour);
			date2.setMinutes(end.min);
			
			if(toMinutes() > end.toMinutes())
				date2.setDate(2);
			
			int differenceMS = (int)(date2.getTime() - date1.getTime()); 
			return new TimeDiff(differenceMS/(1000 * 60 * 60), differenceMS/1000/60 % 60);
		}
	}
}
