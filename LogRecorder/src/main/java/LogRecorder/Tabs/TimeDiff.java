package LogRecorder.Tabs;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;

import com.google.common.base.Supplier;

import lombok.SneakyThrows;

public class TimeDiff {
    
    private TimeDiff() {}
    
    private final static String tabName = "Times";
    private final static JTextPane bottomText = new JTextPane();
    private static Duration totalWorkedCalculated = Duration.ofSeconds(0);
    
    @SneakyThrows
    public static void createTab(JTabbedPane tabbedPane) {
        
        
        JPanel panel_2 = new JPanel();
        panel_2.setLayout(new BorderLayout(0, 0));
        tabbedPane.addTab(tabName, null, panel_2, null);
        
        	JPanel topPanel = new JPanel();
        	topPanel.setLayout(new BorderLayout(0, 0));
        	topPanel.add(TimeInputTable.table, BorderLayout.NORTH);
	        
	        topPanel.add(bottomText, BorderLayout.CENTER);
	        bottomText.setEditable(false);
        
        panel_2.add(topPanel, BorderLayout.CENTER);
        
        JPanel panel = new JPanel();
        panel_2.add(panel, BorderLayout.SOUTH);
        panel.setLayout(new BorderLayout(0, 0));
        
	        JPanel buttonPanel = new JPanel();
	        buttonPanel.setLayout(new BorderLayout(0, 0));
	        panel.add(buttonPanel, BorderLayout.EAST);
	        
	        JButton btnClearButton = new JButton("Clear");
	        btnClearButton.setHorizontalAlignment(SwingConstants.LEFT);
	        btnClearButton.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent arg0) {
	            	bottomText.setText("");
	            	for(int y = 0; y < TimeInputTable.tableModel.getColumnCount(); y++) {
	            		for(int x = 0; x < TimeInputTable.tableModel.getRowCount(); x++) {
	                    	TimeInputTable.tableModel.setValueAt("", x, y);
	                	}
	            	}
	            	TimeInputTable.tableModel.setRowCount(1);
	            	TimeInputTable.updateTable(TimeInputTable.table);
	            }
	        });
	        buttonPanel.add(btnClearButton, BorderLayout.EAST);
	        
	        JButton btnCopyButton = new JButton("Copy");
	        btnCopyButton.setHorizontalAlignment(SwingConstants.LEFT);
	        btnCopyButton.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent arg0) {
	            	Toolkit.getDefaultToolkit()
	                .getSystemClipboard()
	                .setContents(
	                        new StringSelection(bottomText.getText()),
	                        null
	                );
	            }
	        });
	        buttonPanel.add(btnCopyButton, BorderLayout.CENTER);
	        
	        JButton btnAtToTotal = new JButton("Add To Total");
	        btnAtToTotal.setHorizontalAlignment(SwingConstants.LEFT);
	        btnAtToTotal.addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent arg0) {
	    			totalWorkedCalculated = totalWorkedCalculated.plus(TimeInputTable.getAmmountWorkedToday());
	    			TimeInputTable.updateTable(TimeInputTable.table);
	            }
	        });
	        buttonPanel.add(btnAtToTotal, BorderLayout.WEST);
        
        JLabel lblNewLabel = new JLabel("Time difference calculator");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(lblNewLabel, BorderLayout.CENTER);
    }
	
	static class TimeInputTable{
		static JTable table = new JTable();
		private static DefaultTableModel tableModel = new DefaultTableModel(
			new Object[][] {
				{"", "", ""},
			},
			new String[] {
				"Start Time", "Arrow Placeholder", "End Time"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
		};
		
		static {
			table.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					updateTable(table);
				}
			});
			table.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					updateTable(table);
				}
			});
			
			table.setModel(tableModel);
			table.getColumnModel().getColumn(0).setResizable(false);
			table.getColumnModel().getColumn(1).setResizable(false);
			table.getColumnModel().getColumn(1).setPreferredWidth(27);
			table.getColumnModel().getColumn(1).setMaxWidth(27);
			table.getColumnModel().getColumn(2).setResizable(false);
			
			updateTable(table);
		}
		
		
		private static void updateTable(JTable table) {
			Supplier<Integer> rows = () -> table.getRowCount();
			
			if(rows.get() > 1 && (
					((String)table.getValueAt(rows.get()-1, 0)).isEmpty() &&
					((String)table.getValueAt(rows.get()-1, 2)).isEmpty()
			))
				tableModel.removeRow(rows.get()-1);
			
			if(rows.get() > 0 && (
					!((String)table.getValueAt(rows.get()-1, 0)).isEmpty() ||
					!((String)table.getValueAt(rows.get()-1, 2)).isEmpty()
			))
				tableModel.addRow(new Object[] {"", "", ""});
			

			StringBuilder builder = new StringBuilder();
			
			for(int row = 0; row < rows.get(); row++) {
				table.setValueAt("→", row, 1);
				
				if(row == rows.get()-1) continue;
				builder.append(new TimeEntry(row).toString() + System.lineSeparator());
			}
			
			Duration totalWorkedToday = getAmmountWorkedToday();
			
			final String
				timeWorkedTotal = "Time worked for these 30 days   : " + formatDurationAsText(totalWorkedCalculated) + System.lineSeparator(),
				timeWorkedRemaining = "Time remaining for these 30 days: " + formatDurationAsText(Duration.ofHours(160).minus(totalWorkedCalculated)) + System.lineSeparator(),
				timeWorkedToday = "Time worked today: " + formatDurationAsText(totalWorkedToday) + System.lineSeparator();
			
			bottomText.setText(timeWorkedTotal + timeWorkedRemaining + timeWorkedToday + builder.toString());
		}
		
		static Duration getAmmountWorkedToday() {
			Duration totalWorkedToday = Duration.ofSeconds(0);

			for(int row = 0; row < table.getRowCount(); row++)
				totalWorkedToday = totalWorkedToday.plus(new TimeEntry(row).dur);
			
			return totalWorkedToday;
		}
	}
	public static String formatDurationAsText(Duration dur) {
		return String.format("%dh %02dm", dur.toHours(), dur.toMinutesPart());
	}
	
	static class TimeEntry{
		LocalTime start = null, end = null;
		Duration dur = Duration.ofSeconds(0);
		String toString = "??? -> ???";
		TimeEntry(int row){
			String
			time1 = (String)TimeInputTable.table.getValueAt(row, 0),
			time2 = (String)TimeInputTable.table.getValueAt(row, 2);
		
			try {
				start = LocalTime.parse(time1);
				end = LocalTime.parse(time2);
				
				dur = Duration.between(start, end);
				if (start.isAfter(end))// 24 - duration between end and start, note how end and start switched places
					dur = Duration.ofHours(24).minus(Duration.between(end, start));
				
				toString = time1 + " -> " + time2 + " is " + formatDurationAsText(dur);
			} catch (DateTimeParseException e) {
			}
		}
		
		@Override
		public String toString() {
			return toString;
		}
	}
}
