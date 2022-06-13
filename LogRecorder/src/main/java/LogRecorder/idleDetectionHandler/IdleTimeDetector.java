package LogRecorder.idleDetectionHandler;

import org.jppf.utils.SystemUtils;

public abstract class IdleTimeDetector {
	protected abstract long getIdleTimeMillis();
	
	static final IdleTimeDetector detector;
	static {
		if (SystemUtils.isWindows()) detector = new WindowsIdleTimeDetector();
		else if (SystemUtils.isX11()) detector = new X11IdleTimeDetector();
		else if (SystemUtils.isMac()) detector = new MacIdleTimeDetector();
		else detector = null;
	}
	public static long getIdleTimeMS() {
		return detector.getIdleTimeMillis();
	}
}
