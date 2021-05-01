package LogRecorder;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class FileSyncronisedPane extends SpellCheckedPane{
	public FileSyncronisedPane(){
		super();
        getDocument().addDocumentListener(new HumanInteractionDocumentListener(this));
	}
	
	public void setText(String str){
		HumanInteractionDocumentListener.isMachineInteraction = true;
		super.setText(str);
		HumanInteractionDocumentListener.isMachineInteraction = false;
	}
	
	private static class HumanInteractionDocumentListener implements DocumentListener{
    	public static boolean isMachineInteraction = true;
    	
    	SpellCheckedPane topPane;
    	private HumanInteractionDocumentListener(SpellCheckedPane pane){
    		topPane = pane;
    	}
    	
		@Override
		public void insertUpdate(DocumentEvent e) {
			doSomething(e);
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			doSomething(e);
		}
		@Override
		public void changedUpdate(DocumentEvent e) {
			doSomething(e);
		}
		private void doSomething(DocumentEvent e) {
			if(isMachineInteraction) return;
			LogFile.overwrite(topPane.getText());
		}
    }
}