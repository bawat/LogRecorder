package LogRecorder;

import javax.swing.JTextPane;

import com.inet.jortho.SpellChecker;

public class SpellCheckedPane extends JTextPane{
	private static final long serialVersionUID = 3977410490189734116L;

	SpellCheckedPane() {
		super();
		SpellChecker.register(this);
	}
}
